package jp.co.canon.ckbs.eec.service;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;

public class GetFtpCommandTest {

    boolean createListFile(String filename, ArrayList<String> fileList){
        File listFile = new File(filename);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(listFile)))) {
            writer.newLine();
            writer.write(",");
            writer.newLine();
            for(String s : fileList){
                writer.write(s + ",");
                writer.newLine();
            }
            return true;
        } catch (FileNotFoundException e) {

        } catch (SecurityException e) {

        } catch (IOException e){

        }

        return false;
    }

    @Test
    void test_001(){
        ArrayList<String> args = new ArrayList<>();
        args.add("-host");
        args.add("10.1.31.242");
        args.add("-port");
        args.add("22001");
        args.add("-md");
        args.add("passive");
        args.add("-u");
        args.add("ckbs/ckbs");
        args.add("-dir");
        args.add("/VROOT/COMPAT/Optional");
        args.add("-dest");
        args.add("ABCDE.log");
        args.add("-fl");
        args.add("abcd.LIST");

        Command command = FtpCommand.createCommandFromName("get");
        command.execute(args.toArray(new String[0]));
    }

    @Test
    void test_002(){
        ArrayList<String> args = new ArrayList<>();
        args.add("-host");
        args.add("10.1.31.242");
        args.add("-port");
        args.add("22001");
        args.add("-md");
        args.add("passive");
        args.add("-u");
        args.add("ckbs/ckbs");
        args.add("-dest");
        args.add("ABCDE.log");
        args.add("-fl");
        args.add("abcd.LIST");

        Command command = FtpCommand.createCommandFromName("get");
        command.execute(args.toArray(new String[0]));
    }

    @Test
    void test_003(){
        ArrayList<String> args = new ArrayList<>();
        args.add("-host");
        args.add("10.1.31.242");
        args.add("-port");
        args.add("22001");
        args.add("-md");
        args.add("passive");
        args.add("-u");
        args.add("ckbs/ckbs");
        args.add("-dir");
        args.add("/VROOT/COMPAT/Optional");
        args.add("-dest");
        args.add("/LOG/downloads/ABCDEFG");
        args.add("-fl");
        args.add("/LOG/FILELIST.LST");

        try {
            FileUtils.deleteDirectory(new File("/LOG/downloads/ABCDEFG"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> files = new ArrayList<>();
        files.add("ABCDEFG.log");
        createListFile("/LOG/FILELIST.LST", files);

        Command command = FtpCommand.createCommandFromName("get");
        command.execute(args.toArray(new String[0]));
    }

    @Test
    void test_004(){
        ArrayList<String> args = new ArrayList<>();
        args.add("-host");
        args.add("10.1.31.242");
        args.add("-port");
        args.add("22001");
        args.add("-md");
        args.add("active");
        args.add("-u");
        args.add("ckbs/ckbs");
        args.add("-dir");
        args.add("/VROOT/COMPAT/Optional");
        args.add("-dest");
        args.add("/LOG/downloads/ABCDEFG");
        args.add("-fl");
        args.add("/LOG/FILELIST.LST");

        try {
            FileUtils.deleteDirectory(new File("/LOG/downloads/ABCDEFG"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> files = new ArrayList<>();
        files.add("ABCDEFG.log");
        createListFile("/LOG/FILELIST.LST", files);

        Command command = FtpCommand.createCommandFromName("get");
        command.execute(args.toArray(new String[0]));
    }

}
