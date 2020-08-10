package jp.co.canon.cks.eec.fs.rssportal.background.autocollect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Timestamp;

public class CollectThread extends Thread {

    private final Log log = LogFactory.getLog(getClass());
    private final int no;
    private CollectProcess runner;
    private Timestamp runningStartTime;
    private Timestamp runningFinishTime;

    public CollectThread(int threadNo) {
        super();
        this.no = threadNo;
    }

    @Override
    public void run() {
        if(runner==null) {
            log.error("no runner");
            return;
        }
        runningStartTime = getTimestamp();
        runner.run();
        runningFinishTime = getTimestamp();
    }

    public int getNo() {
        return no;
    }

    public void setRunner(CollectProcess runner) {
        this.runner = runner;
    }

    private Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
