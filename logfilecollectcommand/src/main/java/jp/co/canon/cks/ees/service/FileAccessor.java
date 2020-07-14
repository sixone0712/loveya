/**
 * 
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : FileAccessor.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/11/30
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */
package jp.co.canon.cks.ees.service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import java.util.zip.GZIPInputStream;

import jp.co.canon.cks.ees.service.command.RunningStopException;
import jp.co.canon.cks.ees.service.model.ConfigurationModel;
import jp.co.canon.cks.ees.service.model.FileNameCheckRuleModel;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	AbstractManagerを使ってファイルに対してアクセスするクラス
 *
 * @author Tomomitsu TATEYAMA
 * =============================================================================
 * Change History:
 * 2011-01-03	T.Tateyama	特定のディレクトリ内のファイル・ディレクトリ情報を返す様にgetItemInDirectory()メソッドを追加
 * 2011-01-07	T.Tateyama	ルール指定がない場合に、すべてのディレクトリ内容を返す様に修正
 * 2011-01-09	T.Tateyama	パスのクリア方法を変更
 * 2015-04-14	T.TAKIGUCHI	ファイルリストの上限（5,000）を指定できるようgetFiles/readDirectory/getItemInDirectoryメソッドを修正
 * 2016-11-28	J.Sugawara	 16-018-01 新方式フィルタリング機能対応
 */
public class FileAccessor {
	private static final Logger logger = Logger.getLogger(FileAccessor.class.getName());
	private static final int BUFFER_SIZE = 1024;
	private boolean stop = false;
	private ConfigurationModel configuration = null;
	private FileNameAnalyzer timestampAlalyzer = null;
	private long currentDownloadSize = 0;
	private AbstractFileManager fileManager = null;
	private long lastTime = 0;
	private static final int fileupperlimit = 5000;

	/**
	 * ダウロード済のバイト数を返す。
	 * Result:
	 *	@return long	既にダウンロードされたバイト数
	 */
	public long getCurrentDownloadSize() {
		return currentDownloadSize;
	}
	/**
	 *　FileManagerを取得する。
	 * Result:
	 *	@return AbstractFileManager
	 */
	private AbstractFileManager getFileManager() {
		if (fileManager == null) {
			fileManager = FileManagerFactory.createInstance(getConfiguration());
		}
		return fileManager;
	}
	/**
	 *　指定されたログのファイル一覧を取得する
	 * Parameter:
	 * 	@param	s		検索開始日付　指定がない場合なnullを指定
	 * 	@param	e		検索終了日付　指定がない場合なnullを指定
	 *  @param	keywords	キーワード　指定がない場合はnullを指定
	 * Result:
	 *	@return FileList ファイルの一覧
	 */
	public FileList getFiles(java.util.Date s, java.util.Date e, String[] keywords) throws IOException, RunningStopException {
		FileList r = new FileList();
		java.util.TreeMap<Long,java.util.ArrayList<LogFile>> tm = new java.util.TreeMap<Long, java.util.ArrayList<LogFile>>();
		Long totalfilecount;

		try {
			FileNameCheckRuleModel[] rules = getConfiguration().getFileNameRule();
			readDirecotry(null, rules, 0, s, e, 0, keywords,tm);
		} finally {
			getFileManager().close();
		}
		totalfilecount = tm.get(tm.lastKey()).get(0).getSize();

		//トータルファイル数情報を削除
		tm.remove(tm.lastKey());

		for (ArrayList<LogFile> items : tm.values()){
			for (LogFile item : items){
				LogFile file;

				// GZip圧縮有効の場合はファイル名の".gz"拡張子を付加する。
				if(configuration.isGZipOutput()){
					file = item;
					file.setName(file.getName() + ".gz");
				} else {
					file = item;
				}
				r.addFile(file);
			}
		}

		logger.log(Level.INFO, "Total: " + totalfilecount + " Display: " + r.size());

		if (totalfilecount > fileupperlimit){
			LogFile tmp = new LogFile("###FileOverLimit:"+ totalfilecount,0, 0);
			r.addFile(tmp);
		}
		return r;
	}
	/**
	 * ユーザ設定のスリープをさせる
	 */
	private void actionUserSleep() {
		if (getConfiguration().getWaitTime() == 0) return;
		long current = System.currentTimeMillis();
		if (lastTime == 0) {
			lastTime = current;
			return;
		}
		if (lastTime < current - getConfiguration().getWaitInterval()) {
			lastTime = current;
			try {
				Thread.sleep(getConfiguration().getWaitTime());
			} catch (Throwable ex) {}
		}
	}
	
