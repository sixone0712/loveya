/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : DownloadRepository.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/07
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.filecontrol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	アップロードされたファイルに対してユーザがダウンロードした回数を返す
 *	ダウンロードした数は、各要求番号フォルダないのACCESS_HISTORY_FILEファイルの１行目２列目に
 *	保存されているので、その部分を取得する。
 * @author Tomomitsu TATEYAMA
 */
public class AccessHistory {
	private static Logger logger = Logger.getLogger(AccessHistory.class.getName());
	public static final String ACCESS_HISTORY_FILE = "AccessHist.LST";
	private java.util.HashMap cacheMap = new java.util.HashMap(100);
	
	/**
	 * ダウンロードカウント数を返す。
	 *
	 * Parameter:
	 * 	@param	target	対象のファイル
	 *
	 * Result:
	 *	@return int	ユーザがダウンロードした数
	 */
	public int getDownloadCount(File target) {
		String parent = target.getParent();
		if (!cacheMap.containsKey(parent)) {
			try {
				readHistory(target);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Access history file read error.", e);				
			}
		}
		Integer l = (Integer)cacheMap.get(parent);
		return l.intValue();
	}
	/**
	 * アクセス履歴ファイルを読み込む
	 *
	 * Parameter:
	 * 	@param	target	対象のファイル
	 */
	private void readHistory(File target) throws IOException {
		// デフォルト値の登録
		cacheMap.put(target.getParent(), new Integer(0));							
		File hfile = new File(target.getParentFile(), ACCESS_HISTORY_FILE);
		if (!hfile.exists()) return ;
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(hfile));
			String line = reader.readLine();
			if (line != null) {
				String[] v = line.split(",");
				if (v.length == 2 && v[1].matches("[0-9]+")) {
					cacheMap.remove(target.getParent());
					cacheMap.put(target.getParent(), new Integer(v[1]));
				}
			}
		} finally {
			if (reader != null) reader.close();
		}
	}
}
