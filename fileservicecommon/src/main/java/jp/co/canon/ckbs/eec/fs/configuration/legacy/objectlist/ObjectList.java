package jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist;

import jp.co.canon.ckbs.eec.fs.configuration.legacy.SaxParserFactory;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectList {
    @Getter @Setter
    List<Tool> toolList;

    @Getter @Setter
    List<Ots> otsList;

    @Getter @Setter
    List<Common> commonList;

    @Getter @Setter
    List<FileService> fileServiceList;

    Map<String, FileService> toolFileServiceMap = new HashMap<>();

    long timestamp;
    File objectListFile;

    Map<String, FileService> machineToFileServiceMap = new HashMap<>();

    public ObjectList(File objectListFile){
        this.objectListFile = objectListFile;
        load();
    }

    FileService findFileServiceById(String id){
        for(FileService fileService: fileServiceList){
            if (fileService.getId().equals(id)){
                return fileService;
            }
        }
        return null;
    }

    void buildMachineToFileServiceMap(){
        machineToFileServiceMap.clear();
        FileService fileService;
        for (Tool tool: toolList){
            fileService = findFileServiceById(tool.getCollectFsId());
            machineToFileServiceMap.put(tool.getName(), fileService);
        }
    }

    void buildMachineFileServiceMap(){
        toolFileServiceMap.clear();
        for(Tool tool : toolList){
            if (!toolFileServiceMap.containsKey(tool.getCollectFsId())){
                FileService fileService = findFileServiceById(tool.getCollectFsId());
                toolFileServiceMap.put(fileService.getId(), fileService);
            }
        }
    }

    void parseFile(SAXParser parser, ObjectListSaxHandler handler, File dest){
        try {
            parser.parse(dest, handler);
            this.setToolList(handler.getToolList());
            this.setOtsList(handler.getOtsList());
            this.setCommonList(handler.getCommonList());
            this.setFileServiceList(handler.getFileServiceList());
            buildMachineToFileServiceMap();
            buildMachineFileServiceMap();
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    void loadFile(){
        SAXParser parser = SaxParserFactory.createParser();
        ObjectListSaxHandler handler = new ObjectListSaxHandler();
        assert parser != null;
        parseFile(parser, handler, objectListFile);
    }

    synchronized void load(){
        if (objectListFile.lastModified() > timestamp){
            loadFile();
            timestamp = objectListFile.lastModified();
        }
    }

    public Tool getTool(String name){
        for(Tool tool : toolList){
            if (tool.getName().equals(name)){
                return tool;
            }
        }
        return null;
    }

    public FileService getFileServiceByToolName(String toolName){
        Tool tool = getTool(toolName);
        return findFileServiceById(tool.getCollectFsId());
    }

    public FileService[] getAllFileService(){
        return toolFileServiceMap.values().toArray(new FileService[0]);
    }
}