	/**
	 * 指定されたファイルのOutputStreamを返す
	 *
	 * Parameter:
	 * 	@param	outFile	対象のファイル
	 * 	@param	gzip	圧縮が必要な場合
	 *
	 * Result:
	 *	@return OutputStream	開いたOutputStream
	 */
	private OutputStream getOutputStream(File outFile, boolean gzip) throws IOException {
		OutputStream out = null;
		try {
			out = new java.io.FileOutputStream(outFile);			
			if (gzip) {
				out = new java.util.zip.GZIPOutputStream(out);
			}
			return out;
		} catch (IOException ex) {
			if (out != null) out.close();
			throw ex;
		}
	}
	/**
	 * 指定さらたファイルをのInputStreamを取得する
	 *
	 * Parameter:
	 * 	@param	targetFile	対象のファイル名
	 * 	@param	gzip		解凍が必要な場合true それ以外はfalse
	 *
	 * Result:
	 *	@return InputStream	開いたInputStream
	 */
	private InputStream getInputStream(String targetFile, boolean gzip) throws IOException {
		InputStream	in = null;
		try {
			AbstractFileManager afm = getFileManager();
			in = afm.getFileStream(targetFile);
			if(in != null){
				if (gzip) {
					in = new GZIPInputStream(in);
				}
				return in;
			} else {
				return null;
			}
		} catch (IOException ex) {
			if (in != null) in.close();
			throw ex;
		}
	}
	/**
	 * 指定された位置にファイルを取得する
	 *
	 * Parameter:
	 * 	@param	targetFile	対象のファイル名
	 *	@param	outFile		出力するファイル
	 */
	public void writeFile(String targetFile, File outFile) throws IOException, RunningStopException {
		OutputStream out = null;
		try {
			logger.info("open output file : " + outFile.getName());
			if(configuration.isGZipOutput()){
				out = getOutputStream(outFile, true);
			} else {
			out = getOutputStream(outFile, false);
			}
			logger.info("start uploading : " + targetFile );
			// ファイルを取得するためのクラスを準備する
			currentDownloadSize = 0;
			// 引数からファイル名（ロケーション以降のパス名を含む）
			InputStream	in = null;
			try {
				if(configuration.isGZipOutput()){
					String originalFileName = targetFile.substring(0,targetFile.lastIndexOf(".gz"));
					in = getInputStream(originalFileName, false);
				} else {
					in = getInputStream(targetFile, false);
				}
				
				// 中断されていた場合は終了
				if (isStopped()) {
					logger.info("Canceled to download : " + targetFile );
					throw new RunningStopException();
				}
				
				byte[]	buffer = new byte[BUFFER_SIZE];
				while (true) {
					int size = in.read(buffer);
					if (size <= 0) break; 	// 読み込み終了
					currentDownloadSize += size;
					// 書き込み前に中断されているかどうかチェックする。　
					if (isStopped()) {
						logger.info("Canceled to download : " + targetFile );
						throw new RunningStopException();
					}
					// ユーザからスリープさせる
					actionUserSleep(); 				
					// バッファから読み込めらた実際に書きこむ　
					out.write(buffer, 0, size);
				}
			} finally {
				if (in != null) {
					in.close();
				}
			}
		} finally {
			if (out != null) {
				out.close();
			}
		}
		logger.info("complete to download : " + targetFile);
	}
	/**
	 *　指定されたファイルにフィルタしながら書き込む。
	 *
	 * Parameter:
	 * 	@param	targetFile	対象のファイル名
	 *	@param	outFile		出力するファイル
	 *	@param	gzip		圧縮・解凍が必要かどうかを指定　trueの場合は、圧縮解凍が必要
	 *
	 *	@throws	IOException, RunningStopException
	 */
	public void writeFileByFilter(String targetFile, File outFile) throws IOException, RunningStopException {
		FilteredBufferedWriter writer = null;
		try {
			logger.info("open output file : " + outFile.getName());
			
			boolean gzip_out = false;
			String charsetName = getConfiguration().getFilterCharsetName();
			if(configuration.isGZipOutput()){
				gzip_out = true;
			} else {
				if(targetFile.endsWith(".gz")){
					gzip_out = true;
				} else {
					gzip_out = false;
				}
			}
			
			// 書き込みファイルを開く
			if(charsetName != null){
				writer = new FilteredBufferedWriter(new OutputStreamWriter(getOutputStream(outFile, gzip_out), charsetName), getConfiguration().getFilter(), getConfiguration().getFilterLineSeparator());
			} else {
				writer = new FilteredBufferedWriter(new OutputStreamWriter(getOutputStream(outFile, gzip_out)), getConfiguration().getFilter(), getConfiguration().getFilterLineSeparator());
			}
			logger.info("start uploading : " + targetFile );
			// ファイルを取得するためのクラスを準備する
			currentDownloadSize = 0;
			// 引数からファイル名（ロケーション以降のパス名を含む）
			BufferedReader reader = null;
			try {
				String targetFileIn;
				if(configuration.isGZipOutput()){
					targetFileIn = targetFile.substring(0,targetFile.lastIndexOf(".gz"));
				} else {
					targetFileIn = targetFile;
				}
				
				boolean gzip_in = false;
				if(targetFileIn.endsWith(".gz")){
					gzip_in = true;
				} else {
					gzip_in = false;
				}
				
				InputStream in = getInputStream(targetFileIn, gzip_in);
				
				// 中断されていた場合は終了
				if (isStopped()) {
					logger.info("Canceled to download : " + targetFile );
					throw new RunningStopException();
				}
				
				if(charsetName != null){
					reader = new BufferedReader(new InputStreamReader(in, charsetName));
				} else {
					reader = new BufferedReader(new InputStreamReader(in));
				}
				
				while (true) {
					String line = reader.readLine();
					if (line == null) break; 	// 読み込み終了
					currentDownloadSize += line.length();
					// 書き込み前に中断されているかどうかチェックする。　
					if (isStopped()) {
						logger.info("Canceled to download : " + targetFile );
						throw new RunningStopException();
					}
					// ユーザからスリープさせる
					actionUserSleep(); 				
					// バッファから読み込めらた実際に書きこむ　
					writer.writeLine(line);		// １行単位で書き込む
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		logger.info("complete to download : " + targetFile);
	}
	/**
	 * 指定された位置にファイルを取得し外部コマンドを実行する  16-018-01  新方式フィルタリング機能対応
	 *
	 * Parameter:
	 * 	@param	targetFile	対象のファイル名
	 *	@param	outFile		出力するファイル
	 *	@param	configPath	設定ファイルパス
	 */
	public void writeFileByCom(String targetFile, File outFile, String configPath) throws IOException, RunningStopException, InterruptedException {
		// 設定ファイルに記述された外部コマンド
		String execCommand = getConfiguration().getExecCommand();
		// 一時ファイル名
		String tmpFileFullName = outFile.getAbsolutePath()+".tmp";
		// 出力ファイル名
		String outFileFullName = outFile.getAbsolutePath();
		// コマンド作業用パス
		String tmpPath = outFileFullName.substring(0, outFileFullName.lastIndexOf("/") + 1) + "ExecComTemp";

		// ファイルを取得する
		writeFile(targetFile, new File(tmpFileFullName));

		// 外部コマンドを実行する
		new File(tmpPath).mkdir();
		StringBuffer command = new StringBuffer();
		command.append("cd ").append(configPath).append(";"); // 外部コマンド実体は設定ファイルパスと同じディレクトリに在る
		command.append(execCommand).append(" ");
		command.append(tmpFileFullName).append(" ");
		command.append(outFileFullName).append(" ");
		command.append(tmpPath).append(";");
		String[] commands = {"sh","-c", command.toString()};
		try {
			ProcessBuilder pb =  new ProcessBuilder(commands);
			Process p = pb.start();
			p.waitFor();
			int ret = p.exitValue();
			if (ret != 0) {
				logger.log(Level.SEVERE, "Execution of external command failed.");
			}
			logger.log(Level.INFO, "execCommand[sh -c \""+command.toString()+"\"] return code="+ret);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "writeFileByCom() IOException("+e.getMessage()+")["+command.toString()+"]");
			throw e;
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, "writeFileByCom() InterruptedException("+e.getMessage()+")["+command.toString()+"]");
			throw e;
		} finally {
			// 一時ファイルを削除
			delete(new File(tmpFileFullName));
			// コマンド作業用パスを削除
			delete(new File(tmpPath));
		}
	}

	/**
	 * ファイルやディレクトリを再帰的にすべて削除する 16-018-01
	 * @param f ファイルまたはディレクトリ
	 */
	private void delete(File f){
		// 指定したファイル、ディレクトリが無ければ何もしない
		if (f.exists() == false) {
			return;
		}

		// ファイルの場合は、削除する
		if (f.isFile()) {
			f.delete();
		}

		// ディレクトリの場合は、中のファイルとディレクトリを削除してから削除する
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for(int i = 0; i < files.length; i++) {
				delete(files[i]);
			}
			f.delete();
		}
	}

