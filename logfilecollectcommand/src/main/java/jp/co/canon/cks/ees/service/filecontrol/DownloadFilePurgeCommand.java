/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : DownloadFilePurgeCommand.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2012/11/01
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.filecontrol;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.co.canon.cks.ees.service.model.RequestData;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * ダウンロードファイルの削除コマンド
 * 
 * @author Tomomitsu TATEYAMA
 * ============================================================================
 * 2012-11-14	T.TATEYAMA	appendNextTargetの引数変更
 * 2012-11-15	T.TATEYAMA	空ディレクトリの削除の抑止（10バイト未満のファイル名は削除しない）
 * 2013-02-19	T.TATEYAMA 	infoファイルがなくても削除するように変更
 * 2013-07-02	T.TATEYAMA	定期収集のファイルを削除
 */
public class DownloadFilePurgeCommand extends AbstractFilePurgeCommand {
	private static Logger logger = Logger.getLogger(DownloadFilePurgeCommand.class.getName());
	private static final String ERR_FILE = "error.msg";
	private static final String SUFFIX_INFO = ".info";
	private static final String COLLECT_SYS = "FS_CA";
	
	/* 
	 * markの実装
	 * @see jp.co.canon.cks.ees.service.filecontrol.AbstractFilePurgeCommand#mark()
	 */
	@Override
	public void mark() {
		long baseTime = System.currentTimeMillis() - ((long)getModel().getExpireMinute() * 60 * 1000);
		System.out.println(new java.util.Date(baseTime));
		java.io.File[] files = getModel().getDownloadDirectory().listFiles();
		
		//
		java.util.List<RequestData> infoList = new java.util.ArrayList<RequestData>(); 
		//
		java.util.List<File> nextTarget = new java.util.ArrayList<File>();
		long totalSize = 0, clearSize = 0;
		for (int i=0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				if (files[i].getName().length() < 10) continue; // 2013-02-19 
				java.io.File[] children = files[i].listFiles();
				if (children.length == 0 && files[i].getName().length() >= 10) { // 2012.11.15 add condition.
					removeList.add(files[i]);
				}
				boolean check = true;
				for (int n=0; n < children.length; n++) {
					totalSize += children[n].length();
					// 
					// 
					String fileName = children[n].getName().toLowerCase();
					
					// if (!(fileName.endsWith(SUFFIX_INFO) || fileName.equals(ERR_FILE))) continue;
					if (!check || children[n].isDirectory()) continue; // ディレクトリならスキップ　T.TATEYAMA 2013-02-19
					System.out.println(new java.util.Date(children[n].lastModified()));
					check = false;
					if (children[n].lastModified() < baseTime) {
						clearSize += children[n].length();
						if (!removeList.contains(files[i])) {
							removeList.add(files[i]);
						}
					} else if (!fileName.equals(ERR_FILE)) {
						// １度でもダウロードしているか？
//						if (accessHistory.getDownloadCount(children[n]) > 0 &&
//						    (nextTarget.size() < getModel().getPurgeCandidateCount() || 
//						     ((File)nextTarget.get(nextTarget.size()-1)).lastModified() > children[n].lastModified())) {
//							insertList(nextTarget, 0, nextTarget.size(), children[n]);
//							
//							if (nextTarget.size() > getModel().getPurgeCandidateCount()) {
//								nextTarget.remove(nextTarget.size()-1);
//							}
//						}
					}
				}
				// ----　削除リストに含まれない場合はinfoファイルを読み込む
				if (!removeList.contains(files[i])) {
					File info = new File(files[i], files[i].getName() + SUFFIX_INFO);
					if (info.exists()) {
						try {
							// 読み込んで対象ファイルかどうかをチェックする
							RequestData rdata = RequestData.readObject(info);
							if (rdata.getReqSystem().equals(COLLECT_SYS)) {
								infoList.add(rdata);
							}
						} catch (Exception e) {
							logger.log(Level.WARNING, "read data error", e);
						}
					}
				}
			}
		}
		//
		appendCollectionAgentFiles(infoList.toArray(new RequestData[0]));
		// 
		appendNextTarget(nextTarget, totalSize, clearSize, true); // methodに変更
	}
	/**
	 *	定期収集の使用済みファイルを削除する
	 *
	 * Parameters/Result:
	 * @param infoList	定期収集の要求情報
	 */
	private void appendCollectionAgentFiles(RequestData[] infoList) {
		if (infoList.length < 2) {
			return ;
		}
		// ソートして単位に分解
		Arrays.sort(infoList, new Comparator<RequestData>() {
			public int compare(RequestData o1, RequestData o2) {
				int c = o1.getReqEnvironmentId().compareTo(o2.getReqEnvironmentId());
				if (c == 0) {
					c = o1.getComment().compareTo(o2.getComment());
					if (c == 0) {
						long v = o1.getContentsFile().lastModified() - o2.getContentsFile().lastModified();
						if (v > 0) {
							c = 1;
						} else if (v < 0) {
							c = -1;
						}
					}
				}
				return c;
			}
		});
		// 単位が２つ以上あれば、古い方を削除
		for (int i=1; i < infoList.length; i++) {
			if (infoList[i-1].getReqEnvironmentId().equals(infoList[i].getReqEnvironmentId()) &&
					infoList[i-1].getComment().equals(infoList[i].getComment())) {
				File target =  infoList[i-1].getContentsFile().getParentFile();
				if (!removeList.contains(target)) {
					removeList.add(target);
				}
			}
		}
	}
}
