package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class RequestRepository {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public abstract String getRootDir();
    public abstract String getRequestInfoFileName();
    public abstract String getRequestNoPrefix();

    public boolean exists(String requestNo){
        File requestDir = new File(getRootDir(), requestNo);
        return requestDir.exists();
    }

    public void delete(String requestNo){
        File requestDir = new File(getRootDir(), requestNo);
        if (requestDir.exists() && requestDir.isDirectory()) {
            File requestInfoFile = new File(requestDir, getRequestInfoFileName());
            if (requestInfoFile.exists());
            requestInfoFile.delete();
            requestDir.delete();
        }
    }

    protected String createRequestNo(Date createdTime){
        String timeStr = dateFormat.format(createdTime);
        String requestNo;

        int idx = 0;

        requestNo = getRequestNoPrefix() + timeStr + "_" + Integer.toString(idx);
        while(exists(requestNo)){
            ++idx;
            requestNo = getRequestNoPrefix() + timeStr + "_" + Integer.toString(idx);
        }
        return requestNo;
    }
}