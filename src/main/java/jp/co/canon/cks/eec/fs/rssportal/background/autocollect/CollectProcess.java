package jp.co.canon.cks.eec.fs.rssportal.background.autocollect;

import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnector;
import jp.co.canon.cks.eec.fs.rssportal.background.*;
import jp.co.canon.cks.eec.fs.rssportal.common.Tool;
import jp.co.canon.cks.eec.fs.rssportal.dao.CollectionPlanDao;
import jp.co.canon.cks.eec.fs.rssportal.model.FileInfo;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.PlanStatus;
import lombok.Getter;
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

public abstract class CollectProcess implements Runnable {

    protected final long aDayMillis = 24*60*60000;

    @Getter
    protected CollectPlanVo plan;

    private final PlanManager manager;
    private final CollectionPlanDao dao;
    private final FileDownloader downloader;
    protected final Log log;
    protected final FileServiceManageConnector connector;
    private final String planName;

    private CollectThread thread;
    private boolean threading;
    private Runnable notifyJobDone;

    protected List<DownloadRequestForm> requestList;
    protected List<String> failMachines;
    protected long requestFiles;

    private String downloadId;
    private long updatedFiles;

    protected long currentMillis;
    private Timestamp jobStartTime;
    private Timestamp jobDoneTime;
    private Timestamp syncTime;
    private boolean stop;
    private boolean kill;

    private long expectedLastPoint;

    private CollectPipe[] pipes = {this::_listFiles, this::_download, this::_copyFiles, this::_compress};

    /**
     * This method has to set `requestList` and `requestFiles` on success.
     * @throws CollectException
     * @throws InterruptedException     When the manager(parent) asks stop operation.
     */
    abstract protected void createDownloadFileList() throws CollectException, InterruptedException;
    abstract protected Timestamp getLastPoint();
    abstract protected Timestamp getNextPlan();

    public CollectProcess(PlanManager manager, CollectPlanVo plan, CollectionPlanDao dao, FileDownloader downloader,
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
        this.connector = downloader.getConnector();
        syncTime = getTimestamp();
        schedule();
        push();
    }

    private void startProc() {
        printInfo("start collecting");
        requestList = new ArrayList<>();
        failMachines = new ArrayList<>();
        requestFiles = 0;
        downloadId = null;
        updatedFiles = 0;
        currentMillis = System.currentTimeMillis();
        jobStartTime = getTimestamp();
        jobDoneTime = null;
    }

    private void doneProc() {
        printInfo("collecting done");
        printInfo("lastPoint="+Tool.getFtpTimeFormat(plan.getLastPoint()));
        requestList = null;
        failMachines = null;
        requestFiles = -1;
        jobDoneTime = getTimestamp();
        threading = false;
        clearLogFiles();
        notifyJobDone.run();
    }

    private void _listFiles() throws CollectException {
        printInfo("listFiles");
        String status = plan.getLastStatus();
        if(status.equals("completed") || status.equals("halted")) {
            throw new CollectException(plan, "collecting status failed");
        }

        try {
            createDownloadFileList();
        } catch (InterruptedException e) {
            printInfo("stop collecting");
            __exit0();
        }
    }

    private void _download() throws CollectException {
        printInfo("download");
        if(requestList==null)
            throw new CollectException(plan, "null collect file list");
        if(requestFiles==0) {
            printInfo("no files to collect");
            __exit0();
        }

        CollectType collectType = CollectType.valueOf(plan.getPlanType());
        downloadId = downloader.addRequest(collectType, requestList);

        String status;
        do {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                printInfo("stop downloading");
                __exit0();
            }
            status = downloader.getStatus(downloadId);
        } while(status.equalsIgnoreCase("in-progress"));

