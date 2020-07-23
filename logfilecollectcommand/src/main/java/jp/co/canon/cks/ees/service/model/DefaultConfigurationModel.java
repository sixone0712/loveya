/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : DefaultConfigurationModel.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/02
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	ConfigurationModelのデフォルト実装クラス
 *	
 * @author Tomomitsu TATEYAMA
 * ============================================================================
 * Change History :
 * 2012-02-12	T.Tateyama	期間チェックを行うかどうかを返すメソッドを追加
 * 2012-10-30	T.TATEYAMA	FTP接続モードへのメソッドを追加
 * 2016-11-28	J.Sugawara	外部コマンドを追加する  16-018-01 新方式フィルタリング機能対応
 */
public class DefaultConfigurationModel implements ConfigurationModel {
	private String dataName = null;
	private URL	fileLocation = null;
	private boolean usedTimestampFromFileName = false;
	private List<FileNameCheckRuleModel> fileNameRule = new Vector<FileNameCheckRuleModel>();
	private UserInfo authorize = null;
	private long retryInterval = 30000;
	private int retryCount = 5;
	private List<PartExtractionModel> partList = new ArrayList<PartExtractionModel>();
	private String timestampFormat = null;
	private FilterModel filter = null;
	private File downloadDirectory = null;
	private int adminiPort = 58000;
	private boolean hasUniqeFileName = true;
	private long waitTime = 0;
	private long waitInterval = 0;
	private boolean modeZ = false;
	private boolean useCache = false;
	private String toolIdSuffix = null;
	private String[] siteOption = null;
	private String charsetName = null;
	private String lineSeparator = null;
	private boolean gzipOutput = false;
	private String eesp_var;
	private String toolId;
	private String execCommand = null; // 16-018-01  新方式フィルタリング機能対応

	// 
	private int ftpDataConnectionMode = FTP_ACTIVE;	// 2012.10.30 T.TATEYAMA Add

	/**
	 * デフォルトコンストラクタ
	 */
	public DefaultConfigurationModel() {
	}
	/**
	 * データ名を設定する
	 * @param dataName セットする dataName
	 */
	public void setDataName(String dataName) {
		this.dataName = dataName;
	}
	/**
	 * ファイルの位置を示すURLを設定する
	 * @param fileLocation セットする fileLocation
	 */
	public void setFileLocation(URL fileLocation) {
		if (fileLocation == null) {
			throw new IllegalArgumentException("parameter is null");
		}
		this.fileLocation = fileLocation;
	}
	/**
	 * データ名を返す。
	 * @see jp.co.canon.cks.ees.service.ConfigurationModel#getDataName()
	 */
	public String getDataName() {
		return dataName;
	}

	/**
	 * ファイルの位置を示すURLを返す
	 * @see jp.co.canon.cks.ees.service.ConfigurationModel#getFileLocation()
	 */
	public URL getFileLocation() {
		return fileLocation;
	}

	/**
	 * 時間をファイルから取得するかどうかを判定するフラグを返す
	 * @see jp.co.canon.cks.ees.service.ConfigurationModel#isUsedTimestampFromFileName()
	 */
	public boolean isUsedTimestampFromFileName() {
		return usedTimestampFromFileName;
	}
	/**
	 * 時間をファイル名から取得するかどうかを設定する
	 * @param usedTimestampfromFilename セットする usedTimestampfromFilename
	 */
	public void setUsedTimestampfromFileName(boolean usedTimestampfromFilename) {
		this.usedTimestampFromFileName = usedTimestampfromFilename;
	}
	/**
	 * ユーザ情報を返す
	 * @see jp.co.canon.cks.ees.service.ConfigurationModel#getAuthorize()
	 */
	public UserInfo getAuthorize() {
		return authorize;
	}
	/**
	 * ユーザ情報を設定する
	 * Parameter:
	 * 	@param	newVal	ユーザ情報
	 */
	public void setAuthorize(UserInfo newVal) {
		authorize = newVal;
	}

