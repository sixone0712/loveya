package jp.co.canon.ckbs.eec.fs.collect.service.vftp;

import jp.co.canon.ckbs.eec.fs.collect.executor.CustomExecutor;
import jp.co.canon.ckbs.eec.fs.collect.executor.CustomOutputStreamLineHandler;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpSssListRequest;
import jp.co.canon.ckbs.eec.fs.collect.service.VFtpFileInfo;
import jp.co.canon.ckbs.eec.fs.collect.service.configuration.FtpServerInfo;
import org.apache.commons.exec.CommandLine;

import java.util.ArrayList;
import java.util.List;

class SssListProcessThread extends Thread implements CustomOutputStreamLineHandler {
    VFtpSssListRequest request;
    FtpServerInfo ftpServerInfo;
    List<VFtpFileInfo> fileInfoList = new ArrayList<>();
    CustomExecutor executor = new CustomExecutor();
    VFtpListService listService;

    public SssListProcessThread(VFtpSssListRequest request, FtpServerInfo ftpServerInfo, VFtpListService listService){
        this.request = request;
        this.ftpServerInfo = ftpServerInfo;
        this.listService = listService;
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
        request.setStatus(VFtpSssListRequest.Status.EXECUTING);

        executor.execute(cmdLine, this);
        request.setFileList(fileInfoList.toArray(new VFtpFileInfo[0]));

        request.setStatus(VFtpSssListRequest.Status.EXECUTED);
        request.setCompletedTime(System.currentTimeMillis());
        listService.requestCompleted(request.getRequestNo());
    }

    public void stopExecute(){
        request.setStatus(VFtpSssListRequest.Status.CANCEL);
        executor.stop();
    }

    @Override
    public boolean processOutputLine(String line) {
        if (line.startsWith("FILE:")){
            String[] strArr = line.substring(5).split(";");
            VFtpFileInfo info = new VFtpFileInfo();
            info.setFileName(strArr[0]);
            info.setFileSize(Integer.parseInt(strArr[1]));
            info.setFileType("F");
            fileInfoList.add(info);
            return true;
        }
        if (line.startsWith("DIRECTORY:")){
            VFtpFileInfo info = new VFtpFileInfo();
            info.setFileName(line.substring(10));
            info.setFileType("D");
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
        return true;
    }
}
