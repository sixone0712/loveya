/**
 * 
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : AbstractFileManager.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;

import java.io.IOException;
import java.util.TreeMap;

import jp.co.canon.cks.ees.service.command.RunningStopException;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	このクラスはファイルにアクセスするための抽象クラスを定義する。
 *	アクセスの種類は、以下の通り
 *		・開いたファイルのInputStreamを返す
 *		・指定されたディレクトリの一覧を返す
 *		・ファイル取得中の中止（機能によっては意味がない場合がある）
 * @author Tomomitsu TATEYAMA
 * =============================================================================
 * Change History:
 * 2011-01-03	T.Tateyama	ディレクトリ情報を返す様に、getFileInfoAll()メソッドを追加
 * 2011-01-07	T.Tateyama	コメントを追加
 * 2011-01-09	T.Tateyama	パスをクリアするメソッドを追加
 * 2015-04-14	T.TAKIGUCHI	getFileInfosメソッドの引数に、ファイル制限数/キーワード/ファイル情報を格納するツリーマップを追加
 * 2015-04-14	T.TAKIGUCHI	getFileInfoAllメソッドの引数に、ファイル制限数/キーワードを追加
 * 2015-06-22	T.TAKIGUCHI getFileInfosメソッドの引数に開始/終了時刻を追加
 */
public abstract class AbstractFileManager {
	private StringBuffer currentPath = new StringBuffer();
	protected static String PATH_DELIMITER = "/";
	private boolean stop = false;
	

	/**
	 * 現在のディレクトリのファイル一覧をリストで取得する。ファイルの一覧は、ディレクトリの場合は文字で、ファイルの場合はLogFileクラスで返す。
	 *
	 * Parameter:
	 * 	@param	checker	整合性をチェックするクラス
	 * 	@param	filecount	表示するファイルの最大数
	 *  @param	keywords	ファイル一覧表示時のキーワード
	 *  @param	tm			ファイル一覧情報を格納するツリーマップ
	 *  @param	s			ファイル一覧表示時の開始時刻
	 *  @param	e			ファイル一覧表示時の終了時刻
	 * Result:
	 *	@return java.util.List	ファイルの一覧を保持したList
	 */
	abstract public java.util.List getFileInfos(FileNameChecker checker, int filecount, String[] keywords,TreeMap<Long, java.util.ArrayList<LogFile>> tm, java.util.Date s, java.util.Date e ) throws IOException, RunningStopException;
	/**
	 * 指定されたファイルのInputFileStreamを取得する。
	 *
	 * Parameter:
	 * 	@param	name	ファイル名（ただしパス付き）
	 *
	 * Result:
	 *	@return java.io.InputStream	オープンしたInputStream
	 */
	abstract public java.io.InputStream getFileStream(String name) throws IOException;
	
	/**
	 * 内部の中止フラグをセットし、中止条件を設定する。サブクラスで中止判定が必要な
	 * 場合はisStopped()メソッドをチェックする。
	 * 同期して中止できる場合は、このメソッドをオーバーライドする必要がある
	 */
	public void stop() {
		stop = true;
	}
	/**
	 * 中止されたかどうかを返す。
	 *
	 * Result:
	 *	@return boolean		true	中止
	 *						false	続行中
	 */
	protected boolean isStopped() {
		return stop;
	}
	/**
	 * 内部で管理しているパス文字にサブディレクトリを追加する
	 *
	 * Parameter:
	 * 	@param	追加するサブディレクトリ文字
	 */
	public void appendPath(String subdir) {
		if (currentPath.length() == 0)
			currentPath.append(subdir); 
		else
			currentPath.append(PATH_DELIMITER).append(subdir); 
	}
	/**
	 * 最後に追加したディレクトリを削除する。
	 */
	public void removeLastPath() {
		int index = currentPath.lastIndexOf(PATH_DELIMITER);
		if (index > 0) {
			currentPath.delete(index, currentPath.length());
		} else if (currentPath.length() > 0) {
			currentPath.delete(0, currentPath.length());
		}
	}
	/**
	 * 現在のパスを返す。返すバス文字がある場合は、必ずディレクトリの区切り文字を追加する。
	 * Result:
	 *	@return String	
	 */
	protected String getCurrentPath() {
		if (currentPath.length() == 0) return "";
		return currentPath.toString() + PATH_DELIMITER;
	}
	/**
	 *  現在パスをクリアする
	 */
	public void clearPath() {
		currentPath = new StringBuffer();
	}
	/**
	 * このManagerが不要になった際にクローズする。
	 *　実装において、内部のクラスの終了処理が必要な場合オーバーライドする。
	 */
	public void close() {
	}
	/**
	 * ファイルだけではなく、ディレクトリに対しても、名前だけではなく作成時間等の情報を含めて返す。
	 * ファイルとディレクトリはLogItemクラスに変換して返す。
	 * Parameter:
	 * 	@param	checker	ファイル名をチェックするオブジェクト  nullの場合は、チェックせずにすべて返す。
	 *  @param	filecount	表示するファイルの最大数
	 *  @param	keywords	ファイル一覧表示時のキーワード
	 * Result:
	 *	@return java.util.List	ディレクトリおよびファイルの一覧を返す。
	 */
	abstract public LogItem[] getFileInfoAll(FileNameChecker checker, int filecount, String[] keywords) throws IOException, RunningStopException;



}
