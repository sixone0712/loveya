/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : ChangeRule.java
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
 *	ExchangeRuleModelのデフォルト実装
 *
 * @author Tomomitsu TATEYAMA
 */
public class DefaultExchangeRuleModel implements ExchangeRuleModel {
	private String startKey = null;
	private String endKey = null;
	private int rule = 0;
	private String changeStr = null;
	private String exclusionStr = null;
	private boolean regexEndKey = true;
	private int direction = NORMAL_DIRCTION;
	private boolean allReplace = false;
	
	
	/**
	 *　設定されているexclusionStrを返す。
	 * Return:
	 * 	@return exclusionStr
	 */
	public String getExclusionStr() {
		return exclusionStr;
	}
	/**
	 * exclusionStrを設定する。
	 * Parameters:
	 * 	@param exclusionStr セットする exclusionStr
	 */
	public void setExclusionStr(String exclusionStr) {
		this.exclusionStr = exclusionStr;
	}
	/**
	 *　設定されているregexEndKeyを返す。
	 * Return:
	 * 	@return regexEndKey
	 */
	public boolean isRegexEndKey() {
		return regexEndKey;
	}
	/**
	 * regexEndKeyを設定する。
	 * Parameters:
	 * 	@param regexEndKey セットする regexEndKey
	 */
	public void setRegexEndKey(boolean regexEndKey) {
		this.regexEndKey = regexEndKey;
	}
	/* 
	 * getStartKeyの実装
	 * @see jp.co.canon.cks.ees.service.model.ExchangeRuleModel#getStartKey()
	 */
	public String getStartKey() {
		return startKey;
	}
	/**
	 * startKeyを設定する。
	 * Parameters:
	 * @param startKey セットする startKey
	 */
	public void setStartKey(String startKey) {
		this.startKey = startKey;
	}
	/* 
	 * getEndKeyの実装
	 * @see jp.co.canon.cks.ees.service.model.ExchangeRuleModel#getEndKey()
	 */
	public String getEndKey() {
		return endKey;
	}
	/**
	 * endKeyを設定する。
	 * Parameters:
	 * @param endKey セットする endKey
	 */
	public void setEndKey(String endKey) {
		this.endKey = endKey;
	}
	/* 
	 * getRuleの実装
	 * @see jp.co.canon.cks.ees.service.model.ExchangeRuleModel#getRule()
	 */
	public int getRule() {
		return rule;
	}
	/**
	 * ruleを設定する。
	 * Parameters:
	 * @param rule セットする rule
	 */
	public void setRule(int rule) {
		if (rule != ALL_CHANGE_RULE && 
				rule != CHAR_REPLACE_RULE1 && rule != CHAR_REPLACE_RULE2) {
			throw new IllegalArgumentException("rule is not support no:" + rule);
		}
		this.rule = rule;
	}
	/* 
	 * getChangeStrの実装
	 * @see jp.co.canon.cks.ees.service.model.ExchangeRuleModel#getChangeStr()
	 */
	public String getChangeStr() {
		return changeStr;
	}
	/**
	 * changeStrを設定する。
	 * Parameters:
	 * @param changeStr セットする changeStr
	 */
	public void setChangeStr(String changeStr) {
		this.changeStr = changeStr;
	}
	/**
	 *　設定されているdirectionを返す。
	 * Return:
	 * 	@return direction
	 */
	public int getDirection() {
		return direction;
	}
	/**
	 * directionを設定する。
	 * Parameters:
	 * 	@param direction セットする direction
	 */
	public void setDirection(int direction) {
		if (direction != NORMAL_DIRCTION && 
				direction != REVERSE_DIRECTON) {
			throw new IllegalArgumentException("direction is not support no:" + rule);
		}
		this.direction = direction;
	}
	/**
	 *　設定されているallReplaceを返す。
	 * Return:
	 * 	@return allReplace
	 */
	public boolean isAllReplace() {
		return allReplace;
	}
	/**
	 * allReplaceを設定する。
	 * Parameters:
	 * 	@param allReplace セットする allReplace
	 */
	public void setAllReplace(boolean allReplace) {
		this.allReplace = allReplace;
	}
}
