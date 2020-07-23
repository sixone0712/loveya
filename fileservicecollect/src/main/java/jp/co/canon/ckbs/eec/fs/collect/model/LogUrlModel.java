/**
 *
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : LogUrlModel.java
 *
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/11/19
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.ckbs.eec.fs.collect.model;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *
 * ログのURL情報を保持するモデル。
 *
 * @author Tomomitsu TATEYAMA
 */
public interface LogUrlModel {
	/**
	 * 装置ログに接続する際のルート情報をURL接続文字で返す。
	 *
	 * Parameters/Result:
	 *　 @return	URL文字
	 */
	public String getUrlString();
	/**
	 * FTP接続の際に利用するユーザIDを返す。
	 *
	 * Parameters/Result:
	 *　 @return	ユーザID
	 */
	public String getUserId();
	/**
	 * FTP接続の際に利用するパスワードを返す。
	 *
	 * Parameters/Result:
	 *　 @return	パスワード
	 */
	public String getPassword();

	//<FS収集機能のActive対応(CITS)>
	/**
	 * FTP接続の際に利用するFTPモードを返す。
	 *
	 * Parameters/Result:
	 *　 @return	パスワード
	 */
	public String getFtpmode();
}
