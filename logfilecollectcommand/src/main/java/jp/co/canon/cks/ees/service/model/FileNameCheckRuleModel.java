/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : FileNameCheckModel.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/03
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	ファイル名をチェックする際のルールを定義する
 *
 * @author Tomomitsu TATEYAMA
 */
public interface FileNameCheckRuleModel {
	public static final int YEAR_TYPE = 0;	// 年を示すディレクトリ
	public static final int MONTH_TYPE = 1;	// 月を示すディレクトリ
	public static final int DAY_TYPE = 2;	// 日を示すディレクトリ
	public static final int OTHER_TYPE = 3;		//　そのほか

	/**
	 * ファイルパスの位置情報を返す。
	 * Result:
	 *	@return int	指定のデリミタで区切った際の位置を示すインデックス
	 */
	public int getTreeIndex();

	/**
	 * チェックの種類を示す。年、月、日の場合だけ、フィルタ条件が追加で設定できるため
	 *
	 * Result:
	 *	@return int	YEAR_TYPE, MONTH_TYPE, DAY_TYPE, OTHER_TYPEのどれか
	 */
	public int getType();
	/**
	 * ルールを返す。ルールは全て正規表現とする
	 *　ルールのどれかに一致すれば、OKとする
	 *
	 * Result:
	 *	@return String[]	ルールを記述した文字列　
	 */
	public String[] getRules();
	/**
	 * ルールをIteratorの形で返す。
	 *
	 * Result:
	 *	@return java.util.Iterator	ルールを記述した文字列を保持したIterator
	 */
	public java.util.Iterator iteratorRule();
}
