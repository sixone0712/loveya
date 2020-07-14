/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : LocalFileManager.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;

import java.io.*;
//import java.util.logging.Logger;

import jp.co.canon.cks.ees.service.command.RunningStopException;
import jp.co.canon.cks.ees.service.model.ConfigurationModel;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	ローカルファイルに対するAbstractFileManager実装クラス
 *
 * @author Tomomitsu TATEYAMA
 * =============================================================================
 * Change History:
 * 2011-01-03	T.Tateyama	getFileInfoAll()の実装追加
 * 2011-01-07	T.Tateyama	getFileInfoAll()のパラメータがnullの場合の処理を追加
 * 2015-04-14	T.TAKIGUCHI	getFileInfos()/getFileInfoAll()の引数を変更
 * 2015-04-14	T.TAKIGUCHI	getFileInfos()でツリーマップにファイル情報を追加する処理を追加
 */
public class LocalFileManager extends AbstractFileManager {
	//private static final Logger logger = Logger.getLogger(LocalFileManager.class.getName());
	public final static String SUPPORT_PROTOCOL = "file";
	private ConfigurationModel config = null;
	private FileNameAnalyzer timestampAlalyzer = null;

	/**
	 * コンストラクタ　ConfigurationModelを指定して生成する。
	 * @param c	ConfigurationModelを指定する
	 */
	public LocalFileManager(ConfigurationModel c) {
		if (c == null) throw new IllegalArgumentException("configulation is null");
		if (!c.getFileLocation().getProtocol().equalsIgnoreCase(SUPPORT_PROTOCOL)) {
			throw new IllegalArgumentException("not support configuration");
		}
		config = c;
	}
	/**
	 *	内部で保持しているConfigurationModelの返す。
	 *
	 * Result:
	 *	@return ConfigurationModel	内部で保持する設定モデル
	 */
	protected ConfigurationModel getConfig() {
		return config;
	}
	/* (Disable Javadoc)
	 * @see jp.co.canon.cks.ees.service.AbstractFileManager#getFileInfos()
	 */
	public java.util.List getFileInfos(FileNameChecker checker) throws IOException, RunningStopException {
		File file = new File(getConfig().getFileLocation().getPath() + getCurrentPath());
		if (!file.exists()) throw new FileNotFoundException("directory is not found");
		File[] files = file.listFiles();
		java.util.List out = new java.util.ArrayList();
		for (int i=0; i < files.length; i++) {
			if (isStopped()) throw new RunningStopException();
			LogFile item = null;
			if (files[i].isDirectory()) {
				out.add(files[i].getName());
			} else {
				if (checker.matches(files[i].getName())) {
					item = new LogFile();
					item.setName(getCurrentPath() + files[i].getName()); // full path
					item.setSize(files[i].length());
					item.setTimestamp(files[i].lastModified());
					out.add(item);
				}
			}
		}
		return out;
	}
	/**
	 * FileStreamを返す。
	 */
	public java.io.InputStream getFileStream(String name) throws IOException {
		File file = new File(getConfig().getFileLocation().getFile() + name);
		return new BufferedInputStream(new FileInputStream(file)) ;
	}
	/* 
	 * getFileInfoAllの実装
	 * @see jp.co.canon.cks.ees.service.AbstractFileManager#getFileInfoAll(jp.co.canon.cks.ees.service.FileNameChecker)
	 */
	public LogItem[] getFileInfoAll(FileNameChecker checker) throws IOException, RunningStopException {
		File file = new File(getConfig().getFileLocation().getPath() + getCurrentPath());
		if (!file.exists()) throw new FileNotFoundException("directory is not found");
		File[] files = file.listFiles();
		java.util.List out = new java.util.ArrayList();
		for (int i=0; i < files.length; i++) {
			if (isStopped()) throw new RunningStopException();
			if (checker != null && !checker.matches(files[i].getName())) continue;

			LogItem item = new LogItem();
			item.setDirectory(files[i].isDirectory());
			item.setName(getCurrentPath() + files[i].getName()); // full path
			item.setSize(files[i].length());
			item.setTimestamp(files[i].lastModified());
			out.add(item);
		}
		return (LogItem[])out.toArray(new LogItem[0]);
	}

