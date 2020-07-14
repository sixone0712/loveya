/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : AbstarctFilePurgeCommand.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2012/11/01
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.filecontrol;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import jp.co.canon.cks.ees.service.model.FilePurgeConfigurationModel;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * 削除コマンドの抽象化
 * 
 * @author Tomomitsu TATEYAMA
 * ============================================================================
 * 2012-11-14	T.TATEYAMA	予備リストから削除リストへの追加が親ディレクトリだけになっていたため、条件を追加
 */
public abstract class AbstractFilePurgeCommand {
	private static Logger logger = Logger.getLogger(AbstractFilePurgeCommand.class.getName());
	protected java.util.List<File> removeList = new java.util.ArrayList<File>();
	private FilePurgeConfigurationModel model = null;
	protected AccessHistory accessHistory = new AccessHistory();

	/**
	 * デフォルトコンストラクタ
	 */
	public AbstractFilePurgeCommand() {
	}
	/**
	 * ファイル削除設定モデルを返す。
	 *
	 * Result:
	 *	@return CleanDataConfigurationModel	ファイル削除設定モデル
	 */
	public FilePurgeConfigurationModel getModel() {
		return model;
	}
	/**
	 * ファイル削除設定モデルを設定する
	 *
	 * Parameter:
	 * 	@param	model	ファイル削除設定モデル
	 */
	protected void setModel(FilePurgeConfigurationModel newVal) {
		if (newVal == null) throw new IllegalArgumentException("model is null");
		model = newVal;
	}
	/**
	 * ディレクトリごとのファイルを全て削除する
	 *
	 * Parameter:
	 * 	@param	dir	対象のディレクトリ
	 * Return
	 * 	@return	true	正常に削除
	 * 		 	false	異常終了
	 */
	private boolean remveDirectory(File dir) {
		File[] fileList = dir.listFiles();
		for (int i=0; i < fileList.length; i++) {
			if (!fileList[i].delete()) {
				return false;
			}
		}
		return dir.delete();
	}
	/**
	 * 内部でマーキングされた不要ファイルを削除する
	 */
	public void clean() {
		int count = 0;
		for (java.util.Iterator<File> iter=removeList.iterator(); iter.hasNext(); ) {
			java.io.File one = (java.io.File)iter.next();
			if (one.exists()) {
				if (one.isDirectory()) {
					if (remveDirectory(one)) {
						count++;
						logger.info("Remove file : " + one.getName() + " [Access Count:" + accessHistory.getDownloadCount(one) + "]");					
					} else {
						logger.info("Process failed. This dir could not remove.  : " + one.getName());					
					}
				} else if (one.isFile()) { // ファイル指定を削除する
					if (one.delete()) {
						count++;
						logger.info("Remove file : " + one.getName());					
					} else {
						logger.info("Process failed. This file could not remove.  : " + one.getName());					
					}					
				}
			}
		}
		// 新たにログを出力する。
		logger.info("File purge finished. Success count: (" + count + "/" + removeList.size() + ")");
	}
	/**
	 * 設定条件に合わせてダウンロードファイルの削除ファイルを追加する
	 */
	public abstract void mark();
	
	/**
	 * 候補リストの登録ファイルを空き容量が必要なだけ削除リストに追加する
	 *
	 * Parameters/Result:
	 * @param nextTarget　候補リスト
	 * @param totalSize	使用量
	 * @param clearSize　既に削除予定の数量
	 * @param appendDir	親ディレクトリ毎削除リストに加える
	 */
	protected void appendNextTarget(List<File> nextTarget, long totalSize, long clearSize, boolean appendDir) {
		long limitSize = (long)(getModel().getUsedDiskSize() * getModel().getLimitRate()); // 
		if (totalSize - clearSize > limitSize) {
			// もし候補が登録されていてかつ容量をクリアできる数だけ削除対象にマーク
			for (java.util.Iterator<File> iter=nextTarget.iterator(); iter.hasNext();) {
				File one = (File)iter.next();
				clearSize += one.length();
				if (appendDir) {
					if (!removeList.contains(one.getParentFile())) {
						removeList.add(one.getParentFile());
					} 
				} else {
					if (!removeList.contains(one)) {
						removeList.add(one);
					} 					
				}
				if (totalSize - clearSize < limitSize) break;
			}
		}		
	}
	/**
	 * 時間の古い順に追加する。古い順の検索するため、２分法を使って検索位置を再帰的に探す
	 *
	 * Parameter:
	 * 	@param	list	対象を保持しているList
	 *	@param	start	開始インデックス
	 *	@param	end		終了インデックス
	 *	@param	one		対象ファイル
	 */
	protected void insertList(java.util.List<File> list, int start, int end, File one) {
		if (end - start < 10) {
			for (int i=start; i < end; i++) {
				File work = (File)list.get(i);
				if (work.lastModified() > one.lastModified()) {
					list.add(i, one);
					return;
				}
			}
			list.add(end, one);
		} else {
			int halfSize = start + ((end - start) / 2);
			File work = (File)list.get(halfSize);
			if (work.lastModified() > one.lastModified()) {
				insertList(list, start, halfSize, one);
			} else if (work.lastModified() < one.lastModified()) {
				insertList(list, halfSize, end, one);			
			} else {
				list.add(halfSize, one);
			}
		}
	}
}
