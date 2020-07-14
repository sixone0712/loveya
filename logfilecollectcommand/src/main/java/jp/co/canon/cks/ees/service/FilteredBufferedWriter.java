/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : FilterdBufferdWriter.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/11
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.canon.cks.ees.service.model.ExchangeRuleModel;
import jp.co.canon.cks.ees.service.model.FilterModel;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	ファイルへフィルタを行いながら書き込むため、ただしこのクラスではパフォーマンスを一番に
 *	考えているため、write(String)メソッドのみフィルタを行うものとする。
 *	また、この関数には１行のデータがそのままこない場合（２回に分かれて１行を記述）すると
 *	不具合になる。
 *	その点も考慮して使う必要がある。
 * @author Tomomitsu TATEYAMA
 */
public class FilteredBufferedWriter extends BufferedWriter {
	
	private String lineSeparator;
	private FilterModel model = null;
	private java.util.HashMap ruleMap = new java.util.HashMap();

	// ルールに従い２つのサブクラスで変換処理を行う
	/**
	 * 終わりの文字列検索に正規表現を使うパターンを定義
	 */
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
	//------------
	/**
	 * 終わりの文字列検索に単純一致を使うパターンを定義
	 */
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
	 * Writerとモデルを設定して構築する
	 * @param out
	 * @param model
	 */
	public FilteredBufferedWriter(Writer out, FilterModel model, String lineSeparator) {
		super(out);
		setModel(model);
		if(lineSeparator != null){
			this.lineSeparator = lineSeparator;
		} else {
			this.lineSeparator = System.getProperty("line.separator");
		}
	}
	/**
	 *　設定されているmodelを返す。
	 * Return:
	 * 	@return model
	 */
	protected FilterModel getModel() {
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
	 * 置き換えルールを作成し、キャッシュに保持する
	 *
	 * Parameter:
	 * 	@param	key	ログキー
	 * 	@param	models	変換ルールのモデルの配列
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
	 * ルールを適用して変換する
	 *
	 * Parameter:
	 * 	@param	key		ログキー
	 * 	@param	value	変換する文字列
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
		// 最初に固定部分の文字を抜き出す
		String header = "";
		if (getModel().getLogKeyStartPos() > 0 && value.length() > getModel().getLogKeyStartPos()) {
			header = value.substring(0, getModel().getLogKeyStartPos());
			value = value.substring(getModel().getLogKeyStartPos());
		}
		//------------------------
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
		// 最後に文字を追加する
		return header + value;
	}

	/* 
	 * 文字列をファイルにフィルタしながら書き込む
	 * 
	 * @see java.io.Writer#write(java.lang.String)
	 */
	public void writeLine(String str) throws IOException {
		super.write(filter(str));
		super.write(lineSeparator);
	}
}
