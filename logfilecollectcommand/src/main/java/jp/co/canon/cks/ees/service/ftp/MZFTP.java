package jp.co.canon.cks.ees.service.ftp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.zip.InflaterInputStream;

public class MZFTP extends FTP {

	protected final String COMMAND_MODEZ = "MODE Z";
	protected final String COMMAND_MODES = "MODE S";
	
	public MZFTP(String host, int pno) {
		super(host, pno);
	}

	public InputStream ls(String target) throws FTPException, SocketTimeoutException, SocketException, IOException {
		callModeS(); // 圧縮転送をやめる
		return super.ls(target);
	}
	/* VFTPにおいて、LISTコマンドはMODE Zに非対応となった（問題が多いため）ので、MODE Sに変更する
	public List<String> ls(String target) throws FTPException, SocketTimeoutException {
        List<String> list = new ArrayList<String>();   // 返信用のリスト
        ServerSocket serverSocket = getServerSocket(); // サーバーソケットの準備
        
        callModeZ(); // 圧縮転送を開始する。
        
        cd(target);
        sendCommand(COMMAND_LIST + " -a");
        int code = getLastStatusCode(null);
        if (!(code == 150 || code == 125)) {		// 2011-12-14
            throw new FTPException("["+COMMAND_LIST + " Command Failed]", code);
        }

        try {
            serverSocket.setSoTimeout(SERVERSOCKET_TIMEOUT);
            Socket readSocket = serverSocket.accept();
            readSocket.setSoTimeout(SERVERSOCKET_TIMEOUT);
            
            // ストリームがなかった場合は、エラーとなるため、Inflate処理しないようにする。
            BufferedInputStream bis = new BufferedInputStream(readSocket.getInputStream());
            bis.mark(1);
            if(bis.read() != -1){
            	bis.reset();
                BufferedReader reader =
                    new BufferedReader(
                    new InputStreamReader(new InflaterInputStream(bis)));
                String buf = null;
                while ( (buf = reader.readLine()) != null) {
                    list.add(buf);
                }            	
                reader.close();
            }
            
            readSocket.close();
            serverSocket.close();
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
	*/
	
	public InputStream openFileStream(String serverFileName)
			throws FTPException, SocketTimeoutException, SocketException,
			IOException {
        callModeZ(); // 圧縮転送を開始する。
    	//ServerSocket serverSocket = getServerSocket();
        DataSocketAccessor accessor = createReadSocketAccessor();
        sendCommand(COMMAND_RETR + " " + serverFileName);
        ReceiveMessage outBuf = new ReceiveMessage();
        int code = getLastStatusCode(outBuf);

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
        
        // ストリームがなかった場合は、エラーとなるため、Inflate処理しないようにする。
        BufferedInputStream bis = new BufferedInputStream(readSocket.getInputStream());
        bis.mark(1);
        if(bis.read() != -1){
        	bis.reset();
        	return new DataInputStreamEx(new InflaterInputStream(bis), accessor);
        } else {
        	bis.reset();
        	return new DataInputStreamEx(bis, accessor);        	
        }
	}
	
	private void callModeZ() throws SocketTimeoutException, FTPException{
        sendCommand(COMMAND_MODEZ);
        int code = getLastStatusCode(null);
        if (code!= 200) {
            throw new FTPException("["+COMMAND_MODEZ + "Command Failed]", code);
        }       
	}

	private void callModeS() throws SocketTimeoutException, FTPException{
        sendCommand(COMMAND_MODES);
        int code = getLastStatusCode(null);
        if (code!= 200) {
            throw new FTPException("["+COMMAND_MODES + "Command Failed]", code);
        }       
	}
}
