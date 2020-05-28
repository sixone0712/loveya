package jp.co.canon.cks.eec.fs.rssportal.vftp.controller;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jp.co.canon.cks.eec.fs.rssportal.vftp.controller.VFtpController.SssGetItem;
import jp.co.canon.cks.eec.fs.rssportal.vftp.controller.VFtpController.SssGetRequestParam;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get.GetFileParam;
import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.get.GetFileParamList;

class SssGetRequestParamTest {
    @Test
    public void test_01(){
        SssGetRequestParam param = new SssGetRequestParam();
        ArrayList<SssGetItem> list = new ArrayList<>();
        SssGetItem item = new SssGetItem();

        item.setServer("AAA");
        item.setPath("/VROOT/SSS/Optional/AAAA");
        item.setFilename("ABCDEFG.log");
        list.add(item);
        param.setList(list);

        Assertions.assertEquals(list, param.getList());

        GetFileParamList paramList = param.toGetFileParamList();
        GetFileParam[] params = paramList.toArray();

        Assertions.assertEquals(1, params.length);
    }
}