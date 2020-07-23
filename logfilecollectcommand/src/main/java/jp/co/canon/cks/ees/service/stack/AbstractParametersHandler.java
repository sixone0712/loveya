package jp.co.canon.cks.ees.service.stack;


import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAXパラメータハンドラ<p>
 *
 * @author ADK109987
 *
 */
public abstract class AbstractParametersHandler extends DefaultHandler{
    private SAXParser parser = null;
    private String tagKeyName = "";

    protected abstract void startElement (String path);

    protected abstract void endElement (String path);

    protected abstract void updateParameter (String path, Attributes attrs);

    /**
     * コンストラクタ<p>
     */
    public AbstractParametersHandler (){
        super();
    }

    protected void readFile (File file) throws SAXException, ParserConfigurationException, IOException{
        String url = file.toURL().toString();

        if (parser == null){
        	SAXParserFactory factory = SAXParserFactory.newInstance();
        	factory.setNamespaceAware(true);
            parser = factory.newSAXParser();
        }
        parser.parse(url, this);
    }

    /** (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    public void startDocument (){
        initTagPath();
    }

    /** (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endDocument()
     */
    public void endDocument (){
    }

    /** (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement (String url, String localName, String qName, Attributes attributes){
        pushTagPath(localName);

        startElement(getTagPath());
        updateParameter(getTagPath(), attributes);
    }

    /** (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement (String url, String localName, String qName){
        endElement(getTagPath());
        popTagPath(localName);
    }

    private void initTagPath (){
        tagKeyName = "";
    }

    private void pushTagPath (String qName){
        tagKeyName += "/" + qName;
    }

    private void popTagPath (String qName){
        tagKeyName = tagKeyName.substring(0, tagKeyName.lastIndexOf("/"));
    }

    protected String getTagPath (){
        return tagKeyName;
    }
}
