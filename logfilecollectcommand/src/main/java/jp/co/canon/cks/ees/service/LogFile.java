/**
 * 
 * File : LogFile.java
 * 
 * Description:
 *	ログファイルの情報を保持するクラスです。ログファイルの名称、サイズ、更新日付を返します。
 *
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/01
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	装置ログの情報を返すクラス。
 *
 * @author Tomomitsu TATEYAMA
 */
public class LogFile {
	private String name = null;
	private long size =0;
	private long timestamp = 0;

	/**
	 * デフォルトコンストラクタ
	 */
	public LogFile() {
	}
	/**
	 * ファイル名、サイズ、時間を指定して構築する
	 * @param name	ファイル名
	 * @param size	サイズ（バイト）
	 * @param time	更新時間
	 */
	public LogFile(String name, long size, long time) {
		setName(name);
		setSize(size);
		setTimestamp(time);
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
	 * 文字列表記を返す
	 */
	public String toString() {
		return new StringBuffer().append(getName())
		.append(",").append(getTimestamp())
		.append(",").append(getSize()).toString();
	}
}
