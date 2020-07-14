/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : CleanConfigurationSaxHander.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/07
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model.xml;

import java.util.ArrayList;
import java.util.List;

import jp.co.canon.cks.ees.service.model.DefaultFilePurgeConfigurationModel;
import jp.co.canon.cks.ees.service.model.FilePurgeConfigurationModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	ファイル削除設定用のXMLファイルを読み込む
 *
 * @author Tomomitsu TATEYAMA
 * ============================================================================
 * 2012-11-01	T.TATEYAMA	定義情報を複数返す様に修正
 */
public class FilePurgeConfigurationSaxHander extends DefaultHandler {
	//private DefaultFilePurgeConfigurationModel configuration = new DefaultFilePurgeConfigurationModel();
	// Element string
	//private static final String E_INFO = "INFO";
	/** TARGETを追加　*/
	private static final String E_TARGET = "Target";
	
	private static final String A_OUTPUT = "output";
	private static final String A_EXPIREMIN = "expireMin";
	private static final String A_MAXSPACE = "maxSpace";
	private static final String A_LIMIT = "limit";
	private static final String A_PURGERC = "purgeCandidateCount";
	private static final String A_TYPE = "type";
	
	private static final String K_BYTE = "K";
	private static final String M_BYTE = "M";
	private static final String G_BYTE = "G";

	private ArrayList<FilePurgeConfigurationModel> list = new ArrayList<FilePurgeConfigurationModel>();

	/**
	 * パースしたFilePurgeConfigurationModelのリストを返す。
	 * Result:
	 *　@return List<FilePurgeConfigurationModel>	読み込まれた設定情報のリスト
	 */
	public List<FilePurgeConfigurationModel> getConfigurationList() {
		return list;
	}
	/* 
	 * startElementの実装
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
		if (qName.equalsIgnoreCase(E_TARGET)) {
			DefaultFilePurgeConfigurationModel configuration = new DefaultFilePurgeConfigurationModel();
			java.io.File out = new java.io.File(attr.getValue(A_OUTPUT));
			if (out.exists() && out.isDirectory()) { // 存在していて、かつディレクトリでなければエラーとする
				configuration.setDownloadDirectory(out);
			} else {
				throw new SAXException("output directory is wrong.");				
			}
			configuration.setExpireMinute(Integer.parseInt(attr.getValue(A_EXPIREMIN)));
			String work = attr.getValue(A_MAXSPACE).toUpperCase();
			if (work.endsWith(K_BYTE)) {
				configuration.setUsedDiskSize(Long.parseLong(work.substring(0, work.length()-1)) * 1024);				
			} else if (work.endsWith(M_BYTE)) {
				configuration.setUsedDiskSize(Long.parseLong(work.substring(0, work.length()-1)) * 1024 * 1024);				
			} else if (work.endsWith(G_BYTE)) {
				configuration.setUsedDiskSize(Long.parseLong(work.substring(0, work.length()-1)) * 1024 * 1024 * 1024);				
			} else {
				configuration.setUsedDiskSize(Long.parseLong(work));
			}
			work = attr.getValue(A_LIMIT);
			if (work != null) configuration.setLimitRate(Double.parseDouble(work) / 100);
			work = attr.getValue(A_PURGERC);
			if (work != null) configuration.setPurgeCandidateCount(Integer.parseInt(work));
			work = attr.getValue(A_TYPE);
			if (work != null) {
				if (work.equalsIgnoreCase("download")) {
					configuration.setType(FilePurgeConfigurationModel.DOWNLOAD_TYPE);
				} else if (work.equalsIgnoreCase("cache")) {
					configuration.setType(FilePurgeConfigurationModel.CACHE_TYPE);
				}
			}
			list.add(configuration);
		}
		super.startElement(uri, localName, qName, attr);
	}
}
