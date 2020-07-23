/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : ServiceCommand.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/03
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.co.canon.cks.ees.service.command.CommandFactory;
import jp.co.canon.cks.ees.service.command.RunningStopException;
import jp.co.canon.cks.ees.service.model.ConfigurationException;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	装置ログを扱うために呼び出されるmain関数を持ったクラス。
 *	CommandFactoryを使って、パラメータをチェックしながら、実行を行う
 * @author Tomomitsu TATEYAMA
 * =============================================================================
 * Change History:
 * 2011-01-03	T.Tateyama	printCommandHelp()のスコープをprotectedに変更
 * 2011-01-07	T.Tateyama	コメントを追加
 * 2011-11-21	T.Tateyama	追加開発の為に引数を修正
 */
public class ServiceCommand {
	private static Logger logger = Logger.getLogger(ServiceCommand.class.getName());
	private CommandFactory	commandFactory = new CommandFactory();
	private int commandNo = -1;
	
	/**
	 * 設定されているCommandNoを返す
	 * @return commandNo
	 */
	protected int getCommandNo() {
		return commandNo;
	}
	/**
	 * CommandNoを設定する。
	 * @param commandNo セットする commandNo
	 */
	protected void setCommandNo(int commandNo) {
		this.commandNo = commandNo;
	}
	/**
	 * 設定されているCommandFactoryを返す。
	 * @return commandFactory
	 */
	protected CommandFactory getCommandFactory() {
		return commandFactory;
	}
	/**
	 * CommandFactoryを設定する。
	 * @param commandFactory セットする commandFactory
	 */
	protected void setCommandFactory(CommandFactory commandFactory) {
		this.commandFactory = commandFactory;
	}
	/** 
	 * 起動時の引数をチェックする。引数が間違っている場合はIllegalArgumentExceptionを発生させる。
	 * サポートされているパラメータ等は、CommandFactoryに登録されている内容をもとにチェックする。
	 *
	 * Parameter:
	 * 	@param	String[] args	起動時の引数
	 *
	 * Result:
	 *	@return void
	 */
	protected void checkParameters(String[] args) throws IllegalArgumentException {
		// command is Service.exe list | get | status | stop [-option ...] 
		if (args.length <= 1) throw new IllegalArgumentException("parameters are not found.");
		// 初期化ファイル
		getCommandFactory().setConfigurationFile(args[0]);
		for (int i=0; i < getCommandFactory().getCommands().length; i++ ) {
			if (getCommandFactory().getCommands()[i].equalsIgnoreCase(args[1])) {
				setCommandNo(i);
				break;
			}
		}
		if (getCommandNo() == -1) throw new IllegalArgumentException("request command is not found : " + args[1]);
		//------------
		for (int i=2; i < args.length; i++) {
			int option = -1;
			for (int n=0; n < getCommandFactory().getOptions().length; n++) {
				if (getCommandFactory().getOptions()[n].equalsIgnoreCase(args[i])) {
					option = n;
					break;
				}
			}
			if (option == -1) throw new IllegalArgumentException("Illegal parameter value : " + args[i]);
			String v = null;
			if (args.length > i+1) v = args[i+1];
			if (getCommandFactory().setOptions(option, v)) {
				i++;
			}
		}
		// もし必須コマンドが指定されていない場合は、execute時に判定する。
		if (!getCommandFactory().hasRequiedParameter(commandNo)) {
			throw new IllegalArgumentException("Required parameter is not found." );
		}
	}
	/**
	 *　コマンドを実行する
	 *
	 * Parameter:
	 * Result:
	 *	@return void
	 */
	protected void invoke() throws RunningStopException, IOException, ConfigurationException {
		getCommandFactory().create(commandNo).execute();
		System.out.println("Command Successful");
	}
	/**
	 * 実行コマンドのヘルプを表示する。実際は、CommandFactoryクラスが実装する
	 *
	 * Parameter:
	 * Result:
	 *	@return void
	 */
	protected void printCommandHelp() {
		getCommandFactory().printCommandHelp();
	}
	/**
	 *	Javaから起動される。パラメータを解析してコマンドを実行する。
	 *
	 * Parameter:
	 * 	@param	String[] args	起動時パラメータを文字列配列で受け取る。
	 *
	 * Result:
	 *	@return void
	 */
	public static void main(String[] args) {
		ServiceCommand command = new ServiceCommand();
		try {
			command.checkParameters(args);
			command.invoke();
		} catch (IllegalArgumentException ex) {
			logger.log(Level.SEVERE, "command parameters are wrong.", ex);
			command.printCommandHelp();
		} catch (RunningStopException ex) {
			logger.log(Level.INFO, "command stopped.", ex);
			System.out.println(ex.getMessage());
		} catch (ConfigurationException ex) {
			logger.log(Level.WARNING, "stop process. configuration error. see the other exception.", ex);
			System.out.println("configuration error");
		} catch (Throwable ex) {
			logger.log(Level.SEVERE, "command execution error.", ex);
			ex.printStackTrace();	// ログを出力する
		}
	}
}
