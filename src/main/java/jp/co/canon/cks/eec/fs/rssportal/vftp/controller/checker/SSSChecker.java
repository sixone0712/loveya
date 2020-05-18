package jp.co.canon.cks.eec.fs.rssportal.vftp.controller.checker;

public class SSSChecker {
    ContextChecker contextChecker = null;

    private SSSChecker(ContextChecker checker){
        this.contextChecker = checker;
    }

    public static SSSChecker fromDirectoryName(String directory){
        if (directory == null){
            return null;
        }
        String[] data = directory.split("-");

        if (data.length != 3 && data.length != 4){
            return null;
        }

        String dataTypeName = data[0];
        String fromTime = data[1];
        String toTime = data[2];
        String context = null;
        if (data.length == 4){
            context = data[3];
        }
        if (!isValidDataTypeName(dataTypeName)){
            return null;
        }
        if (!TimeChecker.isValidFromTimeToTimeString(fromTime, toTime)){
            return null;
        }
        if (context == null){
            return new SSSChecker(null);
        }
        ContextChecker contextChecker = ContextChecker.fromString(context);
        if (contextChecker == null)
        {
            return null;
        }
        return new SSSChecker(contextChecker);
    }

    private static boolean isValidDataTypeName(String dataTypeName){
        if (dataTypeName.equals("IP_AS_RAW")){
            return true;
        }
        if (dataTypeName.equals("IP_AS_RAW_ERR")){
            return true;
        }
        if (dataTypeName.equals("IP_AS_BMP")){
            return true;
        }
        if (dataTypeName.equals("IP_AS_BMP_ERR")){
            return true;
        }
        return false;
    }

    public String getDeviceName(){
        if (this.contextChecker == null){
            return null;
        }
        return this.contextChecker.getDeviceName();
    }
}