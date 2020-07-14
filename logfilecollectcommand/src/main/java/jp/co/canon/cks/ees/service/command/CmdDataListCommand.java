/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : CmdDataListCommand.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/01/04
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.command;

import java.io.IOException;

import jp.co.canon.cks.ees.service.FileAccessor;
import jp.co.canon.cks.ees.service.LogItem;
import jp.co.canon.cks.ees.service.model.ConfigurationModel;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *
 * @author Tomomitsu TATEYAMA
 * ==================================
 * Change History:
 * 2015-04-14	T.TAKIGUCHI	リスト一覧表示時に制限を超えていた場合の条件を追加
 */
public class CmdDataListCommand extends ListCommand {
	private String directory = null;
	protected static final String FILE_TYPE = "F";
	protected static final String DIR_TYPE = "D";

	/**
	 * 値を指定して構築する。
	 * @param c
	 * @param key
	 * @param period
	 * @param dir
	 */
	public CmdDataListCommand(ConfigurationModel c, String key, String period, String dir) {
		super(c, key, period);
		if (dir != null) {
			// 最初にディレクトリが指定されている場合は、削除する。
			if (dir.startsWith("/")) {
				if (dir.length() == 1) 
					dir = null;
				else
					dir = dir.substring(1);
			}
		}
		if (dir != null && dir.indexOf("..") >= 0) {
			throw new IllegalArgumentException("directory name is wrong. :" + dir);
		}
		directory = dir;
	}
	/* 
	 * executeの実装
	 * @see jp.co.canon.cks.ees.service.command.CommandModel#execute()
	 */
	public void execute() throws RunningStopException, IOException {
		FileAccessor getter = new FileAccessor();
		getter.setConfiguration(config);
		java.util.List list = getter.getItemInDirectory(directory,keywords);
		java.text.DateFormat f = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		java.util.Date d = new java.util.Date();
		for (java.util.Iterator iter=list.iterator(); iter.hasNext();) {
			LogItem file = (LogItem)iter.next();

			// 件数を超えている場合はチェックしない
			if (!file.getName().startsWith("###FileOverLimit") && file.getTimestamp() != 0){
				// キーワードチェックする
				if (!contansKeyword(file.getName())) continue;
				// 時間のチェック
				if (enableTimestamp && !file.isDirectory() && (file.getTimestamp() < startDate.getTime() || endDate.getTime() < file.getTimestamp())) {
					continue;
				}
			}
			d.setTime(file.getTimestamp());
			String kind = (file.isDirectory() ? DIR_TYPE : FILE_TYPE);
			System.out.println(file.getName() + "," + f.format(d) + "," + file.getSize() + "," + kind );
		}
	}

}
