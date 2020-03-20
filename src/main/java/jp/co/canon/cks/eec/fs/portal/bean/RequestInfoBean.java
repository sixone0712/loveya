/**
 * Title: Equipment Engineering Support System.
 *		 - Log Upload Service
 *
 * File : RequestInfoBean.java
 *
 * Author: Tomokazu OKUMURA
 * Date: 2010/12/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 * 2011/01/07 error.msg用のプロパティとgetter、setter追加した。
 * 2011/01/07 ファイル更新日付用のプロパティとgetter、setter追加した。
 * 2011/01/17 ZIPファイルサイズ用のプロパティとgetter、setter追加した。
 */

package jp.co.canon.cks.eec.fs.portal.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * 要求データを格納するJavaBeans
 * @author Tomokazu OKUMURA
 * ====================================================================
 * Change History:
 * 2011-01-18	T.Tateyama	ファイルのサイズの単位変換を切り上げに変更
 * 2012-10-31	T.TATEYAMA	アーカイブファイル名を保持するように追加
 */
public class RequestInfoBean implements Serializable, Comparable {

	/** シリアライズバージョン番号 */
	private static final long serialVersionUID = -7889415384020615373L;
	/** status */
	private String status_;
	/** 要求受付番号 */
	private String requestno_;
	/** 収集完了 */
	private int numerator_;
	/** 登録時ファイル数 */
	private int denominator_;
	/** 要求登録日時 */
	private String date_;
	/** 登録時コメント */
	private String description_;
	/** 要求ファイルリスト */
	private List filelist_;
	/** error.msg */
	private String errorMsg_;
	/** ファイル更新日付 */
	private long lastModified_;
	/** アーカイブファイルサイズ */
	private long archiveFileSize_;
	/** 装置名 */
	private String toolName;
	/** 登録時ユーザ */
	private String userName;
	/** アーカイブファイル名　2012.10.31 add */
	private String archiveFileName = null;
	
	/**
	 * 比較結果を返す。
	 * @param info 対象オブジェクト
	 * @return 対象オブジェクトが大きければ１以上、対象オブジェクトが小さければ-1以下の値を返す。
	 */
	public int compareTo(Object info) {
		return this.date_.compareTo(((RequestInfoBean)info).getDate());
	}
	/**
	 * ステータスを返す。
	 *
	 * Parameters/Result:
	 *　 @return	ステータスを示す文字列
	 */
	public String getStatus() {
		return status_;
	}

	/**
	 * ステータスを設定する
	 *
	 * Parameters/Result:
	 *　 @param status	ステータスを示す文字列
	 */
	public void setStatus(String status) {
		this.status_ = status;
	}

	/**
	 * 要求番号を取得します。
	 * @return 要求番号
	 */
	public String getRequestNo() {
		return requestno_;
	}

	/**
	 * 要求番号を設定します。
	 * @param requestno 要求番号
	 */
	public void setRequestNo(String requestno) {
		this.requestno_ = requestno;
	}

	/**
	 * 完了数を取得します。
	 * @return 完了数
	 */
	public int getNumerator() {
		return numerator_;
	}

	/**
	 * 完了数を設定します。
	 * @param numerator 完了数
	 */
	public void setNumerator(int numerator) {
		this.numerator_ = numerator;
	}

	/**
	 * 全体数を取得します。
	 * @return 全体数
	 */
	public int getDenominator() {
		return denominator_;
	}

	/**
	 * 全体数を設定します。
	 * @param denominator 全体数
	 */
	public void setDenominator(int denominator) {
		this.denominator_ = denominator;
	}

	/**
	 * 要求登録日時を取得します。
	 * @return 要求登録日時
	 */
	public String getDate() {
		return date_;
	}

	/**
	 * 要求登録日時を設定します。
	 * @param date 要求登録日時
	 */
	public void setDate(String date) {
		this.date_ = date;
	}

	/**
	 * コメントを取得します。
	 * @return コメント
	 */
	public String getDescription() {
		return description_;
	}

	/**
	 * コメントを設定します。
	 * @param description コメント
	 */
	public void setDescription(String description) {
		this.description_ = description;
	}

	/**
	 * 要求ファイルリストを取得します。
	 * @return 要求ファイルリスト
	 */
	public List getFileList() {
		return filelist_;
	}

	/**
	 * 要求ファイルリストを設定します。
	 * @param filelist 要求ファイルリスト
	 */
	public void setFileList(List filelist) {
		this.filelist_ = filelist;
	}

	/**
	 * 要求ファイルリストの数を取得します。
	 * @return 要求ファイルリスト数
	 */
	public int getFileListCount() {
		if (filelist_ != null) {
			return this.filelist_.size();
		}
		return 0;
	}

	/**
	 * エラーメッセージを取得します。
	 * @return エラーメッセージ
	 */
	public String getErrorMsg() {
		return errorMsg_;
	}

	/**
	 * 要求登録日時を設定します。
	 * @param date 要求登録日時
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg_ = errorMsg;
	}

	/**
	 * ファイル更新日付を取得します。
	 * @return ファイル更新日付
	 */
	public long getLastModifiedTime() {
		return lastModified_;
	}

	/**
	 * ファイル更新日付を設定します。
	 * @param lastModified ファイル更新日付
	 */
	public void setLastModifiedTime(long lastModified) {
		this.lastModified_ = lastModified;
	}

	/**
	 * アーカイブファイルサイズを取得します。
	 * @return アーカイブファイルサイズ
	 */
	public long getArchiveFileSize() {
		return archiveFileSize_;
	}

	/**
	 * アーカイブファイルサイズを設定します。
	 * @param lastModified アーカイブファイルサイズ
	 */
	public void setArchiveFileSize(long size) {
		this.archiveFileSize_ = size;
	}

	/**
	 * アーカイブファイルサイズを取得します。
	 * @return アーカイブファイルサイズ
	 */
	public long getArchiveFileSizeKB() {
		BigDecimal decimal = new BigDecimal(String.valueOf(archiveFileSize_));
		return decimal.divide(new BigDecimal(Constants.FILESIZE_DIVIDE), Constants.FILESIZE_SCALE, BigDecimal.ROUND_UP).longValue();
	}

	/**
	 *　設定されている装置名を返す。
	 * Return:
	 * 	@return tool 装置名
	 */
	public final String getTool() {
		return toolName;
	}

	/**
	 * 装置名を設定する。
	 * Parameters:
	 * 	@param tool セットする装置名
	 */
	public final void setTool(String tool) {
		this.toolName = tool;
	}

	/**
	 *　設定されている登録時ユーザ名を返す。
	 * Return:
	 * 	@return user 登録時ユーザ名
	 */
	public final String getUser() {
		return userName;
	}

	/**
	 * 登録時ユーザ名を設定する。
	 * Parameters:
	 * 	@param user セットする登録時ユーザ名
	 */
	public final void setUser(String user) {
		this.userName = user;
	}
	/**
	 *　設定されているアーカイブファイル名を返す。
	 * Return:
	 * @return アーカイブファイル名
	 */
	public String getArchiveFileName() {
		return archiveFileName;
	}
	
	/**
	 * アーカイブファイル名を設定する。
	 * Parameters:
	 * @param archiveFileName セットする アーカイブファイル名
	 */
	public void setArchiveFileName(String archiveFileName) {
		this.archiveFileName = archiveFileName;
	}
}
