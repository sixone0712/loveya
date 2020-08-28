package jp.co.canon.ckbs.eec.fs.manage.service.configuration;

import jp.co.canon.ckbs.eec.fs.configuration.Category;
import jp.co.canon.ckbs.eec.fs.configuration.legacy.constructioninfo.ConstructionInfo;
import jp.co.canon.ckbs.eec.fs.configuration.legacy.constructioninfo.Equipment;
import jp.co.canon.ckbs.eec.fs.configuration.legacy.logcollectdefinition.Log;
import jp.co.canon.ckbs.eec.fs.configuration.legacy.logcollectdefinition.LogCollectDefinition;
import jp.co.canon.ckbs.eec.fs.configuration.legacy.logcollectdefinition.LogCollectDefinitionList;
import jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist.FileService;
import jp.co.canon.ckbs.eec.fs.configuration.legacy.logconfiguration.LogConfiguration;
import jp.co.canon.ckbs.eec.fs.configuration.legacy.logconfiguration.LogConfigurationLoader;
import jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist.NetworkDL;
import jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist.ObjectList;
import jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Qualifier("legacy")
public class LegacyConfigurationServiceImpl implements ConfigurationService{
    @Value("${fileservice.constructionServiceDirectory}")
    String constructionServiceDirectory;

    @Value("${fileservice.fileServiceDirectory}")
    String fileServiceDirectory;

    ObjectList objectList;
    LogCollectDefinitionList logCollectDefinitionList;
    ConstructionInfo constructionInfo;
    LogConfigurationLoader logConfigurationLoader;

    @PostConstruct
    void postConstruct() throws Exception{
        File commandsDir = new File(fileServiceDirectory, "commands");
        File definitionsDir = new File(fileServiceDirectory, "definitions");

        objectList = new ObjectList(new File(definitionsDir, "ObjectList.xml"));
        logCollectDefinitionList = new LogCollectDefinitionList(definitionsDir);

        constructionInfo = new ConstructionInfo(new File(constructionServiceDirectory, "ConstructionInfo.xml"));
        logConfigurationLoader = new LogConfigurationLoader(commandsDir);
    }

    @Override
    public Machine[] getMachineList() {
        Equipment[] equipmentList = constructionInfo.getEquipmentList();
        Machine[] machines = new Machine[equipmentList.length];
        for(int idx = 0; idx < equipmentList.length; ++idx){
            Equipment equipment = equipmentList[idx];
            machines[idx] = new Machine(equipment.getName(), equipment.getFabName());
        }
        return machines;
    }

    @Override
    public Category[] getCategories(String machineName) {
        Tool tool = objectList.getTool(machineName);
        if (tool == null){
            return new Category[0];
        }
        LogCollectDefinition logDef = logCollectDefinitionList.getLogCollectDefinition(machineName);
        try {
            List<Category> categoryList = new ArrayList<>();
            Log[] logList = logDef.getLogList();
            for(Log log: logList){
                LogConfiguration conf = logConfigurationLoader.loadLogConfiguration(tool.getName(), tool.getToolType(), log.getKind());
                if (conf != null){
                    categoryList.add(new Category(log.getKind(), conf.getName()));
                }
            }
            return categoryList.toArray(new Category[0]);
        } catch (Exception e) {
            return new Category[0];
        }
    }

    public String getFileServiceHost(String machineName){
        FileService fileService = objectList.getFileServiceByToolName(machineName);
        if (fileService != null){
            return fileService.getHost();
        }
        return null;
    }

    @Override
    public String getFileServiceDownloadUrlPath(String machineName, String filePath){
        FileService fileService = objectList.getFileServiceByToolName(machineName);
        if (fileService != null){
            NetworkDL networkDL = fileService.getNetworkDL();
            StringBuilder builder = new StringBuilder();
            builder.append(networkDL.getUrlPrefix())
                    .append("/")
                    .append(filePath)
                    .append("<")
                    .append(networkDL.getUser())
                    .append("/")
                    .append(networkDL.getPassword());
            String ftpmode = networkDL.getFtpmode();
            if (ftpmode != null && !ftpmode.isEmpty()){
                builder.append("/")
                        .append(networkDL.getFtpmode());

            }
            builder.append(">");

            return builder.toString();
        }
        return null;
    }

    public String[] getAllFileServiceHost(){
        FileService[] fileServicesList = objectList.getAllFileService();
        String[] otsHostList = new String[fileServicesList.length];
        for(int idx = 0; idx < fileServicesList.length; ++idx){
            otsHostList[idx] = fileServicesList[idx].getHost();
        }
        return otsHostList;
    }
}
