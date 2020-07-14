/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : ExchangeRuleModel.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/04
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	フィルタ条件で一致したデータを変換するルールを定義する。
 *
 * @author Tomomitsu TATEYAMA
 */
public interface ExchangeRuleModel {
	public static final int NORMAL_DIRCTION = 1;
	public static final int REVERSE_DIRECTON = 2;
	
	public static final int ALL_CHANGE_RULE = 1;	// 範囲をすべて１つの文字に置き換える
	public static final int CHAR_REPLACE_RULE1 = 2;	//　１文字毎に指定文字に置き換える
	public static final int CHAR_REPLACE_RULE2 = 3;	// 除外文字以外を置き換える
	
	/**
	 *	設定されているstartKeyを返す。
	 * Return:
	 * @return startKey
	 */
	public String getStartKey();
	/**
	 * Endキーを探す方向を返す。
	 * Result:
	 *	@return int	NORMAL_DIRCTION　：　行末方向
	 *				REVERSE_DIRECTON : 行頭方法
	 */
	public int getDirection();
	/**
	 *	設定されているendKeyを返す。
	 * Return:
	 * @return endKey
	 */
	public String getEndKey();
	/**
	 * 終了キーが正規表現かどうかを返す。
	 * Result:
	 *	@return boolean	true 	: 正規表現
	 *					false	: 通常文字
	 */
	public boolean isRegexEndKey();
	/**
	 * 続けて同一ログに条件が一致するすべての文字列を変換するかどうかを返す。
	 * Result:
	 *	@return boolean	true	: 一致した条件の全てを変換する
	 *					false	: 最初に一致した条件を変換する
	 */
	public boolean isAllReplace();
	/**
	 *	設定されているruleを返す。
	 * Return:
	 * @return rule	ALL_CHANGE_RULE 	: 抽出範囲をすべてgetChangeStr()１語に変換
	 * 				CHAR_REPLACE_RULE1	:　１文字を getChangeStr()１語に変換
	 * 				CHAR_REPLACE_RULE2	:　１文字を getChangeStr()１語に変換(除外文字対応）
	 */
	public int getRule();
	/**
	 *	設定されているchangeStrを返す。
	 * Return:
	 * @return changeStr
	 */
	public String getChangeStr();
	/**
	 * 例外文字を返す。これはgetRule()にてCHAR_REPLACE_RULE2が設定された場合のみ有効
	 * Result:
	 *	@return String	例外文字にすべき文字列　順番等は関係ない。
	 */
	public String getExclusionStr();
}