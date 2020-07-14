/**
 * 
 * File : FileList.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/11/30
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	Java5以降では不要であるが、型宣言を簡潔化するため、ファイル一覧を保持するクラスを定義する
 *
 * @author Tomomitsu TATEYAMA
 */
public class FileList extends java.util.ArrayList {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;
	/**
	 * コンストラクタ
	 */
	public FileList() {
		super();
	}
	
	/**
	 * 登録されている数を返す
	 * Result:
	 *	@return int	登録数を返す
	 */
	public int getCount() {
		return size();
	}
	/**
	 * 登録されているLogFileを配列で返す。
	 * Result:
	 *	@return LogFile[]	登録されているLogFile
	 */
	public LogFile[] getFiles() {
		return (LogFile[])toArray(new LogFile[0]);
	}
	/**
	 * 指定位置のLogFileを返す。
	 * Result:
	 *	@return LogFile	指定された位置に登録されているLogFile
	 */
	public LogFile getFileAt(int index) {
		return (LogFile)get(index);
	}
	/**
	 * LogFileをリストの最後に追加する。
	 * Parameter:
	 * 	@param	newVal	追加するLogFile
	 */
	public void addFile(LogFile newVal) {
		super.add(newVal);
	}
}
