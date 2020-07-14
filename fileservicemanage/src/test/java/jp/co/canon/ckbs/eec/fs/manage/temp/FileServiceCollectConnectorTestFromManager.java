package jp.co.canon.ckbs.eec.fs.manage.temp;

import jp.co.canon.ckbs.eec.fs.collect.FileServiceCollectConnector;
import jp.co.canon.ckbs.eec.fs.collect.service.LogFileList;
import org.junit.jupiter.api.Test;

public class FileServiceCollectConnectorTestFromManager {
    @Test
    void test(){
        FileServiceCollectConnector connector =
                new FileServiceCollectConnector("10.1.36.118:8080");

        LogFileList logFileList =
                connector.getFtpFileList("MPA_2",
                        "002",
                        "20200601000000",
                        "20200706235959",
                        "",
                        "");

        System.out.println(logFileList);
    }
}
