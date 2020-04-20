package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.manage.FileInfoModel;
import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.manage.FileServiceManageServiceLocator;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.service.CollectPlanService;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class AutoCollector extends Thread {

    private final CollectPlanService service;
    private FileServiceManage fileServiceManage;
    private CollectPlanVo nextPlan;
    private boolean planUpdated = true;


    @Autowired
    private AutoCollector(CollectPlanService service) throws ServiceException {
        if(service==null) {
            throw new BeanInitializationException("service injection failed");
        }
        this.service = service;
        service.addNotifier(notifyUpdate);
        service.scheduleAllPlans();

        FileServiceManageServiceLocator serviceLocator = new FileServiceManageServiceLocator();
        fileServiceManage = serviceLocator.getFileServiceManage();

        this.start();
    }

    @Override
    public void run() {
        log.info("AutoCollector start");

        try {
            while(true) {
                CollectPlanVo plan = getNext();
                if(plan==null) {
                    sleep(5000);
                    continue;
                }

                Date cur = new Date(System.currentTimeMillis());
                if(plan.getNextAction().before(cur)) {
                    collect(plan);
                }
                sleep(1000);
            }
        } catch (InterruptedException e) {
            log.error("sleep exception occurs");
            e.printStackTrace();
        } catch (RemoteException e) {
            log.error("failed to access esp web service");
            e.printStackTrace();
        }
    }

    private boolean collect(CollectPlanVo plan) throws InterruptedException, RemoteException {

        log.info("collect: "+plan.toString());
        List<DownloadForm> downloadList = createDownloadList(plan);

        int totalFiles = downloadList.stream().mapToInt(item -> item.getFiles().size()).sum();
        if(totalFiles!=0) {
            FileDownloadExecutor executor = new FileDownloadExecutor(fileServiceManage, downloadList);
            executor.start();
        }
        service.updateLastCollect(plan);
        service.schedulePlan(plan);
        return false;
    }

    private List<DownloadForm> createDownloadList(CollectPlanVo plan) throws RemoteException {
        List<DownloadForm> downloadList = new ArrayList<>();
        String[] tools = plan.getTool().split(",");
        String[] types = plan.getLogType().split(",");
        log.info("createDownloadList: tools="+tools.length+" types="+types.length);

        long lastTime = plan.getLastPoint().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        for(String tool: tools) {
            tool = tool.trim();
            for(String type: types) {
                type = type.trim();
                DownloadForm form = new DownloadForm(tool, type);
                downloadList.add(form);

                Calendar from = Calendar.getInstance();
                from.setTimeInMillis(lastTime-1000);
                Calendar to = Calendar.getInstance();
                to.setTimeInMillis(System.currentTimeMillis());

                FileInfoModel[] fileInfos = fileServiceManage.createFileList(tool, type, from, to, "", "");
                for(FileInfoModel file: fileInfos) {
                    long fileTime = file.getTimestamp().getTimeInMillis();
                    if(lastTime<fileTime) {
                        lastTime = fileTime;
                    }

                    dateFormat.setTimeZone(file.getTimestamp().getTimeZone());
                    String time = dateFormat.format(file.getTimestamp().getTime());

                    form.addFile(file.getName(), file.getSize(), time);
                }
                log.info("tool="+tool+" type="+type+": "+fileInfos.length+" files");
                downloadList.add(form);
            }
        }
        log.info("lastpoint="+new Timestamp(lastTime).toString());
        return downloadList;
    }

    private CollectPlanVo getNext() {
        if(planUpdated) {
            nextPlan = service.getNextPlan();
            if(nextPlan!=null) {
                log.info("nextPlan=" + nextPlan.getDescription() + " nextaction=" + nextPlan.getNextAction().toString());
            }
            if(nextPlan!=null) {
                planUpdated = false;
            }
        }
        return nextPlan;
    }

    private Runnable notifyUpdate = ()->{
        System.out.println("notifyUpdate()");
        planUpdated = true;
    };


    private final Log log = LogFactory.getLog(getClass());
}
