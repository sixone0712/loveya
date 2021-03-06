package jp.co.canon.ckbs.eec.fs.collect.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class FtpDownloadRequest extends FtpRequest{
    @Getter @Setter
    String category;

    @Getter @Setter
    String result;

    @Getter @Setter
    boolean archive;

    @Getter @Setter
    String archiveFileName;

    @Getter @Setter
    long archiveFileSize;

    @Getter @Setter
    String archiveFilePath;

    Map<String, RequestFileInfo> fileInfoMap = new HashMap<>();

    @Getter @Setter
    String directory = null;

    @Getter @Setter
    String errorMessage = null;

    @Getter
    long downloadedFileCount = 0;

    @Getter
    long totalFileCount = 0;

    public FtpDownloadRequest(){
        status = Status.WAIT;
    }

    public void fileDownloaded(String fileName, long fileSize){
        RequestFileInfo[] fileList = getFileInfos();
        for (RequestFileInfo info : fileList){
            if (info.netFileName().equals(fileName)){
                info.setSize(fileSize);
                if (info.isDownloaded() == false) {
                    info.setDownloaded(true);
                    if (this.archive == false) {
                        info.setDownloadPath(directory + "/" + info.netFileName());
                    }
                    ++downloadedFileCount;
                }
            }
        }
    }

    public RequestFileInfo[] getFileInfos(){
        return fileInfoMap.values().toArray(new RequestFileInfo[0]);
    }

    public void setFileInfos(RequestFileInfo[] fileInfos){
        fileInfoMap.clear();
        totalFileCount = fileInfos.length;
        for(RequestFileInfo info : fileInfos){
            fileInfoMap.put(info.getName(), info);
        }
    }

    public static boolean checkCompletedStatus(FtpDownloadRequest request){
        Status sts = request.getStatus();
        if (sts == Status.CANCEL){
            return true;
        }
        if (sts == Status.ERROR){
            return true;
        }
        if (sts == Status.EXECUTED){
            return true;
        }
        return false;
    }
}
