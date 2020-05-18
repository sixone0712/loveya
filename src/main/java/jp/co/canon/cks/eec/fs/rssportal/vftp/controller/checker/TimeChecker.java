package jp.co.canon.cks.eec.fs.rssportal.vftp.controller.checker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeChecker {
    public static Date checkDateString(String dateString){
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        int length = dateString.length();
        if (length != 15 && length != 22){
            return null;
        }
        if (length == 22){
            dateString = dateString.substring(0, 15);
        }
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e){

        }
        return date;
    }

    public static boolean isValidFromTimeToTimeString(String fromTime, String toTime){
        Date fromDate = TimeChecker.checkDateString(fromTime);
        if (fromDate == null){
            return false;
        }
        Date toDate = TimeChecker.checkDateString(toTime);
        if (toDate == null){
            return false;
        }
        return true;
    }
}