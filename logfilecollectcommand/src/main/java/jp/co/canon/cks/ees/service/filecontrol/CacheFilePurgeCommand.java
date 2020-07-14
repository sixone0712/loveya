/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : CacheFilePurgeCommand.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2012/11/01
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.filecontrol;

import java.io.File;
import java.util.ArrayList;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * キャッシュファイルの削除コマンド
 * 
 * @author Tomomitsu TATEYAMA
 * ============================================================================
 * 2012-11-14	T.TATEYAMA	appendNextTargetの引数変更
 * 2012-11-15	T.TATEYAMA	削除予備個数の０以上の場合に処理するように修正
 * 2016-12-15	J.Sugawara	15-014-01 オンデマンドシュリンク機能対応
 */
public class CacheFilePurgeCommand extends AbstractFilePurgeCommand {

	/* 
	 * markの実装
	 * @see jp.co.canon.cks.ees.service.filecontrol.AbstractFilePurgeCommand#mark()
	 */
	@Override
	public void mark() {
		long baseTime = System.currentTimeMillis() - ((long)getModel().getExpireMinute() * 60 * 1000);
		System.out.println(new java.util.Date(baseTime));
		ArrayList<File> nextTarget = new ArrayList<File>();
		long totalSize = 0; 
		long clearSize = 0;
		File[] list = getModel().getDownloadDirectory().listFiles();
		for (File one : list) {
			if (one.isDirectory()) { // ディレクトリも削除する 15-014-01
				if (one.lastModified() < baseTime) {
					removeList.add(one);
				}

			} else {
				totalSize += one.length();
				if (one.getName().toLowerCase().endsWith(".zip")) {
					if (one.lastModified() < baseTime) {
						clearSize += one.length();
						removeList.add(one);
					} else if (getModel().getPurgeCandidateCount() > 0){ // 2012.11.15 add condition
						// 予備リストに追加
						if (nextTarget.size() < getModel().getPurgeCandidateCount() || 
						     (nextTarget.get(nextTarget.size()-1)).lastModified() > one.lastModified()) {
							insertList(nextTarget, 0, nextTarget.size(), one);
							
							if (nextTarget.size() > getModel().getPurgeCandidateCount()) {
								nextTarget.remove(nextTarget.size()-1);
							}
						}
					}
				}
			}
		}
		appendNextTarget(nextTarget, totalSize, clearSize, false); // methodに変更
	}

}
