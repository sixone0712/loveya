package jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.list;

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
public class ListRequestRepository extends RequestRepository{
    @Value("${rssportal.vftp.directory.request}")
    private String requestFileRootDirectory;

    private ObjectMapper objectMapper = new ObjectMapper();
    private static String requestInfoFileName = "requestInfo.json";
    private String rootDir = ".";

    @PostConstruct
    private void init(){
        this.rootDir = requestFileRootDirectory + "/LIST";
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
        return "LIST_";
    }

    

    public ListRequest getRequestById(String requestNo) {
        File requestDir = new File(getRootDir(), requestNo);

        File requestInfoFile = new File(requestDir, getRequestInfoFileName());
        FileInputStream is;
        try {
            is = new FileInputStream(requestInfoFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        ListRequest req = null;
        try {
            req = objectMapper.readValue(is, ListRequest.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return req;
    }


    public void save(ListRequest req) {
        if (req.getRequestNo() == null){
            String requestNo = createRequestNo(req.getCreatedTime().getTime());
            req.setRequestNo(requestNo);
        }
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
            File requestDir = new File(getRootDir(), req.getRequestNo());
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