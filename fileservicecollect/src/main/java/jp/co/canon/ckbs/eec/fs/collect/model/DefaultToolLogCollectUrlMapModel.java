/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : DefaultToolLogRepositoryModel.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/11/19
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.ckbs.eec.fs.collect.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *
 *	ToolLogCollectUrlMapModelのデフォルト実装
 *
 * @author Tomomitsu TATEYAMA
 */
public class DefaultToolLogCollectUrlMapModel implements ToolLogCollectUrlMapModel {
	/** 装置名 */
	@Getter @Setter
	private String toolName = null;
	/** 機種名 */
	@Getter @Setter
	private String toolType = null;
	/** 定義データを保持するマップ */
	private Map<String, LogUrlModel>  logUrlMap = new HashMap<String, LogUrlModel> ();
	/** 定義ファイルのタイムスタンプ */
	@Getter @Setter
	private long timestamp = 0;
	
	/**
	 * ログId毎のURL情報を保持したマップを返す。このMapは取得毎に同じ保持されているMapを返す。
	 * 
	 * @see ToolLogCollectUrlMapModel#getLogUrlMap()
	 * @return 装置ログ毎のURL情報を保持したマップ
	 */
	public Map<String, LogUrlModel> getLogUrlMap() {
		return logUrlMap;
	}
}