	/* (Disable Javadoc)
	 * @see jp.co.canon.cks.ees.service.AbstractFileManager#getFileInfos()
	 */
	public java.util.List getFileInfos(FileNameChecker checker,int filecount, String[] keywords,java.util.TreeMap<Long,java.util.ArrayList<LogFile>> tm, java.util.Date s, java.util.Date e) throws  RunningStopException, IOException {
		File file = new File(getConfig().getFileLocation().getPath() + getCurrentPath());
		if (!file.exists()) throw new FileNotFoundException("directory is not found");

		/*2015-04-14*/
		Long count = (long)0;
		Long displayfilecount = (long) 0;
		// filecountを超えた場合の最小日時を保持する
		Long mindate = (long) 0;
		//現在TreeMapに格納されている数を数える
		if (tm.size() != 0) {
			for (java.util.ArrayList<LogFile> items : tm.values()){
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
		/*/2015-04-14*/


		File[] files = file.listFiles();
		java.util.List out = new java.util.ArrayList();
		for (int i=0; i < files.length; i++) {
			if (isStopped()) throw new RunningStopException();
			LogFile item = null;
			if (files[i].isDirectory()) {
				out.add(files[i].getName());
			} else {
				if (checker.matches(files[i].getName())) {
					item = new LogFile();
					item.setName(getCurrentPath() + files[i].getName()); // full path
					item.setSize(files[i].length());

					//ファイル名からTimeStampを解決する場合はファイル名の日付をTimeStampにセットする
					if(config.isUsedTimestampFromFileName()) {
						try {
							if(config.isGZipOutput()) {
								item.setTimestamp(getTimestampAnalyzer().getModifiedTime(getCurrentPath() + files[i].getName() + ".gz"));
							}
							else {
								item.setTimestamp(getTimestampAnalyzer().getModifiedTime(getCurrentPath() + files[i].getName()));
							}
						} catch (java.text.ParseException e1) {
							throw new IOException("Error: don't get the file timestamp ");
						}
					}
					else {
						item.setTimestamp(files[i].lastModified());
					}

					//日付チェックする場合、指定した期間内のファイルでない場合処理を飛ばす
					if(s != null && e != null) {
						if(config.isTimestampCheck() && (item.getTimestamp() < s.getTime() || e.getTime() < item.getTimestamp())){
							continue;
						}
					}
					//Keywordsにヒットするitemのみ処理
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

		// 収集対象全ファイル数を更新
		Long maxkey = Long.MAX_VALUE;
		LogFile totalcount = new LogFile("",count,displayfilecount);
		java.util.ArrayList<LogFile> total = new java.util.ArrayList<LogFile>(java.util.Arrays.<LogFile>asList(totalcount));
		tm.put(maxkey, total);
		if (out.size() == 0)
			out.add(totalcount);

		return out;
	}

	/*
	 * getFileInfoAllの実装
	 * @see jp.co.canon.cks.ees.service.AbstractFileManager#getFileInfoAll(jp.co.canon.cks.ees.service.FileNameChecker)
	 */
	public LogItem[] getFileInfoAll(FileNameChecker checker,int filecount, String[] keywords) throws IOException, RunningStopException {
		File file = new File(getConfig().getFileLocation().getPath() + getCurrentPath());
		if (!file.exists()) throw new FileNotFoundException("directory is not found");
		File[] files = file.listFiles();
		java.util.List out = new java.util.ArrayList();
		for (int i=0; i < files.length; i++) {
			if (isStopped()) throw new RunningStopException();
			if (checker != null && !checker.matches(files[i].getName())) continue;

			LogItem item = new LogItem();
			item.setDirectory(files[i].isDirectory());
			item.setName(getCurrentPath() + files[i].getName()); // full path
			item.setSize(files[i].length());
			item.setTimestamp(files[i].lastModified());
			out.add(item);
		}
		return (LogItem[])out.toArray(new LogItem[0]);
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
}
