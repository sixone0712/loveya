package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.manage.FileInfoModel;
import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.manage.FileServiceManageServiceLocator;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceUsedSOAP;
import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListService;
import jp.co.canon.cks.eec.fs.rssportal.downloadlist.DownloadListVo;
import jp.co.canon.cks.eec.fs.rssportal.dummy.VirtualFileServiceManagerImpl;
import jp.co.canon.cks.eec.fs.rssportal.dummy.VirtualFileServiceModelImpl;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.service.CollectPlanService;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
import jp.co.canon.cks.eec.fs.rssportal.vo.PlanStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.xml.rpc.ServiceException;
import java.io.File;
import java.io.IOException;
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

    private static final boolean useVirtualFileService = true;
    private static final String planRootDir = "planroot";
    private final CollectPlanService service;
    private final DownloadListService downloadListService;
    private final FileServiceManage fileServiceManage;
    private final FileServiceModel fileService;
    private final DownloadMonitor monitor;
    private CollectPlanVo nextPlan;
    private boolean planUpdated = true;
    private boolean halted = false;

    @Autowired
    private CollectPlanner(DownloadMonitor monitor, CollectPlanService service, DownloadListService downloadListService) throws ServiceException {
        if(service==null || monitor==null || downloadListService==null)
            throw new BeanInitializationException("service injection failed");

        this.monitor = monitor;
        this.service = service;
        this.downloadListService = downloadListService;
        service.addNotifier(notifyUpdate);
        service.scheduleAllPlans();

        if(useVirtualFileService) {
            fileService = new VirtualFileServiceModelImpl();
            fileServiceManage = new VirtualFileServiceManagerImpl();
            createVirtualFiles();
        } else {
            fileService = new FileServiceUsedSOAP(DownloadConfig.FCS_SERVER_ADDR);
            FileServiceManageServiceLocator serviceLocator = new FileServiceManageServiceLocator();
            fileServiceManage = serviceLocator.getFileServiceManage();
        }
        this.start();
    }

    private void createVirtualFiles() {
        if(fileServiceManage instanceof VirtualFileServiceManagerImpl) {
            long cur = System.currentTimeMillis();
            Date from = new Date(cur);
            Date to = new Date(cur + (24 * 3600 * 1000));
            String[] tools = {"EQVM88", "EQVM87"};
            String[] types = {"001", "002", "003", "004", "005"};

            ((VirtualFileServiceManagerImpl) fileServiceManage).createVfs(from, to, 60000, tools, types);
        } else {
            throw new BeanInitializationException("couldn't resolve fileService type");
        }
    }

    @Override
    public void run() {
        log.info("AutoCollector start");

        try {
            while(true) {
                if(!halted) {
                    CollectPlanVo plan = getNext();
                    if (plan == null) {
                        sleep(5000);
                        continue;
                    }

                    Date cur = new Date(System.currentTimeMillis());
                    if (plan.getNextAction().before(cur)) {
                        collect(plan);
                    }
                }
                sleep(1000);
            }
        } catch (InterruptedException e) {
            log.error("sleep exception occurs");
            e.printStackTrace();
        } catch (RemoteException e) {
            log.error("failed to access esp web service");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("file operation failed");
            e.printStackTrace();
        }
    }

    private boolean collect(CollectPlanVo plan) throws InterruptedException, IOException {

        log.info("collect: "+plan.toString());
        service.setLastStatus(plan, PlanStatus.collecting);
        List<DownloadForm> downloadList = createDownloadList(plan);

        int totalFiles = downloadList.stream().mapToInt(item -> item.getFiles().size()).sum();
        if(totalFiles!=0) {
            String jobType = useVirtualFileService?"virtual":"auto";
            FileDownloadExecutor executor = new FileDownloadExecutor(
                    jobType,
                    plan.getPlanName(),
                    fileServiceManage,
                    fileService,
                    downloadList,
                    false);
            executor.setMonitor(monitor);
            executor.start();

            while(executor.isRunning())
                sleep(100);
            if(!executor.getStatus().equalsIgnoreCase("complete")) {
                log.error("file download failed [planId="+plan.getId()+"]");
                service.setLastStatus(plan, PlanStatus.suspended);
            } else {
                int copied = copyFiles(plan, executor.getBaseDir());
                if(copied==0) {
                    log.info("collection complete.. but no updated files");
                    service.setLastStatus(plan, PlanStatus.collected);
                    service.updateLastCollect(plan);
                } else {
                    String outputPath = compress(plan);
                    if (outputPath == null) {
                        log.error("failed to pack logs");
                        service.setLastStatus(plan, PlanStatus.suspended);
                    } else {
                        service.setLastStatus(plan, PlanStatus.collected);
                        downloadListService.insert(plan, outputPath);
                        service.updateLastCollect(plan);
                        log.info("plan " + plan.getPlanName()+" "+copied+" files collecting success");
                    }
                }
            }
        }
        service.schedulePlan(plan);
        return true;
    }

    private List<DownloadForm> createDownloadList(CollectPlanVo plan) throws RemoteException {
        List<DownloadForm> downloadList = new ArrayList<>();
        String[] tools = plan.getTool().split(",");
        String[] types = plan.getLogType().split(",");
        String[] typeStrs = plan.getLogTypeStr().split(",");
        log.info("createDownloadList: tools="+tools.length+" types="+types.length);

        long lastTime = plan.getLastPoint().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        Calendar from = Calendar.getInstance();
        from.setTimeInMillis(lastTime-1000);
        Calendar to = Calendar.getInstance();
        to.setTimeInMillis(System.currentTimeMillis());

        for(String tool: tools) {
            tool = tool.trim();
            for(int i=0; i<types.length; ++i) {
                String type = types[i].trim();
                String typeStr = typeStrs[i].trim();
                DownloadForm form = new DownloadForm("undefined", tool, type, typeStr);
                //downloadList.add(form);

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
                downloadList.add(form);
            }
        }
        int totalFiles = downloadList.stream().mapToInt(item->item.getFiles().size()).sum();
        log.info("totalFiles="+totalFiles+" lastpoint="+new Timestamp(lastTime).toString());
        plan.setLastPoint(new Timestamp(lastTime));
        return downloadList;
    }

    private CollectPlanVo getNext() {
        if(planUpdated) {
            nextPlan = service.getNextPlan();
            if(nextPlan!=null && nextPlan.getNextAction()!=null) {
                log.info("nextPlan=" + nextPlan.getDescription() + " nextaction=" + nextPlan.getNextAction().toString());
            }
            if(nextPlan!=null) {
                planUpdated = false;
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
            if(outPath.exists()) {
                log.info(outPath.getName()+" is already exist");
                return copied;
            }
            FileCopyUtils.copy(in, outPath);
        }
        return copied+1;
    }

    private String compress(CollectPlanVo plan) {
        log.info("compress");
        File dir = Paths.get(planRootDir, String.valueOf(plan.getId())).toFile();
        if(dir.exists()==false) {
            log.error("sequence error. no files to compress");
            return null;
        }
        /* Planner doesn't delete result files here.
           Let's implement another process to clean old files up.

        File[] files = dir.listFiles();
        File oldTmp = null, oldZip = null;
        for(File file: files) {
            if(file.isFile()) {
                String fileName = file.getName();
                if(fileName.endsWith(".tmp"))
                    oldTmp = file;
                else if(fileName.endsWith(".zip"))
                    oldZip = file;
            }
        }
        if(oldTmp!=null)
            oldTmp.delete();
        if(oldZip!=null) {
            File rename = new File(oldZip.getAbsolutePath()+".tmp");
            oldZip.renameTo(rename);
        }
        */
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
