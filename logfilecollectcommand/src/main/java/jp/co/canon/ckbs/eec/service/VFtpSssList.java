package jp.co.canon.ckbs.eec.service;

import org.apache.commons.cli.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.logging.Logger;

public class VFtpSssList {
    Logger logger = Logger.getLogger(VFtpSssList.class.getName());
    public static void temp(String[] args){
        String dest = args[0];
        String portStr = args[1];
        String user = args[2];
        String password = args[3];
        String rootDirectory = args[4];
        String destDirectory = args[5];
        int port = Integer.parseInt(portStr);

        System.out.println("dest :" + dest);
        System.out.println("portStr :" + portStr);

        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(dest, port);
            System.out.println("CONNECTED");
            boolean logined = ftpClient.login(user, password);
            if (logined){
                System.out.println("LOGINED");
                boolean cdRoot = ftpClient.changeWorkingDirectory(rootDirectory);
                if (cdRoot){
                    System.out.println("change rootDirectory");
                    boolean cdDest = ftpClient.changeWorkingDirectory(destDirectory);
                    if (cdDest){
                        System.out.println("change destDirectory");
                        FTPFile[] ftpFiles = ftpClient.listFiles();
                        for(FTPFile file : ftpFiles){
                            System.out.println(" " + file.getName());
                        }
                        System.out.println("total : " + ftpFiles.length);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args){
        Options options = new Options();

        options.addOption("t", true, "display current time");
        options.addRequiredOption("ap", "adminPort", true, "admin port number");
        options.addRequiredOption("p", "port", true, "ftp port number");
        options.addRequiredOption("u", "user", true, "ftp user and password");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("t")){
                System.out.println(cmd.getOptionValue("t"));
            }

        } catch (MissingArgumentException e){
            System.out.println(e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
