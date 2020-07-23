

package jp.co.canon.cks.ees.service.filecontrol;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;

import jp.co.canon.cks.ees.service.model.FilePurgeConfigurationModel;
import jp.co.canon.cks.ees.service.model.ConfigurationException;
import jp.co.canon.cks.ees.service.model.ConfigurationReader;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	有効期限が切れたダウンロードファイルを削除処理のメイン関数を定義する
 *	基本的に機能が少ないので、このクラスだけで処理を完結する。
 * @author Tomomitsu TATEYAMA
 * =====================================================
 * Change History:
 * 2011-01-10	T.Tateyama	error.msgをZIPファイルと対等に扱う処理の追加
 * 2011-01-18	T.Tateyama	実行ログを出力するように変更 又、エラーメッセージの修正
 * 2011-12-04	T.Tateyama	zipファイルを出力しないように変更したため、判定方法をinfoファイルに変更
 * 2012-11-01	T.TATEYAMA	削除コマンドを種類ごとに実行できるように修正
 */
public class FilePurgeCommand {
	private static Logger logger = Logger.getLogger(FilePurgeCommand.class.getName());
//	private java.util.List removeList = new java.util.ArrayList();
//	private FilePurgeConfigurationModel model = null;
//	private AccessHistory accessHistory = new AccessHistory();
//	private static final String ERR_FILE = "error.msg";
//	private static final String SUFFIX_INFO = ".info";
//	/**
//	 * デフォルトコンストラクタ
//	 */
//	public FilePurgeCommand() {
//	}
//	/**
//	 * ファイル削除設定モデルを返す。
//	 *
//	 * Result:
//	 *	@return CleanDataConfigurationModel	ファイル削除設定モデル
//	 */
//	public FilePurgeConfigurationModel getModel() {
//		return model;
//	}
//	/**
//	 * ファイル削除設定モデルを設定する
//	 *
//	 * Parameter:
//	 * 	@param	model	ファイル削除設定モデル
//	 */
//	protected void setModel(FilePurgeConfigurationModel newVal) {
//		if (newVal == null) throw new IllegalArgumentException("model is null");
//		model = newVal;
//	}
//	/**
//	 * ディレクトリごとのファイルを全て削除する
//	 *
//	 * Parameter:
//	 * 	@param	dir	対象のディレクトリ
//	 * Return
//	 * 	@return	true	正常に削除
//	 * 		 	false	異常終了
//	 */
//	private boolean remveDirectory(File dir) {
//		File[] fileList = dir.listFiles();
//		for (int i=0; i < fileList.length; i++) {
//			if (!fileList[i].delete()) {
//				return false;
//			}
//		}
//		return dir.delete();
//	}
//	/**
//	 * 内部でマーキングされた不要ファイルを削除する
//	 */
//	public void clean() {
//		int count = 0;
//		for (java.util.Iterator iter=removeList.iterator(); iter.hasNext(); ) {
//			java.io.File one = (java.io.File)iter.next();
//			if (one.exists()) {
//				if (remveDirectory(one)) {
//					count++;
//					logger.info("Remove file : " + one.getName() + " [Access Count:" + accessHistory.getDownloadCount(one) + "]");					
//				} else {
//					logger.info("Process failed. This dir could not remove.  : " + one.getName());					
//				}
//			}
//		}
//		// 新たにログを出力する。
//		logger.info("File purge finished. Success count: (" + count + "/" + removeList.size() + ")");
//	}
//	/**
//	 * 設定条件に合わせてダウンロードファイルの削除ファイルを追加する
//	 */
//	public void mark() {
//		long baseTime = System.currentTimeMillis() - ((long)getModel().getExpireMinute() * 60 * 1000);
//		System.out.println(new java.util.Date(baseTime));
//		java.io.File[] files = getModel().getDownloadDirectory().listFiles();
//		//
//		java.util.List nextTarget = new java.util.ArrayList();
//		long totalSize = 0, clearSize = 0;
//		for (int i=0; i < files.length; i++) {
//			if (files[i].isDirectory()) {
//				java.io.File[] children = files[i].listFiles();
//				if (children.length == 0) removeList.add(files[i]);
//				for (int n=0; n < children.length; n++) {
//					totalSize += children[n].length();
//					// 
//					// INFOとMSGファイルだけを対象とする
//					// 
//					String fileName = children[n].getName().toLowerCase();
//					
//					if (!(fileName.endsWith(SUFFIX_INFO) || fileName.equals(ERR_FILE))) continue;
//					System.out.println(new java.util.Date(children[n].lastModified()));
//					if (children[n].lastModified() < baseTime) {
//						clearSize += children[n].length();
//						if (!removeList.contains(files[i])) {
//							removeList.add(files[i]);
//						}
//					} else if (!fileName.equals(ERR_FILE)) {
//						// １度でもダウロードしているか？
//						if (accessHistory.getDownloadCount(children[n]) > 0 &&
//						    (nextTarget.size() < getModel().getPurgeCandidateCount() || 
//						     ((File)nextTarget.get(nextTarget.size()-1)).lastModified() > children[n].lastModified())) {
//							insertList(nextTarget, 0, nextTarget.size(), children[n]);
//							
//							if (nextTarget.size() > getModel().getPurgeCandidateCount()) {
//								nextTarget.remove(nextTarget.size()-1);
//							}
//						}
//					}
//				}
//			}
//		}
//		long limitSize = (long)(getModel().getUsedDiskSize() * getModel().getLimitRate()); // 
//		if (totalSize - clearSize > limitSize) {
//			// もし候補が登録されていてかつ容量をクリアできる数だけ削除対象にマーク
//			for (java.util.Iterator iter=nextTarget.iterator(); iter.hasNext();) {
//				File one = (File)iter.next();
//				clearSize += one.length();
//				if (!removeList.contains(one.getParentFile())) {
//					removeList.add(one.getParentFile());
//				}
//				if (totalSize - clearSize < limitSize) break;
//			}
//		}
//	}
//	/**
//	 * 時間の古い順に追加する。古い順の検索するため、２分法を使って検索位置を再帰的に探す
//	 *
//	 * Parameter:
//	 * 	@param	list	対象を保持しているList
//	 *	@param	start	開始インデックス
//	 *	@param	end		終了インデックス
//	 *	@param	one		対象ファイル
//	 * Result:
//	 */
//	private void insertList(java.util.List list, int start, int end, File one) {
//		if (end - start < 10) {
//			for (int i=start; i < end; i++) {
//				File work = (File)list.get(i);
//				if (work.lastModified() > one.lastModified()) {
//					list.add(i, one);
//					return;
//				}
//			}
//			list.add(end, one);
//		} else {
//			int halfSize = start + ((end - start) / 2);
//			File work = (File)list.get(halfSize);
//			if (work.lastModified() > one.lastModified()) {
//				insertList(list, start, halfSize, one);
//			} else if (work.lastModified() < one.lastModified()) {
//				insertList(list, halfSize, end, one);			
//			} else {
//				list.add(halfSize, one);
//			}
//		}
//	}
	/**
	 * ファイル削除処理を動かすためのメイン関数
	 *
	 * Parameter:
	 * 	@param	args	引数は設定ファイル名（パス名含む）
	 */
	public static void main(String[] args) {
		// 引数は１つだけ、設定ファイルのパラメータ
		if (args.length == 1) {
			File iniFile = new File(args[0]);
			if (iniFile.exists()) {
				try {
					ConfigurationReader reader = new ConfigurationReader();
					List<FilePurgeConfigurationModel> configList = reader.getFilePurgeConfigurationList(iniFile);
					int count = 0;
					for (FilePurgeConfigurationModel model : configList) {
						count++;
						AbstractFilePurgeCommand manager = null;
						switch (model.getType()) {
						case FilePurgeConfigurationModel.DOWNLOAD_TYPE:
							manager = new DownloadFilePurgeCommand();
							break;
						case FilePurgeConfigurationModel.CACHE_TYPE:
							manager = new CacheFilePurgeCommand();
							break;
						default:
							logger.log(Level.WARNING, "model type unmuch.");
							continue;
						}
						manager.setModel(model);
						manager.mark();
						manager.clean();
						System.out.println("Purge succesful.#" + count+"/"+configList.size());
					}
					System.out.println("Command completed.");
				} catch (ConfigurationException ex) {
					logger.log(Level.SEVERE, "Configuration is wrong.", ex);
					System.out.println("Configuration is wrong.Please see the log file.");
				} catch (Throwable ex) {
					logger.log(Level.SEVERE, "Program error.", ex);
					System.out.println("Program faild.Please see the log file.");
				}
			} else {
				logger.log(Level.SEVERE, "Configuration File is not found. Please check the file.");
				System.out.println("Configuration File is not found. Please check the file.");
			}
		} else {
			logger.log(Level.SEVERE, "Parameter is wrong. Please set the configuration file name.");
			System.out.println("Parameter is wrong. Please set the configuration file name.");
		}
	}
}
