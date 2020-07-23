/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : LogItem.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/01/04
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	FileとDirectoryの両方をオブジェクトを表現するためのクラス。継承はしていないが、LogFileの拡張版
 *	将来的にLogFileとの入れ替えも検討する。
 *
 * @author Tomomitsu TATEYAMA
 */
public class LogItem {
	private String name = null;
	private long size =0;
	private long timestamp = 0;
	private boolean directory = false;
	
	/**
	 * デフォルトコンストラクタ
	 */
	public LogItem() {
	}
	/**
	 * ファイル名、サイズ、時間を指定して構築する
	 * @param name	ファイル名
	 * @param size	サイズ（バイト）
	 * @param time	更新時間
	 */
	public LogItem(String name, long size, long time) {
		setName(name);
		setSize(size);
		setTimestamp(time);
	}
	/**
	 * ファイル名、サイズ、時間、ディレクトリ種別を指定して構築する
	 * @param name	ファイル名
	 * @param size	サイズ（バイト）
	 * @param time	更新時間
	 * @param dir	ディレクトリかどうかを示すフラグ（trueの場合はディレクトリ）
	 */
	public LogItem(String name, long size, long time, boolean dir) {
		this(name, size, time);
		setDirectory(dir);
	}
	/**
	 * ファイル名を返す。このファイル名は、パス名を含んだ名称となる。
	 * @return ファイル名
	 */
	public String getName() {
		return name;
	}
	/**
	 * ファイル名を設定する。
	 * @param name セットするファイル名
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * ファイルのサイズを返す。
	 * @return ファイルサイズ　バイト数。
	 */
	public long getSize() {
		return size;
	}
	/**
	 * ファイルのサイズを設定する。設定する値はバイト数とする
	 * @param size セットする ファイルのバイト数
	 */
	public void setSize(long size) {
		this.size = size;
	}
	/**
	 * ファイルの更新時間を返す。
	 * @return ファイルの更新時間をlongの値で返す。
	 */
	public long getTimestamp() {
		return timestamp;
	}
	/**
	 * ファイルの更新時間を設定する。
	 * @param timestamp セットする更新時間をlong値で設定する。
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 *　設定されているdirectoryを返す。
	 * Return:
	 * 	@return directory
	 */
	public boolean isDirectory() {
		return directory;
	}
	/**
	 * directoryを設定する。
	 * Parameters:
	 * 	@param directory セットする directory
	 */
	public void setDirectory(boolean directory) {
		this.directory = directory;
	}
	/**
	 * 文字列表記を返す
	 */
	public String toString() {
		return new StringBuffer().append(getName())
		.append(",").append(getTimestamp())
		.append(",").append(getSize())
		.append(",").append((isDirectory() ? "D" : "F")).toString();
	}

}
