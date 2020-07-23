package jp.co.canon.ckbs.eec.fs.configuration.legacy.logcollectdefinition;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LogCollectDefinitionList {
    File rootDir;

    Map<String, LogCollectDefinition> logCollectDefinitionMap = new HashMap<>();
    public LogCollectDefinitionList(File rootDir){
        this.rootDir = rootDir;
    }

    public LogCollectDefinition getLogCollectDefinition(String machineName){
        LogCollectDefinition logCollectDefinition = logCollectDefinitionMap.get(machineName);
        if (logCollectDefinition == null){
            File file = new File(this.rootDir, "LogCollectDef_" + machineName +".xml");
            if (!file.exists()){
                return null;
            }
            LogCollectDefinition logCollectDef = null;
            try {
                logCollectDef = new LogCollectDefinition(file);
                logCollectDefinitionMap.put(machineName, logCollectDef);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return logCollectDef;
        }
        return logCollectDefinition;
    }
}
