package jp.co.canon.ckbs.eec.fs.configuration.legacy.logcollectdefinition;

import jp.co.canon.ckbs.eec.fs.configuration.legacy.SaxParserFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class LogCollectDefinition {
    String name;
    String type;

    List<Log> logList;

    File logCollectDefinitionFile;
    long timestamp;

    public LogCollectDefinition(File file) throws Exception {
        this.logCollectDefinitionFile = file;
        load();
    }

    void parseFile(SAXParser parser, LogCollectDefSaxHandler handler, File file){
        try {
            parser.parse(file, handler);
            name = handler.getName();
            type = handler.getType();
            logList = handler.getLogList();
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    void loadFile(){
        SAXParser parser = SaxParserFactory.createParser();
        LogCollectDefSaxHandler handler = new LogCollectDefSaxHandler();
        assert parser != null;
        parseFile(parser, handler, this.logCollectDefinitionFile);
    }

    synchronized void load() throws Exception {
        if (!logCollectDefinitionFile.exists()){
            throw new Exception("file not found");
        }
        if (logCollectDefinitionFile.lastModified() > timestamp){
            loadFile();
            timestamp = logCollectDefinitionFile.lastModified();
        }
    }

    public Log[] getLogList() throws Exception {
        load();
        return logList.toArray(new Log[0]);
    }
}
