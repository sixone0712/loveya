/**
 * 
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : AdminServer.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/04
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	管理用サーバークラス。
 *	スレッドで起動して、他のプロセスからのコマンドを待ち、コマンドが来たら処理する。
 *	対応できるコマンドは次の通り。
 *		・実行状況の照会
 *		・ファイルダウンロードのキャンセル
 *		・自サーバー終了
 *
 * @author Tomomitsu TATEYAMA
 */
public class AdminServer implements Runnable {
	private static Logger logger = Logger.getLogger(AdminServer.class.getName());
	private boolean stop = false;
	private int serverPort = 0;
	private String dataName = null;
	private String requestNo = null;
	public static final String STATUS_CMD = "status";
	public static final String CANCEL_CMD = "cancel";
	public static final String COMPLETE_CMD = "complete";
	private static final String[] COMMANDS = {STATUS_CMD, CANCEL_CMD, COMPLETE_CMD};
	public static final String EOF_WORD = "[EOF]";
	public static final String SUCCESS_WORD = "CMD Success";
	private static final String ALL_PARAM = "all";
	private DownloadExecutor executor = null;
	private boolean startup = false;
	
	/**
	 * ポートと、データ名、要求番号を指定して構築する。
	 * @param port
	 */
	public AdminServer(int port, String dname, String rno) {
		serverPort = port;
		dataName = dname;
		requestNo = rno;
		if (dname == null) throw new IllegalArgumentException("Data Name is null");
	}
	/**
	 * この管理サーバーが管理しているDownloadExecutorを返す。
	 * Result:
	 *	@return DownloadExecutor	この管理サーバーが管理しているDownloadExecutor
	 */
	protected DownloadExecutor getExecutor() {
		return executor;
	}
	/**
	 * この管理サーバーが管理するDownloadExecutorを設定する
	 * Parameter:
	 * 	@param	exe	管理されるDownloadExecutor
	 */
	public void setExecutor(DownloadExecutor exe) {
		executor = exe;
	}
	/**
	 * 管理サーバーに接続してきたクライアントに処理結果を書き込む
	 * 正常にコマンドが処理されればSUCCESS_WORDをクライアントに送信する
	 * 失敗した場合は、エラー内容を書き込む。
	 * Parameter:
	 * 	@param	s		クライアントのソケット
	 *	@param	command	要求されたコマンド
	 *	@param	key		コマンドの整合性を確認するキー
	 */
	private void writeToClient(Socket s, String command, String key) {
		int index = 0;
		for (; index < COMMANDS.length; index++) {
			if (command.equals(COMMANDS[index])) break;
		}
		// -- 対象かどうか判定する
		if (index >= COMMANDS.length) return; // 

		// エラーの場合
		DataOutputStream writer = null;
		try { 
			writer = new DataOutputStream(s.getOutputStream());
			if ((index == 0 && !(key.equals(dataName) || key.equalsIgnoreCase(ALL_PARAM))) || 
				(index != 0 && !key.equals(requestNo))) {
				logger.info("parameter miss match : " + key );
				writer.writeBytes("Parameter error "+EOF_WORD+"\n");
			} else {
				switch (index) {
				case 0: 
					if (key.equalsIgnoreCase(ALL_PARAM)) {
						writer.writeBytes(requestNo + "," + getExecutor().toString() + "\n");
					} else {
						getExecutor().writeStatus(writer);
					}
					writer.writeBytes(SUCCESS_WORD + EOF_WORD + "\n");
					break;
				case 1:
					// PG終了
					getExecutor().cancel();
				case 2:
					stop();
					writer.writeBytes(SUCCESS_WORD + EOF_WORD + "\n");
					break;
				}				
			}
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "writer error", ex);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ex) {}
			}
		}
	}
	/* 
	 * スレッドを実行する。ここでは、ServerPotを使ってポートを監視する。
	 * タイムアウトは行わない。
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		logger.info("Admin:start server");
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(serverPort);
			startup = true;
			while(!isStopped()) {
				BufferedReader reader = null;
				try {
					Socket s = socket.accept();
					logger.info("Admin:connect client");
					reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
					String line = reader.readLine();
					if (line != null) { 					
						logger.info("Admin:accept -> " + line);
						String[] commands = line.split(" ");
						if (commands.length >= 2) {
							writeToClient(s, commands[0], commands[1]);
						}
					}
					reader.close();
					s.close();
					logger.info("Admin:close socket");
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Admin:happen the IOError at admini server ", e);
				}
			}
			socket.close();
			socket = null;
			logger.info("Admin:stop server");
		} catch (IOException e1) {
			logger.log(Level.SEVERE, "Admin:can't open the server port ", e1);
			stop();
		}
	}
	/**
	 * 中断フラグを設定する。スレッドのループを終了するためのフラグを設定
	 */
	private void stop() {
		stop = true;
	}
	/**
	 * 停止したかどうかを返す。実際にこのフラグが設定されてからすぐに止まっているわけではない。
	 * Result:
	 *	@return boolean		true	中止
	 *						false	起動中
	 */
	public boolean isStopped() {
		return stop;
	}
	/**
	 * 開始したかどうかを返す。
	 *
	 * Result:
	 *	@return boolean		true	開始
	 *						false	起動前
	 */
	public boolean isStartup() {
		return startup;
	}
}
