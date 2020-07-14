/**
 * 
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : AdminClient.java
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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	管理サーバーへ接続し、メッセージを送信するクラス
 *	管理サーバーは、ファイルの取得時に別のプロセスで起動しているコマンドの状況や
 *	中止を行うために別プロセスからのメッセージを監視するプロセスである。
 *	このプロセスは、ネットワークの特定のポートを監視して、そこから受信したコマンドに応じて
 *	処理を行う。このクラスは、その管理サーバーへの接続とコマンドの送信を行うクラスである。
 *	但し、コマンドの内容等にはタッチしていないので、あくまでも通信手順の確立だけを行う。
 *
 * @author Tomomitsu TATEYAMA
 */
public class AdminClient {
	private int port = 0;
	
	/**
	 * ポート番号を指定して構築する。
	 * @param port	ポート番号
	 */
	public AdminClient(int port) {
		this.port = port;
	}
	/**
	 * ソケットを作成する
	 *
	 * Result:
	 *	@return Socket	作成したソケットを返す
	 */
	private Socket openSocket() throws java.net.SocketException, java.io.IOException {
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress("localhost", port));
		socket.setSoTimeout(60000);		
		return socket;
	}
	/**
	 * コマンドの送信
	 *
	 * Parameter:
	 * 	@param	command		コマンド文字列（通常は、コマンド　+ " " + パラメータ）
	 * Result:
	 *	@return boolean		true	コマンド実行が成功
	 *						false	コマンド実行が失敗
	 *	@throws	IOException	ネットワークエラー発生時
	 */
	public boolean sendCommand(String command) throws SocketException, IOException {
		boolean r = false;
		Socket socket = null;
		DataOutputStream  writer = null;
		BufferedReader reader = null;
		try {
			socket = openSocket();
			writer = new DataOutputStream (socket.getOutputStream());
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer.writeBytes(command + "\n");
			// 結果を取得する
			while (true) {
				String line = reader.readLine();
				if (line == null) break;
				if (line.endsWith(AdminServer.EOF_WORD)) {
					r = line.startsWith(AdminServer.SUCCESS_WORD);
				} else {
					System.out.println(line);
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (socket != null) {
				socket.close();
			}
		}
		return r;
	}
}
