package jp.co.canon.cks.ees.service.lastpoint;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * 
 * LastPointAccessorを生成するファクトリクラスです。
 * 
 * @author Mitsuhiro Masuda
 *
 */
public class LastPointAccessorFactory {
	
	private static HashMap<String, LastPointAccessorFactory> instanceMap = new HashMap<String, LastPointAccessorFactory>();

	private String eesp_var;
	private String immutableID;
	
	private LastPointAccessor<RTLAFile> ftpCollectAcc;
	private LastPointAccessor<RTLAFile> l2CreateAcc;
	private LastPointAccessor<RTLAFile> rtlaConvAcc;

	private static final String FTPCOLLECT_RELPATH = "/tmp/fcs/RTLAFC/VFTPLastPoint";
	private static final String L2CREATE_RELPATH = "/tmp/fcs/RTLACONV/L2LastPoint";
	private static final String RTLACONV_RELPATH = "/tmp/fcs/RTLACONV/ConvLastPoint";
	
	/**
	 * 
	 * コントラクタです。
	 * 
	 * @param immutableID 装置名
	 */
	protected LastPointAccessorFactory(String eesp_var, String immutableID){
		this.immutableID = immutableID;
		this.eesp_var = eesp_var;
	}
	
	/**
	 * 
	 * 指定された装置のLastPointAccessorFactoryを取得します。
	 * 
	 * @param immutableID　装置名
	 * @return 指定された装置のLastPointAccessorFactory
	 */
	public static synchronized LastPointAccessorFactory getInstance(String eesp_var, String immutableID){
		if(!instanceMap.containsKey(immutableID)){
			LastPointAccessorFactory instance = new LastPointAccessorFactory(eesp_var, immutableID);
			instanceMap.put(immutableID, instance);
		}
		
		return instanceMap.get(immutableID);
	}
	
	
	/**
	 * 
	 * 指定されたimmutableIDのFTP収集機能用LastPointAccessorを取得します。
	 * 
	 * @return LastPointAccessorのインスタンス
	 */
	public synchronized LastPointAccessor<RTLAFile> getFTPCollectAcc() throws IOException {
		if(ftpCollectAcc == null){
			ftpCollectAcc = getAccessor(new File(eesp_var, FTPCOLLECT_RELPATH));
		}
		
		return ftpCollectAcc;
	}
	
	/**
	 * 
	 * 指定されたimmutableIDのRTLAコンバート機能のL2DB生成用LastPointAccessorを取得します。
	 * 
	 * @return LastPointAccessorのインスタンス
	 */
	public synchronized LastPointAccessor<RTLAFile> getL2CreateAcc() throws IOException {
		if(l2CreateAcc == null){
			l2CreateAcc = getAccessor(new File(eesp_var, L2CREATE_RELPATH));
		}
		
		return l2CreateAcc;
	}
	
	/**
	 * 
	 * 指定されたimmutableIDのRTLAコンバート機能のRTLAコンバート処理用LastPointAccessorを取得します。
	 * 
	 * @return LastPointAccessorのインスタンス
	 */
	public synchronized LastPointAccessor<RTLAFile> getRTLAConvertAcc() throws IOException {
		if(rtlaConvAcc == null){
			rtlaConvAcc = getAccessor(new File(eesp_var, RTLACONV_RELPATH));
		}
		
		return rtlaConvAcc;
	}

	/**
	 * 
	 * 指定されたフォルダーをルートに持つLastPointAccessorを返します。
	 * 
	 * @param folder ルートフォルダ
	 * @return 指定されたフォルダーをルートに持つLastPointAccessor
	 */
	private LastPointAccessor<RTLAFile> getAccessor(File folder) throws IOException {
		if(!folder.exists()){
			boolean result = folder.mkdirs();
			if(!result){
				String msg = "Make last point folder error. folderName: " + folder.getAbsolutePath();
				throw new IOException(msg);														
			}
		}
		
		File file = new File(folder, immutableID + ".txt");
		return new LastPointAccessor<RTLAFile>(RTLAFile.class, file);
	}
	
}
