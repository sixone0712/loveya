/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : ReqestDataSaxPaser.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2013/07/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model.xml;

import java.util.ArrayList;
import java.util.List;

import jp.co.canon.cks.ees.service.model.RequestData;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *　要求情報を読み込む為のSAXハンドラー
 *
 * @author Tomomitsu TATEYAMA
 */
public class RequestDataSaxPaser extends DefaultHandler {

	/**
	 * コメント取得用文字列バッファ
	 */
	private StringBuilder buffer = null;
	/**
	 * 読み込み結果の出力オブジェクト
	 */
	private RequestData output = new RequestData();
	/**
	 * 対象ファイルリスト
	 */
	private List<DataHolder> fileList = new ArrayList<DataHolder>();

	// エレメント
	public static final String E_INFO = "Info";
	public static final String E_COMMENT = "Comment";
	public static final String E_LOG = "Log";
	public static final String E_ITEM = "Item";
	public static final String E_STATE = "State";
	
	// 属性
	public static final String A_S_REQNO = "reqNo";
	public static final String A_S_REQUSERID = "reqUserId";
	public static final String A_S_REQSYSTEM = "reqSystem";
	public static final String A_S_REQENV = "reqEnvironmentId";
	public static final String A_L_TIMESTAMP = "timestamp";
	public static final String A_S_TOOLID = "toolId";
	public static final String A_S_LOGID = "logId";
	public static final String A_S_FILENAME = "fileName";
	public static final String A_L_SIZE = "size";
	public static final String A_I_STATE = "state";
	public static final String A_S_RESULT = "result";
	public static final String A_L_COMPTIME = "completedTime";
	public static final String A_B_ARCHIVE = "archived";

	/**
	 * <p>Title:</p> File upload Service
	 * <p>Description:</p>
	 * ファイル情報を保持するための内部クラス
	 * 
	 * @author Tomomitsu TATEYAMA
	 */
	class DataHolder {
		public String fileName = null;
		public long timestamp = 0;
		public long size = 0;
	}
	
	/**
	 * 読み込んだ結果を返す。
	 *
	 * Parameters/Result:
	 * @return
	 */
	public RequestData getOutput() {
		return output;
	}
	
	/* 
	 * startElementの実装
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (E_INFO.equals(qName)) {
			//String reqNo = attributes.getValue(A_S_REQNO);
			//String reqUserId = attributes.getValue(A_S_REQUSERID);
			output.setReqSystem(attributes.getValue(A_S_REQSYSTEM));
			output.setReqEnvironmentId(attributes.getValue(A_S_REQENV));
			//Long timestamp = Long.valueOf(attributes.getValue(A_L_TIMESTAMP));
		} else if (E_COMMENT.equals(qName)) {
			buffer = new StringBuilder();
		} else if (E_LOG.equals(qName)) {
			//String toolId = attributes.getValue(A_S_TOOLID);
			//String logId = attributes.getValue(A_S_LOGID);
		} else if (E_ITEM.equals(qName)) {
			DataHolder h = new DataHolder();
			h.fileName = attributes.getValue(A_S_FILENAME);
			h.timestamp = Long.parseLong(attributes.getValue(A_L_TIMESTAMP));
			h.size = Long.parseLong(attributes.getValue(A_L_SIZE));
			fileList.add(h);
		} else if (E_STATE.equals(qName)) {
			//Integer status = Integer.getInteger(attributes.getValue(A_I_STATE));
			//String result = attributes.getValue(A_S_RESULT);
			//Long completedTime = Long.valueOf(attributes.getValue(A_L_COMPTIME));
			//Boolean archived = Boolean.valueOf(attributes.getValue(A_B_ARCHIVE));
		}		
	}

	/* 
	 * endElementの実装
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (E_COMMENT.equals(qName) && buffer != null) {
			output.setComment(buffer.toString());
		} else if (E_LOG.equals(qName)) {
			if (fileList.size() > 0) {
				long[] timestamps = new long[fileList.size()];
				long[] sizes = new long[fileList.size()];
				String[] fileNames = new String[fileList.size()];
				
				for (int i=0; i < fileList.size(); i++) {
					DataHolder h = fileList.get(i);
					timestamps[i] = h.timestamp;
					sizes[i] = h.size;
					fileNames[i] = h.fileName;
				}
			}
		}
		buffer = null;
	}

	/* 
	 * charactersの実装
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (buffer != null) {
			buffer.append(ch, start, length);
		}
	}

}
