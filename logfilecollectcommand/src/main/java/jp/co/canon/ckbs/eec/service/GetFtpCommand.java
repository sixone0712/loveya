package jp.co.canon.ckbs.eec.service;

import org.apache.commons.cli.*;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GetFtpCommand extends BaseFtpCommand {
    private static Logger logger = Logger.getLogger(GetFtpCommand.class.getName());
    String directory;
    String downloadDirectory;
    String [] files;
    boolean zip = false;
    String zipFileName;

    String commandInfoString;

    String createCommandInfoString(){
        return String.format("(%s, %s, %d, %s, %s, %s)", "list", this.host, this.port, this.ftpmode, this.directory, this.downloadDirectory);
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
            }
            files = fileList.toArray(new String[0]);
        } catch (FileNotFoundException e) {
            throw new IOException(e);
        }
    }

    void inputStreamToFile(InputStream inputStream, String fileName){
        File downloadDirectoryFile = new File(downloadDirectory);
        OutputStream outputStream = null;
        File outputFile = new File(downloadDirectoryFile, fileName);
        try {
            outputStream = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            System.out.println("ERR: FileNotFoundException...");
            logger.severe("ERR: inputStreamToFile FileNotFoundException...("+fileName+")" + commandInfoString);
            e.printStackTrace();
        }
        if (outputStream == null){
            System.out.println("ERR:outputStream == null");
            logger.severe("ERR:inputStreamToFile outputStream == null("+fileName+")" + commandInfoString);
            return;
        }
        byte[] buffer = new byte[8192];

        long total_readed = 0;
        int readed;
        try {
            System.out.println("DOWNLOADING:"+fileName+";" + total_readed);
            while ((readed = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readed);
                total_readed += readed;
            }
            System.out.println("DOWNLOAD_COMPLETE:"+fileName+";"+total_readed);
            logger.fine("STATUS: download complete ("+fileName + ","+ total_readed +")");
        } catch (IOException e) {
            logger.severe("ERR: inputStreamToFile IOException (" +fileName+")"+ commandInfoString + " " + e.getMessage());
            logger.severe("ERR: download failed ("+fileName + ","+ total_readed +")");
            System.out.println("ERR: inputStreamToFile IOException:"+e.getMessage());
        } finally{
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    boolean downloadFile(FTPClient ftpClient, String fileName){
        System.out.println("STATUS:TRY_DOWNLOAD( "+fileName+" )");
        InputStream inputStream = null;
        try {
            if (!ftpmode.equalsIgnoreCase("active")){
                ftpClient.enterLocalPassiveMode();
            }
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
            inputStream = ftpClient.retrieveFileStream(fileName);
        } catch (IOException e) {
            logger.severe("ERR:downloadFile IOException on download file("+fileName+")" + commandInfoString);
            logger.severe("ERR:" + e.getMessage());
            e.printStackTrace();
        }
        if (inputStream == null){
            logger.severe("ERR:downloadFile inputStream == null "+ftpClient.getReplyCode()+commandInfoString);
            System.out.println("ERR:inputStream == null "+ftpClient.getReplyCode());
            return false;
        }

        try {
            inputStreamToFile(inputStream, fileName);
            return true;
        } catch (Exception e){
            logger.severe("ERR:downloadFile IOException on download file("+fileName+").." + commandInfoString);
            logger.severe("ERR:downloadFile " + e.getMessage());
            System.out.println("ERR:Exception");
            return false;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ftpClient.completePendingCommand();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    long downloadFiles(FTPClient ftpClient){
        long downloaded_count = 0;
        File downloadDirectoryFile = new File(downloadDirectory);
        if (!downloadDirectoryFile.exists()){
            downloadDirectoryFile.mkdirs();
        }
        for(String fileName : files){
            if (downloadFile(ftpClient, fileName)){
                ++downloaded_count;
            }
        }
        return downloaded_count;
    }

    void appendZipFile(ZipOutputStream out, File f) throws IOException {
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            byte[] buffer = new byte[1024];
            while (true) {
                int size = in.read(buffer);
                if (size <= 0) break;
                out.write(buffer, 0, size);
            }
        } finally {
            if (in != null) in.close();
        }
    }

    void zipFiles(){
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
        } catch (IOException e){
            logger.severe("ERR: IOException on zipFiles" + commandInfoString);
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
        FTPClient ftpClient = new FTPClient();
        try {
            System.out.println("STATUS:TRY_CONNECT");
            ftpClient.connect(host, port);
            System.out.println("STATUS:TRY_LOGIN");
            boolean logined = ftpClient.login(user, password);
            if (!logined){
                System.out.println("ERR: LOGIN FAILED");
                return;
            }
            logger.info("STATUS:LOGIN_OK " + commandInfoString);
            if (!ftpmode.equalsIgnoreCase("active")){
                ftpClient.enterLocalPassiveMode();
                logger.info("STATUS:ENTER PASSIVE "+commandInfoString);
            }

            boolean moved = true;
            System.out.println("STATUS:TRY_CHANGE_DIRECTORY( " + directory + " )");
            moved = ftpClient.changeWorkingDirectory(directory);
            if (!moved){
                System.out.println("ERR: DIRECTORY MOVE FAILED(DIRECTORY)");
                logger.severe("ERR: DIRECTORY MOVE FAILED(DIRECTORY)" + commandInfoString);
                return;
            }

            long downloaded_count = downloadFiles(ftpClient);

            if (zip){
                zipFiles();
            }
            System.out.println("END DOWNLOAD TOTAL:" + downloaded_count);
            logger.info("END DOWNLOAD TOTAL:" + downloaded_count + commandInfoString);
        } catch (IOException e) {
            System.out.println("ERR: IOEXCEPTION");
            logger.severe("ERR: IOEXCEPTION" + commandInfoString);
            e.printStackTrace();
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void execute(String[] args) {
        Options options = new Options();

        options.addRequiredOption("host", "host", true, "ftp host");
        options.addRequiredOption("port", "port", true, "ftp port");
        options.addRequiredOption("md", "md", true, "ftp mode");
        options.addRequiredOption("u", "user", true, "user id and password( -u user/password)");
        options.addRequiredOption("dir", "dir", true, "ftp root directory");
        options.addRequiredOption("dest", "dest", true, "destination directory");
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
            this.directory = commandLine.getOptionValue("dir");
            this.downloadDirectory = commandLine.getOptionValue("dest");

            String fileListFile = commandLine.getOptionValue("fl");
            loadFileList(fileListFile);

            zip = commandLine.hasOption("az");
            zipFileName = commandLine.getOptionValue("az");

            commandInfoString = createCommandInfoString();

            doCommand();
        } catch (ParseException e) {
            System.out.println("ERR:Command Line Parse Exception");
            logger.severe("ERR:Command Line Parse Exception");
            e.printStackTrace();
//            throw new Exception (e.getMessage());
        } catch (IOException e) {
            System.out.println("ERR:IOException");
            logger.severe("ERR:IOException");
            e.printStackTrace();
        }

    }
}
