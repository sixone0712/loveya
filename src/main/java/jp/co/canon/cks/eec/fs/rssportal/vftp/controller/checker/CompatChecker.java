package jp.co.canon.cks.eec.fs.rssportal.vftp.controller.checker;

public class CompatChecker {
    ContextChecker contextChecker = null;

    private CompatChecker(ContextChecker checker){
        this.contextChecker = checker;
    }

    public static CompatChecker fromFilename(String filename){
        String[] data = filename.split("-");
        if (data.length < 2 || data.length > 5){
            return null;
        }

        String fromTime = data[0];
        String toTime = data[1];
        if(!TimeChecker.isValidFromTimeToTimeString(fromTime, toTime)){
            return null;
        }
        
        boolean unit_exits = false;
        boolean data_exits = false;
        boolean context_exists = false;

        ContextChecker contextChecker = null;

        for (int idx = 2; idx < data.length; ++idx){
            String str = data[idx];
            if (str.startsWith("UI_")){
                if (unit_exits){
                    return null;
                }
                unit_exits = true;
                continue;
            }
            if (str.startsWith("L_")){
                if (data_exits){
                    return null;
                }
                data_exits = true;
                continue;
            }
            if (context_exists){
                return null;
            }
            contextChecker = ContextChecker.fromString(str);
            if (contextChecker == null){
                return null;
            }
            context_exists = true;
        }
        
        return new CompatChecker(contextChecker);
    }

    public String getDeviceName(){
        if (this.contextChecker == null){
            return null;
        }
        return this.contextChecker.getDeviceName();
    }
/*
    public static boolean isValidFilename(String filename){
        String[] data = filename.split("-");
        if (data.length < 2 || data.length > 5){
            return false;
        }

        String fromTime = data[0];
        String toTime = data[1];
        if(!TimeChecker.isValidFromTimeToTimeString(fromTime, toTime)){
            return false;
        }
        
        boolean unit_exits = false;
        boolean data_exits = false;
        boolean context_exists = false;

        for (int idx = 2; idx < data.length; ++idx){
            String str = data[idx];
            if (str.startsWith("UI_")){
                if (unit_exits){
                    return false;
                }
                unit_exits = true;
                continue;
            }
            if (str.startsWith("L_")){
                if (data_exits){
                    return false;
                }
                data_exits = true;
                continue;
            }
            if (context_exists){
                return false;
            }
            if (!ContextChecker.isValidContextString(str)){
                return false;
            }
            context_exists = true;
        }
        
        return true;
    }

    public static String getDeviceNameFromFileName(String filename){
        if (!isValidFilename(filename)){
            return null;
        }
        String[] data = filename.split("-");
        if (data.length != 4){
            return null;
        }
        String context = data[3];
        return ContextChecker.getDeviceNameFromContextString(context);        
    }
    */
}