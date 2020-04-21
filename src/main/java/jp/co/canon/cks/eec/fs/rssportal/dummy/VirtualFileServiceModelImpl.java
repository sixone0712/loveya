package jp.co.canon.cks.eec.fs.rssportal.dummy;

import jp.co.canon.cks.eec.fs.portal.bean.FileInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.LogInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.RequestListBean;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.portal.bussiness.ServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.Map;

public class VirtualFileServiceModelImpl implements FileServiceModel {

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
    public RequestListBean createDownloadList(String system, String tool, String reqNo) throws ServiceException {
        log.warn("nop createDownloadList");
        return null;
    }

    private final Log log = LogFactory.getLog(getClass());
}
