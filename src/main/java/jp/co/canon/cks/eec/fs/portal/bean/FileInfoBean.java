/**
 *
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : FileInfoBean.java
 *
 * Author: Tomokazu OKUMURA
 * Date: 2010/12/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.fs.portal.bean;

import java.math.BigDecimal;


/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * 要求ファイルを格納するJavaBeans
 * @author Tomokazu OKUMURA
 * ====================================================================
 * Change History:
 * 2011-01-18	T.Tateyama	ファイルのサイズの単位変換を切り上げに変更
 */
public class FileInfoBean implements Comparable {

	/** ID */
	private long fileId;
	/** 状態 */
	private String fileStatus;
	/** ログ種別 */
	private String logId;
	/** ファイル名 */
	private String fileName;
	/** ファイルサイズ */
	private long fileSize;
	/** ファイル日付 */
	private String fileDate;
	/** ファイルパス */
	private String filePath;
	/** ファイルかディレクトリかを示す変数　*/
	private boolean file = true;
	
	/**
	 *	compareToの実装
	 *
	 * Parameters:
	 *　 @param info 対象
	 * Result:
	 *　 @return 結果
	 */
	public final int compareTo(Object info) {
		if (info instanceof FileInfoBean) {
			FileInfoBean temp = (FileInfoBean)info;
			return fileDate.compareTo(temp.fileDate);
		}
		throw new IllegalArgumentException("not support object");
	}
	/**
	 *　設定されているidを返す。
	 * Return:
	 * 	@return id
	 */
	public final long getId() {
		return fileId;
	}

	/**
	 * idを設定する。
	 * Parameters:
	 * 	@param id セットする id
	 */
	public final void setId(long id) {
		this.fileId = id;
	}

	/**
	 *　設定されているstatusを返す。
	 * Return:
	 * 	@return status
	 */
	public final String getStatus() {
		return fileStatus;
	}

	/**
	 * statusを設定する。
	 * Parameters:
	 * 	@param status セットする status
	 */
	public final void setStatus(String status) {
		this.fileStatus = status;
	}

	/**
	 *　設定されているログ種別を返す。
	 * Return:
	 * 	@return ログ種別
	 */
	public final String getLogId() {
		return logId;
	}

	/**
	 * ログ種別を設定する。
	 * Parameters:
	 * 	@param logid セットするログ種別
	 */
	public final void setLogId(String logid) {
		this.logId = logid;
	}

	/**
	 *　設定されているファイル名を返す。
	 * Return:
	 * 	@return ファイル名
	 */
	public final String getName() {
		return fileName;
	}

	/**
	 * ファイル名を設定する。
	 * Parameters:
	 * 	@param name セットするファイル名
	 */
	public final void setName(String name) {
		this.fileName = name;
	}

	/**
	 *　設定されているファイルサイズを返す。
	 * Return:
	 * 	@return ファイルサイズ
	 */
	public final long getSize() {
		return fileSize;
	}

	/**
	 * ファイルサイズを設定する。
	 * Parameters:
	 * 	@param size セットするファイルサイズ
	 */
	public final void setSize(long size) {
		this.fileSize = size;
	}

	/**
	 *　設定されているファイル日付を返す。
	 * Return:
	 * 	@return ファイル日付
	 */
	public final String getTimestamp() {
		return fileDate;
	}

	/**
	 * ファイル日付を設定する。
	 * Parameters:
	 * 	@param date セットするファイル日付
	 */
	public final void setTimestamp(String date) {
		this.fileDate = date;
	}

	/**
	 *　設定されているファイルパスを返す。
	 * Return:
	 * 	@return ファイルパス
	 */
	public final String getPath() {
		return filePath;
	}

	/**
	 * ファイルパスを設定する。
	 * Parameters:
	 * 	@param path セットするファイルパス
	 */
	public final void setPath(String path) {
		this.filePath = path;
	}

	/**
	 *	ファイルサイズをKBで取得します。
	 *
	 * Result:
	 *　 @return ファイルサイズ[KB]
	 */
	public final long getSizeKB() {
		BigDecimal decimal = new BigDecimal(String.valueOf(fileSize));
		return decimal.divide(new BigDecimal(Constants.FILESIZE_DIVIDE),
							  Constants.FILESIZE_SCALE, BigDecimal.ROUND_UP).longValue();
	}

	/**
	 *　設定されているfileを返す。
	　* Return:
	 * 	@return file
	 */
	public boolean isFile() {
		return file;
	}

	/**
	 * fileを設定する。
	 * Parameters:
	 * 	@param file セットする file
	 */
	public void setFile(boolean file) {
		this.file = file;
	}
}
