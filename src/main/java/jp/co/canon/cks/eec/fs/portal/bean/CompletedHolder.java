/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : CompletedHolder.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/11/26
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.fs.portal.bean;

import java.io.Serializable;


/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *
 *　完了画面で内部のデータをセッションに保持するためのクラス。
 *
 * @author Tomomitsu TATEYAMA
 */
public class CompletedHolder implements Serializable {
	/** */
	private static final long serialVersionUID = -4666361211640688687L;
	
	/** ソートカラム*/
	private int sortColumn = 0;	// 要求受付番号
	/** ソート種別*/
	private int sortType = -1;
	/** 完了一覧リスト　*/
	private RequestListBean requestList = null;
	
	/**
	 *　設定されているソートカラムを返す。
	 * Return:
	 * 	@return カラム番号を返す。
	 */
	public int getSortColumn() {
		return sortColumn;
	}
	/**
	 * ソートカラムを設定する。
	 * Parameters:
	 * 	@param sortColumn カラム番号
	 */
	public void setSortColumn(int sortColumn) {
		this.sortColumn = sortColumn;
	}
	/**
	 *　設定されているソート種別（降順、昇順）を返す。
	 * Return:
	 * 	@return ソート種別（-1もしくは1）を返す
	 */
	public int getSortType() {
		return sortType;
	}
	/**
	 * ソート種別を設定する。
	 * Parameters:
	 * 	@param sortType ソート種別（-1:降順　1:昇順）
	 */
	public void setSortType(int sortType) {
		this.sortType = sortType;
	}
	/**
	 *　設定されている完了一覧を返す。
	 * Return:
	 * 	@return 完了一覧を保持する要求リスト	
	 */
	public RequestListBean getRequestList() {
		return requestList;
	}
	/**
	 * 完了一覧を設定する。
	 * Parameters:
	 * 	@param requestList 完了一覧が設定されている要求リスト
	 */
	public void setRequestList(RequestListBean requestList) {
		this.requestList = requestList;
	}

}
