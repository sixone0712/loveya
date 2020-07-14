/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : ConfigurationSaxParser.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/03
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model.xml;

import java.net.URL;
import java.util.ArrayList;

import jp.co.canon.cks.ees.service.model.ConfigurationModel;
import jp.co.canon.cks.ees.service.model.DefaultFileNameCheckRuleModel;
import jp.co.canon.cks.ees.service.model.DefaultConfigurationModel;
import jp.co.canon.cks.ees.service.model.DefaultPartExtractionModel;
import jp.co.canon.cks.ees.service.model.UserInfo;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	XMLファイルを読み込んで装置ログ設定を取得する。
 *
 * @author Tomomitsu TATEYAMA
 * ============================================================================
 * 2012-10-30	T.TATEYAMA	ftp接続時の転送モードを追加
 * 2016-11-28	J.Sugawara	外部コマンドを追加する  16-018-01 新方式フィルタリング機能対応
 */
public class ConfigurationSaxParser extends DefaultHandler {
	// Element string
	private static final String E_INFO = "INFO";
	private static final String E_TIMEANALIZE = "TIMEANALIZE";
	private static final String E_PLACE = "PLACE";
	private static final String E_LOCATION = "LOCATION";
	private static final String E_OPTION = "OPTION";
	private static final String E_CHECKRULE = "CHECKRULE";
	private static final String E_PATTERN = "PATTERN";
	// Only Filter 
	private static final String E_FILTER = "FILTER";
	/**----------- ここから
	private static final String E_LOGKEY = "LOGKEY";
	private static final String E_CHANGEKEY = "CHANGEKEY";
	private static final String E_REPLACE = "REPLACE";
		------------------**/
	private static final String E_EXECCOM = "EXECCOM"; // 16-018-01 新方式フィルタリング機能対応
	// all attribute string
	private static final String A_OUTPUT = "output";
	private static final String A_NAME = "name";
	private static final String A_USE_TIMESTAMP = "useTimestampFrom";
	private static final String A_ROOT = "root";
	private static final String A_USER = "user";
	private static final String A_PASSWORD = "password";
	private static final String A_RETRY_INTERVAL = "retryInterval";
	private static final String A_RETRY_COUNT = "retryCount";
	private static final String A_MODEZ = "modeZ";
	private static final String A_USE_CACHE = "useCache";
	private static final String A_TOOL_ID_SUFFIX = "toolIdSuffix";
	private static final String A_SITEOPTION = "siteOption";
	private static final String A_GZIP_OUTPUT = "gzipOutput";
	private static final String A_LEVEL = "level";
	private static final String A_TYPE = "type";
	/*------------------ ここから
	private static final String A_CONDITION = "condition";
	private static final String A_VALUE = "value";
	private static final String A_STARTKEY = "startKey";
	private static final String A_ENDKEY = "endKey";
	private static final String A_RULE = "rule";
	private static final String A_EXCLUSION = "exclusionStr";
	private static final String A_ALLREPLACE = "allReplace";
	private static final String A_REGEXENDKEY = "regexEndKey";
	private static final String A_LOGKEYSTART = "logKeyStartPos";
	ここまで------------------*/
	private static final String A_FORMAT = "format";
	private static final String A_TARGETLEVEL = "targetLevel";
	private static final String A_STARTPOS = "startPos";
	private static final String A_ENDPOS = "endPos";
	private static final String A_DELIMITER = "delimiter";
	private static final String A_DIRECTION = "direction";
	private static final String A_ADMINPORT = "adminPort";
	private static final String A_UNIQUENAME = "uniqueName";
	private static final String A_WAITTME = "waitTime";
	private static final String A_WAITINTERVAL = "waitInterval";
	private static final String A_CONF_PATH = "configurationFilePath";
	private static final String A_CHARSET_NAME = "charsetName";
	private static final String A_LINE_SEPARATOR = "lineSeparator";
	private static final String A_EXECCOMMAND = "execCommand"; // 16-018-01 新方式フィルタリング機能対応
	
