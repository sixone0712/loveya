package jp.co.canon.cks.eec.fs.rssportal.background;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CollectThreadPool {

    private final Log log = LogFactory.getLog(getClass());

    @Value("${rssportal.collect.max-threads}")
    private int maxCollectThreads;

    private CollectThread[] pool;
    private int runningThreads;

    @PostConstruct
    private void postContrcutor() {
        log.info("initialize collect thread pool ("+maxCollectThreads+" threads)");
        pool = new CollectThread[maxCollectThreads];
        for(int i=0; i<pool.length; ++i) {
            pool[i] = new CollectThread(i);
        }
        runningThreads = 0;
    }

    public int execute(CollectProcess runner) {
        if(runner==null) {
            log.error("null runner");
            return -1;
        }
        CollectThread thread = getEmptyThread();
        if(thread==null) {
            log.warn("there isn't a thread available");
            return -1;
        }
        thread.setRunner(runner);
        thread.start();
        return thread.getNo();
    }

    public CollectThread getThread() {
        CollectThread thread = getEmptyThread();
        if(thread==null) {
            return null;
        }
        thread.lock();
        return thread;
    }

    public void putThread(CollectThread thread) {
        if(thread==null) {
            return;
        }
        try {
            thread.join();
            thread.setRunner(null);
            thread.unlock();
        } catch (InterruptedException e) {
            log.error("thread join failed");
            e.printStackTrace();
        }
    }

    private CollectThread getEmptyThread() {
        for(CollectThread thread: pool) {
            if(thread.isAvailable()) {
                return thread;
            }
        }
        return null;
    }

}
