package jp.co.canon.ckbs.eec.service;

import org.apache.commons.cli.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GetFtpCommand extends BaseFtpCommand {
    private static Logger logger = Logger.getLogger(GetFtpCommand.class.getName());
    String rootDir = null;
    String directory = null;
    String downloadDirectory = null;
    String [] files;
    boolean zip = false;
    String zipFileName;

    String commandInfoString;

    FileInfoQueue fileQueue = new FileInfoQueue();
    StatusReporter statusReporter = new StatusReporter();

    String createCommandInfoString(){
        return String.format("(%s, %s, %d, %s, %s, %s, %s)", "get", this.host, this.port, this.ftpmode, this.rootDir, this.directory, this.downloadDirectory);
    }

    void loadFileList(String fileListFile) throws IOException{
        try {
            List<String> fileList = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileListFile))));
            String line;
            while( (line = reader.readLine()) != null){
                if (line.length() == 0){
                    continue;
                }
                String[] lineArr = line.split(",");
                if (lineArr.length == 0){
                    continue;
                }
                fileList.add(lineArr[0]);
                fileQueue.push(new FileInfo(lineArr[0]));
            }
            files = fileList.toArray(new String[0]);
        } catch (FileNotFoundException e) {
            throw new IOException(e);
        }
    }

    void appendZipFile(ZipOutputStream out, File f) throws IOException {
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            byte[] buffer = new byte[16384];
            while (true) {
                int size = in.read(buffer);
                if (size <= 0) break;
                out.write(buffer, 0, size);
            }
        } finally {
            if (in != null) in.close();
        }
    }

    boolean zipFiles(){
        File zipFile = new File(downloadDirectory, zipFileName);

        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(zipFile));
            for (String filename : files) {
                File f = new File(downloadDirectory, filename);
                ZipEntry temp = new ZipEntry(f.getName());
                temp.setTime(f.lastModified());
                out.putNextEntry(temp);
                appendZipFile(out, f);
                out.closeEntry();
            }
            out.close();
            out = null;
            for (String filename : files){
                File f = new File(downloadDirectory, filename);
                f.delete();
            }
            logger.info("STATUS: compression finished" + commandInfoString);
            return true;
        } catch (IOException e){
            statusReporter.reportError("IOException on zipFiles" + commandInfoString);
            logger.severe("ERR: IOException on zipFiles" + commandInfoString);
            return false;
        } finally {
            if (out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void doCommand(){
        int total_thread_count = fileQueue.size() < 3 ? fileQueue.size() : 3;
        ArrayList<GetFtpCommandExecuteThread> threadArrayList = new ArrayList<>();

        FtpServerInfo serverInfo = new FtpServerInfo();
        serverInfo.host = this.host;
        serverInfo.port = this.port;
        serverInfo.ftpmode = this.ftpmode;
        serverInfo.user = this.user;
        serverInfo.password = this.password;

        File downloadDirectoryFile = new File(downloadDirectory);
        if (!downloadDirectoryFile.exists()){
            downloadDirectoryFile.mkdirs();
        }

        for(int idx = 0; idx < total_thread_count; ++idx){
            GetFtpCommandExecuteThread getThread = new GetFtpCommandExecuteThread(serverInfo, rootDir, directory, downloadDirectory, fileQueue, statusReporter);
            threadArrayList.add(getThread);
            getThread.start();
        }

        for(GetFtpCommandExecuteThread th : threadArrayList){
            try {
                th.join();
            } catch (InterruptedException e){
            }
        }

        long downloadedCount = 0;
        boolean exitWithError = false;

        for(GetFtpCommandExecuteThread th : threadArrayList){
            downloadedCount += th.getDownloadedCount();
            if (th.isExitWithError()){
                exitWithError = true;
            }
        }

        if (exitWithError){
            statusReporter.reportError("at least 1 thread is exit with Error");
            return;
        }
        boolean result = true;
        if (zip){
            result = zipFiles();
        }

        if (result){
            statusReporter.println("END DOWNLOAD TOTAL:" + downloadedCount);
            statusReporter.reportStatus("END DOWNLOAD SUCCESSFULLY");
            logger.info("END DOWNLOAD(SUCCESSFUL) TOTAL:" + downloadedCount + commandInfoString);
            return;
        }
        statusReporter.reportStatus("END DOWNLOAD FAILED");
        logger.info("END DOWNLOAD(FAILED) TOTAL:" + downloadedCount + commandInfoString);
    }

    @Override
    public void execute(String[] args) {
        Options options = new Options();

        options.addRequiredOption("host", "host", true, "ftp host");
        options.addRequiredOption("port", "port", true, "ftp port");
        options.addRequiredOption("md", "md", true, "ftp mode");
        options.addRequiredOption("u", "user", true, "user id and password( -u user/password)");
        options.addRequiredOption("root", "root", true, "ftp root directory");
        options.addOption("dir", "dir", true, "ftp dest directory");
        options.addRequiredOption("dest", "dest", true, "save destination directory");
        options.addRequiredOption("fl", "fl", true, "download file list file");
        options.addOption("az", "az", true, "zip after download");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine commandLine = parser.parse(options, args, true);
            String userStr = commandLine.getOptionValue("u");
            this.host = commandLine.getOptionValue("host");
            String portStr = commandLine.getOptionValue("port");
            this.port = Integer.parseInt(portStr);
            this.ftpmode = commandLine.getOptionValue("md");
            String[] userStrArr = userStr.split("/");
            String user = userStrArr[0];
            String password = userStrArr[1];
            this.user = user;
            this.password = password;
            this.rootDir = commandLine.getOptionValue("root");
            if (commandLine.hasOption("dir")) {
                this.directory = commandLine.getOptionValue("dir");
            }
            this.downloadDirectory = commandLine.getOptionValue("dest");

            String fileListFile = commandLine.getOptionValue("fl");
            loadFileList(fileListFile);

            zip = commandLine.hasOption("az");
            zipFileName = commandLine.getOptionValue("az");

            commandInfoString = createCommandInfoString();

            doCommand();
        } catch (ParseException e) {
            statusReporter.reportError("Command Line Parse Exception");
            logger.severe("ERR:Command Line Parse Exception");
            e.printStackTrace();
        } catch (IOException e) {
            statusReporter.reportError("IOException");
            logger.severe("ERR:IOException");
            e.printStackTrace();
        }

    }
}
