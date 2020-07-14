package jp.co.canon.ckbs.eec.fs.manage.service.configuration.legacy.constructioninfo;

import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstructionInfoSaxHandler extends DefaultHandler {

    Ots currentOts = null;
    Fab currentFab = null;

    @Getter
    Map<String, Equipment> machineMap = new HashMap<>();

    @Getter
    Map<String, Fab> fabMap = new HashMap<>();

    @Getter
    Map<String, Ots> otsMap = new HashMap<>();

    @Getter
    List<Fab> fabList = new ArrayList<>();

    @Getter
    List<Equipment> equipmentList = new ArrayList<>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("Line")){
            String fabName = attributes.getValue("name");
            Fab fab = new Fab();
            fab.setName(fabName);
            fabMap.put(fabName, fab);
            fabList.add(fab);
            currentFab = fab;
            return;
        }
        if (qName.equals("Equipment")){
            Equipment equipment = buildEquipment(attributes);
            machineMap.put(equipment.getId(), equipment);
            equipmentList.add(equipment);
            if (currentFab != null){
                currentFab.addMachine(equipment);
                equipment.setFabName(currentFab.getName());
            }
            return;
        }
        if (qName.equals("OTS")){
            Ots ots = buildOts(attributes);
            otsMap.put(ots.getName(), ots);
            currentOts = ots;
            return;
        }
        if (qName.equals("EqDef")){
            String ref = attributes.getValue("ref");
            if (currentOts != null){
                Equipment equipment = machineMap.get(ref);
                if (equipment != null){
                    currentOts.addEquipment(equipment);
                    equipment.setOtsName(currentOts.getName());
                }
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("Line")){
            currentFab = null;
            return;
        }
        if (qName.equals("OTS")){
            currentOts = null;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    Equipment buildEquipment(Attributes attributes){
        String id = attributes.getValue("id");
        String name = attributes.getValue("name");
        String version = attributes.getValue("version");
        String host = attributes.getValue("host");
        String ftpmode = attributes.getValue("ftpmode");
        Equipment equipment = new Equipment();
        equipment.setId(id);
        equipment.setName(name);
        equipment.setVersion(version);
        equipment.setHost(host);
        equipment.setFtpmode(ftpmode);
        return equipment;
    }

    Ots buildOts(Attributes attributes){
        String name = attributes.getValue("name");
        String version = attributes.getValue("version");
        String host = attributes.getValue("host");
        Ots ots = new Ots();
        ots.setName(name);
        ots.setVersion(version);
        ots.setHost(host);
        return ots;
    }
}
