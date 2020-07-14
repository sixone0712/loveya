/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : DefultFileNameCheckRuleModel.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/03
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	FileNameCheckRuleModelのデフォルト実装
 *
 * @author Tomomitsu TATEYAMA
 */
public class DefaultFileNameCheckRuleModel implements FileNameCheckRuleModel {
	private int treeIndex = -1;
	private int type = -1;
	private java.util.List list = new java.util.ArrayList();
	
	/**
	 * デフォルトコンストラクタ
	 */
	public DefaultFileNameCheckRuleModel() {
	}
	/**
	 * インデックスと種類を設定して構築する
	 * @param index	パスの位置
	 * @param type	種類
	 */
	public DefaultFileNameCheckRuleModel(int index, int type) {
		setTreeIndex(index);
		setType(type);
	}
	/* 
	 * getTreeIndexの実装
	 * @see jp.co.canon.cks.ees.service.model.FileNameCheckRuleModel#getTreeIndex()
	 */
	public int getTreeIndex() {
		return treeIndex;
	}

	/* 
	 * getTypeの実装
	 * @see jp.co.canon.cks.ees.service.model.FileNameCheckRuleModel#getType()
	 */
	public int getType() {
		return type;
	}

	/**
	 * treeIndexを設定する。
	 * Parameters:
	 * @param treeIndex セットする treeIndex
	 */
	public void setTreeIndex(int treeIndex) {
		this.treeIndex = treeIndex;
	}

	/**
	 * typeを設定する。
	 * Parameters:
	 * @param type セットする type
	 */
	public void setType(int type) {
		this.type = type;
	}

	/* 
	 * getRulesの実装
	 * @see jp.co.canon.cks.ees.service.model.FileNameCheckRuleModel#getRules()
	 */
	public String[] getRules() {
		return (String[])list.toArray(new String[0]);
	}
	/**
	 * チェックをするためのルールを登録する
	 *
	 * Parameter:
	 * 	@param	newVal	ルールを記述した文字列
	 */
	public void addRule(String newVal) {
		if (!list.contains(newVal)) {
			list.add(newVal);
		}
	}
	/**
	 * Iteratorを返す。
	 */
	public java.util.Iterator iteratorRule() {
		return list.iterator();
	}
}
