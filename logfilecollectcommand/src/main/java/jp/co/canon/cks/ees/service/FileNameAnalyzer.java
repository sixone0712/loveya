/**
 * 
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : FileNameAnalyzer.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import jp.co.canon.cks.ees.service.model.PartExtractionModel;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	ファイル名を解析して、ファイルの時間を取得するクラス
 *
 * @author Tomomitsu TATEYAMA
 */
public class FileNameAnalyzer {
	//private static Logger logger = Logger.getLogger(FileNameAnalyzer.class.getName());
	private SimpleDateFormat dateFormat = new SimpleDateFormat();
	public static final String FILE_DELIMITER = "/";
	private PartExtractionModel[] timestampParts = new PartExtractionModel[0];
	
	
	/**
	 * 設定されているtimestampPartsを返す。
	 * Return:
	 * @return timestampParts
	 */
	public PartExtractionModel[] getTimestampParts() {
		return timestampParts;
	}
	/**
	 * timestampPartsを設定する。
	 * Parameters:
	 * @param timestampParts セットする timestampParts
	 */
	public void setTimestampParts(PartExtractionModel[] timestampParts) {
		this.timestampParts = timestampParts;
	}
	/**
	 * 更新時間をファイル名を解析して取得する
	 * Parameter:
	 * 	@param	name	ファイル名の文字列（パス付かつパスは/)
	 * Result:
	 *	@return long	ファイル更新時間をlong値でかえす。
	 */
	public long getModifiedTime(String name) throws ParseException {
		String[] parts = name.split(FILE_DELIMITER);
		StringBuffer b = new StringBuffer();
		String delimiter = null;
		for (int i=0; i < getTimestampParts().length; i++ ) {
			PartExtractionModel model = getTimestampParts()[i];
			if (delimiter != null) b.append(delimiter); 
			if (parts.length > model.getTreeIndex()) {
				String v = parts[model.getTreeIndex()];
				if (model.getStartPos() == 0 && model.getEndPos() == -1) {
					b.append(v);
				} else {
					int spos, epos;
					if (model.getDirection() == PartExtractionModel.REVERSE_DIRECTION) {
						spos = v.length() - model.getStartPos();
						epos = v.length() - model.getEndPos();
					} else {
						spos = model.getStartPos();
						epos = model.getEndPos();
					}
					if (model.getEndPos() == -1) {
						b.append(v.substring(spos));
					} else {
						b.append(v.substring(spos, epos));
					}
				}
			} else {
				throw new ParseException("Too much index : " + model.getTreeIndex() + " / " + parts.length, 0);
			}
			delimiter = model.getDelimiter();
		}
		return dateFormat.parse(b.toString()).getTime();
	}
	/**
	 * 任意の書式を指定する
	 * Parameter:
	 * 	@param	newVal	書式文字列（java.text.SimpleDateFormatの書式）
	 *
	 *　@see java.text.SimpleDateFormat
	 */
	public void setDateFormatString(String newVal) {
		dateFormat.applyPattern(newVal);
	}
}
