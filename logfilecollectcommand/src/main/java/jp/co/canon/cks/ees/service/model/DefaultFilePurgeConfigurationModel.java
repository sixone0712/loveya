/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : DefaultFilePurgeConfigurationModel.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/07
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model;

import java.io.File;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	FilePurgeConfigurationModelのデフォル実装
 *
 * @author Tomomitsu TATEYAMA
 * ============================================================================
 * 2012-11-01	T.TATEYAMA	種別を追加
 */
public class DefaultFilePurgeConfigurationModel implements FilePurgeConfigurationModel {
	private int expireMinute = 26*60;
	private double limitRate = 1.0;
	private long usedDiskSize = 1024;
	private File downloadDirectory = null;
	private int purgeCandidateCount = 300;
	/** 種別を追加 */
	private int type = DOWNLOAD_TYPE;
	/**
	 *　設定されているexpireMinuteを返す。
	 * Return:
	 * 	@return expireMinute
	 */
	public int getExpireMinute() {
		return expireMinute;
	}
	/**
	 * expireMinuteを設定する。
	 * Parameters:
	 * 	@param expireMinute セットする expireMinute
	 */
	public void setExpireMinute(int expireMinute) {
		this.expireMinute = expireMinute;
	}
	/**
	 *　設定されているlimitRateを返す。
	 * Return:
	 * 	@return limitRate
	 */
	public double getLimitRate() {
		return limitRate;
	}
	/**
	 * limitRateを設定する。
	 * Parameters:
	 * 	@param limitRate セットする limitRate
	 */
	public void setLimitRate(double limitRate) {
		this.limitRate = limitRate;
	}
	/**
	 *　設定されているusedDiskSizeを返す。
	 * Return:
	 * 	@return usedDiskSize
	 */
	public long getUsedDiskSize() {
		return usedDiskSize;
	}
	/**
	 * usedDiskSizeを設定する。
	 * Parameters:
	 * 	@param usedDiskSize セットする usedDiskSize
	 */
	public void setUsedDiskSize(long usedDiskSize) {
		this.usedDiskSize = usedDiskSize;
	}
	/**
	 *　設定されているdownloadDirectoryを返す。
	 * Return:
	 * 	@return downloadDirectory
	 */
	public File getDownloadDirectory() {
		return downloadDirectory;
	}
	/**
	 * downloadDirectoryを設定する。
	 * Parameters:
	 * 	@param downloadDirectory セットする downloadDirectory
	 */
	public void setDownloadDirectory(File downloadDirectory) {
		if (downloadDirectory == null) throw new IllegalArgumentException("parameter is null");
		this.downloadDirectory = downloadDirectory;
	}
	/**
	 *　設定されているpurgeCandidateCountを返す。
	 * Return:
	 * 	@return purgeCandidateCount
	 */
	public int getPurgeCandidateCount() {
		return purgeCandidateCount;
	}
	/**
	 * purgeCandidateCountを設定する。
	 * Parameters:
	 * 	@param purgeCandidateCount セットする purgeCandidateCount
	 */
	public void setPurgeCandidateCount(int purgeCandidateCount) {
		this.purgeCandidateCount = purgeCandidateCount;
	}
	
	/**
	 *　設定されている種別を返す。
	 * Return:
	 * @return 種別
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * 種別を設定する。
	 * Parameters:
	 * @param type セットする種別
	 */
	public void setType(int type) {
		this.type = type;
	}
}
