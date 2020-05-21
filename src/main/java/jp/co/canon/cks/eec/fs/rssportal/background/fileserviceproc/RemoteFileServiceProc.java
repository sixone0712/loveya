package jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc;

import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.portal.bean.RequestInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.RequestListBean;
import jp.co.canon.cks.eec.fs.portal.bussiness.CustomURL;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.portal.bussiness.ServiceException;
import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloadContext;
import jp.co.canon.cks.eec.fs.rssportal.background.FtpWorker;
import org.springframework.lang.NonNull;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RemoteFileServiceProc extends FileServiceProc {

    public RemoteFileServiceProc(@NonNull FileDownloadContext context) {
        super(context, RemoteFileServiceProc.class);
    }

    @Override
    void register() {
        log.info("register()");
        try {
            FileServiceManage manager = context.getFileManager();
            String request = manager.registRequest(
                    context.getSystem(),
                    context.getUser(),
                    context.getTool(),
                    context.getComment(),
                    context.getLogType(),
                    context.getFileNames(),
                    context.getFileSizes(),
                    context.getFileDates());

            log.info("requestNo="+request);
            context.setRequestNo(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    void download() {
        log.info("download()");
        FileServiceManage manager = context.getFileManager();
        FileServiceModel service = context.getFileService();

        monitor.add(context.getSystem(), context.getTool(), context.getRequestNo(), service);

        try {
            while(true) {
                RequestInfoBean requestInfoBean = monitor.get(context.getSystem(), context.getTool(), context.getRequestNo());
                if (requestInfoBean != null) {
                    context.setDownloadFiles(requestInfoBean.getNumerator());
                    if(requestInfoBean.getNumerator() == requestInfoBean.getDenominator()) {
                        monitor.delete(context.getSystem(), context.getTool(), context.getRequestNo());
                        break;
                    }
                }
                sleep(100);
            }

            while(true) {
                RequestListBean requestList = service.createDownloadList(context.getSystem(), context.getTool(), context.getRequestNo());
                RequestInfoBean reqInfo = requestList.get(context.getRequestNo());
                if(reqInfo==null) {
                    Thread.sleep(100);
                    continue;
                }

                String achieveUrl = manager.download(context.getUser(), context.getSystem(), context.getTool(),
                        context.getRequestNo(), reqInfo.getArchiveFileName());
                log.info("download " + reqInfo.getArchiveFileName() + "(url=" + achieveUrl + ")");
                setUrl(achieveUrl);
                break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    void transfer() {
        log.info("transfer()");
        CustomURL url = context.getAchieveUrl();

        FtpWorker worker = new FtpWorker(url.getHost(), url.getPort(), url.getLoginUser(),
                url.getLoginPassword(), url.getFtpMode());

        worker.open();

        Path path = Paths.get(context.getOutPath(), url.getLastFileName());
        if (worker.transfer(url.getFile(), path.toString())) {
            context.setFtpProcComplete(true);
        }
        worker.close();
    }

    @Override
    void extract() {
        Path achieve = Paths.get(context.getOutPath(), getUrl());
        log.info("extract [achieve="+achieve.toString()+")");
        File zipFile = achieve.toFile();

        if(zipFile.toString().endsWith(".zip")) {
            String dir = parseDir(achieve.toString());
            if(zipFile.exists()==false || zipFile.isDirectory()) {
                log.error("wrong achieve file");
                return;
            }

            try {
                byte[] buf = new byte[1024];
                ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
                ZipEntry entry = zis.getNextEntry();
                while(entry!=null) {
                    File tmpFile = new File(dir, entry.getName());
                    FileOutputStream fos = new FileOutputStream(tmpFile);
                    while(zis.read(buf)>0) {
                        fos.write(buf);
                    }
                    fos.close();
                    entry = zis.getNextEntry();
                }
                zis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            zipFile.delete();
        }
    }

    protected void setUrl(@NonNull String url) {
        context.setAchieveUrl(url);
    }

    protected String getUrl() {
        return context.getAchieveUrl().getLastFileName();
    }

    protected String parseDir(@NonNull final String file) {
        String sep = File.separator;
        int idx = file.lastIndexOf(sep);
        if(idx==-1) {
            return "";
        }
        return file.substring(0, idx);
    }
}