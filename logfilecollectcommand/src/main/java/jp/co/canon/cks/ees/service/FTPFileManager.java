/**
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : FTPFileManager.java
 *
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;

import java.io.File;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.canon.cks.ees.service.command.RunningStopException;
import jp.co.canon.cks.ees.service.ftp.FTP;
import jp.co.canon.cks.ees.service.ftp.MZFTP;
import jp.co.canon.cks.ees.service.model.ConfigurationModel;
import jp.co.canon.cks.ees.service.stack.Stack;
import jp.co.canon.cks.ees.service.stack.StackHandler;
import jp.co.canon.cks.ees.service.stack.StackItem;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	AbstractFileManagerのFTPを使った実装。内部にFTPインスタンスを保持し、AbstractFileManagerの抽象メソッドを実装する。
 * @author Tomomitsu TATEYAMA
 * =============================================================================
 * Change History:
 * 2011-01-03	T.Tateyama	getFileInfoAll()の実装追加
 * 2011-01-07	T.Tateyama	getFileInfoAll()のパラメータがnullの場合の処理を追加
 * 2011-01-12	T.Tateyama	FTPのリスト取得で、スペースファイル名に対応する。メソッド追加と、分割処理の置き換え
 * 2011-01-15	T.Tateyama	ファイル取得時にBinaryモードに強制的に変更
 * 2011-01-18	T.Tateyama	シンボリックリンクのファイルを受け取った場合の処理を追加
 * 2011-01-19	T.Tateyama	シンボリックリンクの検証を厳密的に行う
 * 2011-12-14	T.Tateyama	FTPからのリストを解析する際に時間の判定をサイズではなく、コロンの存在でチェックする
 * 2012-01-29	T.Tateyama	FTPのポートのポート指定とファイル取得時のディレクトリ移動を追加する
 * 2012-02-12	T.Tateyama	FTPの年の計算が指定が間違っているため修正
 * 2012-10-30	T.TATEYAMA	FTPのデータ接続モードを設定から指定する
 * 2015-04-14	T.TAKIGUCHI	FTPのリスト取得時に指定された制限までのファイルのみリストに格納するよう修正
 * 2015-05-20	T.TAKIGUCHI	getYear()の判定処理を追加（15-004-01）
 * 2015-05-20	T.TAKIGUCHI	getFileInfoAll()に年情報判定処理を追加（15-004-01）
 * 2015-06-22	T.TAKIGUCHI getFileInfos()にファイル名の日付文字列からタイムスタンプを解決する処理を追加
 */
public class FTPFileManager extends AbstractFileManager {
	private static final Logger logger = Logger.getLogger(FTPFileManager.class.getName());
	public static final String SUPPORT_PROTOCOL = "ftp";
	private ConfigurationModel config = null;
	private FTP currentFTP = null;
	//private FtpClient currentFTP = null;
	private java.text.DateFormat format = null;
	private static final String SYMBOLIC_KEY = "->";
	/** 日付のシンボルを作成 */
	private DateFormatSymbols dateSymbol = new DateFormatSymbols(java.util.Locale.US);
	private FileNameAnalyzer timestampAlalyzer = null;

	/**
	 * RealtimeLogCommandCA.xmlのsiteOption内の置換対象"${mid}"を表すPattern
	 */
	private static final Pattern PATTERN_MID = Pattern.compile("\\$\\{mid\\}");	// <RTLAファイルのフィルタ機能追加>

	/**
	 * FCSホームのパス
	 */
	private static final String FCS_HOME = "/usr/local/canon/ots/fcs";	// <RTLAファイルのフィルタ機能追加>

