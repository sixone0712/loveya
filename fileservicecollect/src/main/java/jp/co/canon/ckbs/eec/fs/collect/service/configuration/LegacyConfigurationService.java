package jp.co.canon.ckbs.eec.fs.collect.service.configuration;

import jp.co.canon.ckbs.eec.fs.configuration.legacy.logcollectdefinition.Log;
import jp.co.canon.ckbs.eec.fs.configuration.legacy.logcollectdefinition.LogCollectDefinition;
import jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist.ObjectList;
import jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LegacyConfigurationService implements ConfigurationService {
    String configDirectory;

    ObjectList objectList;

    Map<String, FtpServerInfo> ftpServerInfoMap = new HashMap<>();
    Map<String, LogCollectDefinition> logCollectDefinitionMap = new HashMap<>();
    public LegacyConfigurationService(@Value("${fileservice.configDirectory}") String configDirectory){
        this.configDirectory = configDirectory;
    }

    @PostConstruct
    void postConstruct(){
        File definitionDir = new File(configDirectory, "definitions");
        File objectListFile = new File(definitionDir, "ObjectList.xml");
        objectList = new ObjectList(objectListFile);
        System.out.println("######### POST CONSTRUCT ########");
        loadFtpServerInfo();
    }

    static String extractHost(String urls){
        if (urls.startsWith("ftp://")){
            urls = urls.substring(6);
        }
        int index = urls.indexOf("/");
        if (index >= 0){
            urls = urls.substring(0, index);
        }
        return urls;
    }

    void loadFtpServerInfo(){
        List<Tool> toolList = objectList.getToolList();
        for (Tool tool : toolList){
            File logDefFile = new File(configDirectory, "definitions/LogCollectDef_" + tool.getName() + ".xml");
            try {
                LogCollectDefinition def = new LogCollectDefinition(logDefFile);
                logCollectDefinitionMap.put(tool.getName(), def);
                Log log = def.getLogList()[0];
                FtpServerInfo ftpServerInfo = new FtpServerInfo();
                ftpServerInfo.setHost(extractHost(log.getUrls()));
                ftpServerInfo.setUser(log.getUser());
                ftpServerInfo.setPassword(log.getPassword());
                ftpServerInfo.setFtpmode(log.getFtpmode());
                ftpServerInfoMap.put(tool.getName(), ftpServerInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public FtpServerInfo getFtpServerInfo(String machine){
        return ftpServerInfoMap.get(machine);
    }

}

