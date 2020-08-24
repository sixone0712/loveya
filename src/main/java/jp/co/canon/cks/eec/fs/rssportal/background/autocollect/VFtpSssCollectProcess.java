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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VFtpSssCollectProcess extends CollectProcess {

    private long lastPointMillis;

    public VFtpSssCollectProcess(PlanManager manager, CollectPlanVo plan, CollectionPlanDao dao, FileDownloader downloader, Log log) {
        super(manager, plan, dao, downloader, log);
        if (!plan.getPlanType().equalsIgnoreCase("vftp_sss")) {
            printError("invalid planType " + plan.getPlanType());
        }
    }

    @Override
    protected void createDownloadFileList() throws CollectException, InterruptedException {
        __checkPlanType();

        String[] machines = plan.getTool().split(",");
        String[] fabs = plan.getFab().split(",");
        String[] directories = plan.getDirectory().split(",");
        if (machines.length == 0 || machines.length != fabs.length)
            throw new CollectException(plan, "parameter exception");

        lastPointMillis = 0;
        String startTime, endTime;
        Timestamp startTs;
        long endMillis;

        if (plan.getLastPoint() == null) {
            startTs = plan.getStart();
        } else {
            startTs = plan.getLastPoint();
        }

        Calendar endCal = Calendar.getInstance();
        endCal.setTimeInMillis(startTs.getTime() + aDayMillis);
        endCal.set(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DATE),
                0, 0, 0);

        endMillis = endCal.getTimeInMillis();
        if (endMillis > plan.getEnd().getTime()) {
            endMillis = plan.getEnd().getTime();
        }

        if (endMillis > currentMillis) {
            throw new CollectException(plan, false);
        }

        startTime = Tool.getVFtpTimeFormat(startTs);
        endTime = Tool.getVFtpTimeFormat(new Timestamp(endMillis));
        printInfo("start=" + startTime + " end=" + endTime);

        List<DownloadRequestForm> list = new ArrayList<>();
        for (int i = 0; i < machines.length; ++i) {
            for (String directory : directories) {
                String _directory = String.format(directory, startTime, endTime);
                VFtpSssListRequestResponse response = connector.createVFtpSssListRequest(machines[i], _directory);
                if (response == null || response.getErrorMessage() != null || response.getRequest() == null) {
                    throw new CollectException(plan, "failed to get file-list");
                }
                response = waitListRequestDone(machines[i], response.getRequest().getRequestNo());
                VFtpFileInfo[] files = response.getRequest().getFileList();
                if (files.length > 0) {
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
        lastPointMillis = endMillis;
    }

    private VFtpSssListRequestResponse waitListRequestDone(String machine, String requestNo) throws CollectException, InterruptedException {
        final long timeout = 10000;
        long start = System.currentTimeMillis();
        while (true) {
            VFtpSssListRequestResponse resp = connector.getVFtpSssListRequest(machine, requestNo);
            if (resp == null || resp.getErrorMessage() != null || resp.getRequest() == null) {
                break;
            } else if (resp.getRequest().getFileList() != null) {
                return resp;
            } else if ((System.currentTimeMillis() - start) > timeout) {
                printError("create list timeout");
                break;
            }
            Thread.sleep(100);
        }
        throw new CollectException(plan, "failed to create file-list");
    }

    @Override
    protected Timestamp getLastPoint() {
        if (lastPointMillis != 0) {
            return new Timestamp(lastPointMillis);
        }
        return null;
    }

    @Override
    protected Timestamp getNextPlan() {
        long todayMillis = getMidnightMillis(System.currentTimeMillis());
        long lastMillis = getMidnightMillis(plan.getLastPoint().getTime());

        if(isSameDay(todayMillis, lastMillis)) {
            Calendar next = Calendar.getInstance();
            next.setTimeInMillis(todayMillis);
            next.add(Calendar.DATE, 1);
            return new Timestamp(next.getTimeInMillis());
        }
        return new Timestamp(System.currentTimeMillis());
    }

    private void __checkPlanType() throws CollectException {
        if(!plan.getPlanType().equals("vftp_sss")) {
            throw new CollectException(plan, "wrong plan type");
        }
    }


}
