/**
 *
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : LogInfoBean.java
 *
 * Author: Tomokazu OKUMURA
 * Date: 2010/12/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 * 2011/01/06 メソッドコメント追加。不要なメソッド、プロパティ削除。
 */

package jp.co.canon.cks.eec.fs.portal.bean;

import java.io.Serializable;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * ログ種類を格納するJavaBeans
 *
 * @author Tomokazu OKUMURA
 * =======================================================================
 * Change History:
 * 2011-01-05 T.Tateyama	コメントの追加と、不要なメソッドにdeprecatedタグを設定、
 * 							fileListForwordingプロパティを追加
 * 							toString()の実装
 * 2012-02-09 T.Tateyama	シリアライズ対応
 */
public class LogInfoBean implements Serializable {

	/** シリアルバージョンＩＤ */
	private static final long serialVersionUID = 8320653571273628372L;
	/** 収集ファイル検索時の検索種別 */
	private int logType;
	/** コンポーネント種別ID(2桁) */
	private String code;
	/** コンポーネント名前 */
	private String logName;
	// ---------- 2010-01-05
	/**　転送先のサーブレット名 */
	private String fileListForwarding = null;

	/**
	 *　設定されているFileListの転送先を返す。
	 * Return:
	 * 	@return 転送先を示す文字列を返す
	 */
	public final String getFileListForwarding() {
		return fileListForwarding;
	}

	/**
	 * FileListの転送先を設定する。
	 * Parameters:
	 * 	@param newVal セットする転送先を示す文字列
	 */
	public final void setFileListForwarding(String newVal) {
		this.fileListForwarding = newVal;
	}

	/**
	 *　設定されている検索条件の種類を返す。
	 * Return:
	 * 	@return 検索条件を示す定数　0:期間・キーワード　1:期間 2: キーワード　3:なし
	 */
	public final int getType() {
		return logType;
	}

	/**
	 * 検索条件の種類を設定する。
	 * Parameters:
	 * 	@param type 検索条件を示す値を設定する。　0:期間・キーワード　1:期間 2: キーワード　3:なし
	 */
	public final void setType(int type) {
		this.logType = type;
	}

	/**
	 *　設定されているログにID（２桁）を返す。
	 * Return:
	 * 	@return ログを識別するためのIDを返す
	 */
	public final String getCode() {
		return code;
	}

	/**
	 * ログを識別するIDを設定する。
	 * Parameters:
	 * 	@param id セットするログを識別するID
	 */
	public final void setCode(String id) {
		this.code = id;
	}

	/**
	 *　設定されているログ名称を返す。
	 * Return:
	 * 	@return ユーザに表示するログ名称を返す。
	 */
	public final String getName() {
		return logName;
	}

	/**
	 * ユーザに表示するログ名称を設定する。
	 * Parameters:
	 * 	@param name セットするユーザに表示するログ名称
	 */
	public final void setName(String name) {
		this.logName = name;
	}

	/**
	 *	文字列表現を返す。
	 *
	 * Result:
	 *　 @return 文字列表現
	 */
	public final String toString() {
		return new StringBuffer().append(getCode()).append(",")
		.append(getName()).append(",")
		.append(getType()).append(",")
		.append(getFileListForwarding()).toString();
	}
}
