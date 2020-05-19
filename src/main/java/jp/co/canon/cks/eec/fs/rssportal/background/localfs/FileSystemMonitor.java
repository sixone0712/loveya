package jp.co.canon.cks.eec.fs.rssportal.background.localfs;

import lombok.NonNull;

import java.io.File;

public abstract class FileSystemMonitor extends Thread {

    protected final String monitorName;
    protected final String monitorPath;
    protected final int minFreeSpace;
    protected final int minFreeSpacePercent;
    protected final long interval;

    private boolean halted;

    protected FileSystemMonitor(@NonNull String monitorName,
                                @NonNull String monitorPath,
                                int minFreeSpace,
                                int minFreeSpacePercent,
                                long interval) {
        this.monitorName = monitorName;
        this.monitorPath = monitorPath;
        this.minFreeSpace = minFreeSpace;
        this.minFreeSpacePercent = minFreeSpacePercent;
        this.interval = interval;
        this.start();
    }

    @Override
    public void run() {
        halted = false;
        File target = new File(monitorPath);
        try {
            while(!target.exists()) {
                sleep(interval);
            }
            if(target.isFile()) {
                errorHandler("monitoring target is not directory");
                return;
            }

            while(true) {
                int level = checkFreeSpace(target);
                if(halted) {
                    if(level==0) {
                        halted = false;
                        restart();
                    } else {
                        cleanup();
                    }
                } else {
                    if (level >= 2) {
                        halt();
                        halted = true;
                        continue;
                    }
                    if(level>0 || checkSpecial())
                        cleanup();
                }
                sleep(interval);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            errorHandler("interrupted exception occurs");
            return;
        }
    }

    private int checkFreeSpace(@NonNull File target) {
        long total = target.getTotalSpace();
        long usable = target.getUsableSpace();
        report(total, usable);
        if(gigabytes(usable)<2)
            return 2;
        if(gigabytes(usable)<minFreeSpace || percent(total, usable)<minFreeSpacePercent)
            return 1;
        return 0;
    }

    protected long gigabytes(long bytes) {
        final long div = 1024*1024*1024;
        return bytes/div;
    }

    protected int percent(long total, long usable) {
        return total!=0?(int)(usable/total):0;
    }

    abstract protected boolean checkSpecial();
    abstract protected void cleanup();
    abstract protected void restart();
    abstract protected void halt();
    abstract protected boolean errorHandler(String error);
    abstract protected void report(long total, long usable);

}
