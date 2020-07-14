/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : CmdDataServiceCommand.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/01/04
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import jp.co.canon.cks.ees.service.command.CmdDataCommandFactory;
import jp.co.canon.cks.ees.service.command.RunningStopException;
import jp.co.canon.cks.ees.service.model.ConfigurationException;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *  Command Data向けの装置ログ収集コマンド
 *  ServiceCommandに対してCommandFactoryを変更する
 *  
 * @author Tomomitsu TATEYAMA
 */
public class CmdDataServiceCommand extends ServiceCommand {
	private static Logger logger = Logger.getLogger(CmdDataServiceCommand.class.getName());
	
	/**
	 * Command Date向けのメイン関数を含むバイナリ
	 *
	 * Parameter:
	 * 	@param	args	引数のパラメータ
	 *
	 * Result:
	 *	@return void
	 *
	 */
	public static void main(String[] args) {
		CmdDataServiceCommand command = new CmdDataServiceCommand();
		command.setCommandFactory(new CmdDataCommandFactory());
		try {
			command.checkParameters(args);
			command.invoke();
		} catch (IllegalArgumentException ex) {
			logger.log(Level.SEVERE, "command parameters are wrong.", ex);
			command.printCommandHelp();
		} catch (RunningStopException ex) {
			logger.log (Level.INFO, "command stopped.", ex);
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
