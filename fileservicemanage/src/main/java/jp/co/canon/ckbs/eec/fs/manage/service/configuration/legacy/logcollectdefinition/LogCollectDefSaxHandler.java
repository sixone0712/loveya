package jp.co.canon.ckbs.eec.fs.manage.service.configuration.legacy.logcollectdefinition;

import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class LogCollectDefSaxHandler extends DefaultHandler {
    Log currentLog = null;

    @Getter
    String name;

    @Getter
    String type;

    @Getter
    List<Log> logList = new ArrayList<>();

    StringBuilder builder = null;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    Log buildLog(Attributes attributes){
        String kind = attributes.getValue("kind");
        String user = attributes.getValue("user");
        String password = attributes.getValue("password");
        String ftpmode = attributes.getValue("ftpmode");
        Log log = new Log();
        log.setKind(kind);
        log.setUser(user);
        log.setPassword(password);
        log.setFtpmode(ftpmode);
        return log;
    }

    void addLog(Log log){
        if (log != null){
            logList.add(log);
        }
    }

    void setLogUrls(String urls){
        if (currentLog != null){
            currentLog.setUrls(urls);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (qName.equalsIgnoreCase("Target")){
            name = attributes.getValue("name");
            type = attributes.getValue("type");
            return;
        }
        if (qName.equalsIgnoreCase("Log")){
            Log log = buildLog(attributes);
            this.addLog(log);
            builder = new StringBuilder();
            return;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (builder != null){
            builder.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (qName.equalsIgnoreCase("Target")){
            return;
        }
        if (qName.equalsIgnoreCase("Log")){
            this.setLogUrls(builder.toString());
            builder = null;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}
