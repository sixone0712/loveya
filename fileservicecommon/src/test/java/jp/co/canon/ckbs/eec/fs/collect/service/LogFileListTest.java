package jp.co.canon.ckbs.eec.fs.collect.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class LogFileListTest {
    @Test
    void test_001(){
        LogFileList logFileList = new LogFileList();
        logFileList.setErrorCode("500");
        Assertions.assertEquals("500", logFileList.getErrorCode());
        logFileList.setErrorMessage("Server Error");
        Assertions.assertEquals("Server Error", logFileList.getErrorMessage());

        ArrayList<FileInfo> list = new ArrayList<>();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilename("aaa.txt");
        fileInfo.setTimestamp("11223345");
        fileInfo.setType("D");
        fileInfo.setSize(0);
        list.add(fileInfo);

        logFileList.setList(list.toArray(new FileInfo[0]));
        Assertions.assertArrayEquals(list.toArray(new FileInfo[0]), logFileList.getList());
    }
}
