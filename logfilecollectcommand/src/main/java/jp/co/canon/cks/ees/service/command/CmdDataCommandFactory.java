/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : CmdDataCommandFactory.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/01/04
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.command;

import jp.co.canon.cks.ees.service.model.ConfigurationException;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	Command Date向けのCommandFactory
 *
 * @author Tomomitsu TATEYAMA
 * ============================================================================
 * 2012-10-30	T.TATEYAMA	圧縮コマンドのサポート(継承元変更の為）
 */
public class CmdDataCommandFactory extends CommandFactory {
	protected Integer DIRECTOR_OPT = new Integer(12);
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
		"-tid",		// 装置名
		"-d"		// ディレクトリ指定
	};
	
	/**
	 * デフォルトコンストラクタ
	 */
	public CmdDataCommandFactory() {
	}
	/**
	 * オプション文字列を返す。
	 */
	public String[] getOptions() {
		return OPTIONS;
	}
	/**
	 * コマンドヘルプを出力する
	 */
	public void printCommandHelp() {
		System.out.println("Usage : [Config　File] [Command] <Option1> <Option2> ...");
		System.out.println(" Command is below:");
		System.out.println("	list   [-url URL] [-u user/password] <-k keyword> <-p startdate,enddate> <-d dir>");
		System.out.println("	get    [-url URL] [-u user/password] [-ap portno] [-r requestno] [[-f filelist...] | [-fl filename]] <-ds size> <-az>");
		System.out.println("	stop   [-ap portno] [-r requestno]");
		System.out.println("	status [-ap portno]");
	}
	/* 
	 * createの実装
	 * @see jp.co.canon.cks.ees.service.command.CommandFactory#create(int)
	 */
	public CommandModel create(int command) throws ConfigurationException {
		switch (command) {
		case GET_FILELIST_COMMAND:
			return new CmdDataListCommand(getConfiguration(), 
					   (String)paramMap.get(KEYWORD_OPT), 
					   (String)paramMap.get(PERIOD_OPT),
					   (String)paramMap.get(DIRECTOR_OPT));
		default:
			return super.create(command);
		}
	}
}
