package jp.co.canon.ckbs.eec.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class FtpCommandTest {
    @Test
    void test_001(){
        Command command = FtpCommand.createCommandFromName("aaa");
        Assertions.assertNull(command);
    }

    @Test
    void test_002(){
        Command command = FtpCommand.createCommandFromName("list");
        Assertions.assertEquals(command.getClass(), ListFtpCommand.class);
    }

    @Test
    void test_003(){
        Command command = FtpCommand.createCommandFromName("get");
        Assertions.assertEquals(command.getClass(), GetFtpCommand.class);
    }

    @Test
    void test_004(){
        ArrayList<String> arrayList = new ArrayList<>();
        FtpCommand.main(arrayList.toArray(new String[0]));
    }

    @Test
    void test_005(){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("abc");
        FtpCommand.main(arrayList.toArray(new String[0]));
    }

    @Test
    void test_006(){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("list");
        arrayList.add("-host");
        FtpCommand.main(arrayList.toArray(new String[0]));
    }

    @Test
    void test_007(){
        FtpCommand ftpCommand = new FtpCommand();
        Assertions.assertNotNull(ftpCommand);
    }
}
