/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : PageInfoBean.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2012/02/09
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.fs.portal.bean;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *
 * @author Tomomitsu TATEYAMA
 */
public class PageInfoBean implements Serializable {
	private static Logger logger = Logger.getLogger(PageInfoBean.class.getName());
	protected static final String IGNORE_CHARS = "[\\\\:\\*\\?\"<>\\|,]";

	/** シリアライズバージョン */
	private static final long serialVersionUID = 8807213758035968596L;
	/** */
	private int fileSortMode = -1;	// ソートのモード　1:昇順、-1:降順	2015-04-14 デフォルトを降順（-1）に変更
	/** */
	private int fileSortType = 1;	// ソートの種類	　0:ファイル名、1:日付、2:サイズ
	/** */
	private DateTimeBeanEx fromDate = new DateTimeBeanEx();
	/** */
	private DateTimeBeanEx toDate = new DateTimeBeanEx();
	/** */
	private String keyword = null;
	/** */
	private String errorMessage = null;
	/** */
	private FileListInfoEx fileList = new FileListInfoEx();
	/** */
	private FileListInfoEx dirList = new FileListInfoEx();
	/** */
	private LogInfoBean logInfo = null;
	/** */
	private Map selectionMap = new java.util.HashMap();
	/** */
	private List dirTree = new java.util.ArrayList();

    /**
     * <p>Title:</p> File upload Service
     * <p>Description:</p>
     *	DateTimeBeanを拡張してCalendarを返す様にする
     *
     * @author Tomomitsu TATEYAMA
     */
    public class DateTimeBeanEx extends DateTimeBean {
    	/** シリアルバージョンＩＤ */
		private static final long serialVersionUID = 1L;
		/** */
    	private String iHour = null;
    	/** */
    	private String iMinute = null;
    	/** */
    	private String iSecond = null;

    	/**
    	 * 内部に保持している日付情報をCalendar型で返す。
    	 * Result:
    	 *	@return java.util.Calendar	変換結果
    	 */
    	public Calendar getCalendar() {
    		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
    		try {
    			java.util.Date d = fmt.parse(getDateTime());
    			Calendar c = Calendar.getInstance();
    			c.setTime(d);
    			return c;
    		} catch (ParseException ex) {
    			logger.log(Level.WARNING, "datetime paser error", ex);
    		}
    		return null;
    	}
		/**
		 * setHourの実装
		 * @see jp.co.canon.cks.ees.upload.web.bean.DateTimeBean#setHour(String)
		 * @param hour	時間
		 */
		public void setHour(String hour) {
			if (hour == null) return;
			iHour = hour;
			if (hour.matches("[0-9]{1,2}")) {
				int v = Integer.parseInt(hour);
				if (0 <= v && v < 24) {
					iHour = null;
					super.setHour(hour);
				}
			}
		}
		/**
		 * setMinuteの実装
		 * @see jp.co.canon.cks.ees.upload.web.bean.DateTimeBean#setMinute(String)
		 * @param	minute	分
		 */
		public void setMinute(String minute) {
			if (minute == null) return;
			iMinute = minute;
			if (minute.matches("[0-9]{1,2}")) {
				int v = Integer.parseInt(minute);
				if (0 <= v && v < 60) {
					iMinute = null;
					super.setMinute(minute);
				}
			}
		}
		/**
		 * setSecondの実装
		 * @see jp.co.canon.cks.ees.upload.web.bean.DateTimeBean#setSecond(String)
		 * @param sec	秒
		 */
		public void setSecond(String sec) {
			if (sec == null) return;
			iSecond = sec;
			if (sec.matches("[0-9]{1,2}")) {
				int v = Integer.parseInt(sec);
				if (0 <= v && v < 60) {
					iSecond = null;
					super.setSecond(sec);
				}
			}
		}
		/**
		 * getHourの実装
		 * @see jp.co.canon.cks.ees.upload.web.bean.DateTimeBean#getHour()
		 * @return 	時間を返す
		 */
		public String getHour() {
			if (iHour != null) return iHour;
			return super.getHour();
		}
		/**
		 * getMinuteの実装
		 * @see jp.co.canon.cks.ees.upload.web.bean.DateTimeBean#getMinute()
		 * @return 分を返す。
		 */
		public String getMinute() {
			if (iMinute != null) return iMinute;
			return super.getMinute();
		}
		/**
		 * getSecondの実装
		 * @see jp.co.canon.cks.ees.upload.web.bean.DateTimeBean#getSecond()
		 * @return 秒を返す
		 */
		public String getSecond() {
			if (iSecond != null) return iSecond;
			return super.getSecond();
		}
		/**
		 * 有効かどうかを返す
		 * Result:
		 *	@return boolean	true:有効	false:無効
		 */
    	public boolean valid() {
    		return (iHour == null && iMinute == null && iSecond == null && date_ == null);
    	}
    }

