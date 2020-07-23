/**
 * 
 * File : UserInfo.java
 *
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/01
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	ユーザとパスワード情報を保持するクラス
 *
 * @author Tomomitsu TATEYAMA
 */
public class UserInfo {
	private String user = null;
	private char[] passwd = null;
	
	/**
	 * ユーザ名を返す。
	 * @return user	ユーザ名を返す
	 */
	public String getUser() {
		return user;
	}
	/**
	 * ユーザ名を設定する
	 * @param user セットする ユーザ名
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * パスワードを返す。
	 * @return passwd	パスワード
	 */
	public char[] getPasswd() {
		return passwd;
	}
	/**
	 * パスワードを設定する
	 * @param passwd セットする パスワード
	 */
	public void setPasswd(char[] passwd) {
		this.passwd = passwd;
	}
}
