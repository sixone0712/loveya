package jp.co.canon.ckbs.eec.service;

import org.apache.commons.cli.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;

public class ListFtpCommand extends BaseFtpCommand {
    String rootDir;
    String destDir;

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
            System.out.println("STATUS:LOGIN_OK");

            boolean moved = true;
            moved = ftpClient.changeWorkingDirectory(rootDir);
            if (!moved){
                System.out.println("ERR: DIRECTORY MOVE FAILED(ROOT DIR)");
                return;
            }
            moved = ftpClient.changeWorkingDirectory(destDir);
            if (!moved){
                System.out.println("ERR: DIRECTORY MOVE FAILED(DEST DIR)");
                return;
            }
            if (!ftpmode.equalsIgnoreCase("active")){
                ftpClient.enterLocalPassiveMode();
                System.out.println("STATUS:ENTER PASSIVE");
            }
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for(FTPFile file : ftpFiles){
                if (file.isDirectory()) {
                    System.out.println("DIRECTORY:"+file.getName());
                } else {
                    System.out.println("FILE:"+file.getName()+";"+file.getSize());
                }
            }
            System.out.println("END TOTAL:" + ftpFiles.length);
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
        options.addRequiredOption("root", "root", true, "ftp root directory");
        options.addRequiredOption("dest", "dest", true, "destination directory");

        CommandLineParser parser = new DefaultParser();

        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args, true);
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
            this.destDir = commandLine.getOptionValue("dest");

            doCommand();
        } catch (ParseException e) {
            System.out.println("ERR: Command Parse Exception");
            e.printStackTrace();
        }
    }
}