	/**
	 * 設定モデルを指定して構築する
	 * Parameter:
	 * 	@param	c	ConfigurationModelを設定する。
	 */
	public FTPFileManager(ConfigurationModel c) {
		if (c == null) throw new IllegalArgumentException("configulation is null");
		if (!c.getFileLocation().getProtocol().equalsIgnoreCase(SUPPORT_PROTOCOL)) {
			throw new IllegalArgumentException("not support configuration");
		}
		config = c;

		// <RTLAファイルのフィルタ機能追加>
		// RealtimeLogCommand.xmlのOption属性で、「sitOption=setfilter mid=${mid}」の${mid}の箇所を
		// RTLAPattern.xmlのidをカンマ区切りで列挙した文字列に置き換える.
		String[] siteOption = config.getSiteOption();
		if(siteOption != null){
			String ids = null;
			for(int i = 0; i < siteOption.length; i++) {
				if(siteOption[i] == null)
					continue;
				Matcher matcher = PATTERN_MID.matcher(siteOption[i]);
				if (matcher.find()) {
					if(ids == null) {
						// RTLAPattern.xmlのid属性をカンマ区切りで列挙した文字列を取得
						try {
							ids = getRTLAPatternIds(config.getToolId(), config.getToolIdSuffix());
						} catch (Exception e) {
							throw new IllegalArgumentException(e.getMessage());
						}
					}
					// "${mid}"部をidsで置換
					siteOption[i] = matcher.replaceAll(ids);
					logger.info("toolId=" + config.getToolId() + ", replace ${mid}... siteOption[" + i + "]=" + siteOption[i] );
				}
			}
		}
	}

