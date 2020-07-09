package jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc;

import jp.co.canon.cks.eec.fs.rssportal.background.DownloadMonitor;
import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloadContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;

import java.util.function.Consumer;

public abstract class FileServiceProc extends Thread {

    protected final FileDownloadContext context;
    protected DownloadMonitor monitor;
    protected int completed;
    protected final Log log;
    protected Consumer<FileServiceProc> notifyError;
    protected Consumer<FileServiceProc> notifyFinish;

    protected FileServiceProc(@NonNull FileDownloadContext context, @NonNull Class clazz) {
        log = LogFactory.getLog(clazz);
        this.context = context;
        this.completed = 1; // 1 means it is on work.
    }

    abstract void register();
    abstract boolean download();
    abstract void transfer();
    abstract void extract();

    @Override
    public void run() {
        log.info("running");
        register();
        if(!download()) {
            log.info(context.getRequestNo()+" download failed");
            completed = -1; // negative means an error occurs.
            notifyCall(notifyError);
            return;
        }
        transfer();
        extract();
        completed = 0;
        notifyCall(notifyFinish);
    }

    protected void notifyCall(Consumer notifier) {
        if(notifier!=null) {
            notifier.accept(this);
        }
    }

    public int getCompleted() {
        return completed;
    }

    public void setMonitor(@NonNull DownloadMonitor monitor) {
        this.monitor = monitor;
    }

    public void setNotifyError(Consumer<FileServiceProc> notifyError) {
        this.notifyError = notifyError;
    }

    public void setNotifyFinish(Consumer<FileServiceProc> notifyFinish) {
        this.notifyFinish = notifyFinish;
    }
}
