package jp.co.canon.cks.eec.fs.rssportal.background;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compressor {

    private final Log log = LogFactory.getLog(getClass());

    public boolean compress(@NonNull final String sourceDir,
                            @NonNull final String destFile) {

        File src = new File(sourceDir);
        if(src.isFile() || src.exists()==false) {
            return false;
        }

        String destDir = parseDir(destFile);
        File destFileDir = new File(destDir);
        if(destFileDir.exists()==false) {
            destFileDir.mkdirs();
        }

        File dest = new File(destFile);
        if(dest.exists()) {
            dest.delete();
        }

        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dest));
            Path srcPath = Paths.get(sourceDir);
            Files.walk(srcPath)
                    .filter(path->Files.isDirectory(path)==false)
                    .forEach(path->{
                        ZipEntry entry = new ZipEntry(srcPath.relativize(path).toString());
                        try {
                            zos.putNextEntry(entry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            log.error("error! failed to put zip entries");
                            e.printStackTrace();
                        }
                    });
            zos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean extract(@NonNull final String sourceFile,
                           @NonNull final String destDir) {
        return false;
    }

    private String parseDir(@NonNull final String file) {
        String sep = File.separator;
        int idx = file.lastIndexOf(sep);
        if(idx==-1) {
            return "";
        }
        return file.substring(0, idx);
    }

}
