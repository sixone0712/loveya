package jp.co.canon.cks.eec.fs.rssportal.background.autocollect;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.VFtpSssListRequestResponse;
import jp.co.canon.ckbs.eec.fs.collect.service.VFtpFileInfo;
import jp.co.canon.cks.eec.fs.rssportal.background.DownloadRequestForm;
import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloader;
import jp.co.canon.cks.eec.fs.rssportal.background.VFtpSssDownloadRequestForm;
import jp.co.canon.cks.eec.fs.rssportal.common.Tool;
import jp.co.canon.cks.eec.fs.rssportal.dao.CollectionPlanDao;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.apache.commons.logging.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class VFtpSssCollectProcess extends CollectProcess {

    public VFtpSssCollectProcess(PlanManager manager, CollectPlanVo plan, CollectionPlanDao dao, FileDownloader downloader, Log log) {
        super(manager, plan, dao, downloader, log);
        if(!plan.getPlanType().equalsIgnoreCase("vftp_sss")) {
            log.error("invalid planType "+plan.getPlanType());
        }
    }

    @Override
    protected void createDownloadFileList() throws CollectException, InterruptedException {
        __checkPlanType();

        String[] machines = plan.getTool().split(",");
        String[] fabs = plan.getFab().split(",");
        String[] directories = plan.getDirectory().split(",");
        if(machines.length==0 || machines.length!=fabs.length)
            throw new CollectException(plan, "parameter exception");

        SimpleDateFormat dateFormat = Tool.getVFtpSimpleDateFormat();
        String startTime, endTime;
        if(plan.getLastPoint()==null) {
            startTime = Tool.getVFtpTimeFormat(plan.getStart());
        } else {
            startTime = Tool.getVFtpTimeFormat(plan.getLastPoint());
        }

        if(currentMillis>plan.getEnd().getTime()) {
            endTime = Tool.getVFtpTimeFormat(plan.getEnd());
        } else {
            endTime = dateFormat.format(currentMillis);
        }

        List<DownloadRequestForm> list = new ArrayList<>();
        for(int i=0; i<machines.length; ++i) {
            for(String directory: directories) {
                String _directory = String.format(directory, startTime, endTime);
                VFtpSssListRequestResponse response = connector.createVFtpSssListRequest(machines[i], _directory);
                if(response==null || response.getErrorMessage()!=null || response.getRequest()==null ||
                        response.getRequest().getFileList()==null) {
                    throw new CollectException(plan, "failed to get file-list");
                }
                VFtpFileInfo[] files = response.getRequest().getFileList();
                if(files.length>0) {
                    VFtpSssDownloadRequestForm form = new VFtpSssDownloadRequestForm(fabs[i], machines[i], _directory);
                    for (VFtpFileInfo file : files) {
                        form.addFile(file.getFileName(), file.getFileSize());
                    }
                    list.add(form);
                }
            }
        }
        requestList = list;
        requestFiles = list.size();
    }

    private void __checkPlanType() throws CollectException {
        if(!plan.getPlanType().equals("vftp_sss")) {
            throw new CollectException(plan, "wrong plan type");
        }
    }
}
