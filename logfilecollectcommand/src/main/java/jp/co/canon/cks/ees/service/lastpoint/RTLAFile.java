/**
 * 
 */
package jp.co.canon.cks.ees.service.lastpoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * RTLAファイルを表すクラスです
 * 
 * @author Mitsuhiro Masuda
 *
 */
public class RTLAFile implements ILogFile {
	
	private final static String RTLA_IDLE_REGEX = "^IDLE_(\\d{14}[\\+-]\\d{4})\\.log(?:\\.gz)?$";
	private final static String RTLA_RESET_REGEX = "^RESET_(\\d{14}[\\+-]\\d{4})\\.log(?:\\.gz)?$";
	private final static String RTLA_OTHER_REGEX = "^.*_(\\d{14}[\\+-]\\d{4})\\.log(?:\\.gz)?$";
	private final static String RTLA_DATE_FORMAT = "yyyyMMddHHmmssZ";
	private final static Pattern rtlaIdlePattern = Pattern.compile(RTLA_IDLE_REGEX);
	private final static Pattern rtlaResetPattern = Pattern.compile(RTLA_RESET_REGEX);
	private final static Pattern rtlaOtherPattern = Pattern.compile(RTLA_OTHER_REGEX);
	// リエントラント不能のためstaticとしない
	private final SimpleDateFormat rtlaSimpleDateFormat = new SimpleDateFormat(RTLA_DATE_FORMAT);
	
	private RTLAFileType fNameType = null;

	private Date fNameDate = null;
	private String fName = null;

	/**
	 * 
	 * コントラクタです
	 * 
	 */
	public RTLAFile(){}
	
	/* (non-Javadoc)
	 * @see jp.co.canon.cks.eesp.fcs.log.file.ILogFile#getFName()
	 */
	public String getFName() {
		return fName;
	}

	/* (non-Javadoc)
	 * @see jp.co.canon.cks.eesp.fcs.log.file.ILogFile#getFNameDate()
	 */
	public Date getFNameDate() {
		return fNameDate;
	}

	/* (non-Javadoc)
	 * @see jp.co.canon.cks.eesp.fcs.log.file.ILogFile#setFName(java.lang.String)
	 */
	public void setFName(String fileName) throws Exception {
		fName = fileName;
		fNameType = null;
		fNameDate = null;
		
		Matcher m;
		
		// Idleパターンに一致するか確認
		m = rtlaIdlePattern.matcher(fileName); 
		if(m.matches()){
			fNameType = RTLAFileType.IDLE;
			String dateStr = m.group(1);
			fNameDate = rtlaSimpleDateFormat.parse(dateStr);
			return;
		}
		
		// Resetパターンに一致するか確認
		m = rtlaResetPattern.matcher(fileName); 
		if(m.matches()){
			fNameType = RTLAFileType.RESET;
			String dateStr = m.group(1);
			fNameDate = rtlaSimpleDateFormat.parse(dateStr);
			return;
		}
		
		// Lotパターンに一致するか確認
		m = rtlaOtherPattern.matcher(fileName);
		if(m.matches()){
			fNameType = RTLAFileType.LOT;
			String dateStr = m.group(1);
			fNameDate = rtlaSimpleDateFormat.parse(dateStr);
			return;
		}
		
		throw new Exception("File name is illegal! fileName = " + fileName);
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ILogFile file) {
		int result = this.getFNameDate().compareTo(file.getFNameDate());
		if(result == 0 && file instanceof RTLAFile){
			RTLAFile rtlaFile = (RTLAFile)file;
			// LOTファイルとIDLEファイルで秒オーダーで同時刻となる場合がある。（IDLEが一瞬で終わるため。）
			// この時、IDLEファイルを優先処理する必要がある。
			
			// ロットファイルの強さを1とする。（ロット処理が一番長いと想定）
			int thisValue = 1;
			int targetValue = 1;
			
			if(this.getFNameType() == RTLAFileType.IDLE){
				// IDLEファイルの強さを-1とする。（IDLE処理が一番短いと想定）
				thisValue = -1;
			} else if (this.getFNameType() == RTLAFileType.RESET){
				// RESETファイルの強さを0とする。
				thisValue = 0;
			}
			
			if(rtlaFile.getFNameType() == RTLAFileType.IDLE){
				// IDLEファイルの強さを-1とする。（IDLE処理が一番短いと想定）
				targetValue = -1;
			} else if (rtlaFile.getFNameType() == RTLAFileType.RESET){
				// RESETファイルの強さを0とする。
				targetValue = 0;
			}
			
			result = thisValue - targetValue;
			
		}
		return result;
		
	}

	/**
	 * 
	 * RTLAのファイルタイプを返します。
	 * 
	 * @return RTLAのファイルタイプ
	 */
	public RTLAFileType getFNameType() {
		return fNameType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RTLAFile [fName=" + fName + ", fNameDate=" + fNameDate
				+ ", fNameType=" + fNameType + "]";
	}
	
	
	
}