	/**
	 * 内部で設定されているConfigurationModelを返す。
	 * Result:
	 *	@return ConfigurationModel	設定されている設定モデル
	 */
	protected ConfigurationModel getConfig() {
		return config;
	}
	/**
	 * FTPインスタンスを返す。内部で保持していない場合は、設定モデルを元に構築する
	 *
	 * Result:
	 *	@return FTP		FTPのインスタンス
	 */
	protected FTP getFTP() throws IOException {
		if (currentFTP == null) {
			if(getConfig().isModeZ()){
				currentFTP = new MZFTP(getConfig().getFileLocation().getHost(),
						 			getConfig().getFileLocation().getPort());
				currentFTP.setMaxRetryCount(getConfig().getRetryCount());
				currentFTP.setRetryWaitTime(getConfig().getRetryInterval());
				if (currentFTP.connect()) {
					currentFTP.login(getConfig().getAuthorize().getUser(),
									new String(getConfig().getAuthorize().getPasswd()));
					String[] siteOption = getConfig().getSiteOption();
					if(siteOption != null){
						for( int i = 0; i< siteOption.length; i++){
							currentFTP.site(siteOption[i]);
						}
					}
				}
			} else {
				currentFTP = new FTP(getConfig().getFileLocation().getHost(),
									 getConfig().getFileLocation().getPort());
				currentFTP.setMaxRetryCount(getConfig().getRetryCount());
				currentFTP.setRetryWaitTime(getConfig().getRetryInterval());
				if (currentFTP.connect()) {
					currentFTP.login(getConfig().getAuthorize().getUser(),
							  		new String(getConfig().getAuthorize().getPasswd()));
					String[] siteOption = getConfig().getSiteOption();
					if(siteOption != null){
						for( int i = 0; i< siteOption.length; i++){
							currentFTP.site(siteOption[i]);
						}
					}
				}
			}
			// ftp転送モードの設定
			currentFTP.setDataConnectionMode(getConfig().getFtpDataConnectionMode());
		}
		return currentFTP;
	}
	/**
	 * 内部で保持しているFTP接続をクローズする
	 */
	protected void closeFTP() {
		if (currentFTP != null) {
			currentFTP.close();
		}
		currentFTP = null;
	}
	/**
	 * AbstractFileManagerをクローズする
	 */
	public void close() {
		super.close();
		closeFTP();
	}
	/**
	 * 指定されたファイルのInputStreamを返す。
	 */
	public java.io.InputStream getFileStream(String name) throws IOException {
		getFTP().binary();	// バイナリに強制的に設定　2011-01-15
		String fname = getConfig().getFileLocation().getFile() + name;
		String path = "/";
		int index = fname.lastIndexOf("/");
		if (index > 0) {
			path = fname.substring(0, index);
			fname = fname.substring(index + 1);
		} else if (index == 0) {
			fname = fname.substring(1);
		}
		getFTP().cd(path);
		return getFTP().openFileStream(fname);
		// return getFTP().openFileStream(getConfig().getFileLocation().getFile() + name);
	}
	/**
	 * ｌｓコマンドで戻ってくる日付を解析するためのformatを返す。月が３バイとの場合は英字書式
	 *
	 * Parameter:
	 * 	@param	month	月を示す文字
	 * Result:
	 *	@return java.text.DateFormat	変換可能なjava.text.DateFormat
	 */
	private java.text.DateFormat getDateTimeFormat(String month) {
		if (format == null) {
			if (month.length() == 3) {
				format = new java.text.SimpleDateFormat("yyyy-MMM-dd HH:mm", java.util.Locale.US);
			} else {
				format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
			}
		}
		return format;
	}
	/**
	 * 月から判定して年を返す。
	 *
	 * Parameters/Result:
	 *　 @param monthStr	月の文字列（短縮形３桁）
	 *　 @param currentYear	現在年
	 *　 @param currentMonth	現在月
	 *　 @return	対象の年
	 */
	private int getYear(String monthStr, int currentYear, int currentMonth) {

		for (int i=0; i < dateSymbol.getShortMonths().length; i++) {
			if (monthStr.equalsIgnoreCase(dateSymbol.getShortMonths()[i])) {

				if (currentMonth == 11 && i == 0) {
					return currentYear + 1;
				}
				else if (currentMonth + 1 == i) {
					return currentYear;
				}
				else if (currentMonth >= i) {
					return currentYear;
				} else {
					return currentYear - 1;
				}
			}
		}
		return currentYear;
	}
	/* (Disable Javadoc)
	 * @see jp.co.canon.cks.ees.service.AbstractFileManager#getFileInfos()
	 */
	public java.util.List getFileInfos(FileNameChecker checker, int filecount, String[] keywords,TreeMap<Long,java.util.ArrayList<LogFile>> tm, java.util.Date s, java.util.Date e) throws IOException, RunningStopException {
		Calendar c = Calendar.getInstance();
		int cuurentYear = c.get(Calendar.YEAR);
		int currentMonth = c.get(Calendar.MONTH);	// month jan = 0, feb = 1, ...

		String path = getConfig().getFileLocation().getFile() + getCurrentPath();
		java.util.List out = new java.util.ArrayList();
		java.io.InputStream in = getFTP().ls(path);
		java.io.BufferedReader reader = null;

		Long count = (long)0;
		Long displayfilecount = (long) 0;
		// filecountを超えた場合の最小日時を保持する
		Long mindate = (long) 0;

		//現在TreeMapに格納されている数を数える
		if (tm.size() != 0) {
			for (ArrayList<LogFile> items : tm.values()){
				for (LogFile item : items){
					displayfilecount++;
				}
			}
			//totalfilecount分を引く
			displayfilecount--;
		}

		// 収集対象全ファイル数を数える
		if (tm.containsKey(Long.MAX_VALUE)){
			count = tm.get(Long.MAX_VALUE).get(0).getSize();
		}

		try {
			reader = new java.io.BufferedReader(new java.io.InputStreamReader(in));

			String buf = null;

			while ( (buf = reader.readLine()) != null) {
				if (isStopped()) throw new RunningStopException();
				String info = buf;
				String[] data = getToken(info);

				// attribute, gid, uid, ?, block size, month, day, year or time, file name
				if (data.length < 5) {
					logger.warning("FTP list command was invalid format. :" + info);
					continue; //
				}
				// 解析する
				int index = data.length -1;
				boolean directory = (info.charAt(0) == 'd');

				LogFile item = null;

				if (directory) {
					out.add(data[index]);
				} else {
					if (checker.matches(data[index])) {
						item = new LogFile();
						item.setName(getCurrentPath() + data[index]);
						StringBuffer dataStr = new StringBuffer();
						//　year or time
						if (data[index-1].indexOf(":") == -1) { // changed condition　2011-12-14
							dataStr.append(data[index-1]).append("-")
								.append(data[index-3]).append("-")
								.append(data[index-2]).append(" 00:00");
						} else {
							dataStr.append(getYear(data[index-3], cuurentYear, currentMonth)).append("-")
							.append(data[index-3]).append("-")
							.append(data[index-2]).append(" ")
							.append(data[index-1]);
						}
						//
						try {
							item.setTimestamp(getDateTimeFormat(data[index-3]).parse(dataStr.toString()).getTime());
						} catch (Throwable ex) {
							logger.log(Level.WARNING, "date paser error", ex);
						}
						item.setSize(Long.parseLong(data[index-4]));

						//ファイル名からTimeStampを解決する場合はファイル名の日付をTimeStampにセットする
						if(config.isUsedTimestampFromFileName()) {
							try {
								if(config.isGZipOutput()) {
									item.setTimestamp(getTimestampAnalyzer().getModifiedTime(item.getName() + ".gz"));
								}
								else {
									item.setTimestamp(getTimestampAnalyzer().getModifiedTime(item.getName()));
								}
							} catch (java.text.ParseException e1) {
								throw new IOException("Error: don't get the file timestamp @ " + item.getName());
							}
						}

						//日付チェックする場合、指定した期間内のファイルでない場合処理を飛ばす
						if(s != null && e != null) {
							if(config.isTimestampCheck() && (item.getTimestamp() < s.getTime() || e.getTime() < item.getTimestamp())){
								continue;
							}
						}

						//Keywordにヒットするitemのみ処理
						if (contansKeyword(item.getName(), keywords)){
							Long key = item.getTimestamp();
							java.util.ArrayList<LogFile> valuelist = null;

							//TreeMapにまだ格納されていないdateは追加
							if(!tm.containsKey(key)){
								valuelist = new java.util.ArrayList<LogFile>();
							} else {
								valuelist = tm.get(key);
							}

							// 制限を超えていない場合の処理
							if(displayfilecount < filecount){
								valuelist.add(item);
								tm.put(key, valuelist);
								displayfilecount++;
							//制限を超えた場合
							} else {
								//現在TreeMapに存在する最古のdateを取得
								mindate = tm.firstKey();

								//最古のファイルより新しいファイルの場合のみ追加
								if(mindate < item.getTimestamp()){
									//削除するdateのファイル数を格納
									int minvaluesize = tm.get(mindate).size();
									//古い要素を削除
									tm.remove(tm.firstKey());
									//表示するファイル数から削除した要素の数を減算
									displayfilecount = displayfilecount - minvaluesize;
									//新しいファイルを追加
									valuelist.add(item);
									tm.put(key, valuelist);
									displayfilecount++;
								}
							}
							count++;
						}
					}
				}
			}
		}
		finally {
			reader.close();
		}

		// 収集対象全ファイル数を更新
		Long maxkey = Long.MAX_VALUE;
		LogFile totalcount = new LogFile("",count,displayfilecount);
		java.util.ArrayList<LogFile> total = new ArrayList<LogFile>(java.util.Arrays.<LogFile>asList(totalcount));
		tm.put(maxkey, total);

		if (out.size() == 0)
			out.add(totalcount);

		return out;
	}
	/**
	 * 停止する
	 */
	public void stop() {
		super.stop();
		if (currentFTP != null) {
			currentFTP.cancel();
		}
	}

