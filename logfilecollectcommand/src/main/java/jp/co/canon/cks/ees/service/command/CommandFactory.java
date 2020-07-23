/**
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : CommandFactory.java
 *
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/03
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.command;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jp.co.canon.cks.ees.service.model.ConfigurationException;
import jp.co.canon.cks.ees.service.model.ConfigurationModel;
import jp.co.canon.cks.ees.service.model.ConfigurationReader;


/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	このクラスは、コマンドを実行するに必要な情報を保持し、作成するためのクラスを定義する。
 *	将来コマンドの内容を変える場合は、このクラスを継承して作成すると、呼び出し側のクラスの変更が
 *　	最小限となるように設定されてる。
 *	機能としては、以下の通り。
 *		・オプション設定を登録する機能
 *		・CommandModel（実際に実行するクラス）を作成する
 *		・コマンドやオプションのチェック情報を返す。又、コマンドラインから実行する際のヘルプドキュメントを出力する
 * @author Tomomitsu TATEYAMA
 * =============================================================================
 * Change History:
 * 2011-01-03	T.Tateyama	クラス変数とprivateメソッドのスコープをprotectedに変更
 * 							setOptions()のチェックでOPTIONSの長さを定数を直接参照していたが、メソッドからの参照する様に変更
 * 2011-01-07	T.Tateyama	コメントを追加
 * 2011-11-20	T.Tateyama	コマンド引数の追加
 * 2012-10-30	T.TATEYAMA	圧縮コマンドのサポート
 * 2016-11-28	J.Sugawara	設定ファイルパスを追加 16-018-01 新方式フィルタリング機能対応
 */
public class CommandFactory {
	public static final int GET_FILELIST_COMMAND = 0;
	public static final int REQUEST_FILES_COMMAND = 1;
	public static final int GET_STATUS_COMMAND = 2;
	public static final int STOPPED_REQUEST_COMMAND = 3;

	private static final String[] COMMANDS = {"list", "get", "status", "stop"};
	private static String OPTIONS[] = {
		"-r",		// 要求番号	get, stopは必須
		"-f",		// ファイル指定　getは必須、そのあとはファイル名をカンマ区切りにてもらう
		"-k",		// キーワードを指定　listのオプション　
		"-p",		// 期間指定を指定　listのオプション　
		"-fl",		// ファイルで、ファイルの一覧を指定する
		"-ds",		// ディスクサイズチェックを行う
		"-url",		// ファイルのURL情報　list/getでの必須パラメータ
		"-u",		// ユーザ情報 list/getでの必須パラメータ
		"-md",		// FTPモード情報 list/getでの必須パラメータ // <FS収集機能のActive対応(CITS)>
		"-ap",		// 管理ポート番号の指定　get/stop/statusの必須パラメータ
		"-az",		// アーカイブ化 2012.10.30
		"-tid"		// 装置名
	};
	protected static Integer REQNO_OPT = new Integer(0);
	protected static Integer FILE_OPT = new Integer(1);
	protected static Integer KEYWORD_OPT = new Integer(2);
	protected static Integer PERIOD_OPT = new Integer(3);
	protected static Integer FILELIST_OPT = new Integer(4);
	protected static Integer DISKCHECK_OPT = new Integer(5);
	protected static Integer URL_OPT = new Integer(6);
	protected static Integer USER_OPT = new Integer(7);
	protected static Integer FTPMODE_OPT = new Integer(8);		//<FS収集機能のActive対応(CITS)>FTPMODE_OPT用に追加
	protected static Integer ADMIN_PORT_OPT = new Integer(9);	//<FS収集機能のActive対応(CITS)>  8-> 9に変更
	protected static Integer ARCHIVE_OPT = new Integer(10);		//<FS収集機能のActive対応(CITS)>  9->10に変更
	protected static Integer TID_OPT = new Integer(11);			//<FS収集機能のActive対応(CITS)> 10->11に変更
	private static final char URL_DELIMITER = '/';

	private File configFile = null;
	private ConfigurationModel configuration = null;
	protected Map<Integer, String> paramMap = new HashMap<Integer, String>();

