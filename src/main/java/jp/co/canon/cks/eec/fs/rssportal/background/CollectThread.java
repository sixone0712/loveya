package jp.co.canon.cks.eec.fs.rssportal.background;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;

public class CollectThread extends Thread {

    private final Log log = LogFactory.getLog(getClass());
    private final int no;
    private CollectProcess runner;
    private Timestamp runningStartTime;
    private Timestamp runningFinishTime;
    private Boolean lock;

    public CollectThread(int threadNo) {
        super();
        this.no = threadNo;
        lock = false;
    }

    @Override
    public void run() {
        if(runner==null) {
            log.error("no runner");
            return;
        }
        runningStartTime = new Timestamp(System.currentTimeMillis());
        runner.run();
        runningFinishTime = new Timestamp(System.currentTimeMillis());
    }

    public int getNo() {
        return no;
    }

    public boolean isLocked() {
        return lock.booleanValue();
    }

    public void lock() {
        synchronized (lock) {
            lock = true;
        }
    }

    public void unlock() {
        synchronized (lock) {
            lock = false;
        }
    }

    public void setRunner(CollectProcess runner) {
        this.runner = runner;
    }

    public boolean isAvailable() {
        if(isLocked()) {
            return false;
        }
        return true;
    }

}
