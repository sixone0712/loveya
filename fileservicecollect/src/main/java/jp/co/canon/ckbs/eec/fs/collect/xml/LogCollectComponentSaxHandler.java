/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : LogCollectComponentSaxHandler.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/11/19
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.ckbs.eec.fs.collect.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	
 *　装置ログコンポーネントファイルのXMLをハンドリングするクラス
 *　読み込んだ値は、このハンドラの各プロパティに設定する。
 *　読み込みプロパティは、以下の通り
 *　　・ログ種別ID
 *　　・名称
 *　　・概要説明
 *　　・実行コマンド
 *　　・検索条件
 *　収集では、実行コマンドを利用する。
 * @author Tomomitsu TATEYAMA
 */
public class LogCollectComponentSaxHandler extends DefaultHandler {
	private static final String E_COMPOID = "CompoID";
	private static final String E_NAME = "Name";
	private static final String E_DESCRIPTION = "Description";
	private static final String E_EXECUTECOMMAND = "ExecuteCommand";
	private static final String E_EXECUTECOMMANDCA = "ExecuteCommandCA";
	private static final String E_SEARCHTYPE = "SearchType";

	private String name = null;
	private String description = null;
	private String executeCommand = null;
	private String executeCommandCA = null;
	private String compoId = null;
	private int searchType = 0;
	private StringBuilder builder = null;
	
	/**
	 * デフォルトコンストラクタ
	 */
	public LogCollectComponentSaxHandler() {
		
	}
	/**
	 *　設定されているnameを返す。
	 * Return:
	 * 	@return name
	 */
	public String getName() {
		return name;
	}

	/**
	 *　設定されているdescriptionを返す。
	 * Return:
	 * 	@return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 *　設定されているexecuteCommandを返す。
	 * Return:
	 * 	@return executeCommand
	 */
	public String getExecuteCommand() {
		return executeCommand;
	}

	/**
	 *　設定されているexecuteCommandCAを返す。
	 * Return:
	 * 	@return executeCommand
	 */
	public String getExecuteCommandCA() {
		return executeCommandCA;
	}

	/**
	 *　設定されているcompoIdを返す。
	 * Return:
	 * 	@return compoId
	 */
	public String getCompoId() {
		return compoId;
	}

	/**
	 *　設定されているsearchTypeを返す。
	 * Return:
	 * 	@return searchType
	 */
	public int getSearchType() {
		return searchType;
	}

	/* 
	 * charactersの実装
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (builder != null) {
			builder.append(ch, start, length);
		}
	}

	/* 
	 * endElementの実装
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals(E_COMPOID)) {
			compoId = builder.toString();
		} else if (qName.equals(E_NAME)) {
			name = builder.toString();
		} else if (qName.equals(E_DESCRIPTION)) {
			description = builder.toString();
		} else if (qName.equals(E_EXECUTECOMMAND)) {
			executeCommand = builder.toString();
		} else if (qName.equals(E_EXECUTECOMMANDCA)) {
			executeCommandCA = builder.toString();
		} else if (qName.equals(E_SEARCHTYPE)) {
			searchType = Integer.parseInt(builder.toString());
		}
		builder = null;
	}

	/* 
	 * startElementの実装
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals(E_COMPOID)) {
			builder = new StringBuilder();
		} else if (qName.equals(E_NAME)) {
			builder = new StringBuilder();
		} else if (qName.equals(E_DESCRIPTION)) {
			builder = new StringBuilder();
		} else if (qName.equals(E_EXECUTECOMMAND)) {
			builder = new StringBuilder();
		} else if (qName.equals(E_EXECUTECOMMANDCA)) {
			builder = new StringBuilder();
		} else if (qName.equals(E_SEARCHTYPE)) {
			builder = new StringBuilder();
		}
	}

}
