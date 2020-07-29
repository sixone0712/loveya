package jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc;

import jp.co.canon.cks.eec.fs.portal.bussiness.CustomURL;
import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloadContext;
import jp.co.canon.cks.eec.fs.rssportal.background.FtpWorker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VFtpCompatFileServiceProc extends Thread {

    private final Log log = LogFactory.getLog(getClass());
    private final FileDownloadHandler handler;
    private final FileDownloadContext context;

    public VFtpCompatFileServiceProc(FileDownloadHandler handler, FileDownloadContext context) {
        this.handler = handler;
        this.context = context;
    }

    @Override
    public void run() {
        log.info("running");
        register();
        /*if(!download()) {
            log.info(context.getRequestNo()+" download failed");
            completed = -1; // negative means an error occurs.
            notifyCall(notifyError);
            return;
        }*/
        download();
        transfer();
        extract();
//        completed = 0;
//        notifyCall(notifyFinish);
    }

    private void register() {
        log.info("register()");
        String requestNo = handler.createDownloadRequest();
        if(requestNo==null) {
            log.error("faield to create vftp(compat) request");
            return;
        }
        context.setRequestNo(requestNo);
        log.info("request-no="+requestNo);
    }

    private void download() {
        log.info("download  machine="+context.getTool()+" category="+context.getLogType());
        while(true) {
            FileDownloadInfo info = handler.getDownloadedFiles(context.getRequestNo());
            if(info.isError()) {
                log.error("download error occurs");
                return;
            }
            if(info.isFinish()) {
                log.info("download completed");
                break;
            }
            try {
                sleep(500);
            } catch (InterruptedException e) {
                log.info("downloading interrupt occurs");
                handler.cancelDownloadRequest(context.getRequestNo());
            }
        }

        String ftpAddress = handler.getFtpAddress(context.getRequestNo());
        if(ftpAddress==null) {
            log.error("no ftp address error");
            return;
        }
        log.info("ftp-address="+ftpAddress);
        context.setAchieveUrl(ftpAddress);
    }

    void transfer() {
        log.info("transfer()");
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

    void extract() {
        if(context.isAchieve()) {
            log.info("extract [achieve="+context.getLocalFilePath()+")");
            if (!context.getLocalFilePath().endsWith(".zip")) {
                log.error("no achieve file");
                return;
            }
            File zip = new File(context.getLocalFilePath());
            if(!zip.exists() || zip.isDirectory()) {
                log.error("wrong achieve file type  "+zip.toString());
                return;
            }

            Path path;
            String sub = context.getSubDir();
            if(sub!=null && !sub.isEmpty()) {
                path = Paths.get(zip.getAbsolutePath(), sub);
            } else {
                path = Paths.get(zip.getAbsolutePath());
            }
            File outDir = path.toFile();
            if(!outDir.exists()) {
                outDir.mkdirs();
            }
            log.info("21414124");
        }


    }

    private String parseDir(@NonNull final String file) {
        String sep = File.separator;
        int idx = file.lastIndexOf(sep);
        if(idx==-1) {
            return "";
        }
        return file.substring(0, idx);
    }
}
