package jp.co.canon.cks.eec.fs.rssportal.dummy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VirtualFileStorage {

    public class VirtualFile {
        public String filename;
        public Date created;
        public long size;
    }

    private String base;
    private byte[] trash4096;
    private List<VirtualFile> virtStorage;

    public VirtualFileStorage() {
        base = null;
        trash4096 = new byte[4096];
        for(int i=0; i<trash4096.length; ++i) {
            trash4096[i] = (byte)((int)'0'+(i%10));
        }
        virtStorage = new ArrayList<>();
    }

    public InputStream getFile(@NonNull String filename, long size, @Nullable Date createdTime) {

        if(isReady()==false) {
            log.error("null base error");
            return null;
        }

        Path path = Paths.get(base, filename);
        File file = path.toFile();
        if(file.exists()) {
            log.error("file exists!");
            return null;
        }

        if((size&0xfff)!=0) {
            size = (size+0xfff)&(~0xfff);
            log.info("do size alignment(size="+size+")");
        }

        try {
            OutputStream fos = new FileOutputStream(file);
            for(int i=0; i<(size/4096); ++i) {
                fos.write(trash4096);
                fos.flush();
            }
            fos.close();

            if(createdTime!=null) {
                BasicFileAttributeView attr = Files.getFileAttributeView(path, BasicFileAttributeView.class);
                FileTime filetime = FileTime.fromMillis(createdTime.getTime());
                attr.setTimes(filetime, filetime, filetime);
            }
            return new FileInputStream(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int createVirtualFiles(@NonNull Date from, @NonNull Date to, long interval /* millis */, @Nullable String prefix) {

        if(isReady()==false) {
            return -1;
        }

        if(interval<60000 || to.before(from)) {
            log.error("input error");
            return -1;
        }
        long period = to.getTime()-from.getTime();
        int files = (int) (period/interval);

        if(prefix==null) {
            prefix = "nogroup";
        }

        long millis = from.getTime();
        for(int i=0; i<files; ++i, millis+=interval) {
            if(i>=100) {
                break;
            }
            StringBuilder sb = new StringBuilder(prefix);
            sb.append("_").append(i+1).append("_").append(System.nanoTime());

            VirtualFile vf = new VirtualFile();
            vf.filename = sb.toString();
            vf.created = new Date(millis);
            vf.size = 4096*((i&0xf)+1);

            virtStorage.add(vf);
        }

        return virtStorage.size();
    }

    public List<VirtualFileStorage.VirtualFile> getFileList(@Nullable String prefix, @Nullable Date from, @Nullable Date to) {

        if(virtStorage==null || virtStorage.size()==0)
            return null;

        if(from==null)
            from = new Date(0);
        if(to==null)
            to = new Date(System.currentTimeMillis());

        List<VirtualFile> list = new ArrayList<>();
        for(VirtualFile vf: virtStorage) {
            if(vf.filename.startsWith(prefix)
                    && vf.created.before(to)
                    && vf.created.after(from)) {
                list.add(vf);
            }
        }
        return list;
    }

    private boolean isReady() {
        if(base!=null) {
            File file = new File(base);
            if(file.exists()==false || file.isDirectory()==false) {
                log.error("base configuration error");
                return false;
            }

            return true;
        }
        return false;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        File file = new File(base);
        if(file.exists() && file.isFile()) {
            log.error("base file exists");
            return;
        }
        if(file.exists()==false) {
            file.mkdirs();
        }
        this.base = base;
    }

    private Log log = LogFactory.getLog(getClass());
}
