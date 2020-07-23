/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : ConfigurationException.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/03
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	このクラスは、設定時に発生するExceptionをまとめて管理するためのクラス。
 *	XML等を利用する場合、複数のExceptionが発生するため、管理が大変であるため
 *	このクラスを使って詳細を隠ぺいする。
 *
 * @author Tomomitsu TATEYAMA
 */
public class ConfigurationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	
	/**
	 * 
	 * コントラクタです。
	 * 
	 * @param arg0 エラーメッセージ
	 */
	public ConfigurationException(String arg0) {
		super(arg0);
	}

	/**
	 * コンストラクタ　メッセージと、原因を設定して構築する。
	 * @param msg	エラーメッセージ
	 * @param e		原因となった例外クラス
	 */
	public ConfigurationException(String msg, Throwable e) {
		super(msg, e);
	}
}
