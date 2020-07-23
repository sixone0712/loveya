package jp.co.canon.cks.ees.service.stack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * <p>STACK.xmlファイルをパースして、Stackオブジェクトを生成するファクトリクラス。</p>
 * @author SUZUKI,Ken(Canon Software Inc.)
 * @version 1.0
 */
public class StackHandler extends AbstractParametersHandler{

    private static final String STACK_ITEM_UPDATE_PATH = "/FilterConfig/items/item";

    /**
     * stack
     */
    private Stack stack;

    /**
     * STACKファイルを解析し、その情報を格納したStackオブジェクトをつくる。
     *
     * @param stackPath STACKファイルのパス
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @return Stackオブジェクト
     */
    public Stack parse(File stackPath) throws IOException, ParserConfigurationException, SAXException {

        // Stackファイルをパースして、新規にStackオブジェクトを生成する
        stack = new Stack();
        stack.setStackItemList(new ArrayList<StackItem>());
        stack.setStackFilePath(stackPath.getCanonicalPath());

        this.readFile(stackPath);

        return stack;
    }

    /**
     * startElement
     *
     * @param path String
     */
    protected void startElement(String path) {
    }

    /**
     * endElement
     *
     * @param path String
     */
    protected void endElement(String path) {
    }

    /**
     * updateParameter
     *
     * @param path String
     * @param attrs Attributes
     */
    protected void updateParameter(String path, Attributes attrs) {
        if(path.equals(STACK_ITEM_UPDATE_PATH)){
        	StackItem item = new StackItem();
            int len = attrs.getLength();
            for(int i = 0; i < len; i++){
                item.setAttr(attrs.getQName(i), attrs.getValue(i));
            }
            stack.getStackItemList().add(item);
        }
    }
}
