package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.fs.portal.bean.RequestInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.RequestListBean;
import jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel;
import jp.co.canon.cks.eec.fs.portal.bussiness.ServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class DownloadMonitor extends Thread {


    private class Target {
        FileServiceModel service;
        String system;
        String tool;
        String requestNo;
        RequestInfoBean reqInfo;
        RequestInfoBean downloadInfo;

        long ts;
        boolean activated;
        Consumer<Integer> updateDownloadFiles;

        private Target(FileServiceModel service, String system, String tool, String requestNo,
                       RequestInfoBean reqInfo, RequestInfoBean downloadInfo, long ts, Consumer<Integer> updateDownloadFiles) {
            this.service = service;
            this.system = system;
            this.tool = tool;
            this.requestNo = requestNo;
            this.reqInfo = reqInfo;
            this.downloadInfo = downloadInfo;
            this.ts = ts;
            this.activated = true;
            this.updateDownloadFiles = updateDownloadFiles;
        }
    }

    private List<Target> targets = new ArrayList<>();

    public DownloadMonitor() {
        this.start();
    }

    @Override
    public void run() {
        log.info("download monitor start");
        try {
            while(true) {
                synchronized (targets) {
                    if (targets.size() == 0) {
                        sleep(500);
                    }

                    for (Target target : targets) {
                        if (target.activated) {
                            synchronized (target) {
                                try {
                                    RequestListBean downloadList = target.service.createDownloadList(target.system,
                                            target.tool, target.requestNo);
                                    if (downloadList != null) {
                                        for (Object item : downloadList.getRequestList()) {
                                            RequestInfoBean bean = (RequestInfoBean) item;
                                            if (bean.getRequestNo().equalsIgnoreCase(target.requestNo)) {
                                                target.downloadInfo = bean;
                                                target.updateDownloadFiles.accept(bean.getFileListCount());
                                            }
                                        }
                                    }
                                    /* deprecated
                                    RequestListBean requestList = target.service.createRequestList(
                                            target.system, target.tool, target.requestNo);
                                    if (requestList != null) {
                                        target.reqInfo = requestList.get(target.requestNo);
                                        if (target.reqInfo != null) {
                                            target.ts = System.currentTimeMillis();
                                        }
                                    }
                                     */
                                } catch (ServiceException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("download monitor exited with exception");
        }
    }

    private Target getTarget(String system, String tool, String requestNo) {
        synchronized (targets) {
            for (Target target : targets) {
                if (target.system.equals(system) && target.tool.equals(tool) && target.requestNo.equals(requestNo)) {
                    return target;
                }
            }
        }
        return null;
    }

    public void add(
            @NonNull final String system,
            @NonNull final String tool,
            @NonNull final String requestNo,
            @NonNull final FileServiceModel service,
            @NonNull final Consumer<Integer> updateDownloadFiles) {

        log.info("monitor.add(system="+system+" tool="+tool+" reqNo="+requestNo+")");
        if(getTarget(system, tool, requestNo)==null) {
            try {
                RequestListBean requestList = service.createDownloadList(system, tool, requestNo);
                RequestInfoBean inf = requestList.get(requestNo);
                synchronized (targets) {
                    targets.add(
                            new Target(service, system, tool, requestNo, inf, null,
                            System.currentTimeMillis(), updateDownloadFiles)
                    );
                }
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete(@NonNull final String system, @NonNull final String tool, @NonNull final String requestNo) {
        Target target = getTarget(system, tool, requestNo);
        if(target!=null) {
            synchronized (targets) {
                //targets.remove(target);
                target.activated = false;
            }
        }
    }

    /*
    public RequestInfoBean get(@NonNull final String system, @NonNull final String tool, @NonNull final String requestNo) {
        Target target = getTarget(system, tool, requestNo);
        return target!=null?target.reqInfo:null;
    }

     */

    public RequestInfoBean getDownloadInfo(@NonNull final String system, @NonNull final String tool, @NonNull final String requestNo) {
        Target target = getTarget(system, tool, requestNo);
        return target!=null?target.downloadInfo:null;
    }

    private final Log log = LogFactory.getLog(getClass());
}
