package jp.co.canon.cks.ees.service.lastpoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.regex.Pattern;

/**
 * 
 * ラストポイント情報にアクセスするためのクラスです。
 * 
 * @author Mitsuhiro Masuda
 *
 * @param <T> ラストポイントの対象となるファイル
 */
public class LastPointAccessor<T extends ILogFile> {
	
	private final static String DELIMITER = ",";
	private final static Pattern DELIMITER_PATTERN = Pattern.compile(DELIMITER);
	
	private File lastPointFile;
	private File lockFile;
	
	private BufferedReader reader = null;
	private FileChannel reader_channel;
	private Class<T> logClass;
	
	/**
	 * 
	 * LastPointAccessorのコントラクタです。
	 * 
	 * @param manager ログファイルを管理するDateFolderManager
	 * @param logClass ログファイルのクラス
	 * @param lastPointFile ラストポイントを格納するファイル
	 */
	public LastPointAccessor(Class<T> logClass, File lastPointFile) throws IOException{
		this.lastPointFile = lastPointFile;
		this.lockFile = new File(lastPointFile.getAbsolutePath() + ".lck");
		
		this.logClass = logClass;
	}
	
	/**
	 * 
	 * ラストポイントファイルを読み込みます
	 * 
	 * @return 読み込まれたLastPoint
	 * @throws Exception　読み込み中にエラーが発生した
	 */
	public FilePosition<T> read() throws Exception {
		// reader_channelの準備
		if(reader == null){
			FileInputStream fis;
			try {
				fis = new FileInputStream(lastPointFile);
			} catch (FileNotFoundException e) {
				return null;
			}
			reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			reader_channel = fis.getChannel();			
		}
		
		// ファイルのロックを取得し読み込み
		FileOutputStream fos = null;
		String line;
		try{
			fos = new FileOutputStream(lockFile);
			FileLock lock = null;
			try{
				lock = fos.getChannel().lock();
				reader_channel.position(0);
				line = reader.readLine();
			} finally {
				// ファイルのロックを解放
				if(lock != null){
					lock.release();
				}
			}
		} finally{
			if(fos != null){
				fos.close();
			}
		}
		
		String[] elements;
		if(line != null){
			elements = DELIMITER_PATTERN.split(line, -1);
		} else {
			return null;
		}
		
		//ILogFileの生成
		T log = logClass.newInstance();
		log.setFName(elements[1]);
		
		// オブジェクトの生成
		FilePosition<T> lp = new FilePosition<T>();
		lp.setLog(log);
		lp.setLineNo(Long.parseLong(elements[2]));
		
		return lp;
	}
	
	/**
	 * 
	 * ストリームを閉じます。
	 * 
	 * @throws IOException IOエラーが発生した
	 */
	public void close() throws IOException{
		if(reader != null){
			reader.close();
			reader = null;
			reader_channel.close();
			reader_channel = null;
		}
	}
	
}
