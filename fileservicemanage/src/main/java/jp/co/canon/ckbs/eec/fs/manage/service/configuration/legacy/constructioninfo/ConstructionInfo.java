package jp.co.canon.ckbs.eec.fs.manage.service.configuration.legacy.constructioninfo;

import jp.co.canon.ckbs.eec.fs.manage.service.configuration.legacy.SaxParserFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConstructionInfo {
    List<Equipment> equipmentList;
    List<Fab> fabList;
    Map<String, Ots> otsMap;

    File infoFile;

    long timestamp;

    public ConstructionInfo(File file){
        this.infoFile = file;
        loadFile();
    }

    void parseFile(SAXParser parser, ConstructionInfoSaxHandler handler, File file){
        try {
            parser.parse(file, handler);
            equipmentList = handler.getEquipmentList();
            fabList = handler.getFabList();
            otsMap = handler.getOtsMap();
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    void loadFile(){
        SAXParser parser = SaxParserFactory.createParser();
        ConstructionInfoSaxHandler handler = new ConstructionInfoSaxHandler();
        assert parser != null;
        parseFile(parser, handler, infoFile);
    }

    synchronized void load(){
        if (infoFile.lastModified() > timestamp){
            loadFile();
            timestamp = infoFile.lastModified();
        }
    }

    public Equipment[] getEquipmentList(){
        load();
        return equipmentList.toArray(new Equipment[0]);
    }

    public Ots getEquipmentOts(String name){
        load();
        String otsName = null;
        for (Equipment eq : equipmentList){
            if (eq.getName().equals(name)){
                otsName = eq.getOtsName();
                break;
            }
        }
        return otsMap.get(otsName);
    }

    public Ots[] getAllOts(){
        load();
        return otsMap.values().toArray(new Ots[0]);
    }
}
