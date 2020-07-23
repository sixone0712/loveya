/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : FilteringOutputStream.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/04
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.canon.cks.ees.service.model.ExchangeRuleModel;
import jp.co.canon.cks.ees.service.model.FilterModel;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	フィルタリングした内容をoutputStreamに出力する。
 *
 * @author Tomomitsu TATEYAMA
 */
public class FilteringOutputStream extends OutputStream {
	private OutputStream outputStream = null;
	private StringBuffer buffer = null;
	private FilterModel model = null;
	private java.util.HashMap ruleMap = new java.util.HashMap();
	
	// ルールに従い４つのサブクラスを定義する
	class ExchangeAction extends AbstractExchangeAction{
		protected Pattern endPattern = null;
		protected Matcher endMatcher = null;
		
		public ExchangeAction(ExchangeRuleModel m) {
			super(m);
			endPattern = Pattern.compile(rule.getEndKey());
		}
		public void init(String value) {
			endMatcher = endPattern.matcher(value);
		}
		public int getEndIndex(int startPos, String v) {
			if (endMatcher.find(startPos)) {
				return endMatcher.start();
			} else {
				return -1;
			}
		}
		public int getEndIndexR(int startPos, String v) {
			int r = -1;
			while (true) {
				if (endMatcher.find()) {
					if (endMatcher.start() >= startPos) break;
					r = endMatcher.start();
				} else {
					break;
				}
			}
			return r;
		}
	}
	class ExchangeActionNoRegex extends AbstractExchangeAction{
		public ExchangeActionNoRegex(ExchangeRuleModel m) {
			super(m);
		}
		public int getEndIndex(int startPos, String v) {
			return v.indexOf(rule.getEndKey(), startPos);
		}
		public int getEndIndexR(int startPos, String v) {
			return v.lastIndexOf(rule.getEndKey(), startPos); 
		}
	}

	/**
	 * 出力先とFilterModelを指定して構築する
	 * @param out
	 * @param model
	 */
	public FilteringOutputStream(OutputStream out, FilterModel model) {
		setModel(model);
		outputStream = out;
	}
	/**
	 *　設定されているmodelを返す。
	 * Return:
	 * 	@return model
	 */
	public FilterModel getModel() {
		return model;
	}
	/**
	 * modelを設定する。
	 * Parameters:
	 * 	@param model セットする model
	 */
	private void setModel(FilterModel model) {
		if (model == null) {
			throw new IllegalArgumentException("set the FilterModle is null");
		}
		this.model = model;
	}
	/**
	 * 
	 *	TODO This section is description for function;
	 *
	 * Parameter:
	 * 	@param	
	 *
	 * Result:
	 *	@return void
	 *
	 */
	private void putRuleData(String key, ExchangeRuleModel[] models) {
		AbstractExchangeAction[] actions = new AbstractExchangeAction[models.length];
		for (int i=0; i < models.length; i++) {
			if (models[i].isRegexEndKey()) {
				actions[i] = new ExchangeAction(models[i]);
			} else {
				actions[i] = new ExchangeActionNoRegex(models[i]);
			}
		}
		ruleMap.put(key, actions);
	}
	/**
	 * 
	 *	TODO This section is description for function;
	 *
	 * Parameter:
	 * 	@param	
	 *
	 * Result:
	 *	@return String
	 *
	 */
	private String convert(String key, String value) {
		AbstractExchangeAction[] actions = (AbstractExchangeAction[])ruleMap.get(key);
		for (int i=0; i < actions.length; i++) {
			if (actions[i].rule.getDirection() == ExchangeRuleModel.NORMAL_DIRCTION)
				value = actions[i].action(value);
			else
				value = actions[i].actionR(value);
		}		
		return value;
	}
	/**
	 * フィルタした結果文字列を返す
	 * Parameter:
	 * 	@param	value	変換前の文字列
	 * Result:
	 *	@return String	フィルタリング結果の文字列
	 */
	protected String filter(String value) {
		if (getModel() == null || value == null) return "";
		String[] logKeys = getModel().getLogKeys();
		for (int i=0; i < logKeys.length; i++) {
			if (value.startsWith(logKeys[i])) {
				if (!ruleMap.containsKey(logKeys[i])) {
					putRuleData(logKeys[i], getModel().getChangeRules(logKeys[i]));
				}
				String v = value.substring(logKeys[i].length());
				v = convert(logKeys[i], v);
				value = logKeys[i] + v;
			}
		}
		return value;
	}
	/* 
	 * writeの実装
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int arg0) throws IOException {
		// １行のデータができるまでバッファリングする。
		if (arg0 == '\r' || arg0 == '\n') {
			if (buffer != null) {
				// writeした内容をフィルタをかけて書き出す
				outputStream.write(filter(buffer.toString()).getBytes());
				buffer = null;				
			}
			outputStream.write(arg0);
		} else {
			if (buffer == null) buffer = new StringBuffer();
			buffer.append((char)arg0);
		}
	}
	/**
	 * ファイルをクローズする。内部で保持しているOutputStreamも同時にクローズする
	 */
	public void close() throws IOException {
		// クローズする前にフラッシュしなければならない。
		if (buffer != null) {
			outputStream.write(filter(buffer.toString()).getBytes());
		}
		super.close();
		outputStream.close();
	}
}
