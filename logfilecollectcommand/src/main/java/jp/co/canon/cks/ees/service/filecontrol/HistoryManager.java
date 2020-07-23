/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : HistoryManager.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/16
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.filecontrol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *
 * @author Tomomitsu TATEYAMA
 */
public class HistoryManager {
	private static Logger logger = Logger.getLogger(HistoryManager.class.getName());
	public static final String ACCESS_HISTORY_FILE = "AccessHist.LST";
	private static HistoryManager instance = new HistoryManager();;
	
	/**
	 * インスタンスを取得する
	 *
	 * Result:
	 *	@return HistoryManager　内部で保持するインスタンス
	 */
	public static HistoryManager getInstance() {
		return instance;
	}
	/**
	 * アクセス履歴を更新する、
	 *	TODO This section is description for function;
	 *
	 * Parameter:
	 * 	@param	file	更新する対象のファイル
	 */
	public synchronized void updateAccessHistory(File file) {
		try {
			updateAccessHistoryLocal(file);
		} catch (Throwable ex) {
			logger.log(Level.SEVERE, "update access history failed.", ex);
		}
	}
	/**
	 * 実際に更新する内部メソッド
	 *
	 * Parameter:
	 * 	@param	file	更新する対象のファイル
	 */
	private void updateAccessHistoryLocal(File file) throws IOException {
		BufferedWriter writer = null;
		BufferedReader reader = null;
		try {
			java.util.ArrayList l = new java.util.ArrayList();
			File accessFile = new File(file.getParentFile(), ACCESS_HISTORY_FILE);
			if (accessFile.exists()) {
				reader = new BufferedReader(new FileReader(accessFile));
				while(true) {
					String line = reader.readLine();
					if (line == null) break;
					l.add(line);
				}
				reader.close();
				reader = null;
			}
			File tempFile = File.createTempFile("ACCS", ".temp", file.getParentFile());
			writer = new BufferedWriter(new FileWriter(tempFile));
			writer.write(new StringBuffer()
								.append(System.currentTimeMillis()).append(",")
								.append(l.size() + 1).toString());
			writer.newLine();
			for (java.util.Iterator iter=l.iterator(); iter.hasNext();) {
				writer.write(iter.next().toString());
				writer.newLine();
			}
			writer.close();
			writer = null;
			//------------------------
			if (accessFile.exists()) {
				if (!accessFile.delete()) {
					tempFile.delete();
					return;
				}
			}
			tempFile.renameTo(accessFile);
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
		}		
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
	public static void main(String[] args) {
		HistoryManager.getInstance().updateAccessHistory(new File("D:\\downloads\\RT0001\\RT0001.zip"));
		System.out.println("finished");
	}
}
