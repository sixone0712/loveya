package jp.co.canon.cks.eec.fs.portal.bussiness;

import jp.co.canon.cks.eec.fs.portal.bean.FileInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.LogInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.RequestListBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.Map;

public class TedFileServiceModelImpl implements FileServiceModel {

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public void logout(String user) throws ServiceException {

    }

    @Override
    public String download(String user, String system, String tool, String reqNo, String fileName) throws ServiceException {
        return null;
    }

    @Override
    public int checkAuth(String user, String password, String passType, String compId) throws ServiceException {
        return 0;
    }

    @Override
    public Map createToolList() throws ServiceException {
        return null;
    }

    @Override
    public LogInfoBean[] createFileTypeList(String tool) throws ServiceException {
        return new LogInfoBean[0];
    }

    @Override
    public FileInfoBean[] createFileList(String tool, String logType, Calendar calFrom, Calendar calTo, String queryStr, String dir) throws ServiceException {
        return new FileInfoBean[0];
    }

    @Override
    public String registRequest(String system, String user, String tool, String comment, String logType, String[] fileName, long[] fileSizes, String[] fileTimestamps) throws ServiceException {
        log.warn("registRequest!");
        something(3000);
        return null;
    }

    @Override
    public int cancelRequest(String user, String tool, String reqNo) throws ServiceException {
        return 0;
    }

    @Override
    public RequestListBean createRequestList(String system, String tool, String reqNo) throws ServiceException {
        return null;
    }

    @Override
    public RequestListBean createDownloadList(String system, String tool, String reqNo) throws ServiceException {
        return null;
    }

    /* something is dummy for something */
    private void something(long l) {
        log.warn("sleep for "+l+" msec");
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
