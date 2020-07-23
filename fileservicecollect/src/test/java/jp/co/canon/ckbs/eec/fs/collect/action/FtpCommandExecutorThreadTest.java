package jp.co.canon.ckbs.eec.fs.collect.action;

import jp.co.canon.ckbs.eec.fs.collect.model.RequestFileInfo;
import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.ArrayList;

@SpringBootTest
public class FtpCommandExecutorThreadTest {
    @Autowired
    CommandRepository commandRepository;
    @Autowired
    FtpDownloadFileRepository downloadFileRepository;

    @Test
    public void test_001(){
        File workDir = new File("/LOG/downloads");
        if (!workDir.exists()){
            workDir.mkdirs();
        }
        FtpCommandExecutorThread th = new FtpCommandExecutorThread(null, commandRepository, downloadFileRepository, workDir, 6201, 10);

        FtpDownloadRequest req = new FtpDownloadRequest();
        req.setRequestNo("AAAA");
        req.setMachine("MPA_2");
        req.setCategory("002");
        req.setArchive(true);
        ArrayList<RequestFileInfo> list = new ArrayList<>();

        list.add(new RequestFileInfo("20200629225500"));

        req.setFileInfos(list.toArray(new RequestFileInfo[0]));

        th.executeRequest(req);
    }
}
