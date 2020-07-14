/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : FilterConfigurationSaxParser.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/16
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model.xml;


import jp.co.canon.cks.ees.service.model.DefaultExchangeRuleModel;
import jp.co.canon.cks.ees.service.model.DefaultFilterModel;
import jp.co.canon.cks.ees.service.model.FilterModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	フィルタの詳細定義を読み込む
 *
 * @author Tomomitsu TATEYAMA
 */
public class FilterConfigurationSaxParser extends DefaultHandler {
	// Element string
	private static final String E_FILTER = "FILTER";
	private static final String E_LOGKEY = "LOGKEY";
	private static final String E_CHANGEKEY = "CHANGEKEY";
	private static final String E_REPLACE = "REPLACE";
		
	// all attribute string
	private static final String A_CONDITION = "condition";
	private static final String A_VALUE = "value";
	private static final String A_STARTKEY = "startKey";
	private static final String A_ENDKEY = "endKey";
	private static final String A_RULE = "rule";
	private static final String A_DIRECTION = "direction";
	private static final String A_EXCLUSION = "exclusionStr";
	private static final String A_ALLREPLACE = "allReplace";
	private static final String A_REGEXENDKEY = "regexEndKey";
	private static final String A_LOGKEYSTART = "logKeyStartPos";
	
	// other define
	private static final String TRUE = "true";
	
	private DefaultFilterModel filter = null;
	private String currentLogKey = null;
	private DefaultExchangeRuleModel currentChangeRule = null;
	
	/**
	 * デフォルトコンストラクタ
	 */
	public FilterConfigurationSaxParser() {
		filter = new DefaultFilterModel();
	}
	/**
	 * 読み込んだConfugurationModelを返す。
	 * Return:
	 * @return configuration ファイルを読み込んだConfigurationModelを返す。
	 */
	public FilterModel getFilterModel() {
		return filter;
	}
	/* 
	 * startElementの実装
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String arg1, String qName, Attributes attr) throws SAXException {
		if (qName.equalsIgnoreCase(E_FILTER)) {
			filter = new DefaultFilterModel();
			String work = attr.getValue(A_LOGKEYSTART);
			if (work != null) {
				filter.setLogKeyStartPos(Integer.parseInt(work));
			}
		} else if (qName.equalsIgnoreCase(E_LOGKEY)) {
			currentLogKey = attr.getValue(A_CONDITION);
		} else if (qName.equalsIgnoreCase(E_CHANGEKEY)) {
			currentChangeRule = new DefaultExchangeRuleModel();
			filter.addRule(currentLogKey, currentChangeRule);
			currentChangeRule.setStartKey(attr.getValue(A_STARTKEY));
			currentChangeRule.setEndKey(attr.getValue(A_ENDKEY));
			String work = attr.getValue(A_ALLREPLACE);
			if (work != null) currentChangeRule.setAllReplace(work.equalsIgnoreCase(TRUE));
			work = attr.getValue(A_DIRECTION);
			if (work != null) currentChangeRule.setDirection(Integer.parseInt(work));
			work = attr.getValue(A_REGEXENDKEY);
			if (work != null) currentChangeRule.setRegexEndKey(work.equalsIgnoreCase(TRUE));
		} else if (qName.equalsIgnoreCase(E_REPLACE)) {
			currentChangeRule.setRule(Integer.parseInt(attr.getValue(A_RULE)));
			currentChangeRule.setChangeStr(attr.getValue(A_VALUE));
			currentChangeRule.setExclusionStr(attr.getValue(A_EXCLUSION));
		}
	}
}
