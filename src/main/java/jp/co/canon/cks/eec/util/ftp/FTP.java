/**
 *
 * File : FTP.java
 *
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/11/30
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.util.ftp;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	このクラスは、FTPの実際に行うクラスを定義する。ASCIIには対応しない。
 *
 * @author Tomomitsu TATEYAMA
 * ============================================================================
 * 2012-10-28 T.Tateyama passiveモード対応
 * 2012-11-14 T.Tatetama passiveモードのIPアドレスへの変換で文字列操作の不具合を修正
 * 2012-12-12 T.TATEYAMA passiveモードをデフォルトに変更、リソースファイルから初期化するように修正
 *
 */
public class FTP {
	private static final Logger logger = Logger.getLogger(FTP.class.getName());
    /**
     * SeverSocket#accept()時のタイムアウト値(ミリ秒単位)
     */
    private static final int SERVERSOCKET_TIMEOUT = 60000;
    /**
     * コマンド一覧
     */
    private final String COMMAND_RETR = "RETR";
    private final String COMMAND_QUIT = "QUIT";
    private final String COMMAND_USER = "USER";
    private final String COMMAND_PASS = "PASS";
    private final String COMMAND_CWD  = "CWD";
    //private final String COMMAND_PWD  = "PWD";
    private final String COMMAND_PORT = "PORT";
    private final String COMMAND_LIST = "LIST";
    private final String COMMAND_BINARY = "TYPE I";
    // 2012-10-28 passive対応
    private final String COMMAND_PASSIVE = "PASV";

    public static final int ACTIVE_MODE = 1;
    public static final int PASSIVE_MODE = 2;
    /** モード状態 */
    private int dataConnectionMode = PASSIVE_MODE;

    /**
     * <p>Title:</p> File upload Service
     * <p>Description:</p>
     * ftpコマンド送信後に送られていくるサーバ側のメッセージを保持する。
     *
     * @author Tomomitsu TATEYAMA
     */
    class ReceiveMessage {
    	/** 全受信メッセージを保持する */
    	public StringBuilder allMessage = new StringBuilder();
    	/** 最後にサーバから送られたメッセージを保持する */
    	public String lastMessage = null;
    }

    /**
     * <p>Title:</p> File upload Service
     * <p>Description:</p>
     * 読込用ソケットにアクセスするためのクラス。
     *
     * @author Tomomitsu TATEYAMA
     */
    public class DataSocketAccessor {
    	/** 読込用ソケット */
    	private Socket dataSocket = null;
    	/** サーバソケット (activeモード接続時) */
    	private ServerSocket serverSocket = null;

    	/**
    	 * 読込用Socketを指定して構築する。
     	 * @param rSocket
    	 */
    	public DataSocketAccessor(Socket rSocket) {
    		dataSocket = rSocket;
    	}

    	/**
    	 * ServerSocketを指定して構築する
    	 * @param sSocket
    	 */
    	public DataSocketAccessor(ServerSocket sSocket) {
    		serverSocket = sSocket;
    	}

    	/**
    	 * 読込用Socketを返す。
    	 *
    	 * Parameters/Result:
    	 *　 @return
    	 *　 @throws IOException
    	 */
    	public Socket getDataSocket() throws IOException {
    		if (dataSocket == null) {
    			if (serverSocket != null) {
	    			dataSocket = serverSocket.accept();
	    			dataSocket.setSoTimeout(SERVERSOCKET_TIMEOUT);
    			}
     		}
   			return dataSocket;
    	}

    	/**
    	 * 内部で保持しているSocketをクローズする。
     	 *
    	 * Parameters/Result:
    	 *　 @throws IOException
    	 */
    	public void close() throws IOException {
    		if (dataSocket != null) {
    			dataSocket.close();
    			dataSocket = null;
    		}
    		if (serverSocket != null) {
    			serverSocket.close();
    			serverSocket = null;
    		}
    	}
    }
    // 2012-10-28 End
    /**
     * <p>Title:</p> File upload Service
     * <p>Description:</p>
     *	クローズ時にソケットも閉じるように拡張したクラス
     * @author Tomomitsu TATEYAMA
     */
    public class DataInputStreamEx extends DataInputStream {
    	private DataSocketAccessor socket = null; // 2012.10.28 Change T.Tateyama
    	public DataInputStreamEx(InputStream in, DataSocketAccessor s) {
    		super(in);
    		socket = s;
    	}
		public void close() throws IOException {
			super.close();
	        if (getLastStatusCode(null) != 226) {
	        	logger.info ("ftp error");
	        }
	        socket.close();
		}
    }

