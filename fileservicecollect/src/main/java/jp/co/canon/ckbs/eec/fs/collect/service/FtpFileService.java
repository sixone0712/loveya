package jp.co.canon.ckbs.eec.fs.collect.service;

import jp.co.canon.ckbs.eec.fs.collect.action.CommandExecutionException;
import jp.co.canon.ckbs.eec.fs.collect.action.ConfigurationException;
import jp.co.canon.ckbs.eec.fs.collect.model.FileInfoModel;
import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.model.RequestFileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FtpFileService {
    @Autowired
    FtpListService listService;

    @Autowired
    FtpDownloadService downloadService;

    private List<FileInfoModel> getList(String machine, String category, String keyword, String startDate, String endDate, String path) throws ConfigurationException {
        if (path == null){
            return listService.getServerFileList(machine, category, keyword, startDate, endDate);
        }
        return listService.getServerFileListInDir(machine, category, path, keyword, startDate, endDate);
    }

    public LogFileList getLogFileList(String machine, String category, String startDate, String endDate, String keyword, String path){
        LogFileList out = new LogFileList();
        if (machine == null){
            out.setErrorCode("400 Bad Request");
            out.setErrorMessage("machine is missing.");
            return out;
        }
        if (category == null){
            out.setErrorCode("400 Bad Request");
            out.setErrorMessage("category is missing.");
            return out;
        }
        try {
            List<FileInfoModel> list = this.getList(machine, category, keyword, startDate, endDate, path);
            List<FileInfo> r = new ArrayList<>();
            for (FileInfoModel src : list) {
                FileInfo dst = new FileInfo();
                dst.setFilename(src.getName());
                dst.setSize(src.getSize());
                dst.setTimestamp(src.getTimestamp());
                dst.setType(src.getType());
                r.add(dst);
            }
            out.setList(r.toArray(new FileInfo[0]));
        } catch (ConfigurationException e){
            out.setErrorCode("500 Server Error");
            out.setErrorMessage("configuration is wrong.");
        } catch (CommandExecutionException e){
            out.setErrorCode("500 Server Error");
            out.setErrorMessage("command execution error.");
        }
        return out;
    }

    public FtpDownloadRequest addDownloadRequest(String machine, String category, String[] files, boolean archive) throws Exception {
        FtpDownloadRequest request = new FtpDownloadRequest();

        request.setMachine(machine);
        request.setCategory(category);
        RequestFileInfo[] fileInfos = new RequestFileInfo[files.length];
        for(int idx = 0; idx < files.length; ++idx){
            fileInfos[idx] = new RequestFileInfo(files[idx]);
        }
        request.setFileInfos(fileInfos);
        request.setArchive(archive);

        return downloadService.addDownloadRequest(request);
    }

    public FtpDownloadRequest[] getFtpDownloadRequest(String machine, String requestNo){
        return downloadService.getFtpDownloadRequest(machine, requestNo);
    }

    public void cancelDownloadRequest(String machine, String requestNo){
        downloadService.cancelDownloadRequest(machine, requestNo);
    }
}
