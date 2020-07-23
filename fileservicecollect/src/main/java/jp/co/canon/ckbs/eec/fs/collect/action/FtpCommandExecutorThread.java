package jp.co.canon.ckbs.eec.fs.collect.action;

import jp.co.canon.ckbs.eec.fs.collect.model.LogCommandDefinitionModel;
import jp.co.canon.ckbs.eec.fs.collect.model.RequestFileInfo;
import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class FtpCommandExecutorThread implements Runnable{
    BlockingQueue<FtpDownloadRequest> mainQueue;
    boolean stop = false;

    CommandRepository commandRepository;
    FtpDownloadFileRepository downloadFileRepository;
    File workingDir;
    int adminPort;
    int niceVal;

    @Getter @Setter
    FtpDownloadRequest currentRequest;

    public FtpCommandExecutorThread(BlockingQueue<FtpDownloadRequest> queue, CommandRepository commandRepository,FtpDownloadFileRepository downloadFileRepository, File workingDir, int adminPort, int niceVal){
        this.mainQueue = queue;
        this.commandRepository = commandRepository;
        this.downloadFileRepository = downloadFileRepository;
        this.workingDir = workingDir;
        this.adminPort = adminPort;
        this.niceVal = niceVal;
    }

    public final void stop(){
        stop = true;
    }

    FtpDownloadRequest dequeue() throws InterruptedException {
        return mainQueue.poll(100, TimeUnit.MILLISECONDS);
    }

    LogCommandDefinitionModel getLogCommand(String machine, String category) throws ConfigurationException {
        return commandRepository.getCommandDefinition(machine, category);
    }

    File createFileNameList(FtpDownloadRequest request) throws Exception {
        File file = null;
        BufferedWriter out = null;
        try {
            file = File.createTempFile("FILE", ".LST", workingDir);
            out = new BufferedWriter(new FileWriter(file));
            for (RequestFileInfo fileInfo : request.getFileInfos()){
                out.write(fileInfo.getName());
                out.newLine();
            }
            return file;
        } catch (IOException e) {
            throw new Exception("Failed in making file(" + file.getPath() + ").", e);
        } finally {
            if (out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    throw new Exception("Failed in the close processing of file(" + file.getPath() + ").", e);
                }
            }
        }
    }

    String createCommandString(File fileNameFile, FtpDownloadRequest request) throws ConfigurationException{
        LogCommandDefinitionModel logCommand = getLogCommand(request.getMachine(), request.getCategory());
        StringBuffer buff = new StringBuffer();
        buff.append(logCommand.getCommand())
                .append(" get ")
                .append(" -fl ").append("\"").append(fileNameFile.getAbsolutePath()).append("\"")
                .append(" -r ").append(request.getRequestNo())
                .append(" -url ").append(logCommand.getLogUrl().getUrlString())
                .append(" -u ").append(logCommand.getLogUrl().getUserId())
                .append("/").append(logCommand.getLogUrl().getPassword())
                .append(" -md ").append(logCommand.getLogUrl().getFtpmode())
                .append(" -ap ").append(adminPort)
                .append(" -tid ").append(request.getMachine());
        if (request.isArchive()){
            buff.append(" -aZ ");
        }
        return buff.toString();
    }

    void callCollectCommand(FtpDownloadRequest request) throws ConfigurationException, Exception {
        File file = createFileNameList(request);
        String command = createCommandString(file, request);

        CommandExecutor proc = new CommandExecutor(workingDir);
        getCurrentRequest().setResult(proc.execute(command, niceVal));
        if (file.exists()){
            file.delete();
        }
    }

    void executeRequest(FtpDownloadRequest request){
        try {
            if (request == null) {
                return;
            }
            if (request.getStatus() == FtpDownloadRequest.Status.CANCEL) {
                return;
            }
            request.setStatus(FtpDownloadRequest.Status.EXECUTING);
            setCurrentRequest(request);
            downloadFileRepository.writeRequest(request);

            callCollectCommand(request);

            downloadFileRepository.readDirectory(request);

            request.setCompletedTime(System.currentTimeMillis());
            if (request.getStatus() == FtpDownloadRequest.Status.EXECUTING) {
                request.setStatus(FtpDownloadRequest.Status.EXECUTED);
            }
            downloadFileRepository.writeRequest(request);
            setCurrentRequest(null);
        } catch (Exception e){

        }
    }

    void executeOneRequest(){
        try {
            FtpDownloadRequest request = dequeue();
            executeRequest(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean stopRequestCommand(String requestNo){
        FtpDownloadRequest request = getCurrentRequest();
        if (!request.getRequestNo().equals(requestNo)){
            return false;
        }
        LogCommandDefinitionModel logCommand;
        try {
            logCommand = getLogCommand(request.getMachine(), request.getCategory());
        } catch (Exception e){
            return false;
        }

        final StringBuilder buff = new StringBuilder();

        buff.append(logCommand.getCommand())
                .append(" stop")												// コマンド名
                .append(" -r ").append(request.getRequestNo())						// 要求番号
                .append(" -ap ").append(adminPort);	 					// AdimPort指定

        if (request.getStatus() == FtpDownloadRequest.Status.EXECUTING){
            request.setStatus(FtpDownloadRequest.Status.CANCEL);
            CommandExecutor proc = new CommandExecutor(workingDir);
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        while(!stop){
            executeOneRequest();
        }
    }
}
