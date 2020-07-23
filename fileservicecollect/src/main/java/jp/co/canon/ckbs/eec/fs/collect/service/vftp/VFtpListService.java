package jp.co.canon.ckbs.eec.fs.collect.service.vftp;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssListRequest;
import jp.co.canon.ckbs.eec.fs.collect.service.FileServiceCollectException;
import jp.co.canon.ckbs.eec.fs.collect.service.configuration.ConfigurationService;
import jp.co.canon.ckbs.eec.fs.collect.service.configuration.FtpServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class VFtpListService {
    @Value("${fileservice.configDirectory}")
    String configDirectory;

    @Autowired
    ConfigurationService configurationService;

    File workingDir = null;

    long lastRequestNumber = 0;
    DateFormat format = new SimpleDateFormat("yyMMddHHmmssSSS");

    Map<String, VFtpSssListRequest> requestMap = new HashMap<>();
    Map<String, SssListProcessThread> listRequestThreadMap = new HashMap<>();

    @PostConstruct
    private void postConstruct(){
        File configDir = new File(configDirectory);
        workingDir = new File(configDir, "Working");
        if (!workingDir.exists()){
            workingDir.mkdirs();
        }
    }

    private File getWorkingDir() {
        return workingDir;
    }

    void addRequest(VFtpSssListRequest request){
        request.setTimestamp(System.currentTimeMillis());
        requestMap.put(request.getRequestNo(), request);
    }

    Date generateRequestTime(){
        final Date currentTime = new Date();
        if (lastRequestNumber >= currentTime.getTime()){
            currentTime.setTime(lastRequestNumber + 1);
        }
        lastRequestNumber = currentTime.getTime();
        return currentTime;
    }

    String generateRequestNoFromTime(Date requestTime, String machine, String directory){
        StringBuilder id = new StringBuilder()
                .append("REQ_SSSLIST_")
                .append(machine).append("_")
                .append(format.format(requestTime));
        return id.toString();
    }

    boolean isValidDirectory(String directory){
        return true;
    }

    public VFtpSssListRequest addListRequest(VFtpSssListRequest request) throws FileServiceCollectException {
        FtpServerInfo ftpServerInfo = configurationService.getFtpServerInfo(request.getMachine());
        if (ftpServerInfo == null){
            throw new FileServiceCollectException(400, "Parameter(machine) is not valid.");
        }
        if (isValidDirectory(request.getDirectory()) == false){
            throw new FileServiceCollectException(400, "Parameter(directory) is not valid");
        }

        Date requestTime = generateRequestTime();

        String requestNo = generateRequestNoFromTime(requestTime, request.getMachine(), request.getDirectory());
        request.setRequestNo(requestNo);
        addRequest(request);

        SssListProcessThread thread = new SssListProcessThread(request, ftpServerInfo, getWorkingDir());
        listRequestThreadMap.put(request.getRequestNo(), thread);
        thread.start();

        return request;
    }

    public VFtpSssListRequest getListRequest(String machine, String requestNo){
        VFtpSssListRequest request = requestMap.get(requestNo);
        if (request != null){
            if (request.getMachine().equals(machine)){
                return request;
            }
        }
        return null;
    }

    public void cancelAndDeleteSssListRequest(String machine, String requestNo){
        SssListProcessThread listRequestThread = listRequestThreadMap.get(requestNo);
        if (listRequestThread != null){
            listRequestThread.stopExecute();
            listRequestThreadMap.remove(requestNo);
        }
    }

}
