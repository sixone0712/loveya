/**
 *
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : AppConfigModel.java
 *
 * Author: Tomokazu OKUMURA
 * Date: 2011/11/16
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.fs.portal.bean;

import java.io.File;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * 
 * ファイルサービスポータルの変更可能な設定を定義するクラス。
 *
 * @author Tomokazu OKUMURA
 * ============================================================================
 * Change History:
 * 2016-11-30 J.Sugawara	15-014-01 オンデマンドシュリンク機能対応
 */
public interface AppConfigModel {

	/** ファイルサービスポータル設定ファイル */
	String CONFIG_FILE = "configuration.xml";

	/**
	 *	表示行数
	 *
	 * Result:
	 *　 @return 表示行数
	 */
	int getMaxLine();

	/**
	 *	絞り込み期間の最大日数
	 *
	 * Result:
	 *　 @return 絞り込み期間の最大日数
	 */
	int getSearchPeriod();

	/**
	 *	要求登録時の許可ファイルサイズ合計
	 *
	 * Result:
	 *　 @return 要求登録時の許可ファイルサイズ合計
	 */
	long getMaxSize();

	/**
	 *	要求登録時の許可ファイル数
	 *
	 * Result:
	 *　 @return 要求登録時の許可ファイル数
	 */
	int getMaxCount();

	/**
	 * 出力先フォルダを返す。
	 *
	 * Parameters/Result:
	 * @return　出力先フォルダ
	 */
	public File getOutputDir();
	/**
	 * シュリンク設定件数を返す。　15-014-01
	 *
	 * Parameters/Result:
	 * @return　出力先フォルダ
	 */
	public int getMaxShrinkLine();

	/**
	 * シュリンク対象ファイル数を返す。　15-014-01
	 *
	 * Parameters/Result:
	 * @return　シュリンク対象ファイル数
	 */
	public int getMaxShrinkFileCount();
}