	/**
	 * コマンドを実行する際のパラメータを情報を標準出力に出力する
	 * change	getコマンドに-aZオプションを追加	2012.10.30
	 */
	public void printCommandHelp() {
		System.out.println("Usage : [Config　File] [Command] <Option1> <Option2> ...");
		System.out.println(" Command is below:");
		System.out.println("	list   [-url URL] [-u user/password] <-k keyword> <-p startdate,enddate>");
		System.out.println("	get    [-url URL] [-u user/password] [-ap portno] [-r requestno] [[-f filelist...] | [-fl filename]] <-ds size> <-az> <-tid ToolID>");
		System.out.println("	stop   [-ap portno] [-r requestno]");
		System.out.println("	status [-ap portno]");
	}

	/**
	 * 設定ファイルのファイルパス文字列を設定する。ファイルが存在しない場合はIllegalArgumentExceptionを発生させる
	 *
	 * Parameter:
	 * 	@param	 path	ファイル名（パス名を含む）フルパスでない場合は、実行時ディレクトリからの相対パスとする
	 */
	public void setConfigurationFile(String path) {
		configFile = new File(path);
		if (!configFile.exists()) throw new IllegalArgumentException("configuration file is not found.:" + path);
	}
	/**
	 * 設定ファイルのファイルパスを返す。16-018-01 新方式フィルタリング機能対応
	 * Result:
	 *	@return String	設定ファイルのファイルパス
	 *
	 */
	public String getConfigurationFilePath() {
		String path = configFile.getAbsolutePath();
		return path.substring(0, configFile.getAbsolutePath().lastIndexOf(File.separator));
	}
	/**
	 * このCommandFactoryがサポートしている、実行時のコマンドの文字列を返す。外部のクラスがパラメータチェックとコマンドIDの判定に利用する。
	 *
	 * Result:
	 *	@return String[]	サポートコマンドを配列で返す。
	 */
	public String[] getCommands() {
		return COMMANDS;
	}
	/**
	 * このCommandFactoryがサポートしているオプションの一覧を返す。外部のクラスがパラメータチェックとオプションIDの判定に利用する
	 *
	 * Result:
	 *	@return String[]	サポートしているオプションを配列で返す。
	 */
	public String[] getOptions() {
		return OPTIONS;
	}
	/**
	 * オプションの値を登録する。
	 * change	 判定条件の変更	2012.10.30
	 *
	 * Parameter:
	 * 	@param	int		id		オプションID（getOptions()で返される配列の位置）
	 * 	@param	String	value	オプションの値
	 *
	 * Result:
	 *	@return boolean true  : 引数の値を利用
	 *					false : 引数の値は利用しない
	 */
	public boolean setOptions(int id, String value) {
		if (value == null && id != ARCHIVE_OPT.intValue()) {
			throw new IllegalArgumentException("Option value is not found.");
		}
		if (0 <= id && id < getOptions().length) {
			paramMap.put(new Integer(id), value);
			return id != ARCHIVE_OPT.intValue();
		}
		return false;
	}
	/**
	 * ConfigurationModelを返す。、存在しない場合は作成する。
	 *
	 * Result:
	 *	@return ConfigurationModel	保持もしくは作成したConfigurationModel
	 */
	protected ConfigurationModel getConfiguration() throws ConfigurationException {
		if (configuration == null) {
			if (configFile == null) throw new IllegalArgumentException("configuration file don't set");
			URL url = null;
			String user = null;
			String passwd = null;
			int adminport = -1;
			String toolID = null;

			ConfigurationReader reader = new ConfigurationReader();
			// AdminPort
			if (paramMap.containsKey(ADMIN_PORT_OPT)) {
				adminport = Integer.parseInt(paramMap.get(ADMIN_PORT_OPT));
			}
			// URL
			if (paramMap.containsKey(URL_OPT)) {
				String root = paramMap.get(URL_OPT);
				if (root != null && root.charAt(root.length()-1) != URL_DELIMITER) {
					root = root + URL_DELIMITER;
				}
				try {
					url = new URL(root);	// file location
				} catch (MalformedURLException ex) {
					throw new IllegalArgumentException("Illigal url.:" + root);
				}
			}
			// user/password
			if (paramMap.containsKey(USER_OPT)) {
				user = paramMap.get(USER_OPT);
				int pos = user.indexOf("/");
				if (pos == -1 || user.length() < pos+1) {
					throw new IllegalArgumentException("Illigal user id or password.:" + user);
				}
				passwd = user.substring(pos+1);
				user = user.substring(0, pos);
			}

			// <FS収集機能のActive対応(CITS)>
			String ftpmode = "passive";
			if (paramMap.containsKey(FTPMODE_OPT)) {
				ftpmode = paramMap.get(FTPMODE_OPT);
			}

			if(paramMap.containsKey(TID_OPT)){
				toolID = paramMap.get(TID_OPT);
			}

			// <FS収集機能のActive対応(CITS)>
			configuration = reader.getConfiguration(configFile, url, user, passwd, ftpmode, adminport, toolID);

			// キャッシュを使用するにもかかわらず、装置名が設定されていない場合はエラー
			if(configuration.isUseCache() && toolID == null){
				throw new IllegalArgumentException("Tool ID is not set!");
			}
		}
		return configuration;
	}
	/**
	 * 必須のパラメータが指定されているかどうかを返す。サポートされていないコマンドIDが指定された場合
	 * IllegalArgumentExceptionを発生させる。
	 * Parameter:
	 * 	@param	command	コマンドID（getCommands()で返される配列の位置）
	 *
	 * Result:
	 *	@return boolean	true	: 指定されている
	 *					false	:　必須パラメータが指定されていない
	 */
	public boolean hasRequiedParameter(int command) {
		switch (command) {
		case GET_FILELIST_COMMAND:
			return paramMap.containsKey(URL_OPT) && paramMap.containsKey(USER_OPT);
		case GET_STATUS_COMMAND:
			return paramMap.containsKey(ADMIN_PORT_OPT);
		case REQUEST_FILES_COMMAND:
			return paramMap.containsKey(ADMIN_PORT_OPT) && paramMap.containsKey(REQNO_OPT) &&
					paramMap.containsKey(URL_OPT) && paramMap.containsKey(USER_OPT) &&
				(paramMap.containsKey(FILE_OPT) || paramMap.containsKey(FILELIST_OPT));
		case STOPPED_REQUEST_COMMAND:
			return paramMap.containsKey(ADMIN_PORT_OPT) && paramMap.containsKey(REQNO_OPT);
		}
		throw new IllegalArgumentException("don't support command id : " + command);
	}
	/**
	 * コマンドIDに一致するCommandModelを作成する。サポートされていないコマンドIDが指定された場合
	 * IllegalArgumentExceptionを発生させる。
	 *
	 * Parameter:
	 * 	@param	command	コマンドID（getCommands()で返される配列の位置）
	 *
	 * Result:
	 *	@return CommandModel	実行すべきCommandModel
	 */
	public CommandModel create(int command) throws ConfigurationException {
		switch (command) {
		case GET_FILELIST_COMMAND:
			return new ListCommand(getConfiguration(),
								   (String)paramMap.get(KEYWORD_OPT),
								   (String)paramMap.get(PERIOD_OPT));
		case REQUEST_FILES_COMMAND:
			return new FilesGetCommand(getConfiguration(),
									  (String)paramMap.get(REQNO_OPT),
									  (String)paramMap.get(FILE_OPT),
									  (String)paramMap.get(FILELIST_OPT),
									  (String)paramMap.get(DISKCHECK_OPT),
									  paramMap.containsKey(ARCHIVE_OPT), 	// 2012.10.30 add
									  this.getConfigurationFilePath()); 	// 16-018-01
		case GET_STATUS_COMMAND:
			return new StatusCommand(getConfiguration());
		case STOPPED_REQUEST_COMMAND:
			return new CancelCommand(getConfiguration(),
									 (String)paramMap.get(REQNO_OPT));
		}
		throw new IllegalArgumentException("don't support command id : " + command);
	}
}
