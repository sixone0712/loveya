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

package jp.co.canon.cks.ees.service.ftp;


import java.io.*;
import java.net.*;
import java.util.*;

import java.util.logging.*;


/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	このクラスは、FTPの実際に行うクラスを定義する。ASCIIには対応しない。
 *
 * @author Tomomitsu TATEYAMA
 * ================================================================
 * 2011-12-14	T.Tateyama	IIS FTPサーバー用に正常判断のコード125を追加する
 * 2012-01-29 	T.Tateyama	ポート番号を指定できる様に修正する
 * 2012-10-28 	T.Tateyama 	passiveモード対応
 * 2012-11-14	T.Tateyama 	passiveモードのIPアドレスへの変換で文字列操作の不具合を修正
 * 2015-04-14	T.TAKIGUCHI	ftpコマンドでリスト情報を取得する際に取得した情報をInputStreamで返すようlsメソッドを修正
 */
public class FTP {
	protected static final Logger logger = Logger.getLogger(FTP.class.getName());
    /**
     * SeverSocket#accept()時のタイムアウト値(ミリ秒単位)
     */
    protected static final int SERVERSOCKET_TIMEOUT = 60000;
    /**
     * コマンド一覧 
     */
    protected final String COMMAND_RETR = "RETR";
    protected final String COMMAND_QUIT = "QUIT";
    protected final String COMMAND_USER = "USER";
    protected final String COMMAND_PASS = "PASS";
    protected final String COMMAND_CWD  = "CWD";
    //private final String COMMAND_PWD  = "PWD";
    protected final String COMMAND_PORT = "PORT";
    protected final String COMMAND_LIST = "LIST";
    protected final String COMMAND_BINARY = "TYPE I";
    protected final String COMMAND_SITE = "SITE";
    // 2012-10-28 passive対応
    protected final String COMMAND_PASSIVE = "PASV";

    public static final int ACTIVE_MODE = 1;
    public static final int PASSIVE_MODE = 2;
    /** モード状態 */
    private int dataConnectionMode = ACTIVE_MODE;
    
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
     * ホスト名とポート番号を指定して構築する
     * @param host	ホスト名／IPアドレス
     * @param pno	ポート番号（-1はデフォルト扱い）
     */
    public FTP(String host, int pno) {
    	hostName = host;
    	if (pno > 0) {
    		PORT = pno;
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

    	//ServerSocket serverSocket = getServerSocket(); T.Tateyama change 2012.10.28
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
        // 2012.10.28 T.Tateyama start
        Socket readSocket = accessor.getDataSocket();
        // FTPサーバからの接続要求待ちタイムアウト時間を設定
        //serverSocket.setSoTimeout(SERVERSOCKET_TIMEOUT);
        //readSocket = serverSocket.accept();
        //readSocket.setSoTimeout(SERVERSOCKET_TIMEOUT);

        //serverSocket.close();
        // 2012.10.28 T.Tateyama end
		return new DataInputStreamEx(readSocket.getInputStream(), accessor);
    }
    /**
     * 対象ディレクトリのリストを返す
     * Parameter:
     * 	@param	target	対象のディレクトリ名
     *
     * Result:
     *	@return InputStream	ディレクトリ名を保持するInputStream
     */
    public InputStream ls(String target) throws FTPException, SocketTimeoutException, SocketException, IOException {

        DataSocketAccessor accessor = createReadSocketAccessor();
        ReceiveMessage outBuf = new ReceiveMessage();
        cd(target);
        sendCommand(COMMAND_LIST + " -a");
        code = getLastStatusCode(null);
        if(!(code == 150 || code == 125)) {
            String comment = "";
            if(code == 550){
                comment = outBuf.toString();
            } else{
                comment = "["+COMMAND_LIST + " Command Failed]";
            }
            throw new FTPException(comment, code);
        }

        logger.info("Finish FTP Command");

        Socket readSocket = accessor.getDataSocket();

        return new DataInputStreamEx(readSocket.getInputStream(), accessor);
    }

    /**
     * サーバ側のステータスコードを返す。
     * @param msg サーバから返されたメッセージを格納する変数 2012-10-28
     * @throws Exception
     * @return int
     */
    protected int getLastStatusCode(ReceiveMessage msg) throws FTPException, SocketTimeoutException {
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
        logger.info(buffer.toString());
        return Integer.parseInt(buffer.substring(0, 3));
    }
    /**
     * send Command to FTPServer.
     * @throws FTPException
     */
    protected void sendCommand(String message) throws FTPException, SocketTimeoutException {
        try {
            clientSocketWriter.write(message + "\r\n");
            clientSocketWriter.flush();
        } catch (SocketTimeoutException e){
            logger.log(Level.WARNING, "send command socket timeout", e);
            throw e;
        } catch (IOException e) {
            throw new FTPException(e);
        }
    }
    
    /**
     * SITEコマンドを送信します。
     * 
     * @param siteOption SITEコマンドのオプション文字列
     * @throws SocketTimeoutException
     * @throws FTPException
     */
    public void site(String siteOption) throws SocketTimeoutException, FTPException{
		sendCommand(COMMAND_SITE + " " + siteOption);
        if ((code = getLastStatusCode(null)) != 200) {
            throw new FTPException("[Site Command Failed]", code);
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
        work = work.substring(work.indexOf("(") + 1); // 2012.11.14 bug
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