        if(!status.equalsIgnoreCase("done")) {
            printError("file download failed");
            throw new CollectException(plan, "file download failed");
        }
    }

    private void _copyFiles() throws CollectException {
        printInfo("copyFiles");
        if(downloadId==null || downloadId.isEmpty())
            throw new CollectException(plan, "null downloadId");

        try {
            updatedFiles = copyFiles(plan, downloader.getBaseDir(downloadId));
        } catch (IOException e) {
            printError("copying files failed");
            throw new CollectException(plan, "copying files failed");
        }
        printInfo("collecting complete. copied="+updatedFiles);
    }

    private void _compress() throws CollectException {
        printInfo("compress updatedFiles="+updatedFiles);
        if(updatedFiles>0) {
            String out = compress(plan);
            if (out == null) {
                log.error("compressing failed");
                throw new CollectException(plan, "compressing failed");
            }
            manager.addCollectLog(plan, out);
            printInfo("output="+out);
        }
    }

    private void __exit0() throws CollectException {
        throw new CollectException(plan, false);
    }

    @Override
    public void run() {

        startProc();
        setStatus(PlanStatus.collecting);

        try {
            for(CollectPipe pipe: pipes) {
                pipe.run();
            }
            printInfo("all pipe finished");
            setStatus(PlanStatus.collected);
            Timestamp lastPoint = getLastPoint();
            if(lastPoint!=null) {
                plan.setLastPoint(lastPoint);
            }
        } catch (CollectException e) {
            if(e.isError()) {
                printError(e.getMessage());
                setStatus(PlanStatus.suspended);
            } else {
                setStatus(PlanStatus.collected);
                Timestamp lastPoint = getLastPoint();
                if(lastPoint!=null) {
                    plan.setLastPoint(lastPoint);
                }
            }
        }

        if(stop) {
            plan.setStop(true);
        }

        plan.setLastCollect(getTimestamp());
        schedule();
        updateStatus();
        push();
        doneProc();
    }

    private void pull() {
        if(!isChangeable()) {
            printError("pull failed in collecting");
            return;
        }
        plan = dao.find(plan.getId());
        syncTime = getTimestamp();
    }

    private void push() {
        if(!isChangeable()) {
            printError("push failed in collecting");
            return;
        }
        dao.update(plan);
        syncTime = getTimestamp();
    }

    private void updateStatus() {
        String lastStatus = plan.getLastStatus();
        if(plan.isStop() || lastStatus.equalsIgnoreCase(PlanStatus.completed.name())
                || lastStatus.equalsIgnoreCase(PlanStatus.halted.name())) {
            plan.setStatus("stop");
        } else {
            plan.setStatus("running");
        }
    }

    public void setStop(boolean val) {
        stop = val;
        plan.setStop(stop);
        updateStatus();
        schedule();
        dao.updateStop(plan.getId(), stop);
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

    public Thread getThread() {
        return thread;
    }

    public boolean isStop() {
        return stop;
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

    private void setStatus(PlanStatus status) {
        plan.setLastStatus(status.name());
        plan.setDetail(status.name());
    }

    private void schedule() {
        Timestamp planning;
        if(stop || isExpired()) {
            planning = null;
        } else {
            Timestamp lastCollectedTime = plan.getLastCollect();
            if(lastCollectedTime==null) {
                // In this case, collect it asap.
                planning = getTimestamp();
            } else if(lastCollectedTime.after(plan.getEnd())) {
                printInfo("collecting completed");
                setStatus(PlanStatus.completed);
                planning = null;
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(lastCollectedTime.getTime());
                if(getCollectBase()>plan.getLastPoint().getTime()) {
                    cal.add(Calendar.MINUTE, 1);
                } else {
                    long interval = plan.getCollectionType()==1/*COLLECTTYPE_CYCLE*/?plan.getInterval():60000;
                    cal.add(Calendar.MILLISECOND, (int) interval);
                }
                planning = new Timestamp(cal.getTimeInMillis());
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

    public void stop() {
        printInfo("stop");
        if(!threading) {
            setStop(true);
            //push();
        } else {
            stop = true;
            if(thread.isAlive()) {
                printInfo("request interrupt");
                thread.interrupt();
            }
        }
    }

    private int copyFiles(CollectPlanVo plan, @NonNull String tmpDir) throws IOException {
        Path planPath = Paths.get(manager.getCollectRoot(), String.valueOf(plan.getId()));
        printInfo("copyFiles(from="+tmpDir+" to="+planPath.toString()+")");
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

    private boolean isChangeable() {
        String status = plan.getLastStatus();
        if(status!=null && status.equalsIgnoreCase(PlanStatus.collecting.name())) {
            return false;
        }
        return true;
    }

    private boolean isExpired() {
        String detail = plan.getDetail();
        if(detail==null || detail.equalsIgnoreCase(PlanStatus.completed.name()))
            return true;
        return false;
    }

    protected Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    protected long getMidnightMillis(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE),0, 0, 0);
        return cal.getTimeInMillis();
    }

    protected boolean isSameDay(long a, long b) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(a);
        c2.setTimeInMillis(b);
        if(c1.get(Calendar.YEAR)==c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH)==c2.get(Calendar.MONTH) &&
                c1.get(Calendar.DATE)==c2.get(Calendar.DATE))
            return true;
        return false;
    }

    private void clearLogFiles() {
        clearLogStorage(true);
    }

    private void clearAllFiles() {
        clearLogStorage(false);
    }

    private void clearLogStorage(boolean filter) {
        if(threading) {
            printError("failed to clear storage");
            return;
        }
        Path path = Paths.get(manager.getCollectRoot(), String.valueOf(plan.getId()));
        if(filter) {
            Tool.deleteDir(path.toFile(), file -> {
                if (file.getName().endsWith(".zip"))
                    return true;
                return false;
            });
        } else {
            Tool.deleteDir(path.toFile());
        }
    }

    protected void printInfo(String str) {
        if(log!=null) {
            String formed = String.format("[%s] %s", planName, str);
            log.info(formed);
        }
    }

    protected void printError(String str) {
        if(log!=null) {
            String formed = String.format("[%s] %s", planName, str);
            log.error(formed);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append(String.format("[%s] ", plan.getPlanType()));
        sb.append(plan.getPlanName()).append(" : ");
        sb.append(plan.getLastStatus()).append(" : ");
        sb.append(plan.getNextAction());
        return sb.toString();
    }

    public boolean modifyPlan(CollectPlanVo plan) {
        printInfo("modifyPlan");
        this.plan.setNextAction(null);

        clearLogFiles();

        this.plan.setPlanName(plan.getPlanName());
        this.plan.setFab(plan.getFab());
        this.plan.setTool(plan.getTool());
        this.plan.setLogType(plan.getLogType());
        this.plan.setLogTypeStr(plan.getLogTypeStr());
        this.plan.setCollectionType(plan.getCollectionType());
        this.plan.setInterval(plan.getInterval());
        this.plan.setCollectStart(plan.getCollectStart());
        this.plan.setStart(plan.getStart());
        this.plan.setEnd(plan.getEnd());
        this.plan.setLastPoint(plan.getStart());
        this.plan.setLastStatus(PlanStatus.registered.name());
        this.plan.setDescription(plan.getDescription());
        this.plan.setOwner(plan.getOwner());
        this.plan.setCommand(plan.getCommand());
        this.plan.setDirectory(plan.getDirectory());

        schedule();
        dao.updatePlan(plan);
        return true;
    }

    public boolean deletePlan() {
        printInfo("deletePlan");
        boolean result = dao.deletePlan(plan.getId());
        if(result) {
            this.plan.setNextAction(null);
            clearAllFiles();
        }
        return result;
    }

    protected long getCollectBase() {
        Timestamp _base = plan.getCreated().after(plan.getCollectStart())?plan.getCreated():plan.getCollectStart();
        Calendar base = Calendar.getInstance();
        base.setTimeInMillis(_base.getTime());
        base.set(Calendar.HOUR_OF_DAY, 0);
        base.set(Calendar.MINUTE, 0);
        base.set(Calendar.SECOND, 0);
        base.set(Calendar.MILLISECOND, 0);
        return base.getTimeInMillis();
    }
}
