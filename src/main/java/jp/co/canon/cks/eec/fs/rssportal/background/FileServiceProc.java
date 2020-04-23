package jp.co.canon.cks.eec.fs.rssportal.background;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;

public abstract class FileServiceProc extends Thread {

    protected final FileDownloadContext context;
    protected DownloadMonitor monitor;
    protected boolean completed;
    protected final Log log;

    protected FileServiceProc(@NonNull FileDownloadContext context, @NonNull Class clazz) {
        log = LogFactory.getLog(clazz);
        this.context = context;
        this.completed = false;
    }

    abstract void register();
    abstract void download();
    abstract void transfer();
    abstract void extract();

    @Override
    public void run() {
        log.info("running");
        register();
        download();
        transfer();
        extract();
        completed = true;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setMonitor(@NonNull DownloadMonitor monitor) {
        this.monitor = monitor;
    }
}
