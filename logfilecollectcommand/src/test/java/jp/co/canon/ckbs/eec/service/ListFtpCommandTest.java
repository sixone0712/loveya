package jp.co.canon.ckbs.eec.service;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class ListFtpCommandTest {
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
        args.add("-root");
        args.add("/VROOT/SSS/Optional");
        args.add("-dest");
        args.add("IP_AS_RAW");

        Command command = FtpCommand.createCommandFromName("list");
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
        args.add("-root");
        args.add("/VROOT/SSS/XXXX_AAAA");
        args.add("-dest");
        args.add("IP_AS_RAW");

        Command command = FtpCommand.createCommandFromName("list");
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
        args.add("-root");
        args.add("/VROOT");
        args.add("-dest");
        args.add("XXXX_AAAA");

        Command command = FtpCommand.createCommandFromName("list");
        command.execute(args.toArray(new String[0]));
    }


}