	/**
	 *　FileNameAnalyzerを返す。FileNameAnalyzerの利用が初めての場合は、
	 *　内部ほ設定を使って構築する。
	 *
	 * Result:
	 *	@return FileNameAnalyzer	FileNameAnalyzerのインスタンス
	 */
	protected FileNameAnalyzer getTimestampAnalyzer() {
		if (timestampAlalyzer == null) {
			timestampAlalyzer = new FileNameAnalyzer();
			timestampAlalyzer.setTimestampParts(getConfiguration().getTimestampParts());
			timestampAlalyzer.setDateFormatString(getConfiguration().getTimestampFormat());
		}
		return timestampAlalyzer;
	}
	/**
	 * コンフィグレーションを返す。
	 * @return configuration
	 */
	public ConfigurationModel getConfiguration() {
		return configuration;
	}
	/**
	 * コンフィグレーションを設定する
	 * @param configuration セットする ConfigurationModel
	 */
	public void setConfiguration(ConfigurationModel configuration) {
		this.configuration = configuration;
	}
	/**
	 *　処理を停止する。
	 */
	public void stop() {
		stop = true;
		if (fileManager != null) {
			fileManager.stop();
		}
	}
	/**
	 *　中止されているかどうかを返す。
	 *
	 * Result:
	 *	@return boolean	true	: 中止されている
	 *					false	: 中止されていない
	 */
	protected boolean isStopped() {
		return stop;
	}
	/**
	 * ディレクトリ内の一覧を読み込んで装置ログファイルの一覧を収取する。
	 *
	 * Parameter:
	 * 	@param	subDir	サブディレクトリ名を指定
	 * 	@param	rule	階層毎のファイルルールを指定する
	 * 	@param	index	階層数（FileLocation以下の階層順番）
	 *  @param	s		検索対象日時の開始
	 *  @param	e		検索対象日時の終了
	 * 	@param	cfDate	フォルダに日付の意味合いを持たせた場合の現在の値
	 *  @param	keywords	ファイルおよびフォルダのキーワード
	 *  @param	tm		表示するファイルを格納するツリーマップ（最大5,000ファイルまでの情報を格納）
	 */
	protected void readDirecotry(String subdir, FileNameCheckRuleModel[] rules, int index, java.util.Date s, java.util.Date e, long cfDate, String[] keywords,java.util.TreeMap<Long,ArrayList<LogFile>> tm) throws IOException, RunningStopException {
		if (rules.length <= index) return;

		// ディレクトリに移動する
		if (subdir != null) getFileManager().appendPath(subdir);
		// 対象をチェックする
		FileNameChecker checker = new FileNameChecker(cfDate);
		checker.setRule(rules[index]);
		//　フィルタリングする。
		checker.setFilter(s, e);
		// パターンに一致したファイル名をTreeMapに格納する
		List<?> items = getFileManager().getFileInfos(checker,fileupperlimit, keywords,tm,s,e);

		for (int i=0; i < items.size(); i++) {
			if (isStopped()) throw new RunningStopException();
			Object item = items.get(i);

			// 取得したリストの要素がディレクトリの場合
			if (item instanceof String) {
				// ディレクトリは全て帰ってきます
				String dir = (String)item;
				if (checker.matches(dir)) {
					readDirecotry(dir, rules, index+1, s, e, checker.getCurrentFolderDate(), keywords,tm);
				}
			// 取得したリストの要素がファイルの場合
			} else if (item instanceof LogFile) {
			}
		}
		if (subdir != null) getFileManager().removeLastPath();
	}
	
