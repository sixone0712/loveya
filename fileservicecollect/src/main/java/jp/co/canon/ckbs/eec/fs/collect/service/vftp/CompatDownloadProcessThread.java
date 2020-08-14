package jp.co.canon.ckbs.eec.fs.collect.service.vftp;

import jp.co.canon.ckbs.eec.fs.collect.executor.CustomExecutor;
import jp.co.canon.ckbs.eec.fs.collect.executor.CustomOutputStreamLineHandler;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpCompatDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.service.configuration.FtpServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class CompatDownloadProcessThread extends Thread implements CustomOutputStreamLineHandler {
    VFtpCompatDownloadRequest request;
    FtpServerInfo ftpServerInfo;
    File workingDir;
    File downloadRoot;
    VFtpDownloadService downloadService;
    CustomExecutor executor = new CustomExecutor();

    public CompatDownloadProcessThread(VFtpCompatDownloadRequest request, FtpServerInfo ftpServerInfo, File workingDir, File downloadRoot, VFtpDownloadService downloadService){
        this.request = request;
        this.ftpServerInfo = ftpServerInfo;
        this.workingDir = workingDir;
        this.downloadRoot = downloadRoot;
        this.downloadService = downloadService;
    }

    File createFileNameList() throws Exception {
        File file = null;
        BufferedWriter out = null;
        try {
            file = File.createTempFile("FILE", ".LST", workingDir);
            if (log.isTraceEnabled()){
                log.trace("create filename list file({}) for {}", file.getPath(), request.getRequestNo());
            }
            out = new BufferedWriter(new FileWriter(file));
            out.write(request.getFile().getName());
            out.newLine();
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


    CommandLine createCommand(File fileList){
        File requestDownloadDir = new File(downloadRoot, request.getRequestNo());

        CommandLine cmdLine = new CommandLine("java");
        cmdLine.addArgument("-cp")
                .addArgument("/usr/local/canon/esp/CanonFileService/Libs/ELogCollector.jar")
                .addArgument("jp.co.canon.ckbs.eec.service.FtpCommand")
                .addArgument("get")
                .addArgument("-host").addArgument(ftpServerInfo.getHost())
                .addArgument("-port").addArgument("22001")
                .addArgument("-md").addArgument(ftpServerInfo.getFtpmode())
                .addArgument("-u").addArgument(ftpServerInfo.getUser()+"/"+ftpServerInfo.getPassword())
                .addArgument("-dir").addArgument("/VROOT/COMPAT/Optional")
                .addArgument("-dest").addArgument(requestDownloadDir.getAbsolutePath())
                .addArgument("-fl").addArgument(fileList.getAbsolutePath());
        if (request.isArchive()){
            cmdLine.addArgument("-az");
            cmdLine.addArgument(request.getFile().getName() + ".zip");
        }
        return cmdLine;
    }

    public void stopExecute(){
        log.trace("stop requested. {}", request.getRequestNo());
        request.setStatus(VFtpCompatDownloadRequest.Status.CANCEL);
        executor.stop();
    }

    @Override
    public void run() {
        request.setStatus(VFtpCompatDownloadRequest.Status.EXECUTING);
        File fileNameListFile = null;
        try {
            fileNameListFile = createFileNameList();
            CommandLine cmdLine = createCommand(fileNameListFile);

            executor.execute(cmdLine, this);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileNameListFile != null){
                if (log.isTraceEnabled()) {
                    log.trace("delete filename list file({}) for {}", fileNameListFile.getPath(), request.getRequestNo());
                }
                fileNameListFile.delete();
            }
            request.setCompletedTime(System.currentTimeMillis());
            request.setStatus(VFtpCompatDownloadRequest.Status.EXECUTED);
            downloadService.compatRequestCompleted(request.getRequestNo());
        }
    }

    @Override
    public boolean processOutputLine(String line) {
        if(line.startsWith("DOWNLOAD_COMPLETE:")){
            line = line.substring(18);
            String[] strArr = line.split(";");
            String filename = strArr[0];
            long size = Long.parseLong(strArr[1]);
            request.downloaded(filename, size);
            return true;
        }
        if (line.startsWith("ERR:")){
            request.setStatus(VFtpCompatDownloadRequest.Status.ERROR);
            return false;
        }
        return true;
    }

    @Override
    public boolean processErrorLine(String line) {
        return true;
    }
}
