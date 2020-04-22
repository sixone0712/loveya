package jp.co.canon.cks.eec.fs.rssportal.dummy;

import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.portal.bean.FileInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.LogInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.RequestInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.RequestListBean;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.portal.bussiness.ServiceException;
import jp.co.canon.cks.eec.fs.rssportal.background.Compressor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class VirtualFileServiceModelImpl implements FileServiceModel {

    private static final String fileCachePath = "virt-cache";
    private FileServiceManage serviceManage;
    private Map<String, RequestListBean> downloadListMap;

    @Override
    public void logout(String user) throws ServiceException {
        log.warn("nop logout");
    }

    @Override
    public String download(String user, String system, String tool, String reqNo, String fileName) throws ServiceException {
        log.warn("nop download");
        return null;
    }

    @Override
    public int checkAuth(String user, String password, String passType, String compId) throws ServiceException {
        log.warn("nop checkAuth");
        return 0;
    }

    @Override
    public Map createToolList() throws ServiceException {
        log.warn("nop createToolList");
        return null;
    }

    @Override
    public LogInfoBean[] createFileTypeList(String tool) throws ServiceException {
        log.warn("nop createFileTypeList");
        return new LogInfoBean[0];
    }

    @Override
    public FileInfoBean[] createFileList(String tool, String logType, Calendar calFrom, Calendar calTo, String queryStr, String dir) throws ServiceException {
        log.warn("nop createFileList");
        return new FileInfoBean[0];
    }

    @Override
    public String registRequest(String system, String user, String tool, String comment, String logType, String[] fileName, long[] fileSizes, String[] fileTimestamps) throws ServiceException {
        log.warn("nop registRequest");
        return null;
    }

    @Override
    public int cancelRequest(String user, String tool, String reqNo) throws ServiceException {
        log.warn("nop cancelRequest");
        return 0;
    }

    @Override
    public RequestListBean createRequestList(String system, String tool, String reqNo) throws ServiceException {
        log.warn("nop createRequestList");
        return null;
    }



    @Override
    public RequestListBean createDownloadList(String system, String tool, @NonNull String reqNo) throws ServiceException {
        log.info("virtmodel.createDownloadList");

        if(downloadListMap==null) {
            downloadListMap = new HashMap<>();
        }
        if(downloadListMap.containsKey(reqNo)) {
            return downloadListMap.get(reqNo);
        }

        Path reqPath = Paths.get(fileCachePath, reqNo);
        File reqFile = reqPath.toFile();
        if(reqFile.exists()==false || reqFile.isFile()) {
            log.error("invalid reqNo error");
            return new RequestListBean();
        }

        int files = reqFile.listFiles().length;
        log.info(files+" files exist");
        if(files==0) {
            log.warn("no files in cache");
            return null;
        }

        Compressor compressor = new Compressor();
        String zipName = reqNo+".zip";
        Path zipPath = Paths.get(fileCachePath, zipName);
        if(compressor.compress(reqPath.toString(), zipPath.toString())) {
            log.info("compressing success "+"["+zipName+"]");
        } else {
            log.info("compressing failed");
        }
        deleteDir(reqPath.toFile());

        RequestListBean request = new RequestListBean();
        List<RequestInfoBean> list = new ArrayList<>();

        // it does minimum operations to test feature.
        RequestInfoBean info = new RequestInfoBean();
        info.setRequestNo(reqNo);
        info.setArchiveFileName(zipName);

        list.add(info);
        request.setRequestList(list);

        downloadListMap.put(reqNo, request);

        return request;
    }

    public void setFileServiceManager(FileServiceManage serviceManager) {
        this.serviceManage = serviceManager;
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

    private final Log log = LogFactory.getLog(getClass());
}
