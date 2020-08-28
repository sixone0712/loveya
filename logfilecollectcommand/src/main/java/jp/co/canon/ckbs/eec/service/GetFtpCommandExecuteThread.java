package jp.co.canon.ckbs.eec.service;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.util.logging.Logger;

public class GetFtpCommandExecuteThread extends Thread{
    private static Logger logger = Logger.getLogger(GetFtpCommandExecuteThread.class.getName());
    FtpServerInfo serverInfo;

    String rootDir;
    String directory;
    String downloadDirectory;

    FileInfoQueue fileQueue;

    String commandInfoString;

    StatusReporter statusReporter;

    int downloadedCount = 0;

    boolean exitWithError = false;

    public GetFtpCommandExecuteThread(FtpServerInfo serverInfo, String rootDir, String directory, String downloadDirectory, FileInfoQueue fileQueue, StatusReporter reporter){
        this.serverInfo = serverInfo;
        this.rootDir = rootDir;
        this.directory = directory;
        this.downloadDirectory = downloadDirectory;
        this.fileQueue = fileQueue;
        this.statusReporter = reporter;

        commandInfoString = createCommandInfoString();
    }

    public int getDownloadedCount(){
        return downloadedCount;
    }

    public boolean isExitWithError(){
        return exitWithError;
    }

    String createCommandInfoString(){
        return String.format("(%s, %s, %d, %s, %s, %s, %s)", "get", serverInfo.host, serverInfo.port, serverInfo.ftpmode, this.rootDir, this.directory, this.downloadDirectory);
    }


    void applyFtpMode(FTPClient ftpClient){
        if (!serverInfo.ftpmode.equalsIgnoreCase("active")){
            ftpClient.enterLocalPassiveMode();
        }
    }

    FTPClient connectServer(){
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(serverInfo.host, serverInfo.port);
            if (!ftpClient.isConnected()){
                return null;
            }
            boolean logined = ftpClient.login(serverInfo.user, serverInfo.password);
            if (!logined){
                ftpClient.disconnect();
                return null;
            }
        } catch (IOException e) {
            try {
                ftpClient.disconnect();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return null;
        }
        applyFtpMode(ftpClient);
        return ftpClient;
    }

    boolean changeDirectory(FTPClient ftpClient){
        boolean moved = false;
        statusReporter.reportStatus("TRY_CHANGE_DIRECTORY( " + rootDir + " )");
        try {
            moved = ftpClient.changeWorkingDirectory(rootDir);
        } catch (IOException e) {
            return false;
        }
        if (!moved){
            return false;
        }
        if (directory == null){
            return true;
        }
        statusReporter.reportStatus("TRY_CHANGE_DIRECTORY( " + directory + " )");
        try {
            moved = ftpClient.changeWorkingDirectory(directory);
        } catch (IOException e) {
            return false;
        }
        return moved;
    }

    FileDownloadResult inputStreamToFile(InputStream inputStream, String fileName){
        File downloadDirectoryFile = new File(downloadDirectory);
        OutputStream outputStream = null;
        File outputFile = new File(downloadDirectoryFile, fileName);
        try {
            outputStream = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            statusReporter.reportError("FileNotFoundException...");
            logger.severe("ERR: inputStreamToFile FileNotFoundException...("+fileName+")" + commandInfoString);
            return FileDownloadResult.RESULT_FAIL;
        }
        byte[] buffer = new byte[8192];
        if (inputStream == null){
            logger.severe("ERR: inputStream == null");
        }

        long total_readed = 0;
        int readed;
        try {
            statusReporter.println("DOWNLOADING:"+fileName+";" + total_readed);
            while ((readed = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readed);
                total_readed += readed;
            }
            statusReporter.println("DOWNLOAD_COMPLETE:"+fileName+";"+total_readed);
            logger.info("STATUS: download complete ("+fileName + ","+ total_readed +")"+commandInfoString);
            return FileDownloadResult.RESULT_COMPLETED;
        } catch (IOException e) {
            logger.severe("WARN: inputStreamToFile IOException (" +fileName+")"+ commandInfoString + " " + e.getMessage());
            logger.severe("WARN: download failed ("+fileName + ","+ total_readed +")"+commandInfoString);
            return FileDownloadResult.RESULT_RETRY;
        } finally{
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    FileDownloadResult downloadFile(FTPClient ftpClient, String fileName){
        applyFtpMode(ftpClient);
        try {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
        } catch (IOException e) {
            logger.severe("ERR: setFileType, setFileTransferMode Failed (" +fileName+")"+ commandInfoString + " " + e.getMessage());
            return FileDownloadResult.RESULT_FAIL;
        }
        InputStream inputStream = null;
        try {
            inputStream = ftpClient.retrieveFileStream(fileName);
        } catch (IOException e) {
            logger.severe("ERR: retrieveFileStream Failed (" +fileName+")"+ commandInfoString + " " + e.getMessage());
            return FileDownloadResult.RESULT_RETRY;
        }
        if (inputStream == null){
            logger.severe("ERR:downloadFile inputStream == null("+fileName+")("+ftpClient.getReplyCode()+")"+commandInfoString);
            return FileDownloadResult.RESULT_RETRY;
        }

        FileDownloadResult result;
        result = inputStreamToFile(inputStream, fileName);
        boolean completePendingCommand = false;
        try {
            logger.severe("Start Complete Pending Command");
            completePendingCommand = ftpClient.completePendingCommand();
        } catch(IOException e){
            logger.severe("IOException Compelete Pending Command");
        }
        logger.severe("End Complete Pending Command");
        if (!completePendingCommand){
            logger.severe("Complete Pending Command failed");
        }

        return result;
    }

    @Override
    public void run() {
        FTPClient ftpClient = null;
        FileInfo fileInfo = fileQueue.poll();
        FileDownloadResult result;
        while(fileInfo != null){
            if (ftpClient == null){
                ftpClient = connectServer();
                if (ftpClient == null){
                    this.exitWithError = true;
                    statusReporter.reportError("Cannot Connect to ftp server.");
                    logger.severe("Cannot Connect to ftp server.");
                    break;
                }
            }
            boolean moved = changeDirectory(ftpClient);
            if (!moved){
                this.exitWithError = true;
                statusReporter.reportError("Change Directory Failed");
                logger.severe("ERR: Change Directory Failed("+ftpClient.getReplyCode()+")"+commandInfoString);
                break;
            }
            result = downloadFile(ftpClient, fileInfo.getFilename());
            if (result == FileDownloadResult.RESULT_RETRY){
                try {
                    logger.severe("Disconnect ("+fileInfo.getFilename()+")");
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ftpClient = null;
                if (fileInfo.getRetryCount() < 3){
                    fileInfo.increaseRetryCount();
                    fileQueue.push(fileInfo);
                    fileInfo = fileQueue.poll();
                    continue;
                }
                result = FileDownloadResult.RESULT_FAIL;
                statusReporter.reportError("DOWNLOAD RETRY OVER...");
                logger.severe("DOWNLOAD RETRY OVER("+fileInfo.getFilename()+")"+commandInfoString);
            }
            if (result == FileDownloadResult.RESULT_FAIL){
                this.exitWithError = true;
                statusReporter.reportError("DOWNLOAD FAILED.");
                logger.severe("DOWNLOAD FAILED.("+fileInfo.getFilename()+")"+commandInfoString);
                break;
            }
            downloadedCount++;
            fileInfo = fileQueue.poll();
        }
        if (ftpClient != null) {
            try {
                logger.severe("Disconnect FINAL.");
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
