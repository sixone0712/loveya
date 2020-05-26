package jp.co.canon.cks.eec.fs.rssportal.vftp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import jp.co.canon.cks.eec.fs.rssportal.vftp.service.ftp.ServerInfo;
import lombok.Getter;
import lombok.Setter;

@Component
public class ServerInfoRepository {
    @Value("${rssportal.vftp.objectlistfile}")
    private String objectListFilePath;

    Map<String, ServerInfo> serverNameToServerInfoMap = new HashMap<>();
    Map<String, ServerInfo> deviceNameToServerInfoMap = new HashMap<>();

    @PostConstruct
    public void reload(){
        String filename = objectListFilePath;

        File f = new File(filename);

        ServerInfoSaxHandler handler = new ServerInfoSaxHandler();

        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setValidating(false);
        parserFactory.setNamespaceAware(false);
        SAXParser parser;
        try {
            parser = parserFactory.newSAXParser();
            parser.parse(f, handler);
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.serverNameToServerInfoMap = handler.getServerNameToServerInfoMap();
        this.deviceNameToServerInfoMap = handler.getDeviceNameToServerInfpMap();
    }

    public String getServerNameByDevice(String deviceName){
        ServerInfo serverInfo = deviceNameToServerInfoMap.get(deviceName);
        if (serverInfo == null){
            return null;
        }
        return serverInfo.getName();
    }

    public ServerInfo getServerInfoByName(String serverName){
        ServerInfo serverInfo = serverNameToServerInfoMap.get(serverName);
        return serverInfo;
    }

    public ServerInfo[] getAllServerInfos(){
        return serverNameToServerInfoMap.values().toArray(new ServerInfo[0]);
    }
}

class ToolInfo{
    private @Getter @Setter String name;
    private @Getter @Setter String structId;
    private @Getter @Setter String type;
    private @Getter @Setter String fsid;
}

class OtsInfo{
    private @Getter @Setter String name;
    private @Getter @Setter String structId;
    private @Getter @Setter String fsid;
}

class DssInfo{
    private @Getter @Setter String name;
    private @Getter @Setter String structId;
    private @Getter @Setter String fsid;
}

class FileServiceInfo {
    private @Getter @Setter String id;
    private @Setter String name;
    private @Setter String host;
    private @Setter String structId;
    private @Setter NetworkDlInfo networkDlInfo;

    private ServerInfo serverInfo = null;

    private static String extractHost(String str){
        String rs = null;
        if (str.startsWith("ftp://")){
            String s = str.substring("ftp://".length());
            int firstSlash = s.indexOf('/');
            if (firstSlash != -1){
                s = s.substring(0, firstSlash);
            }
            int lastColon = s.lastIndexOf(':');
            if (lastColon != -1){
//                String portStr = s.substring(lastColon + 1);
                s = s.substring(0, lastColon);
            }
            rs = s;
        }
        return rs;
    }


    private ServerInfo buildServerInfo(){
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setName(this.name);
        serverInfo.setUsername(networkDlInfo.getUser());
        serverInfo.setPassword(networkDlInfo.getPassword());
        serverInfo.setPort(22001);
        String ftpMode = networkDlInfo.getFtpMode();
        if (ftpMode != null){
            serverInfo.setFtpMode(ftpMode);
        } else {
            serverInfo.setFtpMode("active");
        }
        String ftpUrl = networkDlInfo.getUrlPrefix();
        if (ftpUrl.startsWith("ftp://")){
            serverInfo.setHost(extractHost(ftpUrl));
        }
        else {
            serverInfo.setHost("localhost");
        }
        
        return serverInfo;
    }

    public ServerInfo getServerInfo(){
        if (serverInfo == null){
            serverInfo = this.buildServerInfo();
        }
        return serverInfo;
    }
}

class NetworkDlInfo {
    private @Getter @Setter String user;
    private @Getter @Setter String password;
    private @Getter @Setter String urlPrefix;
    private @Getter @Setter String ftpMode;
}

class ServerInfoSaxHandler extends DefaultHandler {

    private List<OtsInfo> otsList = new ArrayList<>();
    private List<ToolInfo> toolList = new ArrayList<>();

    private Map<String, String> collectionIdMap = new HashMap<>();

    private Map<String, FileServiceInfo> fileServiceMap = new HashMap<>();
    private FileServiceInfo currentFileServiceInfo = null;

    private ToolInfo createTool(Attributes attributes){
        ToolInfo toolInfo = new ToolInfo();
        for(int i = 0; i < attributes.getLength(); ++i){
            String qName = attributes.getQName(i);
            String value = attributes.getValue(i);

            if (qName.equalsIgnoreCase("name")){
                toolInfo.setName(value);
            } else if (qName.equalsIgnoreCase("structid")){
                toolInfo.setStructId(value);
            } else if (qName.equalsIgnoreCase("toolType")){
                toolInfo.setType(value);
            } else if (qName.equalsIgnoreCase("collectfsid")){
                toolInfo.setFsid(value);
            }
        }
        return toolInfo;
    }

