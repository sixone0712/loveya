package jp.co.canon.cks.eec.fs.rssportal.background.autocollect;

import jp.co.canon.cks.eec.fs.rssportal.background.DownloadRequestForm;
import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloader;
import jp.co.canon.cks.eec.fs.rssportal.background.VFtpCompatDownloadRequestForm;
import jp.co.canon.cks.eec.fs.rssportal.common.Tool;
import jp.co.canon.cks.eec.fs.rssportal.dao.CollectionPlanDao;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.apache.commons.logging.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class VFtpCompatCollectProcess extends CollectProcess {

    public VFtpCompatCollectProcess(
            PlanManager manager, CollectPlanVo plan, CollectionPlanDao dao, FileDownloader downloader, Log log) {
        super(manager, plan, dao, downloader, log);
        if(!plan.getPlanType().equalsIgnoreCase("vftp_compat")) {
            log.error("invalid planType "+plan.getPlanType());
        }
    }

    @Override
    protected void createDownloadFileList() throws CollectException, InterruptedException {
        __checkPlanType();

        String[] machines = plan.getTool().split(",");
        String[] fabs = plan.getFab().split(",");
        String[] commands = plan.getCommand().split(",");
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
            for(String command: commands) {
                String _command;
                if(command.equals("") || command.startsWith("none")) {
                    _command = String.format("%s_%s", startTime, endTime);
                } else {
                    _command = String.format(command, startTime, endTime);
                }
                list.add(new VFtpCompatDownloadRequestForm(fabs[i], machines[i], _command, true));
            }
        }
        requestList = list;
        requestFiles = list.size();
    }

    private void __checkPlanType() throws CollectException {
        if(!plan.getPlanType().equals("vftp_compat")) {
            throw new CollectException(plan, "wrong plan type");
        }
    }
}
