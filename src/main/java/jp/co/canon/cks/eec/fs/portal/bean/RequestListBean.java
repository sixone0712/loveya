/**
 * Title: Equipment Engineering Support System.
 *		 - Log Upload Service
 *
 * File : RequestListBean.java
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
 * 要求データをリスト化して格納するJavaBeans
 * @author Tomokazu OKUMURA
 * ============================================================================
 * 2013-06-30	T.Tateyama	状況不明な装置リストのSET/GET
 */
public class RequestListBean implements Serializable {

	/** シリアライズ */
	private static final long serialVersionUID = 8627884246770273026L;
	/** 要求一覧を保持するリスト */
	private List requestlist_;

	private ToolInfo[] disconnectTools = new ToolInfo[0];
	
	/**
	 * 要求一覧を返す
	 *
	 * Parameters/Result:
	 *　 @return　要求一覧
	 */
	public List getRequestList() {
		return requestlist_;
	}
	/**
	 * 要求一覧を設定する。
	 *
	 * Parameters/Result:
	 *　 @param requestinfo	要求一覧
	 */
	public void setRequestList(List requestinfo) {
		this.requestlist_ = requestinfo;
	}
	/**
	 * 指定された位置の要求情報を取得する。
	 *
	 * Parameters/Result:
	 *　 @param index	要求情報を持ったリスト内のインデックス禹
	 *　 @return	要求情報
	 */
	public RequestInfoBean getRequestInfo(int index) {
		return (RequestInfoBean)requestlist_.get(index);
	}

	/**
	 * 指定した位置に要求情報を設定する
	 *
	 * Parameters/Result:
	 *　 @param index	要求情報を設定するリスト内のインデックス
	 *　 @param requestinfo	要求情報
	 */
	public void setRequestInfo(int index, RequestInfoBean requestinfo) {
		this.requestlist_.set(index, requestinfo);
	}

	/**
	 * 要求情報の数を返す。
	 *
	 * Parameters/Result:
	 *　 @return	要求情報リスト内の登録数
	 */
	public int getRequestListCount() {
		if (requestlist_ != null) {
			return this.requestlist_.size();
		}
		return 0;
	}

	/**
	 * 要求情報内の指定された要求受付番号が一致するを情報を取得する。
	 *
	 * Parameters/Result:
	 *　 @param requestno	要求受付番号
	 *　 @return	要求受付番号が一致する要求情報　存在しなければnull
	 */
	public RequestInfoBean get(String requestno) {
		if (requestlist_ != null) {
			for (int i = 0; i < requestlist_.size(); i++) {
				RequestInfoBean element = (RequestInfoBean)requestlist_.get(i);
				if (element.getRequestNo().equals(requestno)) {
					return element;
				}
			}
		}
		return null;
	}
	/**
	 *　設定されている状況不明なホスト一覧を返す。
	 * Return:
	 * @return errorMessages
	 */
	public ToolInfo[] getDisconnectTools() {
		return disconnectTools;
	}
	/**
	 * 状況不明なホスト一覧を設定する。
	 * Parameters:
	 * @param errorMessages セットする errorMessages
	 */
	public void setDisconnectTools(ToolInfo[] unknownTools) {
		this.disconnectTools = unknownTools;
	}
	
}
