/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : CleanDataConfigurationModel.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/06
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model;

import java.io.File;
/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	ダウンロードファイルを削除する設定内容を保持する。
 *
 * @author Tomomitsu TATEYAMA
 * ============================================================================
 * 2012-11-01	T.TATEYAMA	種別を追加
 */
public interface FilePurgeConfigurationModel {
	/** ダウンロードに対する定義 */
	public static final int DOWNLOAD_TYPE = 0;
	/** キャッシュに対する定義 */
	public static final int CACHE_TYPE = 1;
	
	/**
	 * 有効期限を分単位で返す。有効期限の計算は、ファイルの作成時間＋有効期限の
	 * 時間が現在時刻よりも過去の場合とする
	 *
	 * Result:
	 *	@return int	有効期限の時間を分で返す
	 */
	public int getExpireMinute();
	/**
	 * 有効期限を満たないファイルを削除するための、指定された利用ディレクトリサイズの閾値を返す。
	 * この率よりも上の場合は、有効期限を満たないファイルで、かつ古く、ユーザがダウンロードしたファイル
	 * を削除する
	 *
	 * Result:
	 *	@return double	比率（0.9 = 90%)を返す。
	 */
	public double getLimitRate();
	/**
	 * 利用できるディレクトリサイズを返す。単位はバイトとする
	 *
	 * Result:
	 *	@return long	ディレクトリで利用できるディスクサイズ
	 */
	public long getUsedDiskSize();
	/**
	 * 管理するディレクトリを返す。この下のディレクトリがアップロードされたファイルとなる
	 *
	 * Result:
	 *	@return File	ディレクトリを返す
	 */
	public File getDownloadDirectory();
	
	/**
	 * 削除予備リストの件数を返す。これは、ディスクがgetLimitRate()を超えたファイルが存在する場合に
	 * 対象外のファイルを削除するファイルの件数の制限をおこなうために用意されている。
	 * メモリとの兼ね合いがあるので、できるだけ、少ない数の方がよい
	 *
	 * Result:
	 *	@return int	削除する予備のリストの最大要素数を返す
	 */
	public int getPurgeCandidateCount();
	
	/**
	 * パージ対象の種類を返す。
	 *
	 * Parameters/Result:
	 * @return 種類を示す文字列を返す。
	 */
	public int getType();
}