    /**
     * プライベート変数の定義
     */
    private Socket clientSocket;
    private BufferedReader clientSocketReader = null;
    private BufferedWriter clientSocketWriter = null;

    // ポート番号
    private int PORT = 21;
    private int code;

    private boolean cancel = false;
    private long colInterval = 60000;	// 再接続の待ち時間を指定する　デフォルトは１分
    private long maxRetryCount = 3;	// 再接続のリトライ回数を指定する。マイナスの場合は無制限

    private String hostName = null;

    /**
     * ホスト名を指定してFTPを構築する
     * @param host 接続するホスト名／IPアドレス
     */
    public FTP(String host) {
    	this(host, 21);
    }
    /**
     * ホスト名とポート番号を指定してFTPを構築する
     * @param host	接続するホスト名／IPアドレス
     * @param port	接続するポート番号
     */
    public FTP(String host, int port) {
    	hostName = host;
    	if (port != -1) {
    		PORT = port;
    	}
    	initialize();
    }
    /**
     * リソースファイルから初期化する
     *
     * Parameters/Result:
     */
    private void initialize() {
    	try {
    		ResourceBundle b = ResourceBundle.getBundle("eec_ftp");
    		String mode = b.getString("ftpMode");
    		if (mode != null && mode.equalsIgnoreCase("active")) {
    			setDataConnectionMode(ACTIVE_MODE);
    			logger.info("change active mode");
    		}
    	} catch (java.util.MissingResourceException e) {
    		// none
    	} catch (Throwable e) {
    		logger.log(Level.INFO, "resource cannot read.", e);
    	}
    }
    /**
     * 指定された接続先に接続する
     * @return	true	接続された
     * 			false	キャンセルされた
     */
    public boolean connect()  throws FTPException {
    	int tryCount = 0;
        while(maxRetryCount == 0 || tryCount < maxRetryCount){
            if (cancel) return false;
		    if (tryCount > 0) {
			    sleepInterval();	// 再接続する際はスリープする。
		    	logger.info("FTP Server connection retry.:" + tryCount);
		    }
		    tryCount ++;
        	try {
		        this.clientSocket = new Socket(hostName, PORT);
	            clientSocketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	            clientSocketWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
	            this.clientSocket.setSoTimeout(SERVERSOCKET_TIMEOUT);
		        if ( (code = getLastStatusCode(null)) != 220) {
		        	logger.log(Level.WARNING, "",  new FTPException("[Connection Failed]", code));
		        } else {
			        logger.info("Connecting with FTPServer is a success　: " + hostName );
		        	return true;
		        }
		    } catch (UnknownHostException e) {
		        throw new FTPException(e);
		    } catch (IOException e) {
	        	logger.log(Level.WARNING, "HappenError",  e);
		    }
        }
        throw new FTPException("FTP server connection failed.");
    }
    /**
     * 接続を解除する。内部で保持している変数はクローズする。
     */
    public void disconnect() throws FTPException {
        try {
            clientSocket.close();
            clientSocket = null;
            clientSocketReader.close();
            clientSocketReader = null;
            clientSocketWriter.close();
            clientSocketWriter = null;
        } catch (IOException e ) {
            throw new FTPException(e);
        }
    }
    /**
     *	FTP処理をキャンセルする。正し、connect時のみチェックする。
     *
     * Parameter:
     * 	@param
     *
     * Result:
     *	@return void
     */
    public void cancel() {
    	cancel = true;
    }
    /**
     *	FTPをクローズする。
     *
     * Parameter:
     * Result:
     *	@return void
     */
    public void close() {
    	if (clientSocket == null) return; // 開いていないのでなにもしない。
        try {
            logout();
            disconnect();
            logger.log(Level.INFO, "Disconnecting with FTPServer is a success.");
        } catch (SocketTimeoutException e){
            logger.log(Level.WARNING, "Socket was Timeout. Can not logont", e);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Other exception happend", e);
        }
    }
    /**
     *	再接続時の待ち時間を設定する。
     * Parameter:
     * 	@param	 time	待ち時間をミリ秒で指定する。
     */
    public void setRetryWaitTime(long time) {
    	colInterval = time;
    }
    /**
     *	再接続の最大回数を指定する。０の場合は、無限に再接続する。
     *	マイナスを指定された場合はIllegalArgumentExceptionを発生
     * Parameter:
     * 	@param	 cnt	最接続するための最大回数を正の数値で指定する。0の場合は、無限
     */
    public void setMaxRetryCount(int cnt) {
    	if (cnt < 0) throw new IllegalArgumentException("not support argument value:" + cnt);
    	maxRetryCount = cnt;
    }
    /**
     *	待ち時間分だけスリープする。
     * Result:
     *	@return boolean	true:正常終了	false:異常終了
     */
    private boolean sleepInterval () {
        try {
            Thread.sleep(colInterval);
        } catch (InterruptedException e) {
            logger.log(Level.INFO, "", e);
            return false;
        }
        return true;
    }
    /**
     *	FTPサーバーに対してログイン処理を行う。
     *
     * Parameter:
     * 	@param	String	userName	ログインユーザ名を指定する
     * 	@param	String	password	ログインパスワードを指定する。
     *
     * Result:
     *	@throws	FTPException			FTP接続でサーバー側からのエラーが発生した場合にスローする。　（ その他にもホストが見つからない場合、IOExceptionが発生した場合）
     *	@throws	SocketTimeoutException	接続タイムアウトが発生した場合にスローする
     */
    public void login(String userName, String password) throws FTPException, SocketTimeoutException {
        sendCommand(COMMAND_USER + " " + userName);
        if (getLastStatusCode(null) != 230) {
            sendCommand(COMMAND_PASS + " " + password);
            if ((code = getLastStatusCode(null)) != 230) {
                throw new FTPException("[Login Failed]", code);
            }
        }
    }
    /**
     *	FTPサーバーに対してログアウトする。
     *
     * Parameter:
     *
     * Result:
     *	@throws FTPException			FTP接続でサーバー側からのエラーが発生した場合にスローする。
     *	@throws	SocketTimeoutException	Timeoutが発生した場合、スローする。
     */
    protected void logout() throws FTPException, SocketTimeoutException {
        sendCommand(COMMAND_QUIT);
        if ((code = getLastStatusCode(null)) != 221) {
            throw new FTPException("[" + COMMAND_CWD + " Command Failed]", code);
        }
    }
    /**
     *	ディレクトリを変更する
     *
     * Parameter:
     * 	@param	 String	cd	変更するディレクトリを指定する。
     *
     * Result:
     *	@throws FTPException			FTP接続でサーバー側からのエラーが発生した場合にスローする。
     *	@throws	SocketTimeoutException	Timeoutが発生した場合、スローする。
     */
    public void cd(String cdDir) throws FTPException, SocketTimeoutException {
        sendCommand(COMMAND_CWD + " " + cdDir);
        if ((code = getLastStatusCode(null)) != 250) {
            throw new FTPException("["+COMMAND_CWD + " Command Failed]", code);
        }
    }
    /**
     *	カレントディレクトリを要求するコマンドを送信する
     *
    protected void sendPWDCommand() throws FTPException, SocketTimeoutException {
        sendCommand(COMMAND_PWD);
        if ((code = getLastStatusCode()) != 257) {
            throw new FTPException("["+COMMAND_PWD + " Command Failed]", code);
        }
    }
    /**
     *	転送モードをバイナリに設定する
     */
    public void binary() throws FTPException, SocketTimeoutException {
        sendCommand(COMMAND_BINARY);
        if ((code = getLastStatusCode(null)) != 200) {
            throw new FTPException("["+COMMAND_BINARY + "Command Failed]", code);
        }
    }
    /**
     * サーバーソケットを返す
     *
     * Result:
     *	@return ServerSocket	FTPのサーバーソケットを返す。
     */
    protected ServerSocket getServerSocket() throws FTPException, SocketTimeoutException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(0, 1);
            int iPort = serverSocket.getLocalPort();

