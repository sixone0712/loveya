/**
 * 
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : StatusCommand.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/04
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.command;


import jp.co.canon.cks.ees.service.AdminClient;
import jp.co.canon.cks.ees.service.AdminServer;
import jp.co.canon.cks.ees.service.model.ConfigurationModel;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	Statusを返すコマンドを実行する
 *
 * @author Tomomitsu TATEYAMA
 */
public class StatusCommand implements CommandModel {
	private ConfigurationModel config = null;
	
	/**
	 * ConfigurationModelを設定して構築する
	 * @param c	ConfigurationModelを設定する
	 */
	public StatusCommand(ConfigurationModel c) {
		config = c;
	}
	/* 
	 * executeの実装
	 * @see jp.co.canon.cks.ees.service.command.CommandModel#execute()
	 */
	public void execute() throws RunningStopException, java.io.IOException {
		AdminClient client = null;
		// コンフィグレーションから監視ポートとログ名を取得する
		client = new AdminClient(config.getAdminiPort());
		try {
			boolean success = client.sendCommand(AdminServer.STATUS_CMD + " " + config.getDataName());
			if (!success) {
				throw new RunningStopException(RunningStopException.REQUEST_OTHER_PROCESE);
			}
		} catch (java.net.ConnectException ex) {
			throw new RunningStopException(RunningStopException.REQUEST_NOT_FOUND);
		}
	}
}