	/**
	 * リトライ間隔を返す
	 * @see jp.co.canon.cks.ees.service.ConfigurationModel#getWaitInterval()
	 */
	public long getRetryInterval() {
		return retryInterval;
	}
	/**
	 * リトライ間隔を設定する
	 * Parameter:
	 * 	@param		newVal	リトライする時間をミリ秒単位で指定
	 */
	public void setRetryInterval(long newVal) {
		retryInterval = newVal;
	}
	/**
	 * ファイル名チェックルールを返す
	 * @see jp.co.canon.cks.ees.service.ConfigurationModel#getFileNameRule()
	 */
	public FileNameCheckRuleModel[] getFileNameRule() {
		return fileNameRule.toArray(new FileNameCheckRuleModel[0]);
	}
	/**
	 * ファイル名チェックルールを追加する。追加順はディレクトリの階層にあわせること
	 *
	 * Parameter:
	 * 	@param	newVal	追加するFileNameCheckRuleModel
	 */
	public void addFileNameRule(FileNameCheckRuleModel newVal) {
		fileNameRule.add(newVal);
	}
	/**
	 *	設定されているretryCountを返す。
	 * Return:
	 * @return retryCount
	 */
	public int getRetryCount() {
		return retryCount;
	}
	/**
	 * retryCountを設定する。
	 * Parameters:
	 * @param retryCount セットする retryCount
	 */
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	/**
	 * ファイル名から時間の場所を決定する情報を追加する
	 *
	 * Parameter:
	 * 	@param	newVal	時間を特定する場所の情報
	 */
	public void addTimestampPart(PartExtractionModel newVal) {
		partList.add(newVal);
	}
	/**
	 * ファイル名から時間の場所を決定する情報を返す
	 * @see jp.co.canon.cks.ees.service.model.ConfigurationModel#getTimestampParts()
	 */
	public PartExtractionModel[] getTimestampParts() {
		return partList.toArray(new PartExtractionModel[0]);
	}
	/**
	 *　設定されているtimestampFormatを返す。
	 * Return:
	 * 	@return timestampFormat
	 */
	public String getTimestampFormat() {
		return timestampFormat;
	}
	/**
	 * timestampFormatを設定する。
	 * Parameters:
	 * 	@param timestampFormat セットする timestampFormat
	 */
	public void setTimestampFormat(String timestampFormat) {
		this.timestampFormat = timestampFormat;
	}
	/**
	 *　設定されているfilterを返す。
	 * Return:
	 * 	@return filter
	 */
	public FilterModel getFilter() {
		return filter;
	}
	/**
	 * filterを設定する。
	 * Parameters:
	 * 	@param filter セットする filter
	 */
	public void setFilter(FilterModel filter) {
		this.filter = filter;
	}
	/**
	 *　設定されているdownloadDirectoryを返す。
	 * Return:
	 * 	@return downloadDirectory
	 */
	public File getDownloadDirectory() {
		return downloadDirectory;
	}
	/**
	 * downloadDirectoryを設定する。
	 * Parameters:
	 * 	@param downloadDirectory セットする ディレクトリを示すFile
	 */
	public void setDownloadDirectory(File downloadDirectory) {
		if (downloadDirectory == null) {
			throw new IllegalArgumentException("parameter is null");
		}
		this.downloadDirectory = downloadDirectory;
	}
	/**
	 *　設定されている管理ポートを返す。
	 * Return:
	 * 	@return adminiPort
	 */
	public int getAdminiPort() {
		return adminiPort;
	}
	/**
	 * 管理ポートを設定する。
	 * Parameters:
	 * 	@param adminiPort セットする adminiPort
	 */
	public void setAdminiPort(int adminiPort) {
		this.adminiPort = adminiPort;
	}
	/**
	 *　設定されているhasUniqeFileNameを返す。
	 * Return:
	 * 	@return hasUniqeFileName
	 */
	public boolean hasUniqueFileName() {
		return hasUniqeFileName;
	}
	/**
	 * hasUniqeFileNameを設定する。
	 * Parameters:
	 * 	@param hasUniqeFileName セットする hasUniqeFileName
	 */
	public void setUniqueFileName(boolean hasUniqeFileName) {
		this.hasUniqeFileName = hasUniqeFileName;
	}
	/**
	 *　設定されているwaitTimeを返す。
	 * Return:
	 * 	@return waitTime
	 */
	public long getWaitTime() {
		return waitTime;
	}
	/**
	 * waitTimeを設定する。
	 * Parameters:
	 * 	@param waitTime セットする waitTime
	 */
	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}
	/**
	 *　設定されているwaitIntervalを返す。
	 * Return:
	 * 	@return waitInterval
	 */
	public long getWaitInterval() {
		return waitInterval;
	}
	/**
	 * waitIntervalを設定する。
	 * Parameters:
	 * 	@param waitInterval セットする waitInterval
	 */
	public void setWaitInterval(long waitInterval) {
		this.waitInterval = waitInterval;
	}
	/* 
	 * isPeriodCheckの実装
	 * @see jp.co.canon.cks.ees.service.model.ConfigurationModel#isPeriodCheck()
	 */
	public boolean isTimestampCheck() {
		for (Iterator<FileNameCheckRuleModel> iter = fileNameRule.iterator(); iter.hasNext();) {
			FileNameCheckRuleModel one = (FileNameCheckRuleModel)iter.next();
			if (one.getType() != FileNameCheckRuleModel.OTHER_TYPE) {
				return isUsedTimestampFromFileName();
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see jp.co.canon.cks.ees.service.model.ConfigurationModel#isModeZ()
	 */
	public boolean isModeZ() {
		return modeZ;
	}
	
	/**
	 * FTPの圧縮転送を有効にするかどうかを設定する。
	 * 
	 * @param modeZ true: 圧縮転送有効　false: 圧縮転送無効
	 */
	public void setModeZ(boolean modeZ){
		this.modeZ = modeZ;
	}
	
	/* (non-Javadoc)
	 * @see jp.co.canon.cks.ees.service.model.ConfigurationModel#isUseCache()
	 */
	public boolean isUseCache(){
		return useCache;
	}
	
	/**
	 * 
	 * ログ取得時にキャッシュ機能を使用するかを設定する。
	 * 
	 * @param useCache true: キャッシュを使用する、false: キャッシュを使用しない
	 */
	public void setUseCache(boolean useCache){
		this.useCache = useCache;
	}
	
	/* (non-Javadoc)
	 * @see jp.co.canon.cks.ees.service.model.ConfigurationModel#getToolIdSuffix()
	 */
	public String getToolIdSuffix(){
		return toolIdSuffix;
	}
	
	/**
	 * 
	 * キャッシュ取得先を解決するための装置名のサフィックスを設定します
	 * 
	 * @param toolIdSuffix　キャッシュ取得先を解決するための装置名のサフィックス
	 */
	public void setToolIdSuffix(String toolIdSuffix){
		this.toolIdSuffix = toolIdSuffix;
	}
	
	
	/* (non-Javadoc)
	 * @see jp.co.canon.cks.ees.service.model.ConfigurationModel#getSiteOption()
	 */
	public String[] getSiteOption(){
		return siteOption;
	}
	
	/**
	 * ログイン時にSITEコマンドをコールする場合のオプション文字列を設定する。
	 * SITEコマンドをコールしない場合はnullを設定する。
	 * 
	 * @param siteOption SITEコマンドのオプション文字列の配列
	 */
	public void setSiteOption(String[] siteOption){
		this.siteOption = siteOption;
	}
	
	/* (non-Javadoc)
	 * @see jp.co.canon.cks.ees.service.model.ConfigurationModel#getFilterCharsetName()
	 */
	public String getFilterCharsetName(){
		return charsetName;
	}
	
	/**
	 * 読み込み、書き込み時の文字コードを設定する。
	 * 
	 * @param charsetName 読み込み、書き込み時の文字コード
	 */
	public void setFilterCharsetName(String charsetName){
		this.charsetName = charsetName;
	}
	
	/* (non-Javadoc)
	 * @see jp.co.canon.cks.ees.service.model.ConfigurationModel#getFilterLineSeparator()
	 */
	public String getFilterLineSeparator(){
		return lineSeparator;
	}
	
	/**
	 * 
	 * 書き込み時の改行文字列を設定する。
	 * 
	 * @param lineSeparator 書き込み時の改行文字列
	 */
	public void setFilterLineSeparator(String lineSeparator){
		this.lineSeparator = lineSeparator;
	}
	
	/* (non-Javadoc)
	 * @see jp.co.canon.cks.ees.service.model.ConfigurationModel#isGZipOutput()
	 */
	public boolean isGZipOutput() {
		return gzipOutput;
	}

	/**
	 * 
	 * GZIP圧縮してファイル出力するかを設定する。
	 * 
	 * @param gzipOutput true:圧縮処理する  false:圧縮処理しない
	 */
	public void setGZipOutput(boolean gzipOutput){
		this.gzipOutput = gzipOutput;
	}
	
	/**
	 *　設定されているftpデータ接続モードを返す。2012.10.30 T.TATEYAMA ADD
	 * Return:
	 * 	@return ftpDataConnectionMode
	 */
	public int getFtpDataConnectionMode() {
		return ftpDataConnectionMode;
	}
	
	/**
	 * ftpデータ接続モードを設定する。　2012.10.30 T.TATEYAMA ADD
	 * Parameters:
	 * 	@param ftpDataConnectionMode セットする ftpDataConnectionMode
	 */
	public void setFtpDataConnectionMode(int ftpDataConnectionMode) {
		this.ftpDataConnectionMode = ftpDataConnectionMode;
	}
	
	/**
	 * 
	 * EESP_VARの環境変数を設定する
	 * 
	 * @param eesp_var EESP_VARの環境変数
	 */
	public void setEESP_VAR(String eesp_var){
		this.eesp_var = eesp_var;
	}
	
	/* (non-Javadoc)
	 * @see jp.co.canon.cks.ees.service.model.ConfigurationModel#getEespVar()
	 */
	public String getEESP_VAR(){
		return eesp_var;
	}

	/**
	 * 
	 * 装置名称を設定する
	 * 
	 * @param toolId 装置名称
	 */
	public void setToolId(String toolId){
		this.toolId = toolId;
	}
	
	/* (non-Javadoc)
	 * @see jp.co.canon.cks.ees.service.model.ConfigurationModel#getToolId()
	 */
	public String getToolId() {
		return toolId;
	}

	/**
	 *　設定されている外部コマンドを返す。16-018-01 新方式フィルタリング機能対応
	 * Return:
	 * 	@return execCommand
	 */
	public String getExecCommand() {
		return execCommand;
	}
	/**
	 * 外部コマンドを設定する。16-018-01 新方式フィルタリング機能対応
	 * Parameters:
	 * 	@param execCommand セットする外部コマンド
	 */
	public void setExecCommand(String execCommand) {
		this.execCommand = execCommand;
	}

}
