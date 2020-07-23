/**
 *
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : LogCollectDefinitionSaxHandler.java
 *
 * Author: tate
 * Date: 2011/11/19
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.ckbs.eec.fs.collect.xml;

import jp.co.canon.ckbs.eec.fs.collect.model.DefaultLogUrlModel;
import jp.co.canon.ckbs.eec.fs.collect.model.DefaultToolLogCollectUrlMapModel;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import jp.co.canon.ckbs.eec.fs.collect.model.ToolLogCollectUrlMapModel;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *
 * ログ収集定義ファイルのXMLを読み込むハンドリングするクラス
 * ログ種別ID毎のURLとユーザパスワード情報を読み込む
 * 読み込んだ値は、ToolLogCollectUrlMapModelとして取得できる。
 *
 * @author Tomomitsu TATEYAMA
 * =============================================================
 * 2011-11-28	T.Tateyama	属性取得ミス	単体テスト　UTC-21-2
 */
public class LogCollectDefinitionSaxHandler extends DefaultHandler {
	/** TARGETエレメント文字の定数　*/
	private static final String E_TARGET = "Target";
	/** LogUrlsエレメント文字の定数　*/
	private static final String E_URLS = "LogUrls";
	/** Logエレメント文字の定数　*/
	private static final String E_LOG = "Log";

	/** name属性の定数　*/
	private static final String A_NAME = "name";
	/** type属性の定数　*/
	private static final String A_TYPE = "type";
	/** kind属性の定数　*/
	private static final String A_KIND = "kind";
	/** user属性の定数　*/
	private static final String A_USER = "user";
	/** password属性の定数　*/
	private static final String A_PASSWORD = "password";

	// <FS収集機能のActive対応(CITS)>
	/** ftpmode属性の定数　*/
	private static final String A_FTPMODE = "ftpmode";

	/** 戻り値として返す値を保持　*/
	private DefaultToolLogCollectUrlMapModel model = new DefaultToolLogCollectUrlMapModel();
	/** 文字列を一時保持するための変数　*/
	private StringBuilder builder = null;
	/** ログのURLを保持するクラス　*/
	private DefaultLogUrlModel logUrlModel = null;

	/**
	 * ファイルのタイムススタンプを指定して構築する。
	 *　タイムスタンプは読み込みデータを返す際にモデルに設定する。
	 *
	 * @param timestamp	定義ファイルのタイムスタンプ
	 */
	public LogCollectDefinitionSaxHandler(long timestamp) {
		model.setTimestamp(timestamp);
	}
	/**
	 * 読み込んだ内容をモデルとして返す。
	 *
	 * Parameters/Result:
	 *　 @return	読み込んだ内容をログ収集URLマップモデルで返す。
	 */
	public ToolLogCollectUrlMapModel getModel() {
		return model;
	}
	/*
	 * startElementの実装
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String arg0, String arg1, String qName, Attributes attr) throws SAXException {
		if (qName.equals(E_TARGET)) {
			model.setToolName(attr.getValue(A_NAME));
			model.setToolType(attr.getValue(A_TYPE));
		} else if (qName.equals(E_URLS)) {
		} else if (qName.equals(E_LOG)) {
			builder = new StringBuilder();
			logUrlModel = new DefaultLogUrlModel();
			logUrlModel.setUserId(attr.getValue(A_USER));			// 2011-11-28
			logUrlModel.setPassword(attr.getValue(A_PASSWORD));		// 2011-11-28

			// <FS収集機能のActive対応(CITS)>
			logUrlModel.setFtpmode(attr.getValue(A_FTPMODE));		// 2019-05-20

			model.getLogUrlMap().put(attr.getValue(A_KIND), logUrlModel);	// 2011-11-28
		}
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
		if (qName.equals(E_LOG)) {
			logUrlModel.setUrlString(builder.toString());
			builder = null;
		}
	}
}
