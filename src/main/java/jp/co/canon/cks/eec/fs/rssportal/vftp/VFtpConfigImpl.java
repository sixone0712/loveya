package jp.co.canon.cks.eec.fs.rssportal.vftp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class VFtpConfigImpl implements VFtpConfig{
    private String objectlistfile;
    private String requestRootDir;

    private ApplicationContext context;

    @Autowired
    public VFtpConfigImpl(ApplicationContext context){
        this.context = context;
        System.out.println("##### VFtpConfig For Real Loaded!!!!! ######");

        Environment environment = context.getEnvironment();
        objectlistfile = environment.getProperty("rssportal.vftp.objectlistfile");
        if (objectlistfile == null){
            objectlistfile = "/usr/local/canon/esp/CanonFileService/definitions.ObjectList.xml";
        }
        requestRootDir = environment.getProperty("rssportal.vftp.directory.request");
        if (requestRootDir == null){
            requestRootDir = "/LOG/VFTP/Requests";
        }
    }

    @Override
    public String getRequestFileRootDirectory() {
        return requestRootDir;
    }

    @Override
    public String getObjectListFilePath() {
        return objectlistfile;
    }    
}