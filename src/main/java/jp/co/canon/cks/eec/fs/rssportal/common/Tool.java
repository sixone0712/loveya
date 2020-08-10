package jp.co.canon.cks.eec.fs.rssportal.common;

import lombok.NonNull;

import java.io.File;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

public class Tool {

    static public int getCollectTypeNumber(String collectType) {
        if(collectType==null || collectType.isEmpty()) {
            return -1;
        }
        switch (collectType) {
            case "cycle": return 1;
            case "continuous": return 2;
            default:
        }
        return -1;
    }

    static public String toCSVString(List<String> list) {
        StringBuilder sb = new StringBuilder("");
        boolean comma = false;
        for(String item: list) {
            if(comma==false) {
                comma = true;
            } else {
                sb.append(",");
            }
            sb.append(item);
        }
        return sb.toString();
    }

    static public void deleteDir(File file) {
        File[] contents = file.listFiles();
        if(contents!=null) {
            for(File f: contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    static public void deleteDir(File file, Predicate<File> filter) {
        File[] contents = file.listFiles();
        if(contents!=null) {
            for(File f: contents) {
                deleteDir(f, filter);
            }
        }
        if(!filter.test(file)) {
            file.delete();
        }
    }

    static public SimpleDateFormat getSimpleDateFormat() {
        return new SimpleDateFormat("yyyyMMddHHmmss");
    }

    static public SimpleDateFormat getVFtpSimpleDateFormat() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss");
    }

    static public String getFtpTimeFormat(Timestamp ts) {
        if(ts==null)
            return null;
        return getSimpleDateFormat().format(ts.getTime());
    }

    static public String getVFtpTimeFormat(Timestamp ts) {
        if(ts==null)
            return null;
        return getVFtpSimpleDateFormat().format(ts.getTime());
    }
}
