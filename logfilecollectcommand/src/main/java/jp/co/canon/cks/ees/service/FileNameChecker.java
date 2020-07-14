/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : Rule.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;

import java.util.Calendar;

import jp.co.canon.cks.ees.service.model.FileNameCheckRuleModel;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	ファイル、ディレクトリ名をチェックするクラス
 *
 * @author Tomomitsu TATEYAMA
 */
public class FileNameChecker {
	private FileNameCheckRuleModel rule = null;
	private long startDate = 0;
	private long endDate = 999999;
	private long folderDate = 0;
	private long currentFolderDate = 0;
	
	/**
	 * デフォルトコンストラクタ
	 */
	public FileNameChecker() {
		this(0);
	}
	/**
	 * フォルダ日付を設定して構築する
	 * @param fdate
	 */
	public FileNameChecker(long fdate) {
		folderDate = fdate;
	}
	/**
	 * チェックした最新のフォルダ日付を返す。日付は年月日の並べた数値とする
	 *
	 * Result:
	 *	@return long	内部で最後にチェックしたフォルダ日付
	 */
	public long getCurrentFolderDate() {
		return currentFolderDate;
	}
	/**
	 * 設定されているruleを返す。
	 * Return:
	 * @return rule
	 */
	public FileNameCheckRuleModel getRule() {
		return rule;
	}
	/**
	 * ruleを設定する。
	 * Parameters:
	 * @param rule セットする rule
	 */
	public void setRule(FileNameCheckRuleModel rule) {
		this.rule = rule;
	}
	/**
	 * ディレクトリの設定が年・月・日の場合のフィルタ条件を設定する
	 *　設定内容は、内部で保持する。開始・終了のどちらかがnullの場合は設定をクリアする
	 * Parameter:
	 * 	@param	s	開始日付をDateクラスで設定
	 * 	@param	e	終了日付をDateクラスで設定
	 */
	public void setFilter(java.util.Date s, java.util.Date e) {
		if (s == null || e == null) {
			startDate = 0;
			endDate = 999999;
		} else {
			Calendar c = Calendar.getInstance();
			c.setTime(s);
			startDate = (c.get(Calendar.YEAR) % 100) * 10000;
			startDate += (c.get(Calendar.MONTH) + 1) * 100;
			startDate += c.get(Calendar.DATE);
			c.setTime(e);
			endDate = (c.get(Calendar.YEAR) % 100) * 10000;
			endDate += (c.get(Calendar.MONTH) + 1) * 100;
			endDate += c.get(Calendar.DATE);
		}
	}
	/**
	 * 桁数を合わせて内部変数の日付をチェックする
	 *
	 * Parameter:
	 * 	@param	p	有効な桁数 ０以上の数値
	 *
	 * Result:
	 *	@return boolean	true	範囲内
	 *					false	範囲外
	 */
	private boolean checkDate(int p) {
		int start = (int)(startDate / p);
		int end = (int)(endDate / p);
		int current = (int)(currentFolderDate / p);
		return start <= current && current <= end;
	}
	/**
	 * ファイル名がルールに一致しているか返す。一致したルールが１つでもあれば
	 *　ルールに一致していると判断する。
	 * Parameter:
	 * 	@param	name	ファイル・ディレクトリ名（ただし、パスは含まない）
	 *
	 * Result:
	 *	@return boolean	true	: ルールに一致している
	 *					false	: ルール以外
	 */
	public boolean matches(String name) {
		for (java.util.Iterator iter=getRule().iteratorRule(); iter.hasNext();) {
			String v = (String)iter.next();
			if (name.matches(v)) {
				switch (getRule().getType()) {
				case FileNameCheckRuleModel.YEAR_TYPE:
					currentFolderDate = (Integer.parseInt(name) % 100) * 10000;
					return checkDate(10000);
				case FileNameCheckRuleModel.MONTH_TYPE:
					if (folderDate > 0) {
						int month1 = Integer.parseInt(name) * 100;
						currentFolderDate = folderDate + month1;
						return checkDate(100);
					}
				case FileNameCheckRuleModel.DAY_TYPE:
					if (folderDate > 0) {
						int day1 = Integer.parseInt(name);
						currentFolderDate = folderDate + day1;
						return startDate <= currentFolderDate && currentFolderDate <= endDate;
					}
				}
				return true;
			}
		}
		return false;
	}
}
