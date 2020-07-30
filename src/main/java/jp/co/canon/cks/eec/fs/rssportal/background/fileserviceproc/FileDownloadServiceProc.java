package jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc;

import jp.co.canon.cks.eec.fs.portal.bussiness.CustomURL;
import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloadContext;
import jp.co.canon.cks.eec.fs.rssportal.background.FtpWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileDownloadServiceProc extends Thread {

    public enum Status { InProgress, Finished, Canceled, Error }

    private final Log log = LogFactory.getLog(getClass());
    private final FileDownloadHandler handler;
    private final FileDownloadContext context;
    private final FileDownloadServiceCallback callback;

    private Status status;
    private List<Runnable> pipes;

    public FileDownloadServiceProc(FileDownloadHandler handler,
                                   FileDownloadContext context,
                                   FileDownloadServiceCallback callback) {
        this.handler = handler;
        this.context = context;
        this.callback = callback;
        this.status = Status.InProgress;
        this.start();
    }

    private void buildPipes() {
        pipes = new ArrayList<>();
        pipes.add(this::register);
        pipes.add(this::download);
        pipes.add(this::transfer);
        if(context.isAchieveDecompress()) {
            pipes.add(this::decompress);
        }
    }

    @Override
    public void run() {
        buildPipes();
        log.info("download pipe start");
        for(Runnable pipe: pipes) {
            if(status==Status.Error) {
                log.error("stop running");
                callback.call(this);
                return;
            }
            pipe.run();
        }
        status = Status.Finished;
        callback.call(this);
    }

    private void register() {
        log.info("[pipe#register] register()");
        String requestNo = handler.createDownloadRequest();
        if(requestNo==null) {
            log.error("[pipe#register] faield to create vftp(compat) request");
            return;
        }
        context.setRequestNo(requestNo);
        log.info("[pipe#register] request-no="+requestNo);
    }

    private void download() {
        log.info("[pipe#download] download  machine="+context.getTool()+" category="+context.getLogType());
        while(true) {
            FileDownloadInfo info = handler.getDownloadedFiles();
            if(info.isError()) {
                log.error("[pipe#download] download error occurs");
                status = Status.Error;
                return;
            }
            if(info.isFinish()) {
                log.info("[pipe#download] download completed");
                break;
            }
            try {
                sleep(500);
            } catch (InterruptedException e) {
                log.info("[pipe#download] downloading interrupt occurs");
                handler.cancelDownloadRequest();
                status = Status.Error;
            }
        }

        String ftpAddress = handler.getFtpAddress();
        if(ftpAddress==null) {
            log.error("[pipe#download] no ftp address error");
            status = Status.Error;
            return;
        }
        log.info("[pipe#download] ftp-address="+ftpAddress);
        context.setAchieveUrl(ftpAddress);
    }

    private void transfer() {
        log.info("[pipe#transfer] transfer()");
        CustomURL address = context.getAchieveUrl();
        FtpWorker worker = new FtpWorker(address.getHost(), address.getPort(), address.getLoginUser(),
                address.getLoginPassword(), address.getFtpMode());
        worker.open();
        String destination = Paths.get(context.getOutPath(), address.getLastFileName()).toString();
        context.setLocalFilePath(destination);
        if(worker.transfer(address.getFile(), destination)) {
            context.setFtpProcComplete(true);
        }
        worker.close();
    }

    private void decompress() {
        log.info("[pipe#decompress] decompress [achieve="+context.getLocalFilePath()+")");
        if (!context.getLocalFilePath().endsWith(".zip")) {
            log.error("no achieve file");
            status = Status.Error;
            return;
        }

        File zip = new File(context.getLocalFilePath());
        if(!zip.exists() || zip.isDirectory()) {
            log.error("[pipe#decompress] wrong achieve file type  "+zip.toString());
            status = Status.Error;
            return;
        }

        Path path;
        String sub = context.getSubDir();
        if(sub!=null && !sub.isEmpty()) {
            path = Paths.get(zip.getParent(), sub);
        } else {
            path = Paths.get(zip.getParent());
        }
        File outDir = path.toFile();
        if(!outDir.exists()) {
            outDir.mkdirs();
        }

        byte[] buf = new byte[1024*64];
        int size;
        try(ZipInputStream zis = new ZipInputStream(new FileInputStream(zip))) {
            ZipEntry entry = zis.getNextEntry();
            while(entry!=null) {
                File tmpFile = new File(path.toString(), entry.getName());
                try(FileOutputStream fos = new FileOutputStream(tmpFile)) {
                    while((size=zis.read(buf))>0) {
                        fos.write(buf, 0, size);
                    }
                }
                entry = zis.getNextEntry();
            }
        } catch (IOException e) {
            log.error("[pipe#decompress] extraction failed");
            status = Status.Error;
            return;
        }
        zip.delete();
    }

    public Status getStatus() {
        return status;
    }
}
