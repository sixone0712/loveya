package jp.co.canon.cks.eec.fs.rssportal.vftp.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jp.co.canon.cks.eec.fs.rssportal.vftp.controller.VFtpController.SssGetItem;

class SssGetItemTest {
    @Test
    public void test_getset(){
        SssGetItem item = new SssGetItem();

        item.setServer("AAA");
        Assertions.assertEquals("AAA", item.getServer());
        
        item.setPath("/VROOT/SSS/Optional/AAAA");
        Assertions.assertEquals("/VROOT/SSS/Optional/AAAA", item.getPath());

        item.setFilename("ABCDEFG.log");
        Assertions.assertEquals("ABCDEFG.log", item.getFilename());
    }
}