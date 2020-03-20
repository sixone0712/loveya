/**
 *
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : LoginInfo.java
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
 * ログイン情報を保持するクラスです。
 *
 * @author Tomokazu OKUMURA
 */
public class LoginInfo implements Serializable {
	/**  */
	private static final long serialVersionUID = 1L;
	/** 選択装置名 */
	private String toolId;
	private String user = null;
	private int authLevel = 0;
	
	/**
	 * デフォルトコンストラクタ
	 */
	public LoginInfo() {
	}
	
	/**
	 * ユーザIDと権限を指定して構築する
	 * 
	 * @param user	ユーザID
	 * @param level	権限
	 */
	public LoginInfo(String user, int level) {
		setUser(user);
		setAuthLevel(level);
	}

	/**
	 *　設定されている装置名を返す。
	 * Return:
	 * 	@return 装置名
	 */
	public final String getToolId() {
		return toolId;
	}

	/**
	 * 装置名を設定する。
	 * Parameters:
	 * 	@param name セットする装置名
	 */
	public final void setToolId(String name) {
		this.toolId = name;
	}

	/**
	 *　設定されているユーザIDを返す。
	　* Return:
	 * 	@return ユーザID
	 */
	public String getUser() {
		return user;
	}

	/**
	 *　ユーザIDを設定する。
	 * Parameters:
	 * 	@param user ユーザID
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 *　設定されている権限レベルを返す。
	　* Return:
	 * 	@return 権限レベル
	 */
	public int getAuthLevel() {
		return authLevel;
	}

	/**
	 * 権限レベルを設定する。
	 * Parameters:
	 * 	@param authLevel 権限レベル
	 */
	public void setAuthLevel(int authLevel) {
		this.authLevel = authLevel;
	}

}