	private static final String A_FTPMODE = "ftpmode";	// 2012.10.30 T.TATEYAMA
	private static final String ACTIVE_VAL = "active";
	private static final String PASSIVE_VAL = "passive";
	
	// other define
	private static final char URL_DELIMITER = '/';
	private static final String[] TYPE_LIST = {"year", "month", "day", "other"};
	private static final String TRUE = "true";
	
	// working buffer 
	private StringBuffer buffer = null;
	private DefaultConfigurationModel configuration = null;
	private DefaultFileNameCheckRuleModel currentFileNameChecker = null;
	/** ------ ここから
	private DefaultFilterModel filter = null;
	private String currentLogKey = null;
	private DefaultExchangeRuleModel currentChangeRule = null;
	ここまで-----------**/
	private String filterDefineFile = null;
	
	/**
	 * デフォルトコンストラクタ
	 */
	public ConfigurationSaxParser() {
		configuration = new DefaultConfigurationModel();
	}
	/**
	 * 読み込んだConfugurationModelを返す。
	 * Return:
	 * @return configuration ファイルを読み込んだConfigurationModelを返す。
	 */
	public DefaultConfigurationModel getConfiguration() {
		return configuration;
	}
	/**
	 * フィルタ定義ファイルのパスを返す。
	 *
	 * Result:
	 *	@return String	フィルタ定義のパスを返す。
	 */
	public String getFilterDefinePath() {
		return filterDefineFile;
	}
	
	/* 
	 * charactersの実装
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
		if (buffer != null) {
			buffer.append(arg0, arg1, arg2);
		}
		super.characters(arg0, arg1, arg2);
	}

	/* 
	 * endElementの実装
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String arg1, String qName) throws SAXException {
		if (qName.equalsIgnoreCase(E_INFO)) {
		} else if (qName.equalsIgnoreCase(E_LOCATION)) {
		} else if (qName.equalsIgnoreCase(E_CHECKRULE)) {
		} else if (qName.equalsIgnoreCase(E_PATTERN)) {
			String item = buffer.toString();
			if (currentFileNameChecker != null) currentFileNameChecker.addRule(item);
		} else if (qName.equalsIgnoreCase(E_FILTER)) {
			//if (filter.getLogKeys().length > 0) {
			//	configuration.setFilter(filter);
			//}
		}
		buffer = null;
	}
	
	/**
	 * 配列の中の一致する文字列の順番を返す。
	 * Parameter:
	 * 	@param	target	探す対象文字列
	 * 	@param	list	探される文字列の配列
	 * Result:
	 *	@return int		0～	文字列が一致した配列の順番
	 *					-1	一致した文字列が見つからない
	 */
	private int getIndex(String target, String[] list) {
		if (target == null) return -1;
		for (int i=0;i < list.length; i++) {
			if (target.equalsIgnoreCase(list[i])) return i;
		}
		return -1;
	}
	
