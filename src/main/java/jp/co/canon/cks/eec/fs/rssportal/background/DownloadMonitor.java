package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.ckbs.eec.fs.collect.controller.param.FtpDownloadRequestListResponse;
import jp.co.canon.ckbs.eec.fs.collect.model.FtpDownloadRequest;
import jp.co.canon.ckbs.eec.fs.collect.model.VFtpCompatDownloadRequest;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnector;
import jp.co.canon.ckbs.eec.fs.manage.FileServiceManageConnectorFactory;
import jp.co.canon.cks.eec.fs.portal.bean.RequestInfoBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class DownloadMonitor extends Thread {

    @Value("${rssportal.file-service-manager.addr}")
    private String fileServiceAddress;

    private final Log log = LogFactory.getLog(getClass());
    private final FileServiceManageConnectorFactory connectorFactory;
    private FileServiceManageConnector connector;
    private List<Target> targets;

    @Autowired
    public DownloadMonitor(FileServiceManageConnectorFactory connectorFactory) {
        this.connectorFactory = connectorFactory;
    }

    @PostConstruct
    public void __initialize() {
        log.info("DownloadMonitor initialize");
        connector = connectorFactory.getConnector(fileServiceAddress);
        targets = new ArrayList<>();
        this.start();
    }

    private class Target {
        String protocol;
        String machine;
        String requestNo;
        FtpDownloadRequest ftp;

        long timestamp;
        boolean activated;
        Consumer<Long> updateDownloadFiles;

        private Target(String machine, String requestNo, String protocol, Consumer<Long> updateDownloadFiles) {
            this.machine = machine;
            this.requestNo = requestNo;
            this.protocol = protocol;
            this.timestamp = 0;
            this.activated = true;
            this.updateDownloadFiles = updateDownloadFiles;
        }
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
                                if(target.protocol.equals("ftp")) {
                                    log.info(String.format("## %s/%s", target.requestNo, target.machine));
                                    FtpDownloadRequestListResponse response = connector.getFtpDownloadRequestList(target.machine, target.requestNo);
                                    if(response.getErrorCode()!=null) {
                                        log.error("error ("+response.getErrorCode()+") on getting download information " +
                                                "(machine="+target.machine+" request="+target.requestNo+")");
                                        continue;
                                    }
                                    for(FtpDownloadRequest r: response.getRequestList()) {
                                        if(r.getRequestNo().equals(target.requestNo)) {
                                            target.ftp = r;
                                            target.timestamp = System.currentTimeMillis();
                                            target.updateDownloadFiles.accept(r.getDownloadedFileCount());
                                        }
                                    }
                                } else {
                                    // Todo  support vftp
                                }
                            }
                        } else {
                            // Todo  delete the deactivated target.
                        }
                        sleep(1000);
                    }
                }
                sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("download monitor exited with exception");
        }
    }

    private Target getTarget(String machine, String requestNo) {
        synchronized (targets) {
            for (Target target : targets) {
                if (target.machine.equals(machine) && target.requestNo.equals(requestNo)) {
                    return target;
                }
            }
        }
        return null;
    }

    public void add(String machine, String requestNo, String protocol, Consumer<Long> updateDownloadFiles) {
        log.info("monitor.add(machine="+machine+" request="+requestNo+")");
        Target target = getTarget(machine, requestNo);
        if(target==null) {
            targets.add(new Target(machine, requestNo, protocol, updateDownloadFiles));
        }
    }

    public void delete(String machine, String requestNo) {
        Target target = getTarget(machine, requestNo);
        if(target!=null) {
            synchronized (targets) {
                target.activated = false;
            }
        }
    }
}
