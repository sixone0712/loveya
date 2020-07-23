/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : CancelCommand.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/04
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.command;

import java.io.IOException;

import jp.co.canon.cks.ees.service.AdminClient;
import jp.co.canon.cks.ees.service.AdminServer;
import jp.co.canon.cks.ees.service.model.ConfigurationModel;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	ファイル取得を中止するコマンド
 *
 * @author Tomomitsu TATEYAMA
 */
public class CancelCommand implements CommandModel {
	private ConfigurationModel config = null;
	private String requestNo = null;
	
	/**
	 * ConfigurationModelを設定して構築する
	 * @param c	ConfigurationModelを設定する
	 * @param rno	中止する要求番号
	 */
	public CancelCommand(ConfigurationModel c, String rno) {
		if (c == null) throw new IllegalArgumentException("configurationModel is null");
		if (rno == null) throw new IllegalArgumentException("request no is null");
		config = c;
		requestNo = rno;
	}
	/* 
	 * executeの実装
	 * @see jp.co.canon.cks.ees.service.command.CommandModel#execute()
	 */
	public void execute() throws RunningStopException, IOException {
		AdminClient client = null;
		// コンフィグレーションから監視ポートとログ名を取得する
		client = new AdminClient(config.getAdminiPort());
		try {
			boolean success = client.sendCommand(AdminServer.CANCEL_CMD + " " + requestNo);
			if (!success) {
				throw new RunningStopException(RunningStopException.REQUEST_OTHER_PROCESE);
			}
		} catch (java.net.ConnectException ex) {
			throw new RunningStopException(RunningStopException.REQUEST_NOT_FOUND);
		}
	}
}