	/**
	 * ディレクトリ内のファイルおよびディレクトリ一覧を返す。
	 *
	 * Parameter:
	 * 	@param	targetDir	検索対象のルート以下のディレクトリを示す
	 * Result:
	 *	@return List ディレクトリ内のファイルとディレクトリを保持したリストを返す。
	 */
	public List<LogItem> getItemInDirectory(String targetDir, String[] keywords) throws IOException, RunningStopException {
		ArrayList<LogItem> r = new ArrayList<LogItem>();
		FileNameCheckRuleModel[] rules = getConfiguration().getFileNameRule();
		String[] paths = new String[0];
		if (targetDir != null) paths = targetDir.split("/");
		// -------- 初期化
		getFileManager().clearPath();
		// ディレクトリに移動する
		for (int i=0; i < paths.length; i++) {
			getFileManager().appendPath(paths[i]);
		}
		// 対象をチェックする
		FileNameChecker checker = null;
		if (paths.length < rules.length) {
			checker = new FileNameChecker(0);
			checker.setRule(rules[paths.length]);
		}
		// パターンに一致したファイル名を取得する
		LogItem[] items = getFileManager().getFileInfoAll(checker,fileupperlimit,keywords);
		for (int i=0; i < items.length; i++) {
			if (isStopped()) throw new RunningStopException();
			LogItem item = items[i];
			if (item.isDirectory()) {
				// 再帰で処理する場合、直前にmatchesをかけないと日付のキャッシュがそのディレクトリの日付ではない可能性があるため
				// 正しく処理されない。readDirectoryの方ではその様に実装されている。
				// この処理では、ディレクトリによる期間の絞り込みが必要ないのでとりあえず外す。
				// もし将来readDirectoryを修正するのであれば、日付のキャッシュの計算を別で行えるようにするのがよいと思われる。
			} else {
				//トータルファイル数が格納されている要素はタイムスタンプを変更しない
				if (getConfiguration().isUsedTimestampFromFileName() && !item.getName().startsWith("#")) {
					try {
						item.setTimestamp(getTimestampAnalyzer().getModifiedTime(item.getName()));
					} catch (java.text.ParseException ex) {
						logger.log(Level.SEVERE, "change the IOException", ex);
						throw new IOException("Error: don't get the file timestamp.");
					}
				}
			}
			r.add(item);	// リストに追加する
		}
		return r;
	}
}
