/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : CommandModel.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/03
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.command;


/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	コマンドを定義のインタフェース
 * @author Tomomitsu TATEYAMA
 */
public interface CommandModel {
	/**
	 * コマンドの処理を実行する。必要なパラメータは、構築する際に既に渡されている
	 */
	public void execute() throws RunningStopException, java.io.IOException;
}
