package jp.co.canon.cks.eec.fs.rssportal.background.autocollect;

import jp.co.canon.ckbs.eec.fs.collect.service.FileInfo;
import jp.co.canon.ckbs.eec.fs.collect.service.LogFileList;
import jp.co.canon.cks.eec.fs.rssportal.background.DownloadRequestForm;
import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloader;
import jp.co.canon.cks.eec.fs.rssportal.background.FtpDownloadRequestForm;
import jp.co.canon.cks.eec.fs.rssportal.common.Tool;
import jp.co.canon.cks.eec.fs.rssportal.dao.CollectionPlanDao;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.apache.commons.logging.Log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FtpCollectProcess extends CollectProcess {

    private long lastPointMillis;

    public FtpCollectProcess(PlanManager manager, CollectPlanVo plan, CollectionPlanDao dao, FileDownloader downloader, Log log) {
        super(manager, plan, dao, downloader, log);
        if(!plan.getPlanType().equalsIgnoreCase("ftp")) {
            log.error("invalid planType "+plan.getPlanType());
        }
    }

    @Override
    protected void createDownloadFileList() throws CollectException, InterruptedException {
        __checkPlanType();

        String[] machines = plan.getTool().split(",");
        String[] fabs = plan.getFab().split(",");
        String[] categoryCodes = plan.getLogType().split(",");
        String[] categoryNames = plan.getLogTypeStr().split(",");

        if(machines.length==0 || machines.length!=fabs.length || categoryCodes.length==0 ||
                categoryCodes.length!=categoryNames.length)
            throw new CollectException(plan, "parameter exception");

        String startTime, endTime;
        Timestamp startTs = plan.getLastPoint()==null?plan.getStart():plan.getLastPoint();
        SimpleDateFormat dateFormat = Tool.getSimpleDateFormat();
        long endMillis;

        printInfo("---base="+Tool.getFtpTimeFormat(new Timestamp(getCollectBase())));
        if(getCollectBase()>startTs.getTime()) {
            Calendar _end = Calendar.getInstance();
            _end.setTimeInMillis(startTs.getTime());
            _end.add(Calendar.DATE, 1);
            _end.set(Calendar.HOUR_OF_DAY, 0);
            _end.set(Calendar.MINUTE, 0);
            _end.set(Calendar.SECOND, 0);
            _end.set(Calendar.MILLISECOND, 0);
            endMillis = _end.getTimeInMillis();

            if(endMillis>getCollectBase()) {
                endMillis = getCollectBase();
            }

            startTime = Tool.getFtpTimeFormat(startTs);
            endTime = dateFormat.format(endMillis);
        } else {
            startTime = Tool.getFtpTimeFormat(startTs);
            if (currentMillis > plan.getEnd().getTime()) {
                endTime = Tool.getFtpTimeFormat(plan.getEnd());
                endMillis = plan.getEnd().getTime();
            } else {
                endTime = dateFormat.format(currentMillis);
                endMillis = currentMillis;
            }
        }

        printInfo("collecting start="+startTime+" end="+endTime);

        List<DownloadRequestForm> list = new ArrayList<>();

        for(int i=0; i<machines.length; ++i) {
            for(String fail: failMachines) {
                if(machines[i].equals(fail)) {
                    continue;
                }
            }

            List<DownloadRequestForm> _list = new ArrayList<>();
            try {
                for(int j=0; j<categoryCodes.length; ++j) {
                    getFileList(_list, machines[i].trim(), fabs[i].trim(), categoryCodes[j].trim(), categoryNames[j].trim(),
                            startTime, endTime, "", "");
                    if (_list.size() > 0) {
                        list.addAll(_list);
                    }
                }
            } catch (CollectMpaException e) {
                failMachines.add(machines[i]);
            }
        }

        long files = 0;
        log.info("total "+list.size()+" forms created. "+failMachines.size()+" forms failed");
        for(DownloadRequestForm _f: list) {
            FtpDownloadRequestForm f = (FtpDownloadRequestForm)_f;
            files += f.getFiles().size();
            log.info(" machine=" +f.getMachine()+" category="+f.getCategoryName()+" files="+f.getFiles().size());
        }
        requestList = list;
        requestFiles = files;
        lastPointMillis = endMillis;
    }

    @Override
    protected Timestamp getLastPoint() {
        if(lastPointMillis!=0) {
            return new Timestamp(lastPointMillis);
        }
        return null;
    }

    @Override
    protected Timestamp getNextPlan() {
        long interval;
        if (plan.getCollectionType() == 1 /*COLLECTTYPE_CYCLE*/) {
            interval = plan.getInterval();
        } else {
            interval = 60000; /*CONTINUOUS_DEFAULT_INTERVAL*/
        }
        return new Timestamp(plan.getLastCollect().getTime() + interval);
    }

    private void getFileList(List<DownloadRequestForm> list, String machine, String fab, String categoryCode,
                             String categoryName, String start, String end, String keyword, String path)
            throws CollectMpaException, InterruptedException {

        // Interrupt point.
        Thread.sleep(1);

        LogFileList fileList = connector.getFtpFileList(machine, categoryCode, start, end, keyword, path);
        if(fileList.getErrorMessage()!=null) {
            log.error("getFileList error "+fileList.getErrorMessage());
            throw new CollectMpaException(machine, "failed to get file list");
        }

        FtpDownloadRequestForm form = new FtpDownloadRequestForm(fab, machine, categoryCode, categoryName);

        for(FileInfo file: fileList.getList()) {
            if(file.getFilename().endsWith(".") || file.getFilename().endsWith("..") || file.getSize()==0
                    || file.getFilename().startsWith("###")) {
                continue;
            }

            if(file.getType().equalsIgnoreCase("D")) {
                SimpleDateFormat dateFormat = Tool.getSimpleDateFormat();
                try {
                    long _timestamp = dateFormat.parse(file.getTimestamp()).getTime();
                    long _start = dateFormat.parse(start).getTime();
                    long _end = dateFormat.parse(end).getTime();
                    if(_timestamp<_start || _timestamp>_end)
                        continue;
                } catch (ParseException e) {
                    log.warn("timestamp parsing failed");
                }
                getFileList(list, machine, fab, categoryCode, categoryName, start, end, keyword, file.getFilename());
            } else {
                form.addFile(file.getFilename(), file.getSize(), file.getTimestamp());
            }
        }
        list.add(form);
    }

    private void __checkPlanType() throws CollectException {
        if(!plan.getPlanType().equals("ftp")) {
            throw new CollectException(plan, "wrong plan type");
        }
    }
}
