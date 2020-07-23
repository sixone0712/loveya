/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : CommandExecutionException.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/11/23
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.ckbs.eec.fs.collect.action;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *
 *	このクラスは、装置ログコマンド実行時に発生したエラーが発生したときに収集側で使う例外である。
 *
 * @author Tomomitsu TATEYAMA
 */
public class CommandExecutionException extends RuntimeException {
	/** エラーコードの開始文字 */
	private static final String START_CHAR = "-";
	/** エラーコードの終了文字 */
	private static final String END_CHAR = ":";
	/** エラーコード　*/
	private String errorCode = "";
	/** serialVersionID	*/
	private static final long serialVersionUID = 6869471892937698885L;

	/**
	 * コマンドから発生したエラーメッセージを指定して構築する。
	 * 
	 * @param message	"E-xxxx"で始まるエラーメッセージ
	 */
	public CommandExecutionException(String message) {
		super(message);
		int pos1 = message.indexOf(START_CHAR);
		int pos2 = message.indexOf(END_CHAR);
		if (pos1 > 0 && pos1 < pos2) {
			errorCode = message.substring(pos1, pos2);
		}
	}
	/**
	 * エラーメッセージに含まれるエラーコードを返す。コードの部分はマイナスからコロンまでの位置とする。
	 * 判断出来ない場合は、空白を返す。
	 * 
	 * Parameters/Result:
	 *　 @return	エラーコード
	 */
	public String getErrorCode() {
		return errorCode;
	}
}
