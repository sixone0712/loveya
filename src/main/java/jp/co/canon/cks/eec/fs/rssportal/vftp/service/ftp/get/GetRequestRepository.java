package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.RequestRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GetRequestRepository extends RequestRepository{
    @Value("${rssportal.vftp.directory.request}")
    private String requestFileRootDirectory;

    private ObjectMapper objectMapper = new ObjectMapper();
    private static String requestInfoFileName = "requestInfo.json";
    private String rootDir = ".";

    @PostConstruct
    private void init(){
        this.rootDir = requestFileRootDirectory + "/GET";
    }

    @Override
    public String getRootDir() {
        return rootDir;
    }

    @Override
    public String getRequestInfoFileName() {
        return requestInfoFileName;
    }

    @Override
    public String getRequestNoPrefix() {
        return "GET_";
    }

    private File getRequestDirectory(String requestNo){
        return new File(getRootDir(), requestNo);
    }

    public String getRequestDirectoryPath(String requestNo){
        return getRequestDirectory(requestNo).getAbsolutePath();
    }

    public synchronized GetRequest getRequestById(String requestNo){
        File requestDir = getRequestDirectory(requestNo);
        log.debug("GetRequest getRequestById " + requestNo);
        log.debug("requestDir + " + requestDir);
        if (requestDir.exists() && requestDir.isDirectory()) {
            File requestInfoFile = new File(requestDir, getRequestInfoFileName());
            if (requestInfoFile.exists() && requestInfoFile.isFile()) {
                FileInputStream is;
                try {
                    is = new FileInputStream(requestInfoFile);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                }

                GetRequest req = null;
                try {
                    req = objectMapper.readValue(is, GetRequest.class);
                } catch (JsonParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return req;
            }
            log.debug("file not exists");
            return null;
        }
        log.debug("directory not exists");
        return null;
    }

    public synchronized void save(GetRequest req){
        if (req.getRequestNo() == null){
            String requestNo = createRequestNo(req.getCreatedTime().getTime());
            req.setRequestNo(requestNo);
            req.setRequestDir(this.getRequestDirectoryPath(requestNo));
        }
        log.debug("GetRequestRepository Save " + req.getRequestNo());
        String json = null;
        try {
            json = objectMapper.writeValueAsString(req);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (json == null) {
            log.debug("save filed. json is null");
        } else {
            File requestDir = getRequestDirectory(req.getRequestNo());
            if (!requestDir.exists()){
                requestDir.mkdirs();
            }

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(new File(requestDir, getRequestInfoFileName()));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (out != null){
                try {                    
                    out.write(json.getBytes("UTF-8"));
                    out.flush();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    out.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }        
    }
}