	/*
	 * getFileInfoAllの実装
	 * @see jp.co.canon.cks.ees.service.AbstractFileManager#getFileInfoAll(jp.co.canon.cks.ees.service.FileNameChecker)
	 */
	public LogItem[] getFileInfoAll(FileNameChecker checker, int filecount, String[] keywords) throws IOException, RunningStopException {
		int cuurentYear = Calendar.getInstance().get(Calendar.YEAR);
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH);	// month jan = 0, feb = 1, ...
		String path = getConfig().getFileLocation().getFile() + getCurrentPath();
		java.io.InputStream in = getFTP().ls(path);
		java.util.List out = new java.util.ArrayList();
		java.util.TreeMap<Long,ArrayList<LogItem>> tm = new java.util.TreeMap<Long,ArrayList<LogItem>>();

		java.io.BufferedReader reader = null;
		Integer count = 0;
		long displayfilecount = 0;

		// filecountを超えた場合の最小日時を保持する
		long mindate = 0;

		try {
			reader = new java.io.BufferedReader(new java.io.InputStreamReader(in));

			String buf = null;

			while ( (buf = reader.readLine()) != null) {
				if (isStopped()) throw new RunningStopException();
				String info = buf;
				String[] data = getToken(info);

				// attribute, gid, uid, ?, block size, month, day, year or time, file name
				if (data.length < 5) {
					logger.warning("FTP list command was invalid format. :" + info);
					continue; //
				}
				// 解析する
				int index = data.length -1;
				boolean directory = (info.charAt(0) == 'd');
				if (checker != null && !checker.matches(data[index])) continue;

				LogItem item = new LogItem();
				item.setName(getCurrentPath() + data[index]);
				StringBuffer dataStr = new StringBuffer();
				//　year or time
				if (data[index-1].length() == 4) {
					dataStr.append(data[index-1]).append("-")
						.append(data[index-3]).append("-")
						.append(data[index-2]).append(" 00:00");
				} else {
					// 年情報判定処理を追加
					dataStr.append(getYear(data[index-3], cuurentYear, currentMonth)).append("-")
					.append(data[index-3]).append("-")
					.append(data[index-2]).append(" ")
					.append(data[index-1]);
				}
				//
				try {
					item.setTimestamp(getDateTimeFormat(data[index-3]).parse(dataStr.toString()).getTime());
				} catch (Throwable ex) {
					logger.log(Level.WARNING, "date paser error", ex);
				}
				item.setSize(Long.parseLong(data[index-4]));
				item.setDirectory(directory);

				//Keywordsにヒットするitemのみ処理
				if (contansKeyword(item.getName(), keywords)){
					Long key = item.getTimestamp();
					java.util.ArrayList valuelist = null;

					//TreeMapにまだ格納されていないdateは追加
					if(!tm.containsKey(key)){
						valuelist = new java.util.ArrayList();
					} else {
						valuelist = tm.get(key);
					}
					// filecountを超えていない場合の処理
					if(displayfilecount < filecount){
						valuelist.add(item);
						tm.put(key, valuelist);
						displayfilecount++;

					//filecountを超えた場合
					} else {
						//現在TreeMapに存在する最古のdateを取得
						mindate = tm.firstKey();

						//最古のdateより新しいファイルの場合のみ追加
						if(mindate < item.getTimestamp()){
							//削除するdateのファイル数を格納
							int minvaluesize = tm.get(mindate).size();
							//表示するファイル数から減算
							displayfilecount = displayfilecount - minvaluesize;
							//古い要素を削除
							tm.remove(tm.firstKey());
							valuelist.add(item);
							tm.put(key, valuelist);
							displayfilecount++;
						}
					}
					count++;
				}
			}
		}
		finally {
			reader.close();
		}

