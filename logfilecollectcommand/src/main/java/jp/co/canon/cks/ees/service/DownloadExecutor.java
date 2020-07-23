/**
 * 
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : DownloadStatus.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/04
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.co.canon.cks.ees.service.command.RunningStopException;
import jp.co.canon.cks.ees.service.ftp.FTPException;
import jp.co.canon.cks.ees.service.model.ConfigurationModel;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	ファイルのダウンロードを行うクラス。又、進捗状況を保持している。
 *
 * @author Tomomitsu TATEYAMA
 * ======================================================================
 * Change History:
 * 2011-01-09	T.Tateyama 	エラー内容を取得できるように修正
 * 2011-01-12	T.Tateyama	ファイル名の禁則文字を置き換える
 * 2016-11-28	J.Sugawara	 16-018-01 新方式フィルタリング機能対応
 */
public class DownloadExecutor implements Runnable {
	private static Logger logger = Logger.getLogger(DownloadExecutor.class.getName());
	private String requestNo = null;
	private String[] targetFiles = null;
	private java.util.List completeList = new java.util.ArrayList();
	private int currentIndex = 0;
	private FileAccessor fileAccessor = new FileAccessor();
	private String msgKey = null;
	private String configFilePath = ""; // 設定ファイルパス 16-018-01 新方式フィルタリング機能対応
	//-----------------
	protected static final String MSG_IOERR_NET = "msg.io.err.net";
	protected static final String MSG_IOERR_OTHER = "msg.io.err.other";
	protected static final String MSG_USER_CANCEL = "msg.user.cancel";
	protected static final String MSG_INTERR_ECMD = "msg.int.err.ecmd"; // 16-018-01 新方式フィルタリング機能対応
	// ファイル名禁則文字
	protected static final String IGNORE_CHARS = "[\\\\/:\\*\\?\"<>\\|,]";
	private static final String REPLACE_CHAR = "_";
	
	/**
	 * 設定モデル、要求番号、対象ファイル名をもらって構築する。
	 * @param c		装置ログの設定ファイル
	 * @param rno	要求番号　クライアントから指定されるユニークな番号
	 * @param files	対象のファイルを配列で指定
	 * @param configPath	設定ファイルパス 16-018-01 新方式フィルタリング機能対応
	 */
	public DownloadExecutor(ConfigurationModel c, String rno, String[] files, String configPath) {
		if (files == null) throw new IllegalArgumentException("download file is null.");
		requestNo = rno;
		targetFiles = files;
		fileAccessor.setConfiguration(c);
		configFilePath = configPath; // 16-018-01 新方式フィルタリング機能対応
	}
	/**
	 * 登録された要求番号を返す。
	 */
	public String getRequestNo() {
		return requestNo;
	}
	
