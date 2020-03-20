/**
 *
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : ShrinkBean.java
 *
 * Author: msoft
 * Date: 2016/11/11
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */
package jp.co.canon.cks.eec.fs.portal.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * <p>シュリンク設定クラス</p> File upload Service
 * <p>シュリンク設定を格納するクラス</p>
 * 2016-11-11 15-014-01 オンデマンドシュリンク機能対応
 * @author msoft
 * ============================================================================
 * Change History:
 */
public class ShrinkBean implements Serializable {
    /**
     * シリアルID
     */
    private static final long serialVersionUID = 2082173435751478328L;
    /** 編集可不可 */
    private boolean editable = false;
    /** 設定行数 */
    private int rows = 0;
    /** シュリンク設定の有効無効 true:有効 */
    private boolean enable = false;
    /** Keyリスト */
    private ArrayList<String> keyList = new ArrayList<String>();
    /** IDリスト */
    private ArrayList<String> idList = new ArrayList<String>();
    /** 退避するシュリンク設定ファイル名 */
    private final String SHRINK_FILE = "Shrink.dat";
    /** 対象ファイル数 */
    private int maxFileCount = 0;

    /**
     * Shrink設定の編集可不可を取得します。
     * @return 編集可不可
     */
    public boolean getEditable() {
        return editable;
    }

    /**
     * Shrink設定の編集可不可を設定します。
     * @param editable 編集可不可
     */
    public void setEditable(boolean editable) {
    	this.editable = editable;
    }

    /**
     * Shrink設定数を取得します。
     * @return 設定数
     */
    public int getRows() {
        return rows;
    }

    /**
     * Shrink設定数を設定します。また、Shrink設定を初期化します。
     * @param maxLine　設定数
     */
    public void SetRows(int maxLine) {
    	this.rows = maxLine;
        for (int i = 0; i < maxLine; i++) {
            this.keyList.add(new String());
            this.idList.add(new String());
        }
    }

    /**
     * Shrink設定の有効無効を取得します。
     * @return 有効無効
     */
    public boolean getEnable() {
        return enable;
    }

    /**
     * Shrink設定の有効無効を設定します。
     * @param enable 有効無効
     */
    public void setEnable(boolean enable) {
    	this.enable = enable;
    }	

    /**
     * 指定したインデックスのKeyを取得する
     * @param idx Shrink設定リストのインデックス
     * @return 指定したインデックスのKey
     */
    public String getKey(int idx) {
        return this.keyList.get(idx);
    }

    /**
     * 指定したインデックスのKeyを設定する
     * @param idx Shrink設定リストのインデックス
     * @param key 設定するKey
     */
    public void setKey(int idx, String key) {
        this.keyList.set(idx, key);
    }

    /**
     * 指定したインデックスのIDを取得する
     * @param idx Shrink設定リストのインデックス
     * @return 指定したインデックスのID
     */
    public String getId(int idx) {
        return this.idList.get(idx);
    }

    /**
     * 指定したインデックスのIDを設定する
     * @param idx Shrink設定リストのインデックス
     * @param id 設定するID
     */
    public void setId(int idx, String id) {
        this.idList.set(idx, id);
    }

    /**
     * Shrink設定の最大対象ファイル数を取得します。
     * @return 最大対象ファイル数
     */
    public int getMaxFileCount() {
        return maxFileCount;
    }

    /**
     * Shrink設定の最大対象ファイル数を設定します。
     * @param maxFileCount 最大対象ファイル数
     */
    public void setMaxFileCount(int maxFileCount) {
    	this.maxFileCount = maxFileCount;
    }

    /**
     * 退避していたファイルからShrinkBeanに読み込む。
     * @param outPath 保存するパス
     * @param reqNo リクエストNo
     */
    public ShrinkBean readShrinkFile(String outPath, String reqNo) throws Exception {
        //パス文字列を作成	
        StringBuffer pathname = new StringBuffer();
        pathname.append(outPath).append("/");
        pathname.append(Constants.CACHE_DIR).append("/");
        pathname.append(reqNo);
        // ディレクトリ作成
        File path = new File(pathname.toString());
        path.mkdir();
        // ファイルからシュリンク設定を読み込む
        FileInputStream fis = new FileInputStream(pathname + "/" + SHRINK_FILE);
        ObjectInputStream ois = new ObjectInputStream(fis);
        ShrinkBean shrink = (ShrinkBean) ois.readObject();
        ois.close();
        return shrink;
    }
}
