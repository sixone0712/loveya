/**
 * 
 */
package jp.co.canon.cks.ees.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import jp.co.canon.cks.ees.service.lastpoint.LastPointAccessor;
import jp.co.canon.cks.ees.service.lastpoint.LastPointAccessorFactory;
import jp.co.canon.cks.ees.service.lastpoint.RTLAFile;
import jp.co.canon.cks.ees.service.model.ConfigurationModel;

/**
 * 
 * キャッシュ機能を有効にしてFTP取得を行う実装
 * 
 * @author Mitsuhiro Masuda
 *
 */
public class CachedFTPFileManager extends FTPFileManager {

	private static final Logger logger = Logger.getLogger(CachedFTPFileManager.class.getName());
	
	private File cacheFolder;
	
	/**
	 * ファイル収集機能のファイル取得待ちのリトライカウント（10sec * 90 = 15分）
	 */
	private static int MAX_RETRY_CNT = 90;
	
	/**
	 * 
	 * 設定モデルを指定して構築する
	 * 
	 * @param c ConfigurationModelを設定する。
	 */
	public CachedFTPFileManager(ConfigurationModel c) {
		super(c);
		cacheFolder = new File(c.getEESP_VAR(), c.getToolId() + c.getToolIdSuffix());
	}

	/* (non-Javadoc)
	 * @see jp.co.canon.cks.ees.service.FTPFileManager#getFileStream(java.lang.String)
	 */
	@Override
	public InputStream getFileStream(String name) throws IOException {
		// ファイル名称の切り出し
		int index = name.lastIndexOf("/");
		RTLAFile target = new RTLAFile();
		try {
			if (index >= 0) {
				target.setFName(name.substring(index + 1));
			} else{
				target.setFName(name);
			}
		} catch (Exception e){
			// ファイル名称が一致しない場合はnullを返す。
			return null;
		}

		boolean out_log = false; // キャッシュ待ち状態になったことのログ出力を1回に限定するためのフラグ
		int cnt = 0; // リトライカウント
		while(true){
			// LastPointと比較
			LastPointAccessor<RTLAFile> lpa = LastPointAccessorFactory.getInstance(getConfig().getEESP_VAR(), getConfig().getToolId() + getConfig().getToolIdSuffix()).getFTPCollectAcc();
			RTLAFile lastPoint;
			try {
				lastPoint = lpa.read().getLog();
			} catch (Exception e){
				throw new IOException("Last point file read error ! EESP_VAR: " + getConfig().getEESP_VAR() + ", ToolId: " + getConfig().getToolId() + ", ToolIdSuffix: " + getConfig().getToolIdSuffix() + ", Message: " + e.getMessage());
			}
			
			// LastPointと同じか、古い場合
			if(lastPoint.compareTo(target) >= 0){
				// キャッシュが存在するか確認
				// キャッシュが圧縮されていない場合
				File cFile = new File(cacheFolder, target.getFName());
				if(cFile.exists()){
					// キャッシュが存在すればキャッシュより取得
					logger.log(Level.INFO, "Input stream is created from the file. TargetFile: " + cFile);
					return new FileInputStream(cFile);
				} else {
					// キャッシュが圧縮されている場合
					cFile = new File(cFile.getAbsolutePath() + ".gz");
					if(cFile.exists()){
						// キャッシュが存在すればキャッシュより取得
						logger.log(Level.INFO, "Input stream is created from the gzip file. TargetFile: " + cFile);
						return new GZIPInputStream(new FileInputStream(cFile));
					} else {
						// LastPointより過去のファイルである場合は、すでに削除されているため、装置より取得
						logger.log(Level.INFO, "Input stream is created from the FTP connection. ToolID: " + getConfig().getToolId() + ", TargetFile: " + name);
						return super.getFileStream(name);
					}
				}
			} else {
				// FTP収集待機状態であることをログ出力
				if(!out_log){
					logger.log(Level.INFO, "Waiting for FTP collect. ToolID: " + getConfig().getToolId() + ", FileName: " + name);
					out_log = true;
				}
				
				// リトライオーバーしていた場合は抜ける
				if( cnt > MAX_RETRY_CNT ){
					throw new IOException("File collect retry over!! ToolID: " + getConfig().getToolId() + ", FileName: " + name + ", cnt = " + cnt);
				}
				
				// 10secスリープ（1秒ごとに停止確認）
				try {
					for(int i = 0; i < 10; i++){
						if(isStopped()){
							// 停止命令があった場合は抜ける
							break;
						}
						
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					// 割り込みは入らないため、本コードには到達しない
					logger.log(Level.INFO, "Sleep iterrupted of the getFileStream method.", e);
				}
				
				cnt++;
				
				// 停止命令があった場合は抜ける
				if(isStopped()){
					break;
				}
			}
			
			
		}
		
		return null;
	}
}
