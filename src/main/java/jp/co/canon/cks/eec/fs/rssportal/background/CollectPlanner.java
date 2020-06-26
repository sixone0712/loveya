package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListService;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.model.FileInfo;
import jp.co.canon.cks.eec.fs.rssportal.service.CollectPlanService;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.PlanStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import javax.xml.rpc.ServiceException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class CollectPlanner extends Thread {

    @Value("${rssportal.collect.logBase}")
    private String planRootDir;
    private final CollectPlanService service;
    private final DownloadListService downloadListService;
    private CollectPlanVo nextPlan;
    private boolean planUpdated = true;
    private boolean halted = false;
    private long expectedLastPoint;

    private final FileDownloader downloader;

    @Autowired
    private CollectPlanner(DownloadMonitor monitor, CollectPlanService service, DownloadListService downloadListService, FileDownloader downloader) throws ServiceException, MalformedURLException {
        this.downloader = downloader;
        if(service==null || monitor==null || downloadListService==null)
            throw new BeanInitializationException("service injection failed");

        this.service = service;
        this.downloadListService = downloadListService;
        service.addNotifier(notifyUpdate);
        this.start();
    }

    @Override
    public void run() {
        log.info("CollectPlanner start");

        try {
            while(!service.isReady())
                sleep(10000);

            while(true) {
                // halted flag means there are no space anymore to collect files.
                if(!halted) {
                    CollectPlanVo plan = getNext();
                    if (plan == null) {
                        sleep(5000);
                        continue;
                    }
                    if (plan.getNextAction().before(new Date(System.currentTimeMillis())))
                        collect(plan);
                }
                sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            log.error("file or thread operation failed");
            e.printStackTrace();
        }
    }

    private boolean collect(CollectPlanVo plan) throws InterruptedException, IOException {
        log.info("collect: "+plan.toString());

        // change the status of this plan to 'collecting'
        service.setLastStatus(plan, PlanStatus.collecting);
        PlanStatus lastStatus = PlanStatus.collected;

        // figure out a list the plan has to collect.
        List<DownloadForm> downloadList = createDownloadList(plan);
        int totalFiles = downloadList.stream().mapToInt(item -> item.getFiles().size()).sum();

        if (totalFiles != 0) {
            String downloadId = downloader.addRequest(downloadList);
            while (downloader.getStatus(downloadId).equalsIgnoreCase("in-progress"))
                sleep(500);

            if (!downloader.getStatus(downloadId).equalsIgnoreCase("done")) {
                log.error("file download failed [planId=" + plan.getId() + "]");
                lastStatus = PlanStatus.suspended;
            } else {
                // copy downloaded files to the plan's directory.
                int copied = copyFiles(plan, downloader.getBaseDir(downloadId));
                log.info("updated files=" + copied);
                if (copied == 0) {
                    log.info("collection complete.. but no updated files");
                } else {
                    // compress files.
                    String outputPath = compress(plan);
                    if (outputPath == null) {
                        log.error("failed to pack logs");
                        lastStatus = PlanStatus.suspended;
                    } else {
                        downloadListService.insert(plan, outputPath);
                        plan.setLastPoint(new Timestamp(expectedLastPoint));
                        log.info("plan " + plan.getPlanName() + " " + copied + " files collecting success");
                    }
                }
                // stop flag might be changed while the plan was on work.
                // if someone stopped the plan, we have to update status and don't have to reschedule.
                if (planUpdated) {
                    CollectPlanVo dbPlan = service.getPlan(plan.getId());
                    if (dbPlan == null || dbPlan.isStop()) {
                        log.info("stopped plan on work");
                        plan.setStop(true);
                        service.setLastStatus(plan, lastStatus);
                        service.updateLastCollect(plan);
                        return true;
                    }
                }
            }
        } else {
            log.info("no files to collect");
        }
        // update status and reschedule the plan.
        service.setLastStatus(plan, lastStatus);
        service.updateLastCollect(plan);
        service.schedulePlan(plan);
        return true;
    }

    private List<DownloadForm> createDownloadList(CollectPlanVo plan) {
        List<DownloadForm> downloadList = new ArrayList<>();
        String[] tools = plan.getTool().split(",");
        String[] types = plan.getLogType().split(",");
        String[] typeStrs = plan.getLogTypeStr().split(",");
        long lastTime = plan.getLastPoint().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        log.info("createDownloadList: tools="+tools.length+" types="+types.length+" lastPoint="+dateFormat.format(lastTime));

        Calendar from = Calendar.getInstance();
        from.setTimeInMillis(lastTime+1000);
        Calendar to = Calendar.getInstance();
        if(plan.getEnd().before(new Timestamp(System.currentTimeMillis()))) {
            to.setTimeInMillis(plan.getEnd().getTime());
        } else {
            to.setTimeInMillis(System.currentTimeMillis());
        }

        boolean updateLastPoint = true;
        for(String tool: tools) {
            tool = tool.trim();
            for(int i=0; i<types.length; ++i) {
                boolean ret = downloader.createDownloadFileList(downloadList, "undefined", tool, types[i].trim(),
                        typeStrs[i].trim(), from, to, "");
                if(ret==false) {
                    // That form is null means that a timeout has occurred on createFileList.
                    log.error("cannot create download filelist for "+tool+"/"+types[i]);
                    updateLastPoint = false;
                    // There is only 1 last-point for a plan.
                    // it means if createFileList error occurred,
                    // updating last-point is possible to causes some omission logs for a tool which has an error.
                    // That's why it doesn't update a last-point at this point.
                }
            }
        }
        int totalFiles = downloadList.stream().mapToInt(item->item.getFiles().size()).sum();
        if(updateLastPoint) {
            for(DownloadForm form: downloadList) {
                for(FileInfo file: form.getFiles()) {
                    if (expectedLastPoint < file.getMilliTime())
                        expectedLastPoint = file.getMilliTime();
                }
            }
        } else {
            expectedLastPoint = plan.getLastPoint().getTime();
        }

        log.info(String.format("totalFiles=%d (%s~%s) lastPoint=%s",
                totalFiles,
                new Timestamp(from.getTimeInMillis()).toString(),
                new Timestamp(to.getTimeInMillis()).toString(),
                dateFormat.format(expectedLastPoint)));
        return downloadList;
    }

    private CollectPlanVo getNext() {
        if(planUpdated) {
            nextPlan = service.getNextPlan();
            if(nextPlan==null)
                return null;
            if(nextPlan!=null) {
                if(nextPlan.isStop()) {
                    log.info("all plans stopped");
                    nextPlan = null;
                    return null;
                } else {
                    if (nextPlan.getNextAction() != null)
                        log.info("nextPlan=" + nextPlan.getPlanName() + " nextaction=" + nextPlan.getNextAction().toString());
                    planUpdated = false;
                }
            }
        }
        return nextPlan.getNextAction()!=null?nextPlan:null;
    }

    private int copyFiles(CollectPlanVo plan, @NonNull String tmpDir) throws IOException {
        Path planPath = Paths.get(planRootDir, String.valueOf(plan.getId()));
        log.info("copyFiles(from="+tmpDir+" to="+planPath.toString()+")");
        File planRoot = planPath.toFile();
        if(!planRoot.exists()) {
            planRoot.mkdirs();
        }
        File inRoot = Paths.get(tmpDir).toFile();
        return copyFile(inRoot, inRoot, planRoot, 0);
    }

    private int copyFile(File in, final File inRoot, final File outRoot, int copied) throws IOException {
        if(in.isDirectory()) {
            File[] inFiles = in.listFiles();
            if (inFiles != null) {
                for (File f : inFiles) {
                    copied = copyFile(f, inRoot, outRoot, copied);
                }
            }
        } else if(in.isFile()) {
            String subPath = in.getAbsolutePath().substring(inRoot.getAbsolutePath().length());

            File outPath = Paths.get(outRoot.toString(), subPath).toFile();
            File outParent = outPath.getParentFile();
            if(!outParent.exists()) {
                outParent.mkdirs();
            }
            if(!outPath.exists()) {
                FileCopyUtils.copy(in, outPath);
                return copied+1;
            }
            log.info(outPath.getName()+" is already exist");
        }
        return copied;
    }

    private String compress(CollectPlanVo plan) {
        log.info("compress");
        File dir = Paths.get(planRootDir, String.valueOf(plan.getId())).toFile();
        if(dir.exists()==false) {
            log.error("sequence error. no files to compress");
            return null;
        }
        Compressor compressor = new Compressor();
        compressor.addExcludeExtension("zip");
        String zipName = plan.getId()+"_"+System.currentTimeMillis()+".zip";
        Path zipPath = Paths.get(dir.toString(), zipName);
        if(compressor.compress(dir.toString(), zipPath.toString())) {
            log.info("compressing success "+"["+zipName+"]");
        } else {
            log.info("compressing failed");
        }
        log.info("compress done ("+zipName+")");
        return zipPath.toString();
    }

    public void halt() {
        halted = true;
    }

    public void restart() {
        halted = false;
    }

    private Runnable notifyUpdate = ()->{
        planUpdated = true;
    };

    private final Log log = LogFactory.getLog(getClass());
}
