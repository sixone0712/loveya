package jp.co.canon.cks.eec.fs.rssportal.dummy;

import jp.co.canon.cks.eec.fs.manage.*;
import jp.co.canon.cks.eec.fs.rssportal.background.Compressor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualFileServiceManagerImpl implements FileServiceManage {

    private static final String fileCachePath = "virt-cache";
    private static final AtomicInteger keySource = new AtomicInteger(1);

    private VirtualFileStorage vfs;

    public void createVfs(@NonNull Date from, @NonNull Date to, long interval, @NonNull String[] tools, @NonNull String[] types) {
        log.info("create virtual-file-storage");
        String vfsCacheDir = "vfs-cache-dum";

        Path path = Paths.get(vfsCacheDir);
        deleteDir(path.toFile());
        vfs = new VirtualFileStorage();
        vfs.setBase(vfsCacheDir);
        for(String tool:tools) {
            for(String type: types) {
                vfs.createVirtualFiles(from, to, interval, tool+"_"+type);
            }
        }
    }

    @Override
    public int checkAuth(String user, String password, String passType, String compId) throws RemoteException {
        log.warn("nop DummyFileServiceManagerImpl.checkAuth");
        return 0;
    }

    @Override
    public ToolInfoModel[] createToolList() throws RemoteException {
        log.warn("nop DummyFileServiceManagerImpl.createToolList");
        return new ToolInfoModel[0];
    }

    @Override
    public FileTypeModel[] createFileTypeList(String tool) throws RemoteException {
        log.warn("nop DummyFileServiceManagerImpl.createFileTypeList");
        return new FileTypeModel[0];
    }

    @Override
    public FileInfoModel[] createFileList(String tool, String logType, Calendar calFrom, Calendar calTo, String queryStr, String dir) throws RemoteException {
        log.info("virt.createFileList");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(calFrom.getTimeZone());
        String strFrom = dateFormat.format(calFrom.getTime());
        dateFormat.setTimeZone(calTo.getTimeZone());
        String strTo = dateFormat.format(calTo.getTime());

        log.info("  tool="+tool+" logType="+logType+" ["+strFrom+" ~ "+strTo+"]");

        List<VirtualFileStorage.VirtualFile> list = vfs.getFileList(tool+"_"+logType,
                new Date(calFrom.getTimeInMillis()), new Date(calTo.getTimeInMillis()));

        FileInfoModel[] infos = new FileInfoModel[list.size()];
        for(int i=0; i<list.size(); ++i) {
            VirtualFileStorage.VirtualFile vf = list.get(i);

            infos[i] = new FileInfoModel();
            infos[i].setName(vf.filename);
            infos[i].setSize(vf.size);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(vf.created.getTime());
            infos[i].setTimestamp(cal);
        }
        if(true) {
            for(FileInfoModel info: infos) {
                dateFormat.setTimeZone(info.getTimestamp().getTimeZone());
                log.info("    + "+dateFormat.format(info.getTimestamp().getTime())+":"+info.getName());
            }
        }
        return infos;
    }

    @Override
    public String registRequest(String system, String user, String tool, String comment, String logType,
                                String[] fileNames, long[] fileSizes, Calendar[] fileTimestamps) throws RemoteException {
        log.info("virt.registRequest");

        if(fileNames.length==0 || fileNames.length!=fileSizes.length || fileNames.length!=fileTimestamps.length) {
            log.error("input array length error");
            return null;
        }

        String reqNo = createKeyString(system, user, tool, logType);
        String cache = getCachePath(reqNo);
        byte[] buf = new byte[1024];
        for(int i=0; i<fileNames.length; ++i) {
            try {
                InputStream is = vfs.getFile(fileNames[i], fileSizes[i],
                        new Date(fileTimestamps[i].getTimeInMillis()));
                Path path = Paths.get(cache, fileNames[i]);
                OutputStream os = new FileOutputStream(path.toFile());

                while((is.read(buf)>0)) {
                    os.write(buf);
                    os.flush();
                }
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        vfs.clearCache();
        return reqNo;
    }

    @Override
    public int cancelRequest(String user, String tool, String reqNo) throws RemoteException {
        log.warn("nop DummyFileServiceManagerImpl.cancelRequest");
        return 0;
    }

    @Override
    public RequestListModel createRequestList(String system, String tool, String reqNo) throws RemoteException {
        log.warn("nop DummyFileServiceManagerImpl.createRequestList");
        return null;
    }

    @Override
    public DownloadListModel createDownloadList(String system, String tool, String reqNo) throws RemoteException {
        log.warn("virt.createDownloadList");
        return null;
    }

    @Override
    public String download(String user, String system, String tool, @NonNull String reqNo, @NonNull String fileName) throws RemoteException {
        log.info("virt.download");
        Path path = Paths.get(fileCachePath, fileName);
        File file = path.toFile();
        if(file.exists()==false || file.isDirectory()) {
            log.error("no file to download");
            return null;
        }
        return path.toString();
    }

    @Override
    public void logout(String user) throws RemoteException {
        log.warn("nop DummyFileServiceManagerImpl.logout");
    }

    private void deleteDir(@NonNull String dir) {
        File file = new File(dir);
        if(file!=null && file.exists()) {
            deleteDir(file);
        }
    }

    private void deleteDir(@NonNull File file) {
        File[] contents = file.listFiles();
        if(contents!=null) {
            for(File f: contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    private String createKeyString(@NonNull String system, @NonNull String user, @NonNull String tool, @NonNull String logType) {
        return system+"_"+user+"_"+tool+"_"+logType+"_"+System.currentTimeMillis()+"_"+keySource.getAndIncrement();
    }

    private String getCachePath(@NonNull String subPath) {
        File root = new File(fileCachePath);
        if(root.exists()) {
            if(root.isFile()) {
                log.error("cache-path is not directory");
                return null;
            }
        } else {
            root.mkdirs();
        }

        Path path = Paths.get(fileCachePath, subPath);
        File cacheFile = path.toFile();
        if(cacheFile.exists()) {
            if(cacheFile.isFile()) {
                cacheFile.delete();
            } else {
                return cacheFile.getPath();
            }
        }
        cacheFile.mkdirs();
        return cacheFile.getPath();
    }

    private final Log log = LogFactory.getLog(getClass());
}
