/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : ListCommand.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/03
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.command;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import jp.co.canon.cks.ees.service.FileAccessor;
import jp.co.canon.cks.ees.service.FileList;
import jp.co.canon.cks.ees.service.LogFile;
import jp.co.canon.cks.ees.service.model.ConfigurationModel;
import jp.co.canon.cks.ees.service.model.DefaultConfigurationModel;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	ファイルの一覧を要求するコマンド
 *
 * @author Tomomitsu TATEYAMA
 * =============================================================================
 * Change History:
 * 2011-01-03	T.Tateyama	クラス変数とprivateメソッドのスコープをprotectedに変更
 * 2011-01-07	T.Tateyama	コメントを追加
 * 2011-01-10	T.Tateyama	リスト取得の時に再接続待ち時間指定を変更する
 * 2011-01-17	T.Tateyama	キーワードの適用範囲をファイル名だけにする
 * 2012-02-12	T.Tateyama	Com/Err等の日付ディレクトリを持つ場合の条件を追加
 * 2015-04-14	T.TAKIGUCHI	リスト一覧表示時に制限を超えていた場合の条件を追加
 */
public class ListCommand implements CommandModel {
	protected ConfigurationModel config = null;
	protected java.util.Date startDate = null;
	protected java.util.Date endDate = null;
	protected boolean enableTimestamp = false;
	protected String[] keywords = null;
	protected static final String PERIOD_ERR_MSG = "Period value is wrong. Ex:yyyyMMddHHmmss,yyyyMMddHHmmss : ";
	private static final String LIST_RETRY_INTERVAL = "ListRInterval";
	
	/**
	 * ConfigurationModelとパラメータをもらってListCommandを構築する
	 * Parameters:
	 * 	@param c		ログの設定を保持しているConfigurationModel	
	 * 	@param key		キーワードを設定する。指定されていない場合はnullが設定されいる
	 * 	@param period	期間を示す文字列が設定されている。されていない場合は、nullが設定されている
	 */
	public ListCommand(ConfigurationModel c, String key, String period) {
		config = c;
		if (key != null) keywords = key.split(" ");	// スペース区切りで複数ワード
		if (period != null) {
			String[] values = period.split(",");
			if (values.length != 2) throw new IllegalArgumentException(PERIOD_ERR_MSG + period);
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			try {
				startDate = format.parse(values[0]);
				endDate = format.parse(values[1]);
				// 条件を追加する
				if (c.isTimestampCheck()) {
					enableTimestamp = true;					
				}
			} catch (ParseException ex) {
				throw new IllegalArgumentException(PERIOD_ERR_MSG + period);
			}
		}
		// 2011-01-10 T.Tateyama 
		String newVal = System.getProperty(LIST_RETRY_INTERVAL);
		if (newVal != null && newVal.matches("[0-9]+")) {
			((DefaultConfigurationModel)c).setRetryInterval(Integer.parseInt(newVal));
		}
	}
	/**
	 * キーワードが含まれているかチェックする
	 *
	 * Parameter:
	 * 	@param	target	対象の文字列
	 *
	 * Result:
	 *	@return boolean	true	キーワードが含まれている・もしくは登録がない
	 *					false	キーワードが含まれていない
	 */
	protected boolean contansKeyword(String target) {
		if (keywords == null) return true;	// キーワードがな無ければ登録OK
		for (int i=0; i < keywords.length; i++) {
			if (target.indexOf(keywords[i]) >= 0) return true;
		}
		return false;
	}
	/* 
	 * executeの実装
	 * @see jp.co.canon.cks.ees.service.command.CommandModel#execute(jp.co.canon.cks.ees.service.configuration.ConfigurationModel)
	 */
	public void execute() throws RunningStopException, IOException {
		FileAccessor getter = new FileAccessor();
		getter.setConfiguration(config);
		FileList list = getter.getFiles(startDate, endDate, keywords);
		java.text.DateFormat f = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		java.util.Date d = new java.util.Date();
		for (java.util.Iterator iter=list.iterator(); iter.hasNext();) {
			LogFile file = (LogFile)iter.next();
			//-------- 2011-01-17 T.Tateyama 
			String lastName = file.getName();
			int tindex = lastName.lastIndexOf("/");
			if (tindex >= 0) lastName = lastName.substring(tindex+1);
			d.setTime(file.getTimestamp());
			System.out.println(file.getName() + "," + f.format(d) + "," + file.getSize());
		}
	}
}
