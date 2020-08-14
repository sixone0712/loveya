package jp.co.canon.ckbs.eec.fs.collect.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class FtpDownloadFileRepository {
    @Value("${fileservice.collect.ftp.downloadDirectory}")
    String downloadDirectory;

    File downDirFile;
    ObjectMapper objectMapper = new ObjectMapper();

    Map<String, FtpDownloadRequest> requestMap = new HashMap<>();

    @PostConstruct
    void postConstruct(){
        downDirFile = new File(downloadDirectory);
        if (!downDirFile.exists()){
            downDirFile.mkdirs();
        }
    }

    String createRequestDirectory(FtpDownloadRequest request){
        return request.getRequestNo();
    }

    Map<String, FtpDownloadRequest> getCloneRequestMap(){
        Map<String, FtpDownloadRequest> newMap = new HashMap<>();
        for(String requestNo : requestMap.keySet()){
            newMap.put(requestNo, requestMap.get(requestNo));
        }
        return newMap;
    }

    public Map<String, FtpDownloadRequest> getRequestList(){
        synchronized (requestMap) {
            for (FtpDownloadRequest request : requestMap.values()) {
                File requestDir = new File(downloadDirectory, request.getDirectory());
                if (!requestDir.exists()) {
                    removeRequest(request);
                    continue;
                }
                if (request.getStatus() == FtpDownloadRequest.Status.CANCEL) {
                    removeRequest(request);
                    continue;
                }
                if (request.getStatus() == FtpDownloadRequest.Status.WAIT) {
                    continue;
                }
                if (request.getStatus() == FtpDownloadRequest.Status.EXECUTED) {
                    continue;
                }
                if (request.getStatus() == FtpDownloadRequest.Status.EXECUTING) {
                    try {
                        readDirectory(request);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return getCloneRequestMap();
        }
    }

    public void addRequest(FtpDownloadRequest request) throws IOException{
        String requestDirStr = createRequestDirectory(request);
        request.setDirectory(requestDirStr);
        File requestDownDirectory = new File(downloadDirectory, request.getDirectory());
        requestDownDirectory.mkdirs();
        synchronized (requestMap) {
            requestMap.put(request.getRequestNo(), request);
        }
    }

    synchronized void writeRequest(FtpDownloadRequest request, File file) throws IOException {
        try {
            objectMapper.writeValue(file, request);
        } catch (IOException e) {
            throw e;
        }
    }

    public void writeRequest(FtpDownloadRequest request) throws IOException {
        synchronized (request) {
            File requestDownDirectory = new File(downloadDirectory, request.getDirectory());
            requestDownDirectory.mkdirs();
            File requestFile = new File(requestDownDirectory, request.getRequestNo() + ".json");
            writeRequest(request, requestFile);
        }
    }

    public FtpDownloadRequest readRequest(File target) throws Exception{
        if (target == null){
            throw new Exception("file is not found");
        }
        if (!target.exists()){
            throw new Exception("file is not found");
        }
        try {
            FtpDownloadRequest request = objectMapper.readValue(target, FtpDownloadRequest.class);
            return request;
        } catch (IOException e) {
            throw e;
        }
    }

    void readDownFileList(FtpDownloadRequest request) throws IOException {
        File requestDir = new File(downloadDirectory, request.getDirectory());
        File downList = new File(requestDir, "FileList.LST");
        if (downList.exists()){
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(downList));
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    String[] values = line.split(",");
                    String name = values[0];
                    long fileSize = Long.parseLong(values[1]);
                    request.fileDownloaded(name, fileSize);
                }
            } catch (IOException e){
                throw e;
            }
            finally {
                if (reader != null){
                    reader.close();
                }
            }
        }
    }

    void readDirectoryFiles(FtpDownloadRequest request){
        File requestDir = new File(downloadDirectory, request.getDirectory());
        File error = new File(requestDir, "error.msg");
        File downList = new File(requestDir, "FileList.LST");
        File zip = new File(requestDir, request.getRequestNo()+".zip");

        File[] list = requestDir.listFiles();
        for (File downFile : list){
            if (downFile.equals(error)){
                continue;
            }
            if (downFile.equals(downList)){
                continue;
            }
            if (request.isArchive() && downFile.equals(zip)){
                continue;
            }
            request.fileDownloaded(downFile.getName(), downFile.length());
        }
    }

    String readErrorMsg(File file) throws IOException{
        StringBuffer value = new StringBuffer();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));

            String buff;
            while ((buff = br.readLine()) != null) {
                value.append(buff);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }

        return value.toString();
    }

    void readDirectory(FtpDownloadRequest request) throws IOException {
        synchronized (request){
            File requestDownDirectory = new File(downloadDirectory, request.getDirectory());
            File error = new File(requestDownDirectory, "error.msg");
            readDirectoryFiles(request);

            if (request.isArchive()){
                readDownFileList(request);
                File zipFile = new File(requestDownDirectory, request.getRequestNo()+".zip");
                if (zipFile.exists()){
                    request.setArchiveFileName(zipFile.getName());
                    request.setArchiveFileSize(zipFile.length());
                    request.setArchiveFilePath(request.getDirectory() + "/" + request.getArchiveFileName());
                }
            }

            if (error.exists()){
                request.setErrorMessage(readErrorMsg(error));
                request.setStatus(FtpDownloadRequest.Status.ERROR);
            }
        }
    }

    static void deleteDirectory(File dir){
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeRequest(FtpDownloadRequest request){
        synchronized (request){
            File requestDownDirectory = new File(downloadDirectory, request.getDirectory());
            File requestFile = new File(requestDownDirectory, request.getRequestNo() + ".json");
            if (requestFile.exists()){
                deleteDirectory(requestDownDirectory);
            }
            synchronized (requestMap) {
                requestMap.remove(request.getRequestNo());
            }
        }
    }
}
