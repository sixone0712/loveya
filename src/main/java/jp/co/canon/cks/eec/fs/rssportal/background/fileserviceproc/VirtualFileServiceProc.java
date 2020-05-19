package jp.co.canon.cks.eec.fs.rssportal.background.fileserviceproc;

import jp.co.canon.cks.eec.fs.rssportal.background.FileDownloadContext;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class VirtualFileServiceProc extends RemoteFileServiceProc {

    private File achFile;

    public VirtualFileServiceProc(FileDownloadContext context) {
        super(context);
    }

    @Override
    protected void setUrl(String url) {
        context.setLocalFilePath(url);
    }

    @Override
    protected String getUrl() {
        return context.getLocalFilePath();
    }

    @Override
    void transfer() {
        log.info("transfer()");
        File inFile = new File(context.getLocalFilePath());
        if(inFile.exists()==false || inFile.isDirectory()) {
            log.error("couldn't find a achieve file");
            return;
        }

        File outDir = new File(context.getOutPath());
        outDir.mkdirs();

        String lastName = inFile.getName();
        Path outPath = Paths.get(context.getOutPath(), lastName);

        byte[] buf = new byte[1024];
        try {
            InputStream is = new FileInputStream(inFile);
            OutputStream os = new FileOutputStream(outPath.toFile());
            while((is.read(buf))>0) {
                os.write(buf);
                os.flush();
            }
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("transfer done [filename="+outPath.toString()+"]");
        achFile = outPath.toFile();
    }

    @Override
    void extract() {
        log.info("extract [achieve="+achFile.getAbsolutePath()+")");
        File zipFile = achFile;

        if(zipFile.toString().endsWith(".zip")) {
            String dir = parseDir(zipFile.toString());
            if(zipFile.exists()==false || zipFile.isDirectory()) {
                log.error("wrong achieve file");
                return;
            }

            try {
                byte[] buf = new byte[1024];
                ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
                ZipEntry entry = zis.getNextEntry();
                while(entry!=null) {
                    File tmpFile = new File(dir, entry.getName());
                    FileOutputStream fos = new FileOutputStream(tmpFile);
                    while(zis.read(buf)>0) {
                        fos.write(buf);
                    }
                    fos.close();
                    entry = zis.getNextEntry();
                }
                zis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            zipFile.delete();
        }
    }
}
