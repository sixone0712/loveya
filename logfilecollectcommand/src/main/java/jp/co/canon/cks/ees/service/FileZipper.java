/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : FileZipper.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/13
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	アップロードされたファイルをZIPで圧縮します。
 *
 * @author Tomomitsu TATEYAMA
 */
public class FileZipper {
	private Logger logger = Logger.getLogger(FileZipper.class.getName());
	private static final String SUFFIX = ".zip";
	private static final String LIST_FILE_NAME = "FileList.LST";
	private String requestNo = null;
	private File uploadFileDirectory = null;
	
	/**
	 *　設定されているrequestNoを返す。
	 * Return:
	 * 	@return requestNo
	 */
	public String getRequestNo() {
		return requestNo;
	}
	/**
	 * requestNoを設定する。
	 * Parameters:
	 * 	@param requestNo セットする requestNo
	 */
	public void setRequestNo(String requestNo) {
		this.requestNo = requestNo;
	}
	/**
	 * アップロードファイルのディレクトリを返す
	 *
	 * Result:
	 *	@return File	アップロードされたファイルのディレクトリ
	 */
	public File getUploadFileDirectory() {
		return uploadFileDirectory;
	}
	/**
	 * アップロードファイルのディレクトリを設定します
	 *
	 * Parameter:
	 * 	@param	newVal	アップロードされたファイルのディレクトリ
	 */
	public void setUploadFileDirectory(File newVal) {
		uploadFileDirectory = newVal;
	}
	/**
	 * ZipFileにファイルを追加する
	 *
	 * Parameter:
	 * 	@param	out	Zipファイルのストリーム
	 *	@param	f	追加するファイル
	 */
    private void appendZipFile(ZipOutputStream out, File f) throws IOException {
        BufferedInputStream in = null;
        try {
	        in = new BufferedInputStream(new FileInputStream(f));
	        byte[] buffer = new byte[1024];
	        while (true) {
	            int size = in.read(buffer);
	            if (size <= 0) break;
	            out.write(buffer, 0, size);
	        }
        } finally {
        	if (in != null) in.close();
        }
    }
    /**
     * 指定されたファイルのZIPファイルを作成する
     *
     * Parameter:
     * 	@param	files	圧縮するファイル
     *	@param	zipFile	出力するファイル
     */
    private void createZipFile(File[] files, File zipFile) throws IOException {
    	if (files.length == 0) return ;
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(zipFile));
			for (int i=0; i < files.length; i++) {
                ZipEntry temp = new ZipEntry(files[i].getName());
                temp.setTime(files[i].lastModified());  
                out.putNextEntry(temp);
                appendZipFile(out, files[i]);
                out.closeEntry();
			}
			out.close();
		} finally {
			if (out != null) {
				out.close();
			}
		}
    }
    /**
     * 圧縮する
     */
	public void zip() throws IOException {
		File zipFile = new File(getUploadFileDirectory(), requestNo + SUFFIX);
		if (!zipFile.exists()) {
			File[] files = getUploadFileDirectory().listFiles();
			createZipFile(files, zipFile);
			clearLogFiles(files);
		}
	}
	/**
	 * 使用済みのログファイルをクリアする
	 *
	 * Parameter:
	 * 	@param	files	対象のファイル
	 */
	private void clearLogFiles(File[] files) {
		// -----------------------
		if (files.length == 0) return; // １つも無い場合は作成しない
		File listFile = new File(getUploadFileDirectory(), LIST_FILE_NAME);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(listFile));
			for (int i=0; i < files.length; i++) {
				StringBuffer b = new StringBuffer().append(files[i].getName())
				.append(",").append(files[i].length());
				writer.write(b.toString());
				writer.newLine();
				files[i].delete();
			}
		} catch (IOException ex) {
			logger.log(Level.WARNING, "happend the error in log file clear", ex);			
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ex) {
					
				}
			}
		}
	}
}
