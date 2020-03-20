/**
 *
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : FileListInfo.java
 *
 * Author: Tomokazu OKUMURA
 * Date: 2010/12/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.fs.portal.bean;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * 収集ファイルをリスト化して格納するJavaBeans。
 * @author Tomokazu OKUMURA
 */
public class FileListInfo implements Serializable {

	/** シリアライズバージョン番号 */
	private static final long serialVersionUID = -827769751942661752L;
	/** 収集ファイルリスト */
	private List filelist;

	/**
	 *　設定されている収集ファイルリストを返す。
	 * Return:
	 * 	@return 収集ファイルリスト
	 */
	public final List getFileList() {
		return filelist;
	}

	/**
	 * 収集ファイルリストを設定する。
	 * Parameters:
	 * 	@param list セットする収集ファイルリスト
	 */
	public void setFileList(List list) {
		this.filelist = list;
	}

	/**
	 *	収集ファイルリストの指定位置のデータを返す。
	 *
	 * Parameters:
	 *　 @param index 位置
	 * Result:
	 *　 @return データ
	 */
	public final FileInfoBean getFileInfo(int index) {
		return (FileInfoBean) filelist.get(index);
	}

	/**
	 *	収集ファイルリストの指定位置にデータを設定する。
	 *
	 * Parameters/Result:
	 *　 @param index 位置
	 *　 @param fileinfo データ
	 */
	public final void setFileInfo(int index, FileInfoBean fileinfo) {
		this.filelist.set(index, fileinfo);
	}

	/**
	 *	収集ファイルリストのデータ数を返す。
	 *
	 * Parameters/Result:
	 *　 @return データ数
	 */
	public final int getFileListCount() {
		if (filelist != null) {
			return this.filelist.size();
		}
		return 0;
	}

}
