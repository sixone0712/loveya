package jp.co.canon.cks.eec.fs.rssportal.test.controller;

import jp.co.canon.cks.eec.fs.rssportal.dummy.VirtualFileStorage;
import jp.co.canon.cks.eec.fs.rssportal.service.UserPermissionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/download")
    public ModelAndView download(@RequestParam Map<String, Object> param) {

        log.info("test download feature");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("test/download");
        return mav;
    }

    @RequestMapping("/login")
    public ModelAndView loginDummy() {
        log.info("loginDummy");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("login");
        return mav;
    }

    @RequestMapping("/vfs1")
    @ResponseBody
    public String testVirtualFile1() {
        log.info("testVirtualFile1");
        VirtualFileStorage vfs = new VirtualFileStorage();

        vfs.setBase("vfs-cache1");

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Date created = new Date(ts.getTime()-(24*3600*7*1000));
        log.info("request to create file [date="+created.toString()+"]");
        vfs.getFile("test_"+ts.getTime(), 4096*4, created);
        return "vfs1 okay";
    }

    @RequestMapping("/vfs2")
    @ResponseBody
    public String testVirtualFile2() {
        log.info("testVirtualFile2");

        VirtualFileStorage vfs = new VirtualFileStorage();
        vfs.setBase("vfs-cache");

        long cur = System.currentTimeMillis();
        vfs.createVirtualFiles(new Date(cur-(24*3600*1000)), new Date(cur), 60000, "vfs2_test");
        List<VirtualFileStorage.VirtualFile> filelist = vfs.getFileList("vfs2_test", null, null);

        String outputPath = "vfs-out";
        File outputDir = new File(outputPath);
        if(outputDir.exists()==false) {
            outputDir.mkdirs();
        } else if(outputDir.isFile()) {
            return "output path is not valid";
        }

        try {
            for(VirtualFileStorage.VirtualFile vf: filelist) {
                InputStream is = vfs.getFile(vf.filename, vf.size, vf.created);
                Path path = Paths.get(outputPath, vf.filename);
                File outFile = path.toFile();
                OutputStream os = new FileOutputStream(outFile);

                byte[] buf = new byte[1024];
                while((is.read(buf))>0) {
                    os.write(buf);
                    os.flush();
                }
                is.close();
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "file handling exception occurs";
        }

        log.info("vfs2 done");
        return "vfs2 okay";
    }



    private final Log log = LogFactory.getLog(getClass());
}
