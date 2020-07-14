/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : ConfigurationException.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/11/19
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.ckbs.eec.fs.collect.action;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *　
 *　設定ファイルで不備がある場合に発生する例外
 *
 * @author Tomomitsu TATEYAMA
 */
public class ConfigurationException extends Exception {
	/** */
	private static final long serialVersionUID = -3636294112631146515L;
	/** エラーコード */
	private String code = null;
	
	/**
	 * メッセージとエラーコードを指定してConfigurationExceptionを構築する。
	 * エラーメッセージにエラーコードを埋め込んで、メッセージを作成する。
	 * 
	 * @param msg	エラー内容の文字列
	 * @param cd	エラーを識別するコード
	 */
	public ConfigurationException(String msg, String cd) {
		this(msg, null, cd);
	}
	/**
	 * メッセージと原因例外とエラーコードを指定してConfigurationExceptionを構築する。
	 * エラーメッセージにエラーコードを埋め込んで、メッセージを作成する。
	 * 
	 * @param msg 	エラー内容の文字列
	 * @param ex	原因例外
	 * @param cd	エラーコード
	 */
	public ConfigurationException(String msg, Throwable ex, String cd) {
		super(cd + ":" + msg, ex);
		this.code = cd;
	}
	/**
	 * エラーコードを返す。
	 *
	 * Parameters/Result:
	 *　 @return	エラーコードを返す。
	 */
	public final String getCode() {
		return code;
	}
}
