/**
 * 
 * File : ConfigurationModel.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/11/30
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */
package jp.co.canon.cks.ees.service.model;

import java.io.File;
import java.net.*;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	このインタフェースは、設定情報に対してアクセスする内容を定義したインタフェースである。
 *　　コマンドの静的（XMLで定義した内容）な設定情報は、ここから取得可能である。
 *
 * @author Tomomitsu TATEYAMA
 * ============================================================================
 * Change History:
 * 2012-02-12	T.Tateyam	期間チェックを行うかどうかを返す
 * 2012-10-30	T.TATEYAMA	ftpの転送モードを追加する。
 * 2016-11-28	J.Sugawara	外部コマンドを追加する  16-018-01 新方式フィルタリング機能対応
 */
public interface ConfigurationModel {

	/** ftp　activeモード  FTPの同一モードに合わせる */
	public static final int FTP_ACTIVE = 1;
	/** ftp　passiveモード FTPの同一モードに合わせる*/
	public static final int FTP_PASSIVE = 2;
	
	/**
	 *	データの名称を返す。この名称はシステム上ユニークに提供される。
	 *	この名称等を使ってファイルリスト等のテンポラリファイルの管理を行うので
	 *	名称には注意する必要がある。
	 *
	 * Parameter:
	 * Result:
	 *	String	・・・　データ名称を示す文字列を返す。文字列名には、ファイルシステムで使われない文字列は指定してはいけない。
	 */
	public String getDataName();
	
	/**
	 *	ファイルが存在するロケーションを返す。返された値はトップディレクトリを示す。
	 *
	 * Parameter:
	 * Result:
	 *	@return URL		ファイルのトップディレクトリを示すURLを返す。
	 */
	public URL getFileLocation();
	/**
	 *　ファイル名がタイムスタンプとして利用されるかどうかを返す。
	 *　もし、ファイル名から取得する場合は、getTimestampParts(), getTimestampFormat()により詳細な取得が可能となる。
	 *
	 * Parameter:
	 * Result:
	 *	@return boolean	true	：　ファイル名を解析して、時間取得する。
	 *					false	：　ファイルの更新時間をそのまま利用する。
	 */
	public boolean isUsedTimestampFromFileName();
	/**
	 * ログインが必要な場合の認証情報をUserInfoクラスで返す。
	 *
	 * Result:
	 *	@return UserInfo	ユーザ情報をUserInfoクラスで返す。
	 * @see jp.co.canon.cks.ees.service.model.UserInfo
	 */
	public UserInfo getAuthorize();
	/**
	 * 再接続する回数を返す。これはネットワークに接続されたファイルにアクセスする場合に有効となる。
	 * 
	 * Result:
	 *	@return int		0	無制限に再接続
	 *					1～	再接続回数
	 */
	public int getRetryCount();
	/**
	 * 接続をリトライする際の待ち時間をミリ秒で返す。これはネットワークに接続されたファイルにアクセスする場合に有効となる。
	 *
	 * Result:
	 *	@return long	再接続するための待ち時間をミリ秒単位で返す。
	 */
	public long getRetryInterval();
	/**
	 * ファイル名をチェックする際のルールを返す。基本的にファイル名だけでなく、フォルダのルールも行う。
	 * 登録時は、フォルダの階層数は必ず定義しないと正しく取得できない。
	 *
	 * Result:
	 *	@return FileNameCheckRuleModel[]	トップディレクトリ以降、チェックするディレクトリ階層毎のルールを配列で返す。
	 * @see jp.co.canon.cks.ees.service.model.FileNameCheckRuleModel
	 */
	public FileNameCheckRuleModel[] getFileNameRule();
	/**
	 * ファイル名から時間を取得する際の場所を示す情報を返す。場所は、複数のディレクトリで指定されている場合があるので
	 * 複数の場所が指定できるように、ディレクトリ単位で文字列を抽出できるように配列で定義している。
	 *
	 * Result:
	 *	@return PartExtractionModel[]	時間を取得するための文字列の抽出条件を返す。配列の１アイテムがディレクトリ１つと一致する。
	 *　@see jp.co.canon.cks.ees.server.model.PartExtractionModel
	 */
	public PartExtractionModel[] getTimestampParts();
	/**
	 * ファイル名から時間を取得する際に用いられる変換フォーマットをjava.text.SimpleDateFormatに指定できる形式で返す。
	 * ここで指定されるフォーマットは、getTimestampParts()を使って抽出された文字列（配列が複数の場合は、結果を結合した文字列）に
	 * 対してフォーマットを定義する。
	 *
	 * Result:
	 *	@return String	java.text.SimpleDateFormartで指定可能な変換書式文字列
	 *　@see java.text.SimpleDateFormat
	 */
	public String getTimestampFormat();
	/**
	 * フィルタ条件を返す。フィルタしない装置ログデータに関してはnullを返す。
	 *
	 * Result:
	 *	@return FilterModel	フィルタ条件を定義したFilterModelを返す。
	 *　@see jp.co.canon.cks.ees.service.model.FilterModel
	 */
	public FilterModel getFilter();
	/**
	 * ダウンロードするディレクトリを返す。
	 *
	 * Result:
	 *	@return File	ダウンロードするディレクトリを返す
	 */
	public File getDownloadDirectory();
	/**
	 * 管理用のポート番号を返す。
	 *
	 * Result:
	 *	@return int	管理用のTCPポート番号を返す。
	 */
	public int getAdminiPort();
	/**
	 * ユニークな名前を持っているかどうかを返す。
	 * Result:
	 *	@return boolean	true	ユニークなファイル名を持つ
	 *					false	ユニークなファイル名を持たない
	 */
	public boolean hasUniqueFileName();
	/**
	 * 処理を待ち状態（CPUの稼働状況を改善）する時間をミリ秒単位で返す。
	 *
	 * Result:
	 *	@return long	待ち時間をミリ秒で設定する
	 */
	public long getWaitTime();
	/**
	 * 処理を待ち状態（CPUの稼働状況を改善）する実施するに当たっての間隔をミリ秒単位で返す。

	 * Result:
	 *	@return long	次の待ちが発生するまでの時間を定義する
	 */
	public long getWaitInterval();
	/**
	 * タイムスタンプチェックを行うかどうかを返す。
	 *
	 * Parameters/Result:
	 *　 @return	true ：期間チェックを行う False ：期間チェックを行わない
	 */
	public boolean isTimestampCheck();
	
