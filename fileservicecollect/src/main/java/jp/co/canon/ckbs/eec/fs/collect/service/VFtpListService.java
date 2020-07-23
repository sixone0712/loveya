package jp.co.canon.ckbs.eec.fs.collect.service;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssListRequest;
import jp.co.canon.ckbs.eec.fs.collect.service.configuration.ConfigurationService;
import jp.co.canon.ckbs.eec.fs.collect.service.configuration.FtpServerInfo;
import org.apache.commons.exec.*;
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
    Map<String, ListRequestThread> listRequestThreadMap = new HashMap<>();

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

        ListRequestThread thread = new ListRequestThread(request, ftpServerInfo, getWorkingDir());
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
        ListRequestThread listRequestThread = listRequestThreadMap.get(requestNo);
        if (listRequestThread != null){
            listRequestThread.stopExecute();
            listRequestThreadMap.remove(requestNo);
        }
    }

    class ListRequestThread extends Thread implements CustomOutputStreamLineHandler{
        VFtpSssListRequest request;
        FtpServerInfo ftpServerInfo;
        File workDir;
        List<VFtpFileInfo> fileInfoList = new ArrayList<>();
        CustomExecutor executor = new CustomExecutor();

        public ListRequestThread(VFtpSssListRequest request, FtpServerInfo ftpServerInfo, File workDir){
            this.request = request;
            this.ftpServerInfo = ftpServerInfo;
            this.workDir = workDir;
        }

        CommandLine createCommand(){
            CommandLine cmdLine = new CommandLine("java");
            cmdLine.addArgument("-cp")
                    .addArgument("/usr/local/canon/esp/CanonFileService/Libs/ELogCollector.jar")
                    .addArgument("jp.co.canon.ckbs.eec.service.FtpCommand")
                    .addArgument("list")
                    .addArgument("-host").addArgument(ftpServerInfo.getHost())
                    .addArgument("-port").addArgument("22001")
                    .addArgument("-md").addArgument(ftpServerInfo.getFtpmode())
                    .addArgument("-u").addArgument(ftpServerInfo.getUser()+"/"+ftpServerInfo.getPassword())
                    .addArgument("-root").addArgument("/VROOT/SSS/Optional")
                    .addArgument("-dest").addArgument(request.getDirectory());
            return cmdLine;
        }

        @Override
        public void run() {
            CommandLine cmdLine = createCommand();
            String cmdLineString = cmdLine.toString();
            request.setStatus(VFtpSssListRequest.Status.EXECUTING);

            executor.execute(cmdLine, this);
            request.setFileList(fileInfoList.toArray(new VFtpFileInfo[0]));

            request.setStatus(VFtpSssListRequest.Status.EXECUTED);
            request.setCompletedTime(System.currentTimeMillis());
        }

        public void stopExecute(){
            request.setStatus(VFtpSssListRequest.Status.CANCEL);
            executor.stop();
        }

        @Override
        public boolean processOutputLine(String line) {
//            System.out.println("OUTPUT:" + line);
            if (line.startsWith("FILE:")){
                String[] strArr = line.substring(5).split(";");
                VFtpFileInfo info = new VFtpFileInfo();
                info.setFilename(strArr[0]);
                info.setSize(Integer.parseInt(strArr[1]));
                info.setType("F");
                fileInfoList.add(info);
                return true;
            }
            if (line.startsWith("DIRECTORY:")){
                VFtpFileInfo info = new VFtpFileInfo();
                info.setFilename(line.substring(10));
                info.setType("D");
                fileInfoList.add(info);
                return true;
            }
            if (line.startsWith("ERR:")){
                request.setStatus(VFtpSssListRequest.Status.ERROR);
                return false;
            }
            return true;
        }

        @Override
        public boolean processErrorLine(String line) {
            System.out.println("ERROR:" + line);
            return true;
        }
    }
}
