package jp.co.canon.ckbs.eec.fs.collect.service.vftp;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpCompatDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.service.FileServiceCollectException;
import jp.co.canon.ckbs.eec.fs.collect.service.configuration.ConfigurationService;
import jp.co.canon.ckbs.eec.fs.collect.service.configuration.FtpServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class VFtpDownloadService {
    @Value("${fileservice.configDirectory}")
    String configDirectory;

    @Value("${fileservice.collect.vftp.downloadDirectory}")
    String vftpDownloadDirectory;

    @Autowired
    ConfigurationService configurationService;

    long lastRequestNumber = 0;
    DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    StringToOtherTypeMap<VFtpSssDownloadRequest> sssRequestMap = new StringToOtherTypeMap<>();
    StringToOtherTypeMap<SssDownloadProcessThread> sssRequestThreadMap = new StringToOtherTypeMap<>();

    StringToOtherTypeMap<VFtpCompatDownloadRequest> compatRequestMap = new StringToOtherTypeMap<>();
    StringToOtherTypeMap<CompatDownloadProcessThread> compatRequestThreadMap = new StringToOtherTypeMap<>();

    File workingDir;
    File downloadDir;

    CompatDownloadRequestRepository compatDownloadRequestRepository;
    SssDownloadRequestRepository sssDownloadRequestRepository;

    @PostConstruct
    private void postConstruct(){
        downloadDir = new File(vftpDownloadDirectory);
        downloadDir.mkdirs();
        File configDir = new File(configDirectory);
        workingDir = new File(configDir, "Working");
        workingDir.mkdirs();

        compatDownloadRequestRepository = new CompatDownloadRequestRepository(downloadDir);
        sssDownloadRequestRepository = new SssDownloadRequestRepository(downloadDir);
    }

    public synchronized void sssRequestCompleted(String requestNo){
        sssRequestThreadMap.remove(requestNo);
        VFtpSssDownloadRequest request = sssRequestMap.get(requestNo);
        sssDownloadRequestRepository.save(request);
        sssRequestMap.remove(requestNo);
    }

    public synchronized void compatRequestCompleted(String requestNo){
        compatRequestThreadMap.remove(requestNo);
        VFtpCompatDownloadRequest request = compatRequestMap.get(requestNo);
        compatDownloadRequestRepository.save(request);
        compatRequestMap.remove(requestNo);
    }

    synchronized Date generateRequestTime(){
        final Date currentTime = new Date();
        if (lastRequestNumber >= currentTime.getTime()){
            currentTime.setTime(lastRequestNumber + 1);
        }
        lastRequestNumber = currentTime.getTime();
        return currentTime;
    }


    public void stopAll(){
        System.out.println("VFtpDownloadService stopAll");
        sssDownloadRequestRepository.stop();
        compatDownloadRequestRepository.stop();
    }

    String generateSssRequestNoFromTime(Date requestTime, String machine){
        StringBuilder builder = new StringBuilder()
                .append("REQ_SSS_")
                .append(machine).append("_")
                .append(format.format(requestTime));
        return builder.toString();
    }

    String generateCompatRequestNoFromTime(Date requestTime, String machine){
        StringBuilder builder = new StringBuilder()
                .append("REQ_COMPAT_")
                .append(machine).append("_")
                .append(format.format(requestTime));
        return builder.toString();
    }

    void addRequest(VFtpSssDownloadRequest request){
        request.setTimestamp(System.currentTimeMillis());
        sssRequestMap.put(request.getRequestNo(), request);
    }

    public VFtpSssDownloadRequest addSssDownloadRequest(VFtpSssDownloadRequest request) throws FileServiceCollectException {
        FtpServerInfo ftpServerInfo = configurationService.getFtpServerInfo(request.getMachine());
        if (ftpServerInfo == null){
            throw new FileServiceCollectException(400, "Parameter(machine) is not valid.");
        }

        Date requestTime = generateRequestTime();
        String requestNo = generateSssRequestNoFromTime(requestTime, request.getMachine());
        request.setRequestNo(requestNo);
        addRequest(request);

        if (request.isArchive()){
            request.setArchiveFileName(request.getDirectory() + ".zip");
            request.setArchiveFilePath(request.getRequestNo() + "/" + request.getArchiveFileName());
        }

        SssDownloadProcessThread thread = new SssDownloadProcessThread(request, ftpServerInfo, workingDir, downloadDir, this);
        sssRequestThreadMap.put(request.getRequestNo(), thread);
        thread.start();

        return request;
    }

    VFtpSssDownloadRequest getSssDownloadRequest(String requestNo){
        VFtpSssDownloadRequest request = sssRequestMap.get(requestNo);
        if (request != null){
            return request;
        }
        request = sssDownloadRequestRepository.get(requestNo);
        return request;
    }

    public VFtpSssDownloadRequest getSssDownloadRequest(String machine, String requestNo){
        VFtpSssDownloadRequest request = getSssDownloadRequest(requestNo);
        if (request != null){
            if (request.getMachine().equals(machine)){
                return request;
            }
        }
        return null;
    }

    public void cancelAndDeleteSssDownloadRequest(String machine, String requestNo){
        SssDownloadProcessThread thread = sssRequestThreadMap.get(requestNo);
        if (thread != null){
            thread.stopExecute();
        }
    }

    void addRequest(VFtpCompatDownloadRequest request){
        request.setTimestamp(System.currentTimeMillis());
        compatRequestMap.put(request.getRequestNo(), request);
    }

    public VFtpCompatDownloadRequest addCompatDownloadRequest(VFtpCompatDownloadRequest request) throws FileServiceCollectException {
        FtpServerInfo ftpServerInfo = configurationService.getFtpServerInfo(request.getMachine());
        if (ftpServerInfo == null){
            throw new FileServiceCollectException(400, "Parameter(machine) is not valid.");
        }

        Date requestTime = generateRequestTime();
        String requestNo = generateCompatRequestNoFromTime(requestTime, request.getMachine());
        request.setRequestNo(requestNo);
        addRequest(request);

        if (request.isArchive()){
            request.setArchiveFileName(request.getFile().getName() + ".zip");
            request.setArchiveFilePath(request.getRequestNo() + "/" + request.getArchiveFileName());
        }

        CompatDownloadProcessThread thread = new CompatDownloadProcessThread(request, ftpServerInfo, workingDir, downloadDir, this);
        compatRequestThreadMap.put(request.getRequestNo(), thread);
        thread.start();

        return request;
    }

    VFtpCompatDownloadRequest getCompatDownloadRequest(String requestNo){
        VFtpCompatDownloadRequest request = compatRequestMap.get(requestNo);
        if (request != null){
            return request;
        }
        request = compatDownloadRequestRepository.get(requestNo);
        return request;
    }

    public VFtpCompatDownloadRequest getCompatDownloadRequest(String machine, String requestNo){
        VFtpCompatDownloadRequest request = getCompatDownloadRequest(requestNo);
        if (request != null){
            if (request.getMachine().equals(machine)){
                return request;
            }
        }
        return null;
    }

    public void cancelAndDeleteCompatDownloadRequest(String machine, String requestNo){
        CompatDownloadProcessThread thread = compatRequestThreadMap.get(requestNo);
        if (thread != null){
            thread.stopExecute();
        }
    }

    @PreDestroy
    public void preDestroy(){
        this.stopAll();
    }
}