            String ip = clientSocket.getLocalAddress().getHostAddress();
            String command = COMMAND_PORT;
            command = command + " " + ip.replace('.', ',');
            command = command + "," + (iPort >> 8) + "," + (iPort & 0xFF);
            sendCommand(command);
            if ( (code = getLastStatusCode(null)) != 200) {
                throw new FTPException("[" + command + " Command Failed]", code);
            }
            serverSocket.setSoTimeout(SERVERSOCKET_TIMEOUT);
        } catch (IOException e) {
            throw new FTPException(e);
        }
        return serverSocket;
    }

    /**
     *	サーバー側のファイルを開いて、DataInputStreamとして返す。
     *
     * Parameter:
     * 	@param	 String	serverFileName	サーバー側のファイル名を設定する。
     *
     * Result:
     *	@return DataInputStream	開かれたInputStream
     */
    public InputStream openFileStream(String serverFileName) throws FTPException, SocketTimeoutException, SocketException, IOException  {

    	//ServerSocket serverSocket = getServerSocket();
    	DataSocketAccessor accessor = createReadSocketAccessor();
        sendCommand(COMMAND_RETR + " " + serverFileName);
        ReceiveMessage outBuf = new ReceiveMessage();
        code = getLastStatusCode(outBuf);

        if(!(code == 150 || code == 125)) {	// 2011-12-14
            String comment = "";
            if(code == 550){
                comment = outBuf.toString();
            } else{
                comment = "["+COMMAND_RETR + " Command Failed]";
            }
            throw new FTPException(comment, code);
        }
        Socket readSocket = accessor.getDataSocket();
        // FTPサーバからの接続要求待ちタイムアウト時間を設定
        //serverSocket.setSoTimeout(SERVERSOCKET_TIMEOUT);
        //readSocket = serverSocket.accept();
        //readSocket.setSoTimeout(SERVERSOCKET_TIMEOUT);

        //serverSocket.close();
		return new DataInputStreamEx(readSocket.getInputStream(), accessor);
    }
    /**
     * 対象ディレクトリのリストを返す
     * Parameter:
     * 	@param	target	対象のディレクトリ名
     *
     * Result:
     *	@return List	ディレクトリ名を保持するリスト
     */
    public List<String> ls(String target) throws FTPException, SocketTimeoutException {
        List<String> list = new ArrayList<String>();
        //ServerSocket serverSocket = getServerSocket(); T.Tateyama change 2012.10.28
        DataSocketAccessor accessor = createReadSocketAccessor();
        sendCommand(COMMAND_LIST + " " + target);

        code = getLastStatusCode(null);
        if (!(code == 150 || code == 125)) {		// 2012-10-28
            throw new FTPException("["+COMMAND_LIST + " Command Failed]", code);
        }

        try {
        	// T.Tateyama 2012.10.28 Start
            //serverSocket.setSoTimeout(SERVERSOCKET_TIMEOUT);
            //Socket readSocket = serverSocket.accept();
            //readSocket.setSoTimeout(SERVERSOCKET_TIMEOUT);
            Socket readSocket = accessor.getDataSocket();
            // T.Tateyama 2012.10.28 End
            BufferedReader reader =
                new BufferedReader(
                new InputStreamReader(readSocket.getInputStream()));
            String buf = null;
            while ( (buf = reader.readLine()) != null) {
                list.add(buf);
            }
            reader.close();
            // T.Tateyama 2012.10.28 Start
            accessor.close();
            //readSocket.close();
            //serverSocket.close();
            // T.Tateyama 2012.10.28 End
        } catch(SocketTimeoutException e){
            throw e;
        } catch (IOException e) {
            throw new FTPException(e);
        }

        if ( (code = getLastStatusCode(null)) != 226) {
            throw new FTPException("["+COMMAND_LIST + " Command Failed]", code);
        }
        return list;
    }

    /**
     * ftpのサーバからのリターン値を返す。
     * @param msg サーバから返されたメッセージを格納する変数 2012-10-28
     * @throws Exception
     * @return int ftpのリターン値
     */
    private int getLastStatusCode(ReceiveMessage msg) throws FTPException, SocketTimeoutException {
        String buffer = null;
        try {
            do {
                buffer = clientSocketReader.readLine();
                if (msg != null) {
                	msg.allMessage.append(buffer);
                	msg.lastMessage = buffer;
                }
            }
            while (buffer.charAt(3) == '-');
        } catch (SocketTimeoutException e){
            throw e;
        } catch (IOException e) {
            throw new FTPException(e);
        }
        logger.log(Level.FINE, "[RECV] " + buffer.toString());
        return Integer.parseInt(buffer.substring(0, 3));
    }

    /**
     * send Command to FTPServer.
     * @throws FTPException
     */
    private void sendCommand(String message) throws FTPException, SocketTimeoutException {
        try {
            clientSocketWriter.write(message + "\r\n");
            clientSocketWriter.flush();

            logger.log(Level.FINE, "[SEND] " + message.toString());
        } catch (SocketTimeoutException e){
            logger.log(Level.WARNING, "send command socket timeout", e);
            throw e;
        } catch (IOException e) {
            throw new FTPException(e);
        }
    }

    /**
     * ReadSocketAccessorを作成する。
     *
     * Parameters/Result:
     *　 @return
     *　 @throws FTPException
     * @throws SocketTimeoutException
     */
    protected DataSocketAccessor createReadSocketAccessor() throws FTPException, SocketTimeoutException {
    	DataSocketAccessor r = null;
    	switch (getDataConnectionMode()) {
    	case ACTIVE_MODE:
    		r = new DataSocketAccessor(getServerSocket());
    		break;
    	case PASSIVE_MODE:
    		r = new DataSocketAccessor(getSocket());
    		break;
    	default:
    		throw new FTPException("Data connection mode is unknown.");
    	}
    	return r;
    }

    /**
     * Socketを取得する。
     *
     * Parameters/Result:
     *　 @return
     *　 @throws FTPException
     *　 @throws SocketTimeoutException
     */
    protected Socket getSocket() throws FTPException, SocketTimeoutException {
    	Socket socket = null;
    	sendCommand(COMMAND_PASSIVE);
    	ReceiveMessage msg = new ReceiveMessage();
        if ((code = getLastStatusCode(msg)) != 227) {
            throw new FTPException("["+COMMAND_PASSIVE + "Command Failed]", code);
        }
        String work = msg.lastMessage;
        // ()内の文字列を取得
        work = work.substring(work.indexOf("(")+1); // 2012.11.14 bug
        work = work.substring(0, work.indexOf(")"));
        // カンマ区切りで数値化
        ArrayList<Integer> numList = new ArrayList<Integer>();
        for (String one : work.split(",")) {
        	numList.add(new Integer(one));
        }
        // IP, PORT番号を取得
        String ipAddr = new StringBuffer()
        					.append(numList.get(0)).append(".")
        					.append(numList.get(1)).append(".")
        					.append(numList.get(2)).append(".")
        					.append(numList.get(3)).toString();
        int portNo = numList.get(4) * 256 + numList.get(5);
        try {
	        // アドレスの作成
	        InetAddress addr = InetAddress.getByName(ipAddr);
	        socket = new Socket(addr, portNo);
	        socket.setSoTimeout(SERVERSOCKET_TIMEOUT);
        } catch (IOException e) {
            throw new FTPException(e);
        }
        return socket;
    }

    /**
     * 転送モードを設定する
     *
     * Parameters/Result:
     *　 @param newVal 転送モードを示す値を設定 ACTIVE_MODE/PASSIVE_MODE
     */
    public void setDataConnectionMode(int newVal) {
    	dataConnectionMode = newVal;
    }

    /**
     * 転送モードを返す。
     *
     * Parameters/Result:
     *　 @return 転送モードを示す値を返す。 ACTIVE_MODE, PASSIVE_MODE
     */
    public int getDataConnectionMode() {
    	return dataConnectionMode;
    }
}
