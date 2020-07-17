package jp.co.canon.ckbs.eec.fs.configuration.legacy.logconfiguration;

import jp.co.canon.ckbs.eec.fs.configuration.legacy.SaxParserFactory;
import lombok.Getter;

import javax.xml.parsers.SAXParser;
import java.io.File;

public class LogConfiguration {
    @Getter
    String compoId;

    @Getter
    String name;

    @Getter
    String description;

    @Getter
    String executeCommand;

//     String executeCommandCA;

    public static LogConfiguration load(File file) throws Exception {
        SAXParser parser = SaxParserFactory.createParser();
        LogConfigurationSaxHandler handler = new LogConfigurationSaxHandler();
        parser.parse(file, handler);

        LogConfiguration conf = new LogConfiguration();
        conf.compoId = handler.compoId;
        conf.name = handler.name;
        conf.description = handler.description;
        conf.executeCommand = handler.executeCommand;

        return conf;
    }
}
