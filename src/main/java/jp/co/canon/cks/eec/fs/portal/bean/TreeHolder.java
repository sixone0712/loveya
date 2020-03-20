/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : TreeHolder.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/11/25
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.fs.portal.bean;

import java.util.List;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * 装置の階層情報を保持するクラス。
 * 
 * @author Tomomitsu TATEYAMA
 */
public class TreeHolder {
	/** 階層名　*/
	private String name = null;
	/** 階層に所属する対象のリスト　*/
	private List childList = new java.util.ArrayList();
	/** 子階層のリスト　*/
	private List branchList = new java.util.ArrayList();
	/** 階層ID　*/
	private String id = null;
	
	/**
	 * 階層名を指定して構築する
	 * 
	 * @param n	階層名
	 */
	public TreeHolder(String n) {
		name = n;
	}
	/**
	 * 階層に表示する対象リストを配列で返す。
	 *
	 * Parameters/Result:
	 *　 @return	対象リスト
	 */
	public ToolInfo[] getChildren() {
		return (ToolInfo[])childList.toArray(new ToolInfo[0]);
	}
	/**
	 * 子の階層をリストで返す。
	 *
	 * Parameters/Result:
	 *　 @return	子階層リスト
	 */
	public TreeHolder[] getBranch() {
		return (TreeHolder[])branchList.toArray(new TreeHolder[0]);
	}
	/**
	 * 階層名を返す。
	 *
	 * Parameters/Result:
	 *　 @return	階層名
	 */
	public String getName() {
		return name;
	}
	/**
	 * 子の階層を追加する
	 *
	 * Parameters/Result:
	 *　 @param newVal	追加する子階層
	 */
	public void addBranch(TreeHolder newVal) {
		branchList.add(newVal);
	}
	/**
	 * 子の対象を追加する。
	 *
	 * Parameters/Result:
	 *　 @param newVal	追加する対象
	 */
	public void addChild(ToolInfo newVal) {
		childList.add(newVal);
	}
	/**
	 *　設定されている階層IDを返す。
	　* Return:
	 * 	@return 階層ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * 階層IDを設定する。
	 * Parameters:
	 * 	@param id 階層ID
	 */
	public void setId(String id) {
		this.id = id;
	}
}