    /**
	 * デフォルトコンストラクタ
	 */
	public PageInfoBean(LogInfoBean info, int maxLine) {
		this.logInfo = info;
        fromDate.setHour(String.valueOf(0));
        fromDate.setMinute(String.valueOf(0));
        fromDate.setSecond(String.valueOf(0));
        fromDate.setMsec(String.valueOf(0));
        // ===
        toDate.setHour(String.valueOf(23));
        toDate.setMinute(String.valueOf(59));
        toDate.setSecond(String.valueOf(59));
        toDate.setMsec(String.valueOf(999));
        //---
        getFileList().setLineOfPage(maxLine);
        getDirList().setLineOfPage(maxLine);
	}
	/**
	 * 検索条件の開始日付を返す。
	 * Result:
	 *	@return DateTimeBean 開始日付を返す
	 */
	public DateTimeBeanEx getFromDate() {
		return fromDate;
	}
	/**
	 * 検索条件の終了日付を返す。
	 * Result:
	 *	@return DateTimeBean　終了日付を返す。
	 */
	public DateTimeBeanEx getToDate() {
		return toDate;
	}
	/**
	 *　設定されているキーワードを返す。
	 * Return:
	 * 	@return keyword	検索条件のキーワード
	 */
	public String getKeyword() {
		if (keyword == null) return "";
		return keyword;
	}
	/**
	 * キーワードを設定する。
	 * Parameters:
	 * 	@param keyword キーワード
	 */
	public void setKeyword(String keyword) {
		if (keyword != null) {
			keyword = keyword.replaceAll(IGNORE_CHARS, "");
		}
		this.keyword = keyword;
	}
	/**
	 *　設定されているソートモードを返す。
	 * Return:
	 * 	@return int -1:昇順　1：降順
	 */
	public int getFileSortMode() {
		return fileSortMode;
	}
	/**
	 * ソートのモードを設定する。
	 * Parameters:
	 * 	@param fileSortMode 	ソートのモード（昇順、降順）を設定
	 */
	public void setFileSortMode(int fileSortMode) {
		this.fileSortMode = fileSortMode;
	}
	/**
	 *　設定されているソート種別を返す。
	 * Return:
	 * 	@return int ソート種別を返す。0:File 1:Date 2:Size
	 */
	public int getFileSortType() {
		return fileSortType;
	}
	/**
	 * ソート種別（名前、日付、サイズ）を設定する。
	 * Parameters:
	 * 	@param fileSortType ソート種別
	 */
	public void setFileSortType(int fileSortType) {
		this.fileSortType = fileSortType;
	}
	/**
	 *　設定されているエラーメッセージを返す。
	 * Return:
	 * 	@return errorMessage	表示するエラーメッセージを返す
	 */
	public String getErrorMessage() {
		if (errorMessage == null) return "";
		return errorMessage;
	}
	/**
	 * エラーメッセージを設定する。
	 * Parameters:
	 * 	@param errorMessage セットするエラーメッセージ
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	/**
	 *　ファイル一覧のリストを返す。
	 * Result:
	 *	@return FileListInfoEx	ファイル一覧を返す。
	 */
	public FileListInfoEx getFileList() {
		return fileList;
	}
	/**
	 * ディレクトリ一覧のリストを返す。
	 * Result:
	 *	@return FileListInfoEx	ディレクトリ一覧を返す。
	 */
	public FileListInfoEx getDirList() {
		return dirList;
	}
	/**
	 *　設定されているLogInfoBeanを返す。
	 * Return:
	 * 	@return logInfo ログ情報を返す
	 */
	public LogInfoBean getLogInfo() {
		return logInfo;
	}
	/**
	 * ディレクトリTreeを返す
	 * Result:
	 *	@return List	ディレクトリの階層情報を保持したList
	 */
	public List getDirTree() {
		return dirTree;
	}
	/**
	 * 選択されたインデックスを保持するマップを返す
	 * Result:
	 *	@return Map	選択されたインデックスを保持するマップ
	 */
	public Map getSelectionMap() {
		return selectionMap;
	}
	/**
	 * 文字列表現を返す。
	 * @return	このオブジェクトを示す文字列
	 */
	public String toString() {
		return new StringBuffer()
		.append("GET  FileList Search ")
        .append("from: " + getFromDate().getDateTime())
        .append(" -> ")
        .append("to: " + getToDate().getDateTime()).toString();
	}

}
