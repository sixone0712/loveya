package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.rssportal.dao.CollectionPlanDao;
import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListService;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.model.FileInfo;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.PlanStatus;
import org.apache.commons.logging.Log;
import org.springframework.lang.NonNull;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CollectProcess implements Runnable {

    private CollectPlanVo plan;

    private final PlanManager manager;
    private final CollectionPlanDao dao;
    private final FileDownloader downloader;
    private final Log log;
    private final String planName;

    private CollectThread thread;
    private boolean threading;
    private Runnable notifyJobDone;

    private Timestamp jobStartTime;
    private Timestamp jobDoneTime;
    private Timestamp syncTime;
    private boolean stop;
    private boolean kill;

    private long expectedLastPoint;

    public CollectProcess(
            PlanManager manager,
            CollectPlanVo plan,
            CollectionPlanDao dao,
            FileDownloader downloader,
            Log log ) {
        this.plan = plan;
        this.manager = manager;
        this.dao = dao;
        this.downloader = downloader;
        this.log = log;
        this.planName = String.format("#%d", plan.getId());
        this.threading = false;
        this.stop = plan.isStop();
        this.kill = false;
        syncTime = getTimestamp();
        schedule();
        push();
    }

    private void startProc() {
        printInfo("startProc");
        jobStartTime = getTimestamp();
        jobDoneTime = null;
    }

    private void doneProc() {
        printInfo("doneProc");
        jobDoneTime = getTimestamp();
        threading = false;
        notifyJobDone.run();
    }

    @Override
    public void run() {

        startProc();
        PlanStatus result = PlanStatus.collected;

        setStatus(PlanStatus.collecting);
        List<DownloadForm> downloadList = createDownloadList(plan);
        int totalFiles = downloadList.stream().mapToInt(item->item.getFiles().size()).sum();

        if(totalFiles>0) {
            try {
                String downloadId = downloader.addRequest(downloadList);
                while(downloader.getStatus(downloadId).equalsIgnoreCase("in-progress")) {
                    Thread.sleep(500);
                }

                if(!downloader.getStatus(downloadId).equalsIgnoreCase("done")) {
                    printError("file download failed");
                    result = PlanStatus.suspended;
                } else {
                    int copied = copyFiles(plan, downloader.getBaseDir(downloadId));
                    printInfo("updated "+copied+" files");
                    if(copied==0) {
                        printInfo("collection complete.. but no updated files");
                    } else {
                        String outputPath = compress(plan);
                        if(outputPath==null) {
                            printError("failed to pack logs");
                            result = PlanStatus.suspended;
                        } else {
                            manager.addCollectLog(plan, outputPath);
                            plan.setLastPoint(new Timestamp(expectedLastPoint));
                            printInfo(copied + " files collecting success");
                        }
                    }
                    if(stop || kill) {
                        // LET'S THINK OF THE BETTER WAY!

                    }
                }

            } catch (InterruptedException e) {
                printError("error on collecting");
                e.printStackTrace();
            } catch (IOException e) {
                printError("copyFiles error");
                e.printStackTrace();
            }
        } else {
            printInfo("no files to collect");
        }
        plan.setLastStatus(result.name());
        plan.setLastCollect(getTimestamp());
        schedule();
        push();
        doneProc();
    }

    public void pull() {
        if(!isChangeable()) {
            printError("pull failed in collecting");
            return;
        }
        plan = dao.find(plan.getId());
        syncTime = getTimestamp();
    }

    public void push() {
        if(!isChangeable()) {
            printError("push failed in collecting");
            return;
        }
        dao.updatePlan(plan);
        syncTime = getTimestamp();
    }

    public Timestamp getSchedule() {
        return plan.getNextAction();
    }

    public Timestamp getJobStartTime() {
        return jobStartTime;
    }

    public String getStatus() {
        return plan.getLastStatus();
    }

    public boolean isThreading() {
        return threading;
    }

    public void allocateThreadContainer(CollectThread thread) {
        printInfo("allocateThreadContainer(thread="+thread.getNo()+")");
        if(thread==null) {
            printError("thread is not available");
            return;
        }
        this.thread = thread;
        startCollect();
    }

    public void freeThreadContainer() {
        printInfo("freeThreadContainer");
        if(threading) {
            printError("freeThreadContainer failed");
            return;
        }
        thread = null;
    }

    public void setNotifyJobDone(Runnable notifier) {
        notifyJobDone = notifier;
    }

    private void startCollect() {
        threading = true;
        if(thread==null) {
            return;
        }
        thread.setRunner(this);
        thread.start();
    }

    public void schedule() {
        if(!isChangeable()) {
            printError("failed to schedule the plan");
            return;
        }

        Timestamp planning;
        if(stop || isExpired()) {
            planning = null;
        } else {
            Timestamp lastCollectedTime = plan.getLastCollect();
            if(lastCollectedTime==null) {
                // In this case, collect it asap.
                planning = getTimestamp();
            } else {
                long interval;
                if (plan.getCollectionType() == 1 /*COLLECTTYPE_CYCLE*/) {
                    interval = plan.getInterval();
                } else {
                    interval = 60000; /*CONTINUOUS_DEFAULT_INTERVAL*/
                }
                planning = new Timestamp(lastCollectedTime.getTime() + interval);
            }
        }
        if(planning!=null) {
            if(planning.after(plan.getEnd())) {
                planning = plan.getEnd();
            }
        }
        plan.setNextAction(planning);
        printInfo(toString());
    }

    private int copyFiles(CollectPlanVo plan, @NonNull String tmpDir) throws IOException {
        Path planPath = Paths.get(manager.getCollectRoot(), String.valueOf(plan.getId()));
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
        File dir = Paths.get(manager.getCollectRoot(), String.valueOf(plan.getId())).toFile();
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
                    printError("cannot create download filelist for "+tool+"/"+types[i]);
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

        printInfo(String.format("totalFiles=%d (%s~%s) lastPoint=%s",
                totalFiles,
                new Timestamp(from.getTimeInMillis()).toString(),
                new Timestamp(to.getTimeInMillis()).toString(),
                dateFormat.format(expectedLastPoint)));
        return downloadList;
    }

    private boolean isChangeable() {
        String status = plan.getLastStatus();
        if(status!=null && status.equalsIgnoreCase(PlanStatus.collecting.name())) {
            return false;
        }
        return true;
    }

    private boolean isExpired() {
        if(plan.getDetail().equalsIgnoreCase(PlanStatus.completed.name()))
            return true;
        return false;
    }

    private Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    private void updateSyncTimestamp() {
        syncTime = new Timestamp(System.currentTimeMillis());
    }

    private void updateJobStartTimestamp() {
        jobStartTime = new Timestamp(System.currentTimeMillis());
    }

    private void printInfo(String str) {
        if(log!=null) {
            String formed = String.format("[%s] %s", planName, str);
            log.info(formed);
        }
    }

    private void printError(String str) {
        if(log!=null) {
            String formed = String.format("[%s] %s", planName, str);
            log.error(formed);
        }
    }

    private void setStatus(PlanStatus status) {
        plan.setLastStatus(status.name());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append(plan.getPlanName()).append(" : ");
        sb.append(plan.getLastStatus()).append(" : ");
        sb.append(plan.getNextAction());
        return sb.toString();
    }
}
