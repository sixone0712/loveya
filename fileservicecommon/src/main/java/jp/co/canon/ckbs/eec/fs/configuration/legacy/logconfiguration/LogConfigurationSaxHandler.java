package jp.co.canon.ckbs.eec.fs.configuration.legacy.logconfiguration;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LogConfigurationSaxHandler extends DefaultHandler {
    String compoId;
    String name;
    String description;
    String executeCommand;
    String searchType;

    StringBuilder builder = null;
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (qName.equalsIgnoreCase("CompoID")){
            builder = new StringBuilder();
        }
        if (qName.equalsIgnoreCase("Name")){
            builder = new StringBuilder();
        }
        if (qName.equalsIgnoreCase("Description")){
            builder = new StringBuilder();
        }
        if (qName.equalsIgnoreCase("ExecuteCommand")){
            builder = new StringBuilder();
        }
        if (qName.equalsIgnoreCase("SearchType")){
            builder = new StringBuilder();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if (builder != null) {
            builder.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (qName.equalsIgnoreCase("CompoID")){
            if (builder != null){
                compoId = builder.toString();
                builder = null;
            }
            return;
        }
        if (qName.equalsIgnoreCase("Name")){
            if (builder != null){
                name = builder.toString();
                builder = null;
            }
            return;
        }
        if (qName.equalsIgnoreCase("Description")){
            if (builder != null){
                description = builder.toString();
                builder = null;
            }
            return;
        }
        if (qName.equalsIgnoreCase("ExecuteCommand")){
            if (builder != null){
                executeCommand = builder.toString();
                builder = null;
            }
            return;
        }
        if (qName.equalsIgnoreCase("SearchType")){
            if (builder != null){
                searchType = builder.toString();
                builder = null;
            }
        }
    }
}