	/**
	 * ダウンロードするファイルをFileクラスで返す。
	 *　ダウンロードするファイルは、ダウンロードディレクトリの下の要求書番号の下のディレクトリ名となる。
	 * Parameter:
	 * 	@param	name	ファイル名。ファイル名はパスを含む
	 *
	 * Result:
	 *	@return File	作成されたFileクラス
	 */
	protected File createDownladFile(String name) {
		File parent = new File(fileAccessor.getConfiguration().getDownloadDirectory(), requestNo);
		if (!parent.exists()) parent.mkdir();
		
		String b = "";
		if (fileAccessor.getConfiguration().hasUniqueFileName()) {
			int index = name.lastIndexOf("/");
			if (index == -1) {
				b = name;
			} else {
				b = name.substring(index+1);							
			}
		} else {
			b = name; // .replaceAll("/", "_"); ここでは置き換えない
		}
		// ------------- 禁則文字をすべて置き換える
		b = b.replaceAll(IGNORE_CHARS, REPLACE_CHAR);
		return new File(parent, b.toString());
	}
	/**
	 * ファイルを削除する。
	 *
	 * Parameter:
	 * 	@param	target	対象のファイル
	 *
	 * Result:
	 *	@return boolean	true	正常終了
	 *					false	異常終了
	 */
	private boolean clearFile(File target) {
		if (target != null && target.exists()) {
			logger.info("erase file :" + target.getName());
			return target.delete();
		}
		return true;
	}
	/**
	 * ファイル取得を実行する。全てファイルを収集するか、途中でエラーが発生するか、処理が中断するまで
	 * 実行する。
	 * 詳細スレッドでの実行も考えているため、rumメソッドとする。
	 */
	public void run() {
		for (;currentIndex < targetFiles.length && !fileAccessor.isStopped(); currentIndex++) {
			String name = targetFiles[currentIndex];
			File outFile = createDownladFile(name);
			try {
				if (fileAccessor.getConfiguration().getFilter() != null) {
					fileAccessor.writeFileByFilter(name, outFile);
				} else if (fileAccessor.getConfiguration().getExecCommand() != null) { // 16-018-01 新方式フィルタリング機能対応
					fileAccessor.writeFileByCom(name, outFile, configFilePath); // ファイル取得後、外部コマンドを実行
				} else {
					fileAccessor.writeFile(name, outFile);
				}
			} catch (RunningStopException ex) {
				logger.log(Level.SEVERE, "cancel file collecting", ex);
				clearFile(outFile);	// 途中のファイルは削除する
				msgKey = MSG_USER_CANCEL;
				return ;
			} catch (IOException ex) {
				logger.log(Level.SEVERE, "happen io exception. stop collecting.", ex);
				clearFile(outFile);	// 途中のファイルは削除する
				if (ex instanceof java.net.SocketException || ex instanceof FTPException) {
					msgKey = MSG_IOERR_NET;
				} else {
					msgKey = MSG_IOERR_OTHER;					
				}
				return ;
			} catch (InterruptedException ex) { // 16-018-01  新方式フィルタリング機能対応
				logger.log(Level.SEVERE, "happen InterruptedException. stop collecting.", ex);
				clearFile(outFile);	// 途中のファイルは削除する
				msgKey = MSG_INTERR_ECMD;
				return ;
			}
			logger.info("complete to download : " + name);
			// 
			StringBuffer b = new StringBuffer();
			b.append(targetFiles[currentIndex]).append(",")
			.append(fileAccessor.getCurrentDownloadSize()).append(",")
			.append("Complete");
			completeList.add(b);
		}
		if (fileAccessor.isStopped())
			logger.info("stopped file collecting");
		else
			logger.info("finished file collecting");
		//---------------
		if (currentIndex < targetFiles.length) {
			msgKey = MSG_USER_CANCEL;
		}
	}
	/**
	 * エラーメッセージのキーのを返す。
	 *
	 * Result:
	 *	@return String	エラー概要を示す文字列
	 */
	public String getErrorMsgKey() {
		return msgKey;
	}
	/**
	 * 処理をキャンセルする
	 */
	public void cancel() {
		fileAccessor.stop();	// 終了を通知する
	}
	/**
	 * ファイルダウンロードのステータス詳細をDataOutputStreamに書き出す
	 * Parameter:
	 * 	@param	stream	書き込むDataOutputStream
	 */
	public void writeStatus(java.io.DataOutputStream stream) throws IOException {
		// ダウンロードした詳細は次の通り。
		for (java.util.Iterator iter=completeList.iterator();iter.hasNext();) {
			Object target = iter.next();
			stream.writeBytes(target.toString() + "\n");
		}
		if (currentIndex >= targetFiles.length) return; //　全て終わった場合
		// 現在はこれ
		StringBuffer b = new StringBuffer();
		b.append(targetFiles[currentIndex]).append(",")
		.append(fileAccessor.getCurrentDownloadSize()).append(",")
		.append("Executeing");
		stream.writeBytes(b.toString() + "\n");
		// のこりはそれ		
		for (int i=currentIndex + 1; i < targetFiles.length; i++) {
			b = new StringBuffer();
			b.append(targetFiles[i]).append(",")
			.append(0).append(",")
			.append("Queueing");
			stream.writeBytes(b.toString() + "\n");			
		}
	}
	/**
	 * 現在のダウンロードステータスを出力する。
	 */
	public String toString() {
		return new StringBuffer()
		.append(targetFiles.length).append(",")
		.append(completeList.size()).append(",")
		.append(0).append(",")
		.append(fileAccessor.getCurrentDownloadSize()).toString();
	}	
}
