/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : ServiceException.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/11/26
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.fs.portal.bussiness;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * 
 *　サービス要求時に発生する例外。画面からサービス要求された場合は全てのこの例外で報告する。
 *
 * @author Tomomitsu TATEYAMA
 */
public class ServiceException extends Exception {

	/** シリアライズ番号 */
	private static final long serialVersionUID = -1067526547329895650L;
	/**
	 * メッセージを指定して構築する
	 * 
	 * @param message	例外時のメッセージ
	 */
	public ServiceException(String message) {
		super(message);
	}
	/**
	 * 原因例外を指定して構築する。
	 * 
	 * @param cause	原因例外
	 */
	public ServiceException(Throwable cause) {
		super(cause);
	}
	/**
	 * メッセージと例外を指定して構築する。
	 * 
	 * @param message	メッセージ
	 * @param cause		原因例外
	 */
	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
