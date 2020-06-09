package jp.co.canon.cks.eec.fs.rssportal.background.localfs;

import lombok.NonNull;

import java.io.File;

public abstract class FileSystemMonitor extends Thread {

    protected String monitorName;
    protected String monitorPath;
    protected int minFreeSpace;
    protected int minFreeSpacePercent;
    protected long interval;

    private boolean halted;

    protected FileSystemMonitor(@NonNull String monitorName) {
        this.monitorName = monitorName;
    }

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

    protected void configure(@NonNull String monitorPath, int minFreeSpace, int minFreeSpacePercent, long interval) {
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
            while(!isReady()) {
                sleep(10000);
            }
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

    protected void deleteDir(@NonNull File file) {
        File[] contents = file.listFiles();
        if(contents!=null) {
            for(File f: contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    protected long gigabytes(long bytes) {
        final long div = 1024*1024*1024;
        return bytes/div;
    }

    protected int percent(long total, long usable) {
        return total!=0?(int)(usable*100/total):0;
    }

    protected boolean isReady() {return true;}
    abstract protected boolean checkSpecial();
    abstract protected void cleanup();
    abstract protected void restart();
    abstract protected void halt();
    abstract protected boolean errorHandler(String error);
    abstract protected void report(long total, long usable);

}
