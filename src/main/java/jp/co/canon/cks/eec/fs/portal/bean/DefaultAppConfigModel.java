/**
 *
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : DefaultAppConfigModel.java
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
 * AppConfigModelのデフォルト実装クラス。
 *
 * @author Tomokazu OKUMURA
 * ============================================================================
 * Change History:
 * 2016-11-30 J.Sugawara	15-014-01 オンデマンドシュリンク機能対応
 */
public class DefaultAppConfigModel implements AppConfigModel {

	/** 表示行数 */
	private int maxLine;

	/** 絞り込み期間の最大日数 */
	private int searchPeriod;

	/** 要求登録時の許可ファイルサイズ合計 */
	private long maxSize;

	/** 要求登録時の許可ファイル数 */
	private int maxCount;

	/** 出力先ディレクトリ　*/
	private File outputDir = null;

	/** シュリンク設定件数 15-014-01 */
	private int maxShrinkLine;

	/** シュリンク対象ファイル数 15-014-01 */
	private int maxShrinkFileCount;

	/**
	 * デフォルトコンストラクタ
	 */
	public DefaultAppConfigModel() {
	}

	/**
	 *　設定されている表示行数を返す。
	 * Return:
	 * 	@return 表示行数
	 */
	public final int getMaxLine() {
		return maxLine;
	}

	/**
	 * 表示行数を設定する。
	 * Parameters:
	 * 	@param value セットする表示行数
	 */
	public final void setMaxLine(int value) {
		this.maxLine = value;
	}

	/**
	 *　設定されている絞り込み期間の最大日数を返す。
	 * Return:
	 * 	@return 絞り込み期間の最大日数
	 */
	public final int getSearchPeriod() {
		return searchPeriod;
	}

	/**
	 * 絞り込み期間の最大日数を設定する。
	 * Parameters:
	 * 	@param value セットする絞り込み期間の最大日数
	 */
	public final void setSearchPeriod(int value) {
		this.searchPeriod = value;
	}

	/**
	 *　設定されている要求登録時の許可ファイルサイズ合計を返す。
	 * Return:
	 * 	@return 要求登録時の許可ファイルサイズ合計
	 */
	public final long getMaxSize() {
		return maxSize;
	}

	/**
	 * 要求登録時の許可ファイルサイズ合計を設定する。
	 * Parameters:
	 * 	@param value セットする要求登録時の許可ファイルサイズ合計
	 */
	public final void setMaxSize(long value) {
		this.maxSize = value;
	}

	/**
	 *　設定されている要求登録時の許可ファイル数を返す。
	 * Return:
	 * 	@return 要求登録時の許可ファイル数
	 */
	public final int getMaxCount() {
		return maxCount;
	}

	/**
	 * 要求登録時の許可ファイル数を設定する。
	 * Parameters:
	 * 	@param value セットする要求登録時の許可ファイル数
	 */
	public final void setMaxCount(int value) {
		this.maxCount = value;
	}

	/**
	 *　設定されている出力ディレクトリを返す。
	 * Return:
	 * @return 出力ディレクトリ
	 */
	public File getOutputDir() {
		return outputDir;
	}

	/**
	 * 出力ディレクトリを設定する。
	 * Parameters:
	 * @param outputDir セットする 出力ディレクトリ
	 */
	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}

	/**
	 * 設定されているシュリンク設定件数を返す。 15-014-01
	 * Return:
	 * 	@return シュリンク設定件数
	 */
	public final int getMaxShrinkLine() {
		return maxShrinkLine;
	}

	/**
	 * シュリンク設定件数を設定する。 15-014-01
	 * Parameters:
	 * 	@param value シュリンク設定件数
	 */
	public final void setMaxShrinkLine(int value) {
		this.maxShrinkLine = value;
	}

	/**
	 *　設定されているシュリンク対象ファイル数を返す。 15-014-01
	 * Return:
	 * 	@return シュリンク対象ファイル数
	 */
	public final int getMaxShrinkFileCount() {
		return maxShrinkFileCount;
	}

	/**
	 * シュリンク対象ファイル数を設定する。 15-014-01
	 * Parameters:
	 * 	@param value シュリンク対象ファイル数
	 */
	public final void setMaxShrinkFileCount(int value) {
		this.maxShrinkFileCount = value;
	}

}
