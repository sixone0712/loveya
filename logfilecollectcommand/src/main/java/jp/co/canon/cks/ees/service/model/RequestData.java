/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : RequestData.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2013/07/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import jp.co.canon.cks.ees.service.model.xml.RequestDataSaxPaser;


/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *　要求情報を保持するクラス
 *
 * @author Tomomitsu TATEYAMA
 */
public final class RequestData {
	/** 要求システム */
	private String reqSystem = null;
	/** コメント */
	private String comment = null;
	/** 環境ID */
	private String reqEnvironmentId = null;
	/** 読み込みファイル */
	private File contentsFile = null;

	/**
	 *　設定されているreqSystemを返す。
	 * Return:
	 * @return reqSystem
	 */
	public String getReqSystem() {
		return reqSystem;
	}

	/**
	 * reqSystemを設定する。
	 * Parameters:
	 * @param reqSystem セットする reqSystem
	 */
	public void setReqSystem(String reqSystem) {
		this.reqSystem = reqSystem;
	}

	/**
	 *　設定されているcommentを返す。
	 * Return:
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * commentを設定する。
	 * Parameters:
	 * @param comment セットする comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 *　設定されているreqEnvironmentIdを返す。
	 * Return:
	 * @return reqEnvironmentId
	 */
	public String getReqEnvironmentId() {
		return reqEnvironmentId;
	}

	/**
	 * reqEnvironmentIdを設定する。
	 * Parameters:
	 * @param reqEnvironmentId セットする reqEnvironmentId
	 */
	public void setReqEnvironmentId(String reqEnvironmentId) {
		this.reqEnvironmentId = reqEnvironmentId;
	}

	/**
	 *　設定されているcontentsFileを返す。
	 * Return:
	 * @return contentsFile
	 */
	public File getContentsFile() {
		return contentsFile;
	}

	/**
	 * contentsFileを設定する。
	 * Parameters:
	 * @param contentsFile セットする contentsFile
	 */
	protected void setContentsFile(File contentsFile) {
		this.contentsFile = contentsFile;
	}

	/**
	 * ファイルを読み込みRequestDataを返す。
	 *
	 * Parameters/Result:
	 * @param target　対象ファイル
	 * @return　インスタンスを返す
	 * @throws IOException
	 */
	public static RequestData readObject(File target) throws Exception {
		RequestDataSaxPaser saxHandler = new RequestDataSaxPaser();
        if (target.exists()) {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setValidating(false);
            parserFactory.setNamespaceAware(false);
            SAXParser parser = parserFactory.newSAXParser();
            parser.parse(target, saxHandler);

            RequestData result = saxHandler.getOutput();
    		result.setContentsFile(target);
    		
    		return result;
        }
        throw new IllegalArgumentException("file is not found.");
	}
}
