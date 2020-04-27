package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.manage.FileInfoModel;
import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.manage.FileServiceManageServiceLocator;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceUsedSOAP;
import jp.co.canon.cks.eec.fs.rssportal.dummy.VirtualFileServiceManagerImpl;
import jp.co.canon.cks.eec.fs.rssportal.dummy.VirtualFileServiceModelImpl;
import jp.co.canon.cks.eec.fs.rssportal.model.DownloadForm;
import jp.co.canon.cks.eec.fs.rssportal.service.CollectPlanService;
import jp.co.canon.cks.eec.fs.rssportal.vo.CollectPlanVo;
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
    private final FileServiceManage fileServiceManage;
    private final FileServiceModel fileService;
    private final DownloadMonitor monitor;
    private CollectPlanVo nextPlan;
    private boolean planUpdated = true;

    @Autowired
    private CollectPlanner(DownloadMonitor monitor, CollectPlanService service) throws ServiceException {
        if(service==null) {
            throw new BeanInitializationException("service injection failed");
        }
        this.monitor = monitor;
        this.service = service;
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
        } catch (IOException e) {
            log.error("file operation failed");
            e.printStackTrace();
        }
    }

    private boolean collect(CollectPlanVo plan) throws InterruptedException, IOException {

        log.info("collect: "+plan.toString());
        List<DownloadForm> downloadList = createDownloadList(plan);

        boolean failed = false;
        int totalFiles = downloadList.stream().mapToInt(item -> item.getFiles().size()).sum();
        if(totalFiles!=0) {
            String jobType = useVirtualFileService?"virtual":"auto";
            FileDownloadExecutor executor = new FileDownloadExecutor(
                    jobType,
                    "",
                    fileServiceManage,
                    fileService,
                    downloadList,
                    false);
            executor.setMonitor(monitor);
            executor.start();

            while(executor.isRunning()) sleep(100);
            if(!executor.getStatus().equalsIgnoreCase("complete")) {
                log.error("file download failed [planId="+plan.getId()+"]");
                failed = true;
            } else {
                copyFiles(plan, executor.getBaseDir());
                compress(plan);
            }
        }
        service.updateLastCollect(plan, failed);
        service.schedulePlan(plan);
        return true;
    }

    private List<DownloadForm> createDownloadList(CollectPlanVo plan) throws RemoteException {
        List<DownloadForm> downloadList = new ArrayList<>();
        String[] tools = plan.getTool().split(",");
        String[] types = plan.getLogType().split(",");
        log.info("createDownloadList: tools="+tools.length+" types="+types.length);

        long lastTime = plan.getLastPoint().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        Calendar from = Calendar.getInstance();
        from.setTimeInMillis(lastTime-1000);
        Calendar to = Calendar.getInstance();
        to.setTimeInMillis(System.currentTimeMillis());

        for(String tool: tools) {
            tool = tool.trim();
            for(String type: types) {
                type = type.trim();
                DownloadForm form = new DownloadForm(tool, type);
                downloadList.add(form);

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
            if(nextPlan!=null) {
                log.info("nextPlan=" + nextPlan.getDescription() + " nextaction=" + nextPlan.getNextAction().toString());
            }
            if(nextPlan!=null) {
                planUpdated = false;
            }
        }
        return nextPlan;
    }

    private void copyFiles(CollectPlanVo plan, @NonNull String tmpDir) throws IOException {
        Path planPath = Paths.get(planRootDir, String.valueOf(plan.getId()));
        log.info("copyFiles(from="+tmpDir+" to="+planPath.toString()+")");
        File planRoot = planPath.toFile();
        if(!planRoot.exists()) {
            planRoot.mkdirs();
        }
        File inRoot = Paths.get(tmpDir).toFile();
        copyFile(inRoot, inRoot, planRoot);
    }

    private void copyFile(File in, final File inRoot, final File outRoot) throws IOException {
        if(in.isDirectory()) {
            File[] inFiles = in.listFiles();
            if (inFiles != null) {
                for (File f : inFiles) {
                    copyFile(f, inRoot, outRoot);
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
                return;
            }
            FileCopyUtils.copy(in, outPath);
        }
    }

    private void compress(CollectPlanVo plan) {
        log.info("compress");
        File dir = Paths.get(planRootDir, String.valueOf(plan.getId())).toFile();
        if(dir.exists()==false) {
            log.error("sequence error. no files to compress");
            return;
        }
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

        Compressor compressor = new Compressor();
        String zipName = plan.getId()+".zip";
        Path zipPath = Paths.get(dir.toString(), zipName);
        if(compressor.compress(dir.toString(), zipPath.toString())) {
            log.info("compressing success "+"["+zipName+"]");
        } else {
            log.info("compressing failed");
        }
        log.info("compress done ("+zipName+")");
    }

    private Runnable notifyUpdate = ()->{
        planUpdated = true;
    };

    public File getZipPath(int planId) {
        CollectPlanVo plan = service.getPlan(planId);
        if(plan==null || plan.getLastCollect()==null)
            return null;

        File zip = null;
        File[] files = Paths.get(planRootDir, String.valueOf(plan.getId())).toFile().listFiles();
        for(File file: files) {
            if(file.isFile() && file.getName().endsWith(".zip")) {
                zip = file;
                break;
            }
        }
        if(zip==null)
            return null;
        return zip;
    }

    private final Log log = LogFactory.getLog(getClass());
}
