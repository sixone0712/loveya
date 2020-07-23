package jp.co.canon.cks.ees.service.stack;

import java.util.List;


/**
 * <p>STACK.xmlの情報を管理する。</p>
 * @author SUZUKI,Ken(Canon Software Inc.)
 * @version 1.0
 */
public class Stack {
    /**
     * 以下のルールでItem属性値を格納する。<BR>
     * ・Map key : StackItemのidをInteger型で格納<BR>
     * ・Map value : StackItemオブジェクトを格納
     */
    private List<StackItem> stackItemList;

    /**
     * このオブジェクトが表すSTACKファイルのパス。
     */
    private String stackFilePath;

    /**
     * Stack.xml内にある全てのItem情報を取得する。
     *
     * @return IStackItemのリスト
     */
    public List<StackItem> getStackItemList(){
        return stackItemList;
    }

    /**
     * Stack.xml内にある全てのItem情報を設定する。
     *
     * @param stackItemList StackItemのリスト
     */
    public void setStackItemList(List<StackItem> stackItemList){
        this.stackItemList = stackItemList;
    }

    /**
     * このオブジェクトが表すSTACKファイルのパスを取得する。
     *
     * @return STACKファイルのパス。
     */
    public String getStackFilePath(){
        return stackFilePath;
    }

    /**
     * このオブジェクトが表すSTACKファイルのパスを設定する。
     *
     * @param stackFilePath STACKファイルのパス。
     */
    public void setStackFilePath(String stackFilePath){
        this.stackFilePath = stackFilePath;
    }
}
