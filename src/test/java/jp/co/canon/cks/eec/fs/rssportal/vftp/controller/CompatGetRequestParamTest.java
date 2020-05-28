package jp.co.canon.cks.eec.fs.rssportal.vftp.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jp.co.canon.cks.eec.fs.rssportal.vftp.controller.VFtpController.CompatGetRequestParam;

public class CompatGetRequestParamTest {
    @Test
    public void test_getset(){
        CompatGetRequestParam param = new CompatGetRequestParam();
        param.setFilename("ABCDDEFG.log");
        Assertions.assertEquals("ABCDDEFG.log", param.getFilename());
    }
}