package jp.co.canon.ckbs.eec.fs.configuration.legacy;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SaxParserFactory {
    public static SAXParser createParser(){
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/validation/schema", true);
            return factory.newSAXParser();
        } catch (Exception e){
            return null;
        }
    }
}
