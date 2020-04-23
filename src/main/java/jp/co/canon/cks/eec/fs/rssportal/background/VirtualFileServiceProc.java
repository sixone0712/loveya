package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.portal.bean.RequestInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.RequestListBean;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.portal.bussiness.ServiceException;
import jp.co.canon.cks.eec.fs.rssportal.dummy.VirtualFileServiceManagerImpl;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;

public class VirtualFileServiceProc extends RemoteFileServiceProc {

    public VirtualFileServiceProc(FileDownloadContext context) {
        super(context);
    }

    @Override
    protected void setUrl(String url) {
        context.setLocalFilePath(url);
    }

    @Override
    protected String getUrl() {
        return context.getLocalFilePath();
    }

    @Override
    void transfer() {
        log.info("transfer()");
        File inFile = new File(context.getLocalFilePath());
        if(inFile.exists()==false || inFile.isDirectory()) {
            log.error("couldn't find a achieve file");
            return;
        }

        File outDir = new File(context.getOutPath());
        outDir.mkdirs();

        String lastName = inFile.getName();
        Path outPath = Paths.get(context.getOutPath(), lastName);

        byte[] buf = new byte[1024];
        try {
            InputStream is = new FileInputStream(inFile);
            OutputStream os = new FileOutputStream(outPath.toFile());
            while((is.read(buf))>0) {
                os.write(buf);
                os.flush();
            }
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("transfer done [filename="+outPath.toString()+"]");
    }
}
