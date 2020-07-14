/**
 *
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : UploadMonitoring.java
 *
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/13
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */
package jp.co.canon.ckbs.eec.fs.collect.action;

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
 *
 *　装置コマンドから、収集状況を問い合わせるクラス。
 *　収集状況としては、要求数とダウンロード数を返す。
 *
 * @author Tomomitsu TATEYAMA
 */
public class UploadMonitoring {
	/** ステータスコマンド定数　*/
	public static final String STATUS_CMD = "status";
	/** 終了文字定数　*/
	public static final String EOF_WORD = "[EOF]";
	/** コマンド実行結果 */
	public static final String SUCCESS_WORD = "CMD Success";
	/** 接続ポート番号　*/
	private int port = 0;

	/**
	 * <p>Title:</p> File upload Service
	 * <p>Description:</p>
	 *	結果を返すためのクラス
	 * @author Tomomitsu TATEYAMA
	 */
	public class StatusHolder {
		private int totalCount = 0;
		private int completeCount = 0;
		private String requestNo = null;

		/**
		 *　設定されているtotalCountを返す。
		 * Return:
		 * 	@return totalCount
		 */
		public int getTotalCount() {
			return totalCount;
		}
		/**
		 * totalCountを設定する。
		 * Parameters:
		 * 	@param totalCount セットする totalCount
		 */
		public void setTotalCount(int totalCount) {
			this.totalCount = totalCount;
		}
		/**
		 *　設定されているcompleteCountを返す。
		 * Return:
		 * 	@return completeCount
		 */
		public int getCompleteCount() {
			return completeCount;
		}
		/**
		 * completeCountを設定する。
		 * Parameters:
		 * 	@param completeCount セットする completeCount
		 */
		public void setCompleteCount(int completeCount) {
			this.completeCount = completeCount;
		}
		/**
		 *　設定されているrequestNoを返す。
		 * Return:
		 * 	@return requestNo
		 */
		public String getRequestNo() {
			return requestNo;
		}
		/**
		 * requestNoを設定する。
		 * Parameters:
		 * 	@param requestNo セットする requestNo
		 */
		public void setRequestNo(String requestNo) {
			this.requestNo = requestNo;
		}

	}

	/**
	 * ポート番号を指定して構築する。
	 * @param port	ポート番号
	 */
	public UploadMonitoring(int port) {
		this.port = port;
	}
	/**
	 * 接続ポートを指定する。
	 *
	 * Parameters/Result:
	 *　 @param port	ポート番号
	 */
	public void setPort(int port) {
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
	private boolean sendCommand(String command, StringBuffer out) throws SocketException, IOException {
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
				if (line.endsWith(EOF_WORD)) {
					r = line.startsWith(SUCCESS_WORD);
				} else {
					out.append(line).append("\r");
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
	/**
	 * 指定されたリクエスト番号の状態を返す。
	 *
	 * Parameter:
	 * Result:
	 *	@return StatusHolder	状態を返す。nullの場合はコマンドが実行されていないことを示す。
	 */
	public StatusHolder getStatus() throws IOException {
		StringBuffer b = new StringBuffer();
		if (sendCommand(STATUS_CMD + " all", b)) {
			StatusHolder r = new StatusHolder();
			String[] values = b.toString().split(",");
			if (values.length > 2) {
				r.setRequestNo(values[0]);
				r.setTotalCount(Integer.parseInt(values[1]));
				r.setCompleteCount(Integer.parseInt(values[2]));
			}
			return r;
		}
		return null;
	}

}
