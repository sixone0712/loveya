package jp.co.canon.ckbs.eec.service;

import org.apache.commons.cli.*;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GetFtpCommand extends BaseFtpCommand {

    String directory;
    String downloadDirectory;
    String [] files;
    boolean zip = false;
    String zipFileName;

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
                if (lineArr[0].length() > 0){
                    fileList.add(lineArr[0]);
                }
            }
            files = fileList.toArray(new String[0]);
        } catch (FileNotFoundException e) {
            throw new IOException(e);
        } catch (IOException e){
            throw e;
        }
    }

    void inputStreamToFile(InputStream inputStream, String fileName){
        File downloadDirectoryFile = new File(downloadDirectory);
        OutputStream outputStream = null;
        File outputFile = new File(downloadDirectoryFile, fileName);
        if (!outputFile.getParentFile().exists()){
            outputFile.getParentFile().mkdirs();
        }
        try {
            outputStream = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            System.out.println("ERR: FileNotFoundException...");
            e.printStackTrace();
        }
        if (outputStream == null){
            System.out.println("ERR:outputStream == null");
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
        } catch (IOException e) {
            System.out.println("ERR: IOException:"+e.getMessage());
        } finally{
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    void downloadFile(FTPClient ftpClient, String fileName){
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
            e.printStackTrace();
        }
        if (inputStream == null){
            System.out.println("ERR:inputStream == null "+ftpClient.getReplyCode());
            return;
        }

        try {
            inputStreamToFile(inputStream, fileName);
        } catch (Exception e){
            System.out.println("ERR:Exception");
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

    void downloadFiles(FTPClient ftpClient){
        File downloadDirectoryFile = new File(downloadDirectory);
        if (!downloadDirectoryFile.exists()){
            downloadDirectoryFile.mkdirs();
        }
        for(String fileName : files){
            downloadFile(ftpClient, fileName);
        }
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
        } catch (IOException e){

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
            if (!ftpmode.equalsIgnoreCase("active")){
                ftpClient.enterLocalPassiveMode();
            }

            boolean moved = true;
            System.out.println("STATUS:TRY_CHANGE_DIRECTORY( " + directory + " )");
            moved = ftpClient.changeWorkingDirectory(directory);
            if (!moved){
                System.out.println("ERR: DIRECTORY MOVE FAILED(DIRECTORY)");
                return;
            }

            downloadFiles(ftpClient);

            if (zip){
                zipFiles();
            }
            System.out.println("END DOWNLOAD TOTAL:" + 0);
        } catch (IOException e) {
            System.out.println("ERR: IOEXCEPTION");
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

            doCommand();
        } catch (ParseException e) {
            System.out.println("ERR:Command Line Parse Exception");
            e.printStackTrace();
//            throw new Exception (e.getMessage());
        } catch (IOException e) {
            System.out.println("ERR:IOException");
            e.printStackTrace();
        }

    }
}
