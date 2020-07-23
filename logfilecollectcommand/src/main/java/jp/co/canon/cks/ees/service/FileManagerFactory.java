/**
 * 
 * File : FileReaderCreatetor.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/01
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;


import jp.co.canon.cks.ees.service.model.ConfigurationModel;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	このクラスは、装置毎のURLに対応したAbstractFileManagerクラスを生成するためのクラスです。
 *
 * @author Tomomitsu TATEYAMA
 */
public final class FileManagerFactory {
	
	/**
	 *　FileManagerを構築する。
	 *
	 * Parameter:
	 * 	@param	config	ConfigurationModelを設定する。
	 *
	 * Result:
	 *	@return AbstractFileManager	
	 * @see jp.co.canon.cks.ees.service.AbstractFileManager
	 */
	public static AbstractFileManager createInstance(ConfigurationModel config) {
		if (config == null) throw new IllegalArgumentException("parameter is null");
		String protocol = config.getFileLocation().getProtocol().toLowerCase();
		if (protocol.equalsIgnoreCase(LocalFileManager.SUPPORT_PROTOCOL)) {
			return new LocalFileManager(config);
		} else if (protocol.equalsIgnoreCase(FTPFileManager.SUPPORT_PROTOCOL)) {
			if(config.isUseCache()){
				return new CachedFTPFileManager(config);
			} else {
				return new FTPFileManager(config);
			}
		} else {
			throw new IllegalArgumentException("not support protoclo");
		}
	}
}
