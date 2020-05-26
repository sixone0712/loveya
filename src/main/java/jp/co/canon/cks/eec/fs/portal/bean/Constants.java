/**
 * Title: Equipment Engineering Support System.
 *         - Log Upload Service
 *
 * File : Constants.java
 *
 * Author: Tomokazu OKUMURA
 * Date: 2010/12/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 * 2011/01/05 (ULS-003,004,005) エラーメッセージ用の定義を追加した。
 * 2011/01/06 リファラ取得用の定義を追加した。
 * 2011/01/07 エラーメッセージを追加した。
 * 2011/01/09 DownloadFileRepository用に定義を追加した。
 * 2011/01/17 ファイルサイズ算出用にスケールと1024の定義を追加した。
 */
package jp.co.canon.cks.eec.fs.portal.bean;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * Webアプリケーションで利用する定数を定義します。
 * @author Tomokazu OKUMURA
 * =======================================================================
 * Change History:
 * 2011-01-08 T.Tateyama	定数の追加と修正、
 * 2016-11-30 J.Sugawara	15-014-01 オンデマンドシュリンク機能対応
 */
public class Constants {

    /** 日付フォーマット文字列 */
    public static final String DATE_PATTERN = "yyyy/MM/dd";
    /** エンコード方式の指定 */
    public static final String CONTENTTYPE = "text/html; charset=UTF-8";
    /** パラメータのエンコード方式 */
    public static final String CHARACTER_ENCODE = "UTF-8";

    /**ResponseHeader key string */
    public static final String HEADER_REFERER = "referer";

    /**DownloadRepository定義*/
	/**  */
    public static final String DOWNLOAD_REPOSITORY_FILELIST = "filelist";
	/** */
    public static final String DOWNLOAD_REPOSITORY_LASTMODIFIED = "lastModified";
	/** */
    public static final String DOWNLOAD_REPOSITORY_ERRORMSG = "errormsg";
	/** */
    public static final String DOWNLOAD_REPOSITORY_ARCHIVEDFILESIZE = "archivedfilesize";
	/**　圧縮ファイルの拡張子 */
    public static final String DOWNLOAD_REPOSITORY_EXTENSION_ZIP = ".zip";

    /**リソースファイル名*/
	/** */
    public static final String RESOURCE_BASENAME = "application";
    /**エラーリソースキー*/
	/** */
    public static final String ERRMSG_SEARCHPERIOD_1 = "error.search_period_1";
	/** */
    public static final String ERRMSG_SEARCHPERIOD_2 = "error.search_period_2";
	/** */
    public static final String ERRMSG_EXECUTE_1 = "error.execute_error_1";
	/** */
    public static final String ERRMSG_EXECUTE_2 = "error.execute_error_2";
	/** */
    public static final String ERRMSG_REQUESTFILEINFO_1 = "error.completed_request_error_1";
	/** */
    public static final String ERRMSG_REQUESTFILEINFODOWNLOAD_1 = "error.deleted_request_error_1";
	/** */
    public static final String ERRMSG_CHECKPURGEFILE_1 = "error.check_to_purge_files_1";
	/** */
    public static final String ERRMSG_SERVER_ERROR = "error.portal_001";
    
    public static final String ERRMSG_REGIST_FILE_NOTFOUND = "error.regist_001";
    /**ファイルサイズ算出用*/
    public static final int FILESIZE_SCALE = 0;
	/** */
    public static final int FILESIZE_DIVIDE = 1024;

	/** 年 */
	public static final int EXPIRES_YEAR = 1970;

	// セッション定義用
	/** ログイン情報取得用 */
	public static final String LOGIN_INFO_PROPERTY = "LoginProperty";

	// 設定ファイル
	/** 構成表示設定ファイル */
	public static final String CONFIG_CONSTRUCT_DISPLAY = "temp/ConstructDisplay.xml";
	/** */
	public static final String LOGIN_PRAM = "loginInfo";
	/** */
	public static final String ERROR_ATTR_NAME = "error";
	
	/** */
	public static final String ERRMSG_DICONNECT_ERROR = "error.disconnect_list";
	/** キャッシュディレクトリ名 15-014-01 */
	public static final String CACHE_DIR = "cache";
	/** シュリンク設定のファイル名 15-014-01 */
	public static final String SHRINK_FILE = "Shrink.dat";
	/** シュリンク完了ファイル名 15-014-01 */
	public static final String SHRINKCOMPLETE_FILE = "ShrinkComplete";

}