	/**
	 * FTPの圧縮転送を有効にするかどうかを返す。
	 * 
	 * @return true: 圧縮転送有効　false: 圧縮転送無効
	 */
	public boolean isModeZ();
	
	/**
	 * 
	 * ログ取得時にキャッシュ機能を使用するかを返す。
	 * 
	 * @return true: キャッシュを使用する、false: キャッシュを使用しない
	 */
	public boolean isUseCache();
	
	/**
	 * 
	 * キャッシュ取得先を解決するための装置名のサフィックスを取得します。
	 * 
	 * @return キャッシュ取得先を解決するための装置名のサフィックス
	 */
	public String getToolIdSuffix();
	
	/**
	 * ログイン時にSITEコマンドをコールする場合のオプション文字列を返す。
	 * SITEコマンドをコールしない場合はnullを返す。
	 * 
	 * @return SITEコマンドのオプション文字列の配列
	 */
	public String[] getSiteOption();
	
	/**
	 * 
	 * 読み込み、書き込み時の文字コードを返す。
	 * 
	 * @return 読み込み、書き込み時の文字コード
	 */
	public String getFilterCharsetName();	
	
	/**
	 * 
	 * 書き込み時の改行文字列を返す。
	 * 
	 * @return 書き込み時の改行文字列
	 */
	public String getFilterLineSeparator();
	
	/**
	 * 
	 * GZIP圧縮してファイル出力するかを返す。
	 * 
	 * @return true:圧縮処理する  false:圧縮処理しない
	 */
	public boolean isGZipOutput();
	
	/**
	 * ftpのデータ接続モードをかえす。 2012-10-30
	 *
	 * Parameters/Result:
	 *　 @return データ接続モードを示す数値を返す。　FTP_ACTIVE/FTP_PASSIVE
	 */
	public int getFtpDataConnectionMode();
	
	/**
	 * 
	 * EESP_VARの環境変数を返す
	 * 
	 * @return EESP_VARの環境変数
	 */
	public String getEESP_VAR();
	
	/**
	 * 
	 * 装置名称を返す
	 * 
	 * @return 装置名称
	 */
	public String getToolId();
	
	/**
	 * 
	 * 外部コマンドを返す 16-018-01 新方式フィルタリング機能対応
	 * 
	 * @return 外部コマンド
	 */
	public String getExecCommand();
}
