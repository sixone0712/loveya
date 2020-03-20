/**
 *
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : ToolInfo.java
 *
 * Author: Tomokazu OKUMURA
 * Date: 2011/11/16
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.fs.portal.bean;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * 装置情報を保持するクラスです。
 *
 * @author Tomokazu OKUMURA
 * =====================================================
 * 2011.11.29	J.Tsuruta	プロパティ名が混同するため名称を変更
 * 2013.06.30 	T.Tateyama	収集サーバ情報を保持するように修正
 */
public class ToolInfo {

	/**
	 * 構成IDです。
	 */
	private String structId;	// 2011.11.29 modify by J.Tsuruta

	/**
	 * 装置名です。
	 */
	private String targetname;

	/**
	 * 装置種別です。
	 */
	private String targettype;

	/**
	 * 収集サーバのＩＤ
	 */
	private String collectServerId = "0";
	
	/**
	 * 収集サーバのホスト名
	 */
	private String collectHostName = null;
	
	/**
	 *　設定されている構成IDを返す。
	 * Return:
	 * 	@return 構成ID
	 */
	public final String getStructId() {	// 2011.11.29 modify by J.Tsuruta
		return structId;
	}

	/**
	 * 構成IDを設定する。
	 * Parameters:
	 * 	@param id セットする構成ID
	 */
	public final void setStructId(String id) {	// 2011.11.29 modify by J.Tsuruta
		this.structId = id;
	}

	/**
	 *　設定されている装置名を返す。
	 * Return:
	 * 	@return 装置名
	 */
	public final String getToolName() {
		return targetname;
	}

	/**
	 * 装置名を設定する。
	 * Parameters:
	 * 	@param name セットする装置名
	 */
	public final void setToolName(String name) {
		this.targetname = name;
	}

	/**
	 *　設定されている装置種別を返す。
	 * Return:
	 * 	@return 装置種別
	 */
	public final String getToolType() {
		return targettype;
	}

	/**
	 * 装置種別を設定する。
	 * Parameters:
	 * 	@param type セットする装置種別
	 */
	public final void setToolType(String type) {
		this.targettype = type;
	}

	/**
	 *　設定されているcollectServerIdを返す。
	 * Return:
	 * @return collectServerId
	 */
	public String getCollectServerId() {
		return collectServerId;
	}

	/**
	 * collectServerIdを設定する。
	 * Parameters:
	 * @param collectServerId セットする collectServerId
	 */
	public void setCollectServerId(String collectServerId) {
		this.collectServerId = collectServerId;
	}

	/**
	 *　設定されているcollectHostNameを返す。
	 * Return:
	 * @return collectHostName
	 */
	public String getCollectHostName() {
		return collectHostName;
	}

	/**
	 * collectHostNameを設定する。
	 * Parameters:
	 * @param collectHostName セットする collectHostName
	 */
	public void setCollectHostName(String collectHostName) {
		this.collectHostName = collectHostName;
	}

}
