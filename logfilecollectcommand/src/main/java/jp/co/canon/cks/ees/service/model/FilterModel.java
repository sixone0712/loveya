/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : FilterModel.java
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
 *	フィルタ変換のための情報にアクセスするモデルを定義する。
 *
 * @author Tomomitsu TATEYAMA
 *
 */
public interface FilterModel {
	/**
	 * ログキーを返す。ログキーはログの行先頭文字から完全一致する文字列を示す。
	 * このログキーに一致した場合、そのログは変換対象であると認識される。
	 * 実際の変換は、getChangeRules()を使って変換条件を取得する。
	 * １つのログキーに関して複数の変換ルールを保持する
	 * Result:
	 *	@return String[]	ログの行先頭からの完全一致文字列の配列を返す。
	 */
	public String[] getLogKeys();
	/**
	 * ログキーに対応した文字列を変換するためのルールを返す。
	 * ログキーに対して複数の定義が存在するため、配列にて返す。
	 * Result:
	 *	@return ChangeRule[]	ログを変換するためのルールをChangeRuleの配列で返す。
	 *　@see jp.co.canon.cks.ees.service.ExchangeRuleModel
	 */
	public ExchangeRuleModel[] getChangeRules(String key);
	/**
	 * ログキーの開始位置は固定のためログキーの開始位置を返す。
	 *
	 * Result:
	 *	@return int	ログキーが開始される先頭の位置
	 */
	public int getLogKeyStartPos();
}
