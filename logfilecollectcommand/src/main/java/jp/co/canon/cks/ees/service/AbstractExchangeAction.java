/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : AbstractExchangeAction.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/06
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */
package jp.co.canon.cks.ees.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.canon.cks.ees.service.model.ExchangeRuleModel;


/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	フィルタリングされる文字列の変換を実際に行う抽象クラスを定義する。
 *	フィルタリングされる文字列の範囲を検索する場合、行頭から行末、行末から行頭の
 *	変換が考えられるが、２つのロジックでは複雑になるため、この抽象クラスを使って処理を簡単にする
 *
 * @author Tomomitsu TATEYAMA
 */
public abstract class AbstractExchangeAction {
	protected ExchangeRuleModel rule = null;
	private Pattern startPattern = null;


	/**
	 * ExchangeRuleModelを指定して構築する。
	 * @param m	変換するルールを定義したExchangeRuleModel
	 */
	public AbstractExchangeAction(ExchangeRuleModel m) {
		if (m == null) throw new IllegalArgumentException("ExchangeRuleModel is null");
		rule = m;
		startPattern = Pattern.compile(rule.getStartKey());
	}
	/**
	 * 指定の位置から行末方向へ終了文字列位置を検索して返す
	 * Parameter:
	 * 	@param	startPos	開始文字の検索位置
	 *	@param	v			検索対象の文字列
	 * Result:
	 *	@return int	終了キーワードが見つかった位置
	 */
	public abstract int getEndIndex(int startPos, String v);
	/**
	 * 指定の位置から行頭方向へ終了文字列位置を検索して返す
	 * Parameter:
	 * 	@param	startPos	開始文字の検索位置
	 *	@param	v			検索対象の文字列
	 * Result:
	 *	@return int	終了キーワードが見つかった位置
	 */
	public abstract int getEndIndexR(int endPos, String v);
	/**
	 * 変換対象の文字に対して初期化を行う。
	 * サブクラスでの実装が必須ではないため、こちらで実態を定義する
	 * Parameter:
	 * 	@param	value	対象の文字列
	 */
	public void init(String value) {
	}
	/**
	 * 指定したルールに従い変換した文字列の追加を行う
	 * Parameter:
	 * 	@param	v	変換対象も文字
	 * 	@param	b	出力先もStringBuffer
	 */
	protected void appendChangeValue(String v, StringBuffer b) {
		switch (rule.getRule()) {
		case ExchangeRuleModel.ALL_CHANGE_RULE:
			b.append(rule.getChangeStr());
			break;
		case ExchangeRuleModel.CHAR_REPLACE_RULE1:
			for (int i=0; i < v.length(); i++) {
				b.append(rule.getChangeStr());
			}
			break;
		case ExchangeRuleModel.CHAR_REPLACE_RULE2:
			for (int i=0; i < v.length(); i++) {
				char c = v.charAt(i);
				if (rule.getExclusionStr() != null && 
						rule.getExclusionStr().indexOf(c) == -1) {
					b.append(rule.getChangeStr());
				} else {
					b.append(c);
				}
			}
			break;
		}
	}
	/**
	 * 行末方向へ変換文字を探して、変換する
	 * Parameter:
	 * 	@param	value	変換前の文字列
	 * Result:
	 *	@return String	変換後の文字列
	 */
	public String action(String value) {
		StringBuffer b = new StringBuffer();
		int last = 0;
		Matcher m = startPattern.matcher(value);
		init(value);
		do {
			if (!m.find(last)) break;
			b.append(value.substring(last, m.end()));
			last = getEndIndex(m.end(), value);
			if (last != -1) {
				if (m.end() < last) {
					appendChangeValue(value.substring(m.end(), last), b);
				}
			} else {
				if (m.end() + 1 < value.length()) {
					appendChangeValue(value.substring(m.end()), b);
				}
				break;
			}
		} while (rule.isAllReplace());
		if (last != -1) b.append(value.substring(last));
		return b.toString();
	}
	/**
	 * 行頭方向へ変換文字を探して、変換する
	 * Parameter:
	 * 	@param	value	変換前の文字列
	 * Result:
	 *	@return String	変換後の文字列
	 */
	public String actionR(String value) {
		StringBuffer b = new StringBuffer();
		Matcher m = startPattern.matcher(value);
		init(value);
		java.util.List l = new java.util.ArrayList();
		while (m.find()) {
			l.add(new Integer(m.start()));
		}
		
		int start = value.length();
		for (int i=l.size()-1; i >= 0; i--) {
			Integer end = (Integer)l.get(i);
			if (end.intValue() <= start) {
				b.insert(0, value.substring(end.intValue(), start));
				start = getEndIndexR(end.intValue(), value);
				if (start != -1) {
					start++;
					StringBuffer temp = new StringBuffer();
					appendChangeValue(value.substring(start, end.intValue()), temp);
					b.insert(0, temp);
				} else {
					b.insert(0, value.substring(0, end.intValue()));
				}
			}
			if (!rule.isAllReplace()) break;
		}
		if (start != -1) b.insert(0, value.substring(0, start));
		return b.toString();
	}
}
