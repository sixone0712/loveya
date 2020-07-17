package jp.co.canon.ckbs.eec.fs.configuration.legacy.logconfiguration;

import java.io.File;
import java.io.FileFilter;

public class LogConfigurationLoader {
    private static final String COMPO_DIR_TOOL = "Tools/${toolName}/";
    private static final String COMPO_DIR_TYPE = "ToolTypes/${toolType}/";
    private static final String COMPO_DIR_ALL = "All/";

    File rootDir;

    public LogConfigurationLoader(File rootDir){
        this.rootDir = rootDir;
    }

    File getConfigurationDir(String tool, String toolType){
        File toolDir = new File(rootDir, COMPO_DIR_TOOL.replace("${toolName}", tool));
        if (toolDir.isDirectory()){
            return toolDir;
        }
        File toolTypeDir = new File(rootDir, COMPO_DIR_TYPE.replace("${toolType}", toolType));
        if (toolTypeDir.isDirectory()){
            return toolTypeDir;
        }
        File allDir = new File(rootDir, COMPO_DIR_ALL);
        if (allDir.isDirectory()){
            return allDir;
        }
        return null;
    }

    public LogConfiguration loadLogConfiguration(String tool, String toolType, String logId){
        File commandDir = getConfigurationDir(tool, toolType);
        if (!commandDir.exists()){
            return null;
        }
        File[] dirList = commandDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith(logId + "_");
            }
        });
        if (dirList.length > 0){
            File confFile = new File(dirList[0], "configuration.xml");
            if (confFile.exists()){
                try {
                    return LogConfiguration.load(confFile);

                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }
        return null;
    }
}
