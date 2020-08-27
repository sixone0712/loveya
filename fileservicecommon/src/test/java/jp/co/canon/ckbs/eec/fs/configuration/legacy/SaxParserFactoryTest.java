package jp.co.canon.ckbs.eec.fs.configuration.legacy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.SAXParser;

public class SaxParserFactoryTest {
    @Test
    void test_001(){
        SaxParserFactory saxParserFactory = new SaxParserFactory();
        Assertions.assertNotNull(saxParserFactory);
    }

    @Test
    void test_002(){
        SAXParser parser = SaxParserFactory.createParser();
        Assertions.assertNotNull(parser);
    }
}
