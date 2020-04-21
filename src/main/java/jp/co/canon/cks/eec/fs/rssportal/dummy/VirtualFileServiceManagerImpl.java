package jp.co.canon.cks.eec.fs.rssportal.dummy;

import jp.co.canon.cks.eec.fs.manage.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VirtualFileServiceManagerImpl implements FileServiceManage {

    private VirtualFileStorage vfs;

    public void createVfs(@NonNull Date from, @NonNull Date to, long interval, @NonNull String[] tools, @NonNull String[] types) {
        log.info("create virtual-file-storage");
        vfs = new VirtualFileStorage();
        vfs.setBase("vfs-cache-dum");

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
    public String registRequest(String system, String user, String tool, String comment, String logType, String[] fileNames, long[] fileSizes, Calendar[] fileTimestamps) throws RemoteException {
        log.warn("nop DummyFileServiceManagerImpl.registRequest");
        return null;
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
        log.warn("nop DummyFileServiceManagerImpl.createDownloadList");
        return null;
    }

    @Override
    public String download(String user, String system, String tool, String reqNo, String fileName) throws RemoteException {
        log.warn("nop DummyFileServiceManagerImpl.download");
        return null;
    }

    @Override
    public void logout(String user) throws RemoteException {
        log.warn("nop DummyFileServiceManagerImpl.logout");
    }

    private final Log log = LogFactory.getLog(getClass());
}