	/* 
	 * startElementの実装
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String arg1, String qName, Attributes attr) throws SAXException {
		if (qName.equalsIgnoreCase(E_INFO)) {
			configuration.setDataName(attr.getValue(A_NAME)); 		// ログの名称
			String hasTime = attr.getValue(A_USE_TIMESTAMP);		// 時間の取得場所
			configuration.setUsedTimestampfromFileName(hasTime.equalsIgnoreCase(A_NAME));
			java.io.File out = new java.io.File(attr.getValue(A_OUTPUT));
			if (out.exists() && out.isDirectory()) { // 存在していて、かつディレクトリでなければエラーとする
				configuration.setDownloadDirectory(out);
			} else {
				throw new SAXException("output directory is wrong.");				
			}
			String work = attr.getValue(A_ADMINPORT);	// 監視ポートの指定
			if (work != null) configuration.setAdminiPort(Integer.parseInt(work));
			// ファイル名がユニークかどうかのプロパティを追加 12/12
			work = attr.getValue(A_UNIQUENAME);
			if (work != null) configuration.setUniqueFileName(work.equalsIgnoreCase(TRUE));
			// 待ち時間指定 12/12
			work = attr.getValue(A_WAITTME);
			if (work != null) configuration.setWaitTime(Long.parseLong(work));
			// 待ち時間インターバル　12/15
			work = attr.getValue(A_WAITINTERVAL);
			if (work != null) configuration.setWaitInterval(Long.parseLong(work));
		} else if (qName.equalsIgnoreCase(E_TIMEANALIZE)) {
			configuration.setTimestampFormat(attr.getValue(A_FORMAT));
		} else if (qName.equalsIgnoreCase(E_PLACE)) {
			DefaultPartExtractionModel one = new DefaultPartExtractionModel();
			one.setTreeIndex(Integer.parseInt(attr.getValue(A_TARGETLEVEL)));	// 階層番号
			String work = attr.getValue(A_STARTPOS);
			if (work != null) one.setStartPos(Integer.parseInt(work));	// 終了位置
			work = attr.getValue(A_ENDPOS);
			if (work != null) one.setEndPos(Integer.parseInt(work));	// 終了位置
			work = attr.getValue(A_DIRECTION);
			if (work != null) one.setDirection(Integer.parseInt(work));	// 方向
			work = attr.getValue(A_DELIMITER);
			if (work != null) one.setDelimiter(work);	// デリミタ
			configuration.addTimestampPart(one);	// 追加していないかった
		} else if (qName.equalsIgnoreCase(E_LOCATION)) {
			String root = attr.getValue(A_ROOT);
			try {
				if (root != null && root.charAt(root.length()-1) != URL_DELIMITER) {
					root = root + URL_DELIMITER;
				}
				configuration.setFileLocation(new URL(root));	// file location
			} catch (Exception ex) {
				throw new SAXException("url is failure.", ex);
			}
		} else if (qName.equalsIgnoreCase(E_OPTION)) {
			UserInfo user = new UserInfo();
			user.setUser(attr.getValue(A_USER));
			user.setPasswd(attr.getValue(A_PASSWORD).toCharArray());
			configuration.setAuthorize(user);
			String interval = attr.getValue(A_RETRY_INTERVAL);
			if (interval != null) configuration.setRetryInterval(Integer.parseInt(interval)*1000);
			String retryCnt = attr.getValue(A_RETRY_COUNT);
			if (retryCnt != null) configuration.setRetryCount(Integer.parseInt(retryCnt));
			String modeZ = attr.getValue(A_MODEZ);
			if (modeZ != null){
				if (modeZ.equalsIgnoreCase("true")){
					configuration.setModeZ(true);
				} else if (modeZ.equalsIgnoreCase("false")){
					configuration.setModeZ(false);
				} else {
					throw new SAXException("modeZ attribute of \"Option\" element is illegal! value: " + modeZ);
				}
			}
			
			String useCache = attr.getValue(A_USE_CACHE);
			if(useCache != null){
				if(useCache.equalsIgnoreCase("true")){
					configuration.setUseCache(true);
				} else if (useCache.equalsIgnoreCase("false")){
					configuration.setUseCache(false);
				} else {
					throw new SAXException("useCache attribute of \"Option\" element is illegal! value: " + useCache);
				}
			}
			
			configuration.setToolIdSuffix(attr.getValue(A_TOOL_ID_SUFFIX));
			
			String siteOption = attr.getValue(A_SITEOPTION);
			if(siteOption != null){
				try {
					configuration.setSiteOption(convertOption(siteOption));
				} catch (Exception e) {
					throw new SAXException("siteOption attribute of \"Option\" element is illegal! value; " + siteOption);
				}
			}
			
			String gzipOutputOption = attr.getValue(A_GZIP_OUTPUT);
			if(gzipOutputOption != null){
				if(gzipOutputOption.equalsIgnoreCase("true")){
					configuration.setGZipOutput(true);
				} else if (gzipOutputOption.equalsIgnoreCase("false")){
					configuration.setGZipOutput(false);
				} else {
					throw new SAXException("gzipOutput attribute of \"Option\" element is illegal! value: " + gzipOutputOption);
				}
			}
			// 2012.10.30 T.TATEYAMA passive対応の為に、属性を追加
			String ftpMode = attr.getValue(A_FTPMODE);
			if (ftpMode == null) {
				ftpMode = PASSIVE_VAL;
			}
			if (ACTIVE_VAL.equals(ftpMode)) {
				configuration.setFtpDataConnectionMode(ConfigurationModel.FTP_ACTIVE);
			} else if (PASSIVE_VAL.equals(ftpMode)) {
				configuration.setFtpDataConnectionMode(ConfigurationModel.FTP_PASSIVE);				
			} else {
				throw new SAXException("ftpmode attribute of \"Option\" element is illegal! value: " + ftpMode);				
			}
		} else if (qName.equalsIgnoreCase(E_CHECKRULE)) {
			int level = Integer.parseInt(attr.getValue(A_LEVEL));	// URL以下の階層を示す
			int type = getIndex(attr.getValue(A_TYPE), TYPE_LIST);					// 階層の種類を示す。year, month, day, hour, min, other
			if (type == -1) throw new SAXException("rule is not found : " + attr.getValue(A_TYPE));
			currentFileNameChecker = new DefaultFileNameCheckRuleModel(level, type);
			configuration.addFileNameRule(currentFileNameChecker);
		} else if (qName.equalsIgnoreCase(E_PATTERN)) {
			buffer = new StringBuffer();
		} else if (qName.equalsIgnoreCase(E_FILTER)) {
			filterDefineFile = attr.getValue(A_CONF_PATH);
			configuration.setFilterCharsetName(attr.getValue(A_CHARSET_NAME));
			String lineSeparator = attr.getValue(A_LINE_SEPARATOR);
			if(lineSeparator != null){
				if(lineSeparator.equals("LF")){
					configuration.setFilterLineSeparator("\n");
				} else if (lineSeparator.equals("CRLF")){
					configuration.setFilterLineSeparator("\r\n");				
				} else {
					throw new SAXException("lineSeparator attribute of \"Filter\" element is illegal! value; " + lineSeparator);
				}
			}
		} else if (qName.equalsIgnoreCase(E_EXECCOM)) { // 16-018-01 新方式フィルタリング機能対応
			String execCommand = attr.getValue(A_EXECCOMMAND);
			configuration.setExecCommand(execCommand);
		}
	}
	
	/**
	 * 
	 * 文字列を解析し、”；”区切りに分解します。
	 * この時"\"をエスケープ文字列として認識します。
	 * 
	 * @param option 変換対象の文字列
	 * @return 分解結果
	 * @throws Exception 文字列が不正な場合にthrowされます。
	 */
	private String[] convertOption(String option) throws Exception{
		ArrayList<String> optArray = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		boolean escapeFlag = false;
		
		for(int i = 0; i < option.length(); i++){
			char c = option.charAt(i);
			if(c == '\\'){
				if(escapeFlag){
					sb.append(c);
					escapeFlag = false;
				} else {
					escapeFlag = true;
				}
				
			} else if (c == ';'){
				if(escapeFlag){
					sb.append(c);
					escapeFlag = false;
				} else {
					optArray.add(sb.toString());
					sb.setLength(0);
				}
			} else {
				if(escapeFlag){
					throw new Exception("Illegal option string. option: " + option);
				} else {
					sb.append(c);
				}
			}
		}
		
		// 最後が;で終わっている場合を考慮
		if(!sb.equals("")){
			optArray.add(sb.toString());
		}
		
		return optArray.toArray(new String[0]);
	}
}
