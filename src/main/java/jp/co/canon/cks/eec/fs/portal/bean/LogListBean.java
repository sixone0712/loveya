/**
 * Title: Equipment Engineering Support System.
 *		 - Log Upload Service
 *
 * File : LogListBean.java
 *
 * Author: Tomokazu OKUMURA
 * Date: 2010/12/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 * 2011/01/06 メソッドコメント追加。
 */

package jp.co.canon.cks.eec.fs.portal.bean;

import java.util.List;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * ファイル種類をリスト化して格納するJavaBeans
 * @author Tomokazu OKUMURA
 */
public class LogListBean {


	/**
	 * ファイル種類の一覧リスト
	 */
	private List list_;

	/**
	 * ファイル種類の一覧を取得します。
	 * @return ファイル種類の一覧リスト
	 */
	public List getLogInfoList() {
		return list_;
	}

	/**
	 * ファイル種類の一覧を設定します。
	 * @param loginfo ファイル種類の一覧リスト
	 */
	public void setLogInfoList(List loginfo) {
		this.list_ = loginfo;
	}

	/**
	 * ログ種類を取得します。
	 * @param index インデックス
	 * @return ログ種類
	 */
	public LogInfoBean getLogInfo(int index) {
		return (LogInfoBean) list_.get(index);
	}

	/**
	 * ログ種類を設定します。
	 * @param index インデックス
	 * @param loginfo ログ種類
	 */
	public void setLogInfo(int index, LogInfoBean loginfo) {
		this.list_.set(index, loginfo);
	}

	/**
	 * リストの登録数を取得します。
	 * @return リストの登録数
	 */
	public int getLogListCount() {
		if (list_ != null) {
			return this.list_.size();
		}
		return 0;
	}
}