    private OtsInfo createOts(Attributes attributes){
        OtsInfo otsInfo = new OtsInfo();
        for (int i = 0; i < attributes.getLength(); ++i){
            String qName = attributes.getQName(i);
            String value = attributes.getValue(i);
            
            if (qName.equalsIgnoreCase("name")){
                otsInfo.setName(value);
            } else if (qName.equalsIgnoreCase("structId")){
                otsInfo.setStructId(value);
            } else if (qName.equalsIgnoreCase("collectfsid")){
                otsInfo.setFsid(value);
            }
        }
        return otsInfo;
    }

    private DssInfo createDss(Attributes attributes){
        DssInfo dssInfo = new DssInfo();
        for (int i = 0; i < attributes.getLength(); ++i){
            String qName = attributes.getQName(i);
            String value = attributes.getValue(i);
            
            if (qName.equalsIgnoreCase("name")){
                dssInfo.setName(value);
            } else if (qName.equalsIgnoreCase("structId")){
                dssInfo.setStructId(value);
            } else if (qName.equalsIgnoreCase("collectfsid")){
                dssInfo.setFsid(value);
            }
        }
        return dssInfo;
    }

    private FileServiceInfo createFileService(Attributes attributes){
        FileServiceInfo info = new FileServiceInfo();
        for (int i = 0; i < attributes.getLength(); ++i){
            String qName = attributes.getQName(i);
            String value = attributes.getValue(i);
            
            if (qName.equalsIgnoreCase("host")){
                info.setHost(value);
            } else if (qName.equalsIgnoreCase("id")){
                info.setId(value);
            } else if (qName.equalsIgnoreCase("name")){
                info.setName(value);
            } else if (qName.equalsIgnoreCase("structid")){
                info.setStructId(value);
            }
        }
        return info;
    }

    private NetworkDlInfo createNetworkDlInfo(Attributes attributes){
        NetworkDlInfo info = new NetworkDlInfo();
        for (int i = 0; i < attributes.getLength(); ++i){
            String qName = attributes.getQName(i);
            String value = attributes.getValue(i);
            
            if (qName.equalsIgnoreCase("urlPrefix")){
                info.setUrlPrefix(value);
            } else if (qName.equalsIgnoreCase("user")){
                info.setUser(value);
            } else if (qName.equalsIgnoreCase("password")){
                info.setPassword(value);
            } else if (qName.equalsIgnoreCase("ftpMode")){
                info.setFtpMode(value);
            }
        }
        return info;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    private void startElementInner(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("tool")){
            ToolInfo toolInfo = createTool(attributes);
            collectionIdMap.put(toolInfo.getName(), toolInfo.getFsid());
            toolList.add(toolInfo);
            return;
        }
        if (qName.equalsIgnoreCase("dss")){
            DssInfo dssInfo = createDss(attributes);
            return;
        }
        if (qName.equalsIgnoreCase("ots")){
            OtsInfo otsInfo = createOts(attributes);
            otsList.add(otsInfo);
            return;
        }
        if (qName.equalsIgnoreCase("fileService")){
            FileServiceInfo info = createFileService(attributes);
            fileServiceMap.put(info.getId(), info);
            currentFileServiceInfo = info;
            return;
        }
        if (qName.equalsIgnoreCase("common")){
            return;
        }
        if (qName.equalsIgnoreCase("networkdl")){
            NetworkDlInfo info = createNetworkDlInfo(attributes);
            if (currentFileServiceInfo != null){
                currentFileServiceInfo.setNetworkDlInfo(info);
            }
            return;
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        startElementInner(uri, localName, qName, attributes);
        super.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endDocument() throws SAXException{
        
    }

    public Map<String, ServerInfo> getServerNameToServerInfoMap(){
        Map<String, ServerInfo> serverNameToServerInfoMap = new HashMap<>();

        for(ToolInfo tool : this.toolList){
            FileServiceInfo fileServiceInfo = fileServiceMap.get(tool.getFsid());
            if (fileServiceInfo != null){
                ServerInfo serverInfo = fileServiceInfo.getServerInfo();
                serverNameToServerInfoMap.put(serverInfo.getName(), serverInfo);
            }
        }
        return serverNameToServerInfoMap;
    }

    public Map<String, ServerInfo> getDeviceNameToServerInfpMap(){
        Map<String, ServerInfo> deviceNameToServerInfoMap = new HashMap<>();

        for(ToolInfo tool : this.toolList){
            FileServiceInfo fileServiceInfo = fileServiceMap.get(tool.getFsid());
            if (fileServiceInfo != null){
                ServerInfo serverInfo = fileServiceInfo.getServerInfo();
                deviceNameToServerInfoMap.put(tool.getName(), serverInfo);
            }
        }
        return deviceNameToServerInfoMap;
    }
}
