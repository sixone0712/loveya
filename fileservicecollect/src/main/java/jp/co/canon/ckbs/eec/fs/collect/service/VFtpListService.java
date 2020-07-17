package jp.co.canon.ckbs.eec.fs.collect.service;

import jp.co.canon.ckbs.eec.fs.collect.model.VFtpListRequest;
import org.apache.commons.exec.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

@Component
public class VFtpListService {
    @Value("${fileservice.configDirectory}")
    String configDirectory;

    File workingDir = null;

    long lastRequestNumber = 0;
    DateFormat format = new SimpleDateFormat("yyMMddHHmmssSSS");

    Map<String, VFtpListRequest> requestMap = new HashMap<>();


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

    void addRequest(VFtpListRequest request){
        requestMap.put(request.getRequestNo(), request);
    }
/*
    StringBuilder createListCommand(String machine, String directory){
        StringBuilder command = new StringBuilder();

        command.append(" list ");

        command.append(" -host ").append("10.1.31.243").append(" ");
        command.append(" -port ").append("22001").append(" ");
        command.append(" -root ").append("/VROOT/SSS/Optional").append(" ");
        command.append(" -dest ").append(directory).append(" ");
        command.append(" -u ")
                .append("ckbs").append("/")
                .append("ckbs").append(" ");

        command.append(" -md ")
                .append("passive").append(" ");

        return command;
    }

 */

/*
    public void getServerFileList(String machine, String directory){
        StringBuilder command = createListCommand(machine, directory);

        CommandExecutor proc = new CommandExecutor(getWorkingDir());
        String result = null;
        try {
            result = proc.execute(command.toString());
        } catch (Exception ex){

        }

        if (result != null){
            StringTokenizer st = new StringTokenizer(result, System.getProperty("line.separator"));
            for(; st.hasMoreTokens();){
                String line = st.nextToken();
            }
        }
    }

 */

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

    public VFtpListRequest addListRequest(VFtpListRequest request){
        Date requestTime = generateRequestTime();

        String requestNo = generateRequestNoFromTime(requestTime, request.getMachine(), request.getDirectory());
        request.setRequestNo(requestNo);
        addRequest(request);

        /*
        ListRequestThread thread = new ListRequestThread(request, getWorkingDir());
        thread.start();
         */

        return request;
    }

    static class ListRequestThread extends Thread implements ExecuteStreamHandler{
        VFtpListRequest request;
        boolean stop = false;
        File workDir;
        ExecuteWatchdog watchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);

        public ListRequestThread(VFtpListRequest request, File workDir){
            this.request = request;
            this.workDir = workDir;
        }

        String createCommandString(){
            StringBuilder builder = new StringBuilder();
            builder.append("java -cp /usr/local/canon/esp/CanonFileService/Libs/ELogCollector.jar jp.co.canon.ckbs.eec.service.FtpCommand ")
                    .append("list ")
                    .append("-host ").append("10.1.31.242").append(" ")
                    .append("-port ").append("21").append(" ")
                    .append("-md ").append("passive").append(" ")
                    .append("-u ").append("ckbs/ckbs").append(" ")
                    .append("-root ").append("/VROOT/SSS/Optional").append(" ")
                    .append("-dest ").append(request.getDirectory());
            return builder.toString();
        }

        CommandLine createCommand(){
            CommandLine cmdLine = new CommandLine("java");
            cmdLine.addArgument("-cp")
                    .addArgument("/usr/local/canon/esp/CanonFileService/Libs/ELogCollector.jar")
                    .addArgument("jp.co.canon.ckbs.eec.service.FtpCommand")
                    .addArgument("list")
                    .addArgument("-host").addArgument("10.1.31.242")
                    .addArgument("-port").addArgument("21")
                    .addArgument("-md").addArgument("passive")
                    .addArgument("-u").addArgument("ckbs/ckbs")
                    .addArgument("-root").addArgument("/VROOT/SSS/Optional")
                    .addArgument("-dest").addArgument(request.getDirectory());
            return cmdLine;
        }

        @Override
        public void run() {
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            DefaultExecutor executor = new DefaultExecutor();
            executor.setWatchdog(watchdog);
            executor.setStreamHandler(this);
            CommandLine cmdLine = createCommand();
            try {
                executor.execute(cmdLine, resultHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                resultHandler.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void setProcessInputStream(OutputStream os) throws IOException {

        }

        @Override
        public void setProcessErrorStream(InputStream is) throws IOException {

        }

        @Override
        public void setProcessOutputStream(InputStream is) throws IOException {

        }
    }
}
