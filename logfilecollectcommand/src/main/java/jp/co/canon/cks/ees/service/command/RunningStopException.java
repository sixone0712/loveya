/**
 * 
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : RunningStopException.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/01
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.command;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	実行停止が発生したときに発生するException
 *
 * @author Tomomitsu TATEYAMA
 */
public class RunningStopException extends Exception {
	public static final int USER_STOP = 100;
	public static final int REQUEST_OTHER_PROCESE = 0;
	public static final int REQUEST_NOT_FOUND = 1;
	public static final int EXISTS_OTHER_REQUESTED = 2;
	public static final int DISKSIZE_MAX = 3;
	private int causeno = 0;
	static final String[] MESSAGE = {
		"ERR-0101: Request number is not found.",
		"WAR-0001: Request is not found.",
		"ERR-0100: Request other data.",
		"ERR-0201: When you write file, Log Files Size has been exceed the max."
	};
	/**
	 * シリアル番号
	 */
	private static final long serialVersionUID = 1340940106952527820L;
	
	/**
	 * 理由コードを指定しないで構築する。その場合はユーザストップ理由と課程する
	 */
	public RunningStopException() {
		this(USER_STOP);
	}
	/**
	 * 理由コードを指定して構築する
	 * @param cause
	 */
	public RunningStopException(int cause) {
		this.causeno = cause;
	}
	/**
	 * このブログラムだけで使う理由コードを返す
	 *
	 * Parameter:
	 * 	@return	int	理由コードを返す
	 */
	protected int getLocalCauseNo() {
		return causeno;
	}
	/**
	 * エラーメッセージを返す。
	 * Return:
	 * @return String	エラーメッセージ
	 */
	public String getMessage() {
		if (getLocalCauseNo() < MESSAGE.length) {
			return MESSAGE[getLocalCauseNo()];
		} else {
			return "";
		}
	}
}
