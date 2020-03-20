/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : ProgressHolder.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/11/26
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.fs.portal.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *　要求一覧（状況照会）画面の表示データをセッションに格納するために利用する。
 *
 * @author Tomomitsu TATEYAMA
 */
public class RequestHolder implements Serializable {
	/** シリアライズバージョン番号 */
	private static final long serialVersionUID = -8427636755200051311L;
	/** 選択リスト　*/
	private List selectedRequets = new ArrayList();
	/** ソートカラム　*/
	private int sortColumn = 4;	// 時間
	/** ソート種別　*/
	private int sortType = -1;
	/** 要求一覧　*/
	private RequestListBean requestList = null;
	
	/**
	 *　設定されている選択要求番号のリストを返す。
	 * Return:
	 * 	@return 選択要求番号を保持したリスト
	 */
	public List getSelectedRequets() {
		return selectedRequets;
	}
	/**
	 * 選択要求番号のリストを設定する。
	 * Parameters:
	 * 	@param selectedRequets 選択要求番号を保持したリスト
	 */
	public void setSelectedRequets(List selectedRequets) {
		this.selectedRequets = selectedRequets;
	}
	/**
	 *　設定されているソートカラムを返す。
	 * Return:
	 * 	@return カラム番号
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
	 *　設定されているソート種別を返す。
	 * Return:
	 * 	@return ソート種別
	 */
	public int getSortType() {
		return sortType;
	}
	/**
	 * ソート種別を設定する。
	 * Parameters:
	 * 	@param sortType ソート種別（-1:降順 1:昇順)
	 */
	public void setSortType(int sortType) {
		this.sortType = sortType;
	}
	/**
	 *　設定されている要求一覧を返す。
	 * Return:
	 * 	@return 要求一覧
	 */
	public RequestListBean getRequestList() {
		return requestList;
	}
	/**
	 * 要求一覧を設定する。
	 * Parameters:
	 * 	@param requestList 要求一覧
	 */
	public void setRequestList(RequestListBean requestList) {
		this.requestList = requestList;
	}
}
