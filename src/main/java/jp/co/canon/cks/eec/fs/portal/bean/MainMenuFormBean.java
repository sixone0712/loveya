/**
 *
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : MainMenuFormBean.java
 *
 * Author: Tomokazu OKUMURA
 * Date: 2011/11/16
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.fs.portal.bean;

import java.util.List;


/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * メインメニュー画面の画面情報を保持します。
 *
 * @author Tomokazu OKUMURA
 * ===============================================
 * 2011-11-30	T.Tateyama	不要なメンバーの削除
 */
public class MainMenuFormBean {

	/** 構成表示一覧 */
	private List displayList;
	/**
	 * エラーメッセージ
	 */
	private String errorMessage;

	/**
	 * デフォルトコンストラクタ
	 */
	public MainMenuFormBean() {
		errorMessage = "";
	}

	/**
	 *　設定されている構成表示一覧を返す。
	 * Return:
	 * 	@return 構成表示一覧
	 */
	public final List getDisplayList() {
		return displayList;
	}

	/**
	 * 構成表示一覧を設定する。
	 * Parameters:
	 * 	@param list セットする構成表示一覧
	 */
	public final void setDisplayList(List list) {
		this.displayList = list;
	}

	/**
	 *　設定されているエラーメッセージを返す。
	 * Return:
	 * 	@return エラーメッセージ
	 */
	public final String getError() {
		return errorMessage;
	}

	/**
	 * エラーメッセージを設定する。
	 * Parameters:
	 * 	@param error セットするエラーメッセージ
	 */
	public final void setError(String error) {
		this.errorMessage = error;
	}

}
