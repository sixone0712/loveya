package jp.co.canon.ckbs.eec.fs.manage.service.configuration.legacy.toolinfo;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class ToolInfoSaxHandler extends DefaultHandler {

    private StringBuilder builder = null;

    FtpInfo currentFtpInfo = null;
    LogData currentLogData = null;

    ToolInfo toolInfo = new ToolInfo();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("ToolType")){
            String name = attributes.getValue("name");
            String version = attributes.getValue("version");
            toolInfo.setName(name);
            toolInfo.setVersion(version);
            return;
        }
        if (qName.equals("FTP")){
            String id = attributes.getValue("id");
            String port = attributes.getValue("port");
            FtpInfo ftpInfo = new FtpInfo();
            ftpInfo.setId(id);
            ftpInfo.setPort(Integer.parseInt(port));
            currentFtpInfo = ftpInfo;
            return;
        }
        if (qName.equals("User")){
            builder = new StringBuilder();
            return;
        }
        if (qName.equals("Password")){
            builder = new StringBuilder();
            return;
        }
        if (qName.equals("LogData")){
            String kind = attributes.getValue("kind");
            String ref = attributes.getValue("ref");
            LogData logData = new LogData();
            logData.setKind(kind);
            logData.setRef(ref);
            currentLogData = logData;
            builder = new StringBuilder();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (builder != null) {
            builder.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("FTP")){
            toolInfo.addFtpInfo(currentFtpInfo);
            currentFtpInfo = null;
            return;
        }
        if (qName.equals("User")){
            if (currentFtpInfo != null){
                currentFtpInfo.setUser(builder.toString());
            }
            return;
        }
        if (qName.equals("Password")){
            if (currentFtpInfo != null){
                currentFtpInfo.setPassword(builder.toString());
            }
            return;
        }
        if (qName.equals("LogData")){
            if (currentLogData != null){
                currentLogData.setPath(builder.toString());
            }
            toolInfo.addLogData(currentLogData);
        }
    }

    public ToolInfo getToolInfo(){
        return toolInfo;
    }
}
