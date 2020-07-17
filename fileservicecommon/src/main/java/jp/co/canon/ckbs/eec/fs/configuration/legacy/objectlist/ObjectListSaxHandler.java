package jp.co.canon.ckbs.eec.fs.configuration.legacy.objectlist;

import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class ObjectListSaxHandler extends DefaultHandler {

    @Getter
    List<Tool> toolList = new ArrayList<>();
    @Getter
    List<Ots> otsList = new ArrayList<>();
    @Getter
    List<Common> commonList = new ArrayList<>();
    @Getter
    List<FileService> fileServiceList = new ArrayList<>();
    @Getter
    FileService currentFileService = null;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    void addTool(Tool tool){
        if (tool != null){
            toolList.add(tool);
        }
    }

    void addOts(Ots ots){
        if (ots != null){
            otsList.add(ots);
        }
    }

    void addCommon(Common common){
        if (common != null){
            commonList.add(common);
        }
    }

    void addFileService(FileService fileService){
        if (fileService != null){
            fileServiceList.add(fileService);
        }
    }

    void setNetworkDL(NetworkDL networkDL){
        if (currentFileService != null){
            currentFileService.setNetworkDL(networkDL);
        }
    }

    Tool buildTool(Attributes attributes){
        String name = attributes.getValue("name");
        String type = attributes.getValue("toolType");
        String structId = attributes.getValue("structId");
        String collectFsId = attributes.getValue("collectFsId");
        return new Tool(name, type, structId, collectFsId);
    }

    Ots buildOts(Attributes attributes){
        String name = attributes.getValue("name");
        String structId = attributes.getValue("structId");
        String collectFsId = attributes.getValue("collectFsId");
        return new Ots(name, structId, collectFsId);
    }

    Common buildCommon(Attributes attributes){
        String name = attributes.getValue("name");
        String structId = attributes.getValue("structId");
        String collectFsId = attributes.getValue("collectFsId");
        return new Common(name, structId, collectFsId);
    }

    FileService buildFileService(Attributes attributes){
        String id = attributes.getValue("id");
        String name = attributes.getValue("name");
        String host = attributes.getValue("host");
        String structId = attributes.getValue("structId");
        return new FileService(id, name, host, structId);
    }

    NetworkDL buildNetworkDL(Attributes attributes){
        String user = attributes.getValue("user");
        String password = attributes.getValue("password");
        String urlPrefix = attributes.getValue("urlPrefix");

        return new NetworkDL(user, password, urlPrefix);
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (qName.equalsIgnoreCase("Tool")){
            this.addTool(buildTool(attributes));
        }
        if (qName.equalsIgnoreCase("OTS")){
            this.addOts(buildOts(attributes));
        }
        if (qName.equalsIgnoreCase("Common")){
            this.addCommon(buildCommon(attributes));
        }
        if (qName.equalsIgnoreCase("FileService")){
            FileService fileService = buildFileService(attributes);
            this.addFileService(fileService);
            currentFileService = fileService;
        }
        if (qName.equalsIgnoreCase("NetworkDL")){
            this.setNetworkDL(buildNetworkDL(attributes));
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}
