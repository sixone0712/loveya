package jp.co.canon.ckbs.eec.fs.collect.service;

import jp.co.canon.ckbs.eec.fs.collect.action.ConfigurationException;
import jp.co.canon.ckbs.eec.fs.collect.model.FileInfoModel;
import jp.co.canon.ckbs.eec.fs.collect.service.ftp.FtpListService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class FtpListServiceTest {
    @Autowired
    FtpListService listService;

    @Test
    void test_001(){
        try {
            List<FileInfoModel> list;
            list = listService.getServerFileList("MPA_2",
                                "002",
                                "",
                                "20200601000000",
                                "20200706235959"
                    );
            System.out.println(list);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }
}
