/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : DefaultFilterModel.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/04
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	FilterModelのデフォルト実装
 *
 * @author Tomomitsu TATEYAMA
 */
public class DefaultFilterModel implements FilterModel {
	private java.util.HashMap repository = new java.util.HashMap();
	private int logkeyStartPos = 15;
	
	/* 
	 * getLogKeysの実装
	 * @see jp.co.canon.cks.ees.service.model.FilterModel#getLogKeys()
	 */
	public String[] getLogKeys() {
		return (String[])repository.keySet().toArray(new String[0]);
	}
	/**
	 * ルールを定義する。ログキーに対してルールを追加する。LogKeyが登録されていない場合は
	 * リポジトリに新たに追加する
	 * Parameter:
	 * 	@param	key		ログのキー（データの先頭からの一致文字列
	 * 	@param	rule	ログキーに対応する変更ルール　ChangeRuleクラス型
	 * @see jp.co.canon.cks.ees.service.DefaultExchangeRuleModel
	 */
	public void addRule(String key, ExchangeRuleModel rule) {
		if (key == null) throw new IllegalArgumentException("key is null");
		if (rule == null) throw new IllegalArgumentException("rule is null");
		if (repository.containsKey(key)) {
			java.util.List l = (java.util.List)repository.get(key);
			l.add(rule);
		} else {
			java.util.Vector v = new java.util.Vector();
			v.add(rule);
			repository.put(key, v);
		}
	}
	/*
	 * getChangeRulesの実装
	 * @see jp.co.canon.cks.ees.service.FilerModel#getChangeRules()
	 */
	public ExchangeRuleModel[] getChangeRules(String key) {
		if (repository.containsKey(key)) {
			java.util.List l = (java.util.List)repository.get(key);
			return (ExchangeRuleModel[])l.toArray(new ExchangeRuleModel[0]);
		} else {
			return new ExchangeRuleModel[0];
		}
	}
	/**
	 *　設定されているlogkeyStartPosを返す。
	 * Return:
	 * 	@return logkeyStartPos
	 */
	public int getLogKeyStartPos() {
		return logkeyStartPos;
	}
	/**
	 * logkeyStartPosを設定する。
	 * Parameters:
	 * 	@param logkeyStartPos セットする logkeyStartPos
	 */
	public void setLogKeyStartPos(int logkeyStartPos) {
		this.logkeyStartPos = logkeyStartPos;
	}
}
