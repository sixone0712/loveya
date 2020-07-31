package jp.co.canon.cks.eec.fs.rssportal.background;

import jp.co.canon.cks.eec.util.ftp.FTP;
import jp.co.canon.cks.eec.util.ftp.FTPException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.*;
import java.net.SocketTimeoutException;
import java.util.List;

public class FtpWorker implements Closeable {

    private final Log log = LogFactory.getLog(getClass());
    private FTP ftp;

    final private String host;
    final private int port;
    final private boolean activeMode;
    private String user;
    private String pass;
    private List<Job> jobs;

    private class Job {
        String source;
        String destination;
    }

    public FtpWorker(@NonNull final String host, int port,
                     @Nullable final String user, @Nullable final String pass,
                     final String ftpMode) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        if(ftpMode.equalsIgnoreCase("active")) {
            this.activeMode = true;
        } else {
            this.activeMode = false;
        }
    }

    public FtpWorker(@NonNull final String host, int port, final String ftpMode) {
        this(host, port, null, null, ftpMode);
    }

    public FtpWorker(@NonNull final String host, int port) {
        this(host, port, null, null, "passive");
    }

    public void open() {
        log.trace("FtpWorker.open()");
        ftp = new FTP(host, port);
        try {
            ftp.connect();
            ftp.login(user, pass);
            ftp.binary();
            ftp.setDataConnectionMode(activeMode?1:2);
        } catch (FTPException | SocketTimeoutException e) {
            log.error("error! ftp connection failed");
            e.printStackTrace();
        }
    }

    public void add(@NonNull final String source, @NonNull final String destination) {
        // TBD
    }
    public void work() {
        // TBD
    }

    public boolean transfer(@NonNull final String source, @NonNull final String destination) {
        log.trace("FtpWorker.transfer()");
        int idx = destination.lastIndexOf(File.separator);
        String destdir;
        if(idx==-1) {
            log.error("error! check destination");
            return false;
        }
        destdir = destination.substring(0, idx);

        log.warn("transfer src="+source+" dest="+destination);
        log.warn("transfer destdir="+destdir);

        File outDir = new File(destdir);
        if(outDir.exists()==false) {
            outDir.mkdirs();
            log.info("output-dir "+destdir+" has been created");
        }
        File outFile = new File(destination);
        if(outFile.exists()) {
            log.info("delete old files");
            outFile.delete();
        }

        try {
            byte[] buf = new byte[256];
            int size;

            InputStream is = ftp.openFileStream(source);
            OutputStream os = new FileOutputStream(outFile);

            while((size=is.read(buf))>0) {
                os.write(buf, 0, size);
                os.flush();
            }
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void close() {
        log.trace("FtpWorker.close()");
        ftp.close();
    }
}
