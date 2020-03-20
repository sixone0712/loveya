/**
 *
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : LoginFormBean.java
 *
 * Author: Tomokazu OKUMURA
 * Date: 2011/11/16
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.fs.portal.bean;

import java.io.Serializable;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * ユーザログイン画面の画面情報を保持します。
 *
 * @author Tomokazu OKUMURA
 * ============================================================================
 * Change History:
 * 2012-02-09 T.Tateyama シリアライズ化
 */
public class LoginFormBean implements Serializable {

	/** シリアルバージョン番号 */
	private static final long serialVersionUID = -2339985369013492963L;

	/**
	 * ログインユーザ名
	 */
	private String username;

	/**
	 * パスワード
	 */
	private String password;

	/**
	 * エラーメッセージ
	 */
	private String errorMessage;

	/**
	 * デフォルトコンストラクタ
	 */
	public LoginFormBean() {
		username = "";
		password = "";
		errorMessage = "";
	}

	/**
	 *　設定されているログインユーザ名を返す。
	 * Return:
	 * 	@return ログインユーザ名
	 */
	public final String getUsername() {
		return username;
	}

	/**
	 * ログインユーザ名を設定する。
	 * Parameters:
	 * 	@param user セットするログインユーザ名
	 */
	public final void setUsername(String user) {
		this.username = user;
	}

	/**
	 *　設定されているパスワードを返す。
	 * Return:
	 * 	@return パスワード
	 */
	public final String getPassword() {
		return password;
	}

	/**
	 * パスワードを設定する。
	 * Parameters:
	 * 	@param pass セットするパスワード
	 */
	public final void setPassword(String pass) {
		this.password = pass;
	}

	/**
	 *　設定されているエラーメッセージを返す。
	 * Return:
	 * 	@return エラーメッセージ
	 */
	public final String getError() {
		return errorMessage;
	}

	/**
	 * エラーメッセージを設定する。
	 * Parameters:
	 * 	@param error セットするエラーメッセージ
	 */
	public final void setError(String error) {
		this.errorMessage = error;
	}

}
