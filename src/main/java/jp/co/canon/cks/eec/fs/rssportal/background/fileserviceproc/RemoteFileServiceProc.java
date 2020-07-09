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

    private int downloadFiles = 0;
    private final long checkPointExpire = 60000*3; // 3 minutes
    private long checkPointTime;
    private int checkPointFiles;

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
    boolean download() {
        log.info("download() (tool="+context.getTool()+" type="+context.getLogType()+")");
        FileServiceManage manager = context.getFileManager();
        FileServiceModel service = context.getFileService();

        monitor.add(context.getSystem(), context.getTool(), context.getRequestNo(), service, files->{
            downloadFiles = files;
            context.setDownloadFiles(files);
            setCheckPoint();
        });

        try {
            setCheckPoint();
            while(downloadFiles!=context.getFileCount()) {
                sleep(500);
                if(isCheckPointExpired()) {
                    log.warn("no progress for a long time "+
                            String.format("%s/%s/%s", context.getTool(), context.getLogType(), context.getSubDir()));
                }
            }
            monitor.delete(context.getSystem(), context.getTool(), context.getRequestNo());
            log.info("ready to download (tool="+context.getTool()+" type="+context.getLogType()+")");

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
                if(achieveUrl==null) {
                    // When there is no file to download, 'achieveUrl' is null.
                    // We consider that who asks this download requested invalid download.
                    log.error("null downloaded file");
                    return false;
                }
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
        return true;
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
            if(zipFile.exists()==false || zipFile.isDirectory()) {
                log.error("wrong achieve file");
                return;
            }
            String dir = parseDir(achieve.toString());
            String subDir = context.getSubDir();
            if(subDir!=null && !subDir.equals("")) {
                dir += File.separator+subDir;
            }
            File dirHandle = new File(dir);
            if(!dirHandle.exists()) {
                dirHandle.mkdirs();
            }

            try {
                byte[] buf = new byte[1024];
                int size;
                ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
                ZipEntry entry = zis.getNextEntry();
                while(entry!=null) {
                    File tmpFile = new File(dir, entry.getName());
                    FileOutputStream fos = new FileOutputStream(tmpFile);
                    while((size=zis.read(buf))>0) {
                        fos.write(buf, 0, size);
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

    private void setCheckPoint() {
        checkPointTime = System.currentTimeMillis();
        checkPointFiles = downloadFiles;
    }

    private boolean isCheckPointExpired() {
        if(checkPointFiles==downloadFiles && (System.currentTimeMillis()-checkPointTime)>checkPointExpire) {
            return true;
        }
        return false;
    }

}
