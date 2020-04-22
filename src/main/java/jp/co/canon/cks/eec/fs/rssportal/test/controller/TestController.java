package jp.co.canon.cks.eec.fs.rssportal.test.controller;

import jp.co.canon.cks.eec.fs.manage.FileInfoModel;
import jp.co.canon.cks.eec.fs.portal.bean.RequestInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.RequestListBean;
import jp.co.canon.cks.eec.fs.portal.bussiness.ServiceException;
import jp.co.canon.cks.eec.fs.rssportal.dummy.VirtualFileServiceManagerImpl;
import jp.co.canon.cks.eec.fs.rssportal.dummy.VirtualFileServiceModelImpl;
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
import java.rmi.RemoteException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @RequestMapping("/vfsm")
    @ResponseBody
    public String testVirtualFileServiceManager() {
        log.info("testVirtualFileServiceManager");

        VirtualFileServiceManagerImpl manager = new VirtualFileServiceManagerImpl();
        VirtualFileServiceModelImpl service = new VirtualFileServiceModelImpl();

        String[] tools = {"EQVM88", "EQVM87"};
        String[] types = {"001", "002", "003", "004", "005"};

        long cur = System.currentTimeMillis();
        manager.createVfs(new Date(cur), new Date(cur+(24*3600*1000)), 60000, tools, types);

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();
        from.setTimeInMillis(cur);
        to.setTimeInMillis(cur+(3600*1000));

        FileInfoModel[] fileInfos = null;
        try {
             fileInfos = manager.createFileList(tools[0], types[0], from, to, "", "");

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(from.getTimeZone());
        String strFrom = dateFormat.format(from.getTime());
        dateFormat.setTimeZone(to.getTimeZone());
        String strTo = dateFormat.format(to.getTime());

        if(true) { /* logging */
            log.info(strFrom + " ~ " + strTo);
            for (FileInfoModel file : fileInfos) {
                dateFormat.setTimeZone(file.getTimestamp().getTimeZone());
                log.info("  + " + dateFormat.format(file.getTimestamp().getTime()) + " : " + file.getName());
            }
            log.info("total files=" + fileInfos.length);
        }

        String[] fileNames = new String[fileInfos.length];
        long[] fileSizes = new long[fileInfos.length];
        Calendar[] fileDates = new Calendar[fileInfos.length];
        for (int i=0; i<fileInfos.length; ++i) {
            fileNames[i] = fileInfos[i].getName();
            fileSizes[i] = fileInfos[i].getSize();
            fileDates[i] = fileInfos[i].getTimestamp();
        }

        String reqNo = null;
        try {
            reqNo = manager.registRequest("virt", "espadmin", tools[0], "", types[0],
                    fileNames, fileSizes, fileDates);

            RequestListBean request = service.createDownloadList("virt", tools[0], reqNo);
            RequestInfoBean info = request.get(reqNo);
            if(info==null) {
                return "requestinfobean=null";
            }
            String achieveFilename = info.getArchiveFileName();
            log.info("achieve-file-name="+achieveFilename);

            String url = manager.download("espamdin", "virt", tools[0], reqNo, achieveFilename);

            log.info("url="+url);

        } catch (ServiceException | RemoteException e) {
            e.printStackTrace();
        }


        log.info("testVirtualFileServiceManager done");
        return "vfsm";
    }



    private final Log log = LogFactory.getLog(getClass());
}
