package jp.co.canon.ckbs.eec.fs.collect.service.ftp;

import jp.co.canon.ckbs.eec.fs.collect.action.CommandRepository;
import jp.co.canon.ckbs.eec.fs.collect.action.FtpCommandExecutorThread;
import jp.co.canon.ckbs.eec.fs.collect.action.FtpDownloadFileRepository;
import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Component
public class FtpDownloadService {
    @Autowired
    CommandRepository commandRepository;

    @Autowired
    FtpDownloadFileRepository downloadFileRepository;

    @Value("${fileservice.collect.ftp.downloadDirectory}")
    String ftpDownloadDirectory;

    @Value("${fileservice.configDirectory}")
    String configDirectory;

    BlockingDeque<FtpDownloadRequest> mainQueue = new LinkedBlockingDeque<>();
    long lastRequestNumber = 0;
    DateFormat format = new SimpleDateFormat("yyMMddHHmmssSSS");
    List<FtpCommandExecutorThread> commandExecutorThreadList = new ArrayList<>();

    @PostConstruct
    private void postConstruct(){
        File downDir = new File(ftpDownloadDirectory);
        if (!downDir.exists()){
            downDir.mkdirs();
        }

        File configDir = new File(configDirectory);
        File requestDir = new File(configDir, "Requests");
        if (!requestDir.exists()){
            requestDir.mkdirs();
        }
        File workDir = new File(configDir, "Working");
        int adminPortStart = 6201;
        for(int idx = 0; idx < 6; ++idx){
            FtpCommandExecutorThread commandExecutorThread = new FtpCommandExecutorThread(mainQueue,
                    commandRepository,
                    downloadFileRepository,
                    workDir,
                    adminPortStart + idx,
                    10);
            commandExecutorThreadList.add(commandExecutorThread);
            Thread th = new Thread(commandExecutorThread);
            th.start();
        }
    }

    public void stopAll(){
        for (FtpCommandExecutorThread commandExecutorThread : commandExecutorThreadList){
            commandExecutorThread.stop();
        }
        commandExecutorThreadList.clear();
    }

    Date generateRequestTime(){
        final Date currentTime = new Date();
        if (lastRequestNumber >= currentTime.getTime()){
            currentTime.setTime(lastRequestNumber + 1);
        }
        lastRequestNumber = currentTime.getTime();
        return currentTime;
    }

    String generateRequestNoFromTime(Date requestTime, String machine, String category){
        StringBuilder id = new StringBuilder()
                .append("REQ_")
                .append(machine).append("_")
                .append(category).append("_")
                .append(format.format(requestTime));
        return id.toString();
    }

    void enqueueDownloadRequest(FtpDownloadRequest request) throws Exception{
        downloadFileRepository.addRequest(request);
        mainQueue.add(request);
    }

    public FtpDownloadRequest addDownloadRequest(FtpDownloadRequest request) throws Exception{
        final Date requestTime = generateRequestTime();

        String requestNo = generateRequestNoFromTime(requestTime, request.getMachine(), request.getCategory());
        request.setTimestamp(requestTime.getTime());
        request.setRequestNo(requestNo);
        enqueueDownloadRequest(request);
        return request;
    }

    public FtpDownloadRequest[] getFtpDownloadRequest(String machine, String requestNo){
        ArrayList<FtpDownloadRequest> list = new ArrayList<>();

        Map<String, FtpDownloadRequest> requestMap = downloadFileRepository.getRequestList();
        for(FtpDownloadRequest request : requestMap.values()){
            if (machine != null && !request.getMachine().equals(machine)){
                continue;
            }
            if (requestNo != null && !request.getRequestNo().equals(requestNo)){
                continue;
            }
            list.add(request);
        }
        return list.toArray(new FtpDownloadRequest[0]);
    }

    public boolean cancelDownloadRequest(String machine, String requestNo){
        for (FtpDownloadRequest request : mainQueue){
            if (!request.getMachine().equals(machine)){
                continue;
            }
            if (request.getRequestNo().equals(requestNo)){
                request.setStatus(FtpDownloadRequest.Status.CANCEL);
                downloadFileRepository.removeRequest(request);
                return true;
            }
        }
        for (FtpCommandExecutorThread thread : commandExecutorThreadList){
            FtpDownloadRequest request = thread.getCurrentRequest();
            if (request != null){
                if (!request.getMachine().equals(machine)){
                    continue;
                }
                if (request.getRequestNo().equals(requestNo)) {
                    boolean rc = thread.stopRequestCommand(requestNo);
                    request.setStatus(FtpDownloadRequest.Status.CANCEL);
                    downloadFileRepository.removeRequest(request);
                    return rc;
                }
            }
        }
        return false;
    }
}
