/**
 * 
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : FileGetCommand.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/04
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.co.canon.cks.ees.service.AdminClient;
import jp.co.canon.cks.ees.service.AdminServer;
import jp.co.canon.cks.ees.service.DownloadExecutor;
import jp.co.canon.cks.ees.service.FileZipper;
//import jp.co.canon.cks.ees.service.FileZipper;
import jp.co.canon.cks.ees.service.model.ConfigurationModel;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	ファイルを取得するコマンドを実装するクラス
 *
 * @author Tomomitsu TATEYAMA
 * ======================================================================
 * Change History:
 * 2011-01-09	T.Tateyama エラー内容をファイルに出力できるように修正
 * 2011-11-27	T.Tateyama Zip圧縮しないように修正
 * 2012-10-30	T.TATEYAMA コンストラクタの指定によりZIP圧縮／非圧縮を切り換える。
 * 2016-11-28	J.Sugawara	設定ファイルパスを追加 16-018-01 新方式フィルタリング機能対応
 */
public class FilesGetCommand implements CommandModel {
	private static Logger logger = Logger.getLogger(FilesGetCommand.class.getName());
	private ConfigurationModel config = null;
	private String requestNo = null;
	private String paramStr = null;
	private int mode = 0; // 0: パラメータからの指定　1:ファイルからの指定
	private String[] fileList = null;
	private long diskCheckSize = -1;
	// 定数を追加する
	protected static final String RESOURCE_NAME = "jp.co.canon.cks.ees.service.Message";
	protected static final String MSG_IOERR_ZIP = "msg.io.err.zip";
	private static final String ERR_FILE = "error.msg";

	// 2012.10.30 T.TATEYAMA 
	private boolean actionArchive = false;
	private String configFilePath = ""; // 16-018-01 新方式フィルタリング機能対応

	/**
	 * パラメータを指定して構築する
	 * @param c			設定モデル
	 * @param rno		要求番号
	 * @param files		対象のファイル（カンマ区切りで複数）
	 * @param paramFile	複数のファイルでコマンドで収まらない場合はテンポラリファイルからファイルを取得
	 * @param diskCheck	ディスクチェック用のパラメータnull以外の場合はそのダウンロードフォルダのサイズをチェックする
	 * @param archive	ZIP圧縮するかどうかを指定。	2012.10.30 add
	 * @param configPath	設定ファイルのパス。	16-018-01 新方式フィルタリング機能対応
	 */
	public FilesGetCommand(ConfigurationModel c, String rno, String files, String paramFile, String diskCheck, boolean archive, String configPath) {
		if (c == null) throw new IllegalArgumentException("configuration model is null");
		if (rno == null) throw new IllegalArgumentException("request no is null");
		// パラメータを保持
		config = c;
		requestNo = rno;
		// ファイル名の取得実装で行う
		if (files != null) {
			paramStr = files;
			mode = 0;
		} else if (paramFile != null) {
			paramStr = paramFile;
			mode = 1;
		} else {
			throw new IllegalArgumentException("upload file don't select.");
		}
		if (diskCheck != null) {
			try {
				updateDiskCheckSize(diskCheck);
			} catch (Throwable ex) {
				throw new IllegalArgumentException("disksize check parameter wrong.");
			}
		}
		actionArchive = archive;	// 2012.10.30
		configFilePath = configPath; // 16-018-01 新方式フィルタリング機能対応
	}
	/**
	 * ディスクチェックパラメータの内容をチェック展開する
	 *
	 * Parameter:
	 * 	@param	diskCheck	サイズが書かれている文字列末尾の文字によりサイズが異なる
	 */
	private void updateDiskCheckSize(String diskCheck) {
		if (diskCheck != null) {
			//------------------
			diskCheck = diskCheck.toLowerCase();
			if (diskCheck.endsWith("k")) {
				diskCheckSize = Long.parseLong(diskCheck.substring(0, diskCheck.length()-1));
				diskCheckSize *= 1024;	// Kbyte
			} else if (diskCheck.endsWith("m")) {
				diskCheckSize = Long.parseLong(diskCheck.substring(0, diskCheck.length()-1));
				diskCheckSize *= 1024 * 1024;	// Mbyte
			} else if (diskCheck.endsWith("g")) {
				diskCheckSize = Long.parseLong(diskCheck.substring(0, diskCheck.length()-1));
				diskCheckSize *= 1024 * 1024 * 1024;	// Gbyte
			} else {
				diskCheckSize = Long.parseLong(diskCheck.substring(0, diskCheck.length()-1));
			}
			//------------------
		}		
	}
	/**
	 *　現在のディスクサイズを確認する
	 *
	 * Result:
	 *	@return long　ダウンロードディレクトリのディスク使用量
	 */
	private long getUsedDiskSizeAtDownload() {
		File[] files = config.getDownloadDirectory().listFiles();
		
		long totalSize = 0;
		for (int i=0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				java.io.File[] children = files[i].listFiles();
				for (int n=0; n < children.length; n++) {
					totalSize += children[n].length();
				}
			} else {
				totalSize += files[i].length();
			}
		}
		