		// 作成したTreeMapの全要素をoutに追加
		for(java.util.ArrayList<LogItem> data : tm.values()) {
			for(Object li : data) {
				out.add(li);
			}
		}

		// 制限を超えていた場合は要素（ファイル名にトータルファイル数を格納。サイズ、タイムスタンプは0）を1つ追加。
		if (count > filecount){
			LogItem tmp = new LogItem("###FileOverLimit:"+ count,0, 0,false);
			out.add(tmp);
		}

		return (LogItem[])out.toArray(new LogItem[0]);
	}
	/**
	 * lsコマンドで返ってきたリストをトークンに分解する
	 *
	 * Parameter:
	 * 	@param	lsLine	lsコマンドで入力された返された１行データ
	 * Result:
	 *	@return String[]	それぞれの項目を文字列の配列で返す。
	 */
	public String[] getToken(String lsLine) {
		java.util.ArrayList r = new java.util.ArrayList();
		int pos = 0;
		for (int i=0; i < lsLine.length() && r.size() < 8; i++) {
			if (lsLine.charAt(i) == ' ') {
				if (pos != i) {
					r.add(lsLine.substring(pos, i));
				}
				pos = i+1;	// 次の文字から開始
			}
		}
		// 残りを処理
		if (pos < lsLine.length()) {
			String name = lsLine.substring(pos).trim();
			if (lsLine.startsWith("l")) {	// 属性がlのため
				int spos = name.indexOf(SYMBOLIC_KEY);
				if (spos > 0) {
					name = name.substring(0, spos).trim();
				}
			}
			r.add(name);
		}
		return (String[])r.toArray(new String[0]);
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
	protected boolean contansKeyword(String target, String[] keywords) {
		if (keywords == null) return true;	// キーワードがな無ければ登録OK
		for (int i=0; i < keywords.length; i++) {
			if (target.indexOf(keywords[i]) >= 0) return true;
		}
		return false;
	}

	/**
	 *　FileNameAnalyzerを返す。FileNameAnalyzerの利用が初めての場合は、
	 *　内部設定を使って構築する。
	 *
	 * Result:
	 *	@return FileNameAnalyzer	FileNameAnalyzerのインスタンス
	 */
	protected FileNameAnalyzer getTimestampAnalyzer() {
		if (timestampAlalyzer == null) {
			timestampAlalyzer = new FileNameAnalyzer();
			timestampAlalyzer.setTimestampParts(config.getTimestampParts());
			timestampAlalyzer.setDateFormatString(config.getTimestampFormat());
		}
		return timestampAlalyzer;
	}

	// <RTLAファイルのフィルタ機能追加>
	/**
	 * toolIdに対応するRTLAPattern.xmlのID(subIDを除く)をカンマ区切りで並べる
	 *
	 * @param toolId 装置
	 * @param toolIdSuffix RTLAPatternフォルダ格納先のsuffix
	 * @return RTLAPattern.xmlのID(subIDを除く)をカンマ区切りで並べた文字列.
	 * @throws Exception
	 */
	private String getRTLAPatternIds(String toolId, String toolIdSuffix) throws Exception {
		// 装置に対応するRTLAPattern.xmlをパース
		StackHandler handler = new StackHandler();
		Stack stack = handler.parse(new File(FCS_HOME, "/conf/models/" + toolId + toolIdSuffix + "/RTLAPattern.xml"));

		// subIDを分離したIDのSetを作る
		List<StackItem> stackItemsList = stack.getStackItemList();
		TreeSet<Integer> idSet = new TreeSet<Integer>();
		for(StackItem stackItem : stackItemsList) {
			String stackID = stackItem.getId();
			int keyIdx = stackID.indexOf('_');
			int id;
			if(keyIdx < 0)
				id = Integer.parseInt(stackID);
			else
				id = Integer.parseInt(stackID.substring(0, keyIdx));
			idSet.add(id);
		}
		if(idSet.isEmpty())
			throw new Exception("[" + toolId + toolIdSuffix + "] No Items in RTLAPattern.xml");

		// IDをカンマ区切りで列挙する
		StringBuilder sb = new StringBuilder();
		Iterator<Integer> it = idSet.iterator();
		while(it.hasNext())
			sb.append(it.next()).append(",");
		sb.deleteCharAt(sb.lastIndexOf(",")); // 最後のカンマを削除
		return sb.toString();
	}
}
