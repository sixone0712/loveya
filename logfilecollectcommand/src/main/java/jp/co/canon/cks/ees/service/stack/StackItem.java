package jp.co.canon.cks.ees.service.stack;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <p>STACK.xmlのItemタグの属性情報を管理する。</p>
 * @author SUZUKI,Ken(Canon Software Inc.)
 * @version 1.0
 */
public class StackItem {

    /**
     * id属性名の規定値
     */
    public static final String ATTR_NAME_ID = "id";
    /**
     * key属性名の規定値
     */
    public static final String ATTR_NAME_KEY = "key";
    /**
     * subKey属性名の規定値
     */
    public static final String ATTR_NAME_SUBKEY = "subKey";
    /**
     * item属性名の規定値
     */
    public static final String ATTR_NAME_ITEM = "item";
    /**
     * unit属性名の規定値
     */
    public static final String ATTR_NAME_UNIT = "unit";


    /**
     * item属性値の区切り子
     */
    public static final String REGIX_ITEM = "/";
    /**
     * unit属性値の区切り子
     */
    public static final String REGIX_UNIT = "/";


    /**
     * 文字列長0の文字列
     */
    public static final String STR_EMPTY = "";

    /**
     * id
     */
    private String id;

    /**
     * key
     */
    private String key;

    /**
     * keyPattern
     */

    /**
     * subkey
     */
    private String subkey;
    /**
     * subkeyPattern
     */
    private Pattern subkeyPattern;
    /**
     * item
     */
    private String[] item;
    /**
     * StackItemを生成する。
     */
    public StackItem() {
    }

    /**
     * idを設定する。
     *
     * @param id
     */
    private void setId(String id){
        this.id = id;
    }

    /**
     * idを取得する。
     *
     * @return id
     */
    public String getId(){
        return this.id;
    }

    /**
     * keyを設定する。
     *
     * @param key
     */
    private void setKey(String key){
        this.key = key;
    }

    /**
     * keyを取得する。
     *
     * @return key文字列
     */
    public String getKey(){
        return this.key;
    }

    /**
     * keyPatternを取得する。
     *
     * @return keyPattern
     */

    /**
     * subkeyを設定する。
     *
     * @param subkey
     */
    private void setSubkey(String subkey) throws PatternSyntaxException {
       this.subkey = subkey;
       this.subkeyPattern = Pattern.compile(this.subkey);
    }

    /**
     * subkeyを取得する。
     *
     * @return subkey
     */
    public String getSubkey(){
        return this.subkey;
    }

    /**
     * subkeyPatternを取得する。
     *
     * @return subkeyPattern
     */
    public Pattern getSubkeyPattern(){
        return this.subkeyPattern;
    }

    /**
     * itemを設定する。
     *
     * @param item
     */
    private void setItem(String item){

        // 実装仕様
        // --------------------------------
        //              [入力]        [生成する配列]
        //  <1>          null         null
        //  <2>          ""           サイズ1{""}の配列
        //  <3>          "/"          サイズ2{"",""}の配列
        //  <4>          "/aaa/"      サイズ3("","aaa",""}の配列
        // --------------------------------
        // ※配列の要素にnullは入れない

        if(item == null){
            this.item = null;
            return;
        }

        this.item = item.split(REGIX_ITEM);

        // 引数item内の区切り子"/"の数をカウント
        int rgxCnt = 0;
        int idx = 0;
        while(idx < item.length()){
            idx = item.indexOf(REGIX_ITEM, idx);
            if(idx < 0){
                break;
            }
            rgxCnt++;
            idx++;
        }

        // split結果の配列サイズが足りない場合、要素を拡張
        if(this.item.length < rgxCnt + 1){
            String[] tmp = this.item;
            this.item = new String[rgxCnt + 1];
            for(idx = 0; idx < tmp.length; idx++){
                this.item[idx] = tmp[idx];
            }
            for(; idx < rgxCnt + 1; idx++){
                this.item[idx] = STR_EMPTY;
            }
        }
    }

    /**
     * itemを取得する。
     *
     * @return item
     */
    public String[] getItem(){
        return this.item;
    }

    /**
     * 属性値を設定する。
     *
     * @param attrName 属性名
     * @param attrValue 属性値
     * @return 成功のときtrue。attrNameが既定の属性名のどれとも一致しない場合、falseを戻す。
     */
    public boolean setAttr(String attrName, String attrValue){

        if(attrName.equals(ATTR_NAME_ID)){
            setId(attrValue);
        }
        else if(attrName.equals(ATTR_NAME_KEY)){
            setKey(attrValue);
        }
        else if(attrName.equals(ATTR_NAME_SUBKEY)){
            setSubkey(attrValue);
        }
        else if(attrName.equals(ATTR_NAME_ITEM)){
            setItem(attrValue);
        }
        else{
            return false;
        }

        return true;
    }
}