		return totalSize;
	}
	/**
	 * パラメータファイルを読み込み　取得ファイル名の一覧を返す
	 * Parameter:
	 * 	@param	file	パラメータファイル
	 *
	 * Result:
	 *	@return String[]	取得ファイルの一覧
	 */
	private String[] getRequestFileNames(String file) throws IOException {
		BufferedReader reader = null;
		File one = new File(file);
		if (!one.exists()) throw new IllegalArgumentException("parameter file is not found.");
		java.util.List l = new java.util.ArrayList();
		try {
			reader = new BufferedReader(new FileReader(one));
			while(true) {
				String line = reader.readLine();
				if (line == null) break;
				l.add(line);
			}
			reader.close();
			one.delete();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "parameter file can't read.", ex);
			if (reader != null) {
				reader.close();
			}
			throw ex;
		}
		return (String[])l.toArray(new String[0]);
	}
	/**
	 * エラーメッセージがあれば、エラーを出力する
	 *
	 * Parameter:
	 * 	@param	msg	エラーメッセージ
	 */
	private void writeErrorMessage(String msgKey) throws IOException {
		if (msgKey == null) return;
		ResourceBundle resource = ResourceBundle.getBundle(RESOURCE_NAME);
		if (resource != null) {
			String value = resource.getString(msgKey);
			if (value != null) {
				File msgFile = new File(new File(config.getDownloadDirectory(), requestNo), ERR_FILE);
				BufferedWriter writer = null;
				try {
					writer = new BufferedWriter(new FileWriter(msgFile));
					writer.write(value);
				} finally {
					if (writer != null) {
						writer.close();
					}
				}				
			}
		}
	}
	/* 
	 * executeの実装
	 * @see jp.co.canon.cks.ees.service.command.CommandModel#execute()
	 */
	public void execute() throws RunningStopException, IOException {
		//　取得ファイル名を取り出す。
		switch (mode) {
		case 0:
			fileList = paramStr.split(",");
			break;
		case 1:
			fileList = getRequestFileNames(paramStr);
			break;
		}
		//　ファイルの指定が無い場合は正常終了
		if (fileList.length == 0) {
			logger.info("request file names didn't set.:" + requestNo);
			return ;
		}
		// コマンドを実行する
		DownloadExecutor executor = new DownloadExecutor(config, requestNo, fileList, configFilePath);// 16-018-01 新方式フィルタリング機能対応
		AdminServer server = null;
		try {
			server = new AdminServer(config.getAdminiPort(), config.getDataName(), requestNo);
			server.setExecutor(executor);	// 設定が漏れている
			Thread th = new Thread(server);
			th.start();	// サーバーを別スレッドで起動する
			for (int i=0; i < 100; i ++) {
				try {
					Thread.sleep(100);
				} catch (Throwable ex) {}
				if (server.isStartup()) break;
				if (!th.isAlive()) {		
					throw new RunningStopException(RunningStopException.EXISTS_OTHER_REQUESTED);
				}
			}
			// --------------------
			if (diskCheckSize > 0) {
				if (diskCheckSize < getUsedDiskSizeAtDownload()) {
					logger.log(Level.WARNING, "download directory size is full.");
					throw new RunningStopException(RunningStopException.DISKSIZE_MAX);
				}
			}
			// --------------------
			System.out.println("Command Execution");
			executor.run(); // カレントで実行する
			// ------------- 終わったらキャンセルされても 2011-11-27
			if (actionArchive) {
				FileZipper zipper = new FileZipper();
				zipper.setRequestNo(requestNo);
				zipper.setUploadFileDirectory(new File(config.getDownloadDirectory(), requestNo));
				try {
					zipper.zip();
				} catch (IOException ex) {
					logger.log(Level.SEVERE, "error in zipper", ex);
					if (executor.getErrorMsgKey() == null) {
						writeErrorMessage(MSG_IOERR_ZIP);
					}
				}
			}
			//---------------
			writeErrorMessage(executor.getErrorMsgKey());
		} finally {
			AdminClient client = new AdminClient(config.getAdminiPort());
			try {
				if (!server.isStopped()) {
					client.sendCommand(AdminServer.COMPLETE_CMD + " " + requestNo);
				}
			} catch (IOException ex) {
				// ログを出力
				logger.log(Level.SEVERE, "happend the AdminClient", ex);
			}
		}
	}
}
