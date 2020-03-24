/**
 * 
 * File : FTPExecutionException.java
 * 
 * Description:
 *
 *
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/11/30
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.util.ftp;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	FTP内で発生するExceptionをこのクラスで表現する。
 *
 * @author Tomomitsu TATEYAMA
 */
public class FTPException extends java.io.IOException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7706600110015924622L;
    private int code;
    private String rawMessage = "";    // Add(05.06.16)

    private static Map<String, String> code2ErrorMap;

    static {
        code2ErrorMap = new HashMap<String, String>();
        code2ErrorMap.put("110", "Restart marker reply.");
        code2ErrorMap.put("120", "Service ready in nnn minutes.");
        code2ErrorMap.put("125", "Data connection already open; transfer starting.");
        code2ErrorMap.put("110", "Restart marker reply.");
        code2ErrorMap.put("110", "Restart marker reply.");
        code2ErrorMap.put("150", "File status okay; about to open data connection.");
        code2ErrorMap.put("200", "Command okay.");
        code2ErrorMap.put("202", "Command not implemented, superfluous at this site.");
        code2ErrorMap.put("211", "System status, or system help reply.");
        code2ErrorMap.put("212", "Directory status.");
        code2ErrorMap.put("213", "File status.");
        code2ErrorMap.put("214", "Help message.On how to use the server or the meaning of a particular non-standard command.");
        code2ErrorMap.put("215", "NAME system type. Where NAME is an official system name from the list in the Assigned Numbers document.");
        code2ErrorMap.put("220", "Service ready for new user.");
        code2ErrorMap.put("221", "Service closing control connection. Logged out if appropriate.");
        code2ErrorMap.put("225", "Data connection open; no transfer in progress.");
        code2ErrorMap.put("226", "Closing data connection.");
        code2ErrorMap.put("227", "Entering Passive Mode (h1,h2,h3,h4,p1,p2).");
        code2ErrorMap.put("230", "User logged in, proceed.");
        code2ErrorMap.put("250", "Requested file action okay, completed.");
        code2ErrorMap.put("257", "'PATHNAME' created.");
        code2ErrorMap.put("331", "User name okay, need password.");
        code2ErrorMap.put("332", "Need account for login.");
        code2ErrorMap.put("350", "Requested file action pending further information.");
        code2ErrorMap.put("421", "Service not available, closing control connection.");
        code2ErrorMap.put("425", "Can't open data connection.");
        code2ErrorMap.put("426", "Connection closed; transfer aborted.");
        code2ErrorMap.put("450", "Requested file action not taken.");
        code2ErrorMap.put("451", "Requested action aborted. Local error in processing.");
        code2ErrorMap.put("452", "Requested action not taken. Insufficient storage space in system.");
        code2ErrorMap.put("500", "Syntax error, command unrecognized.");
        code2ErrorMap.put("501", "Syntax error in parameters or arguments.");
        code2ErrorMap.put("502", "Command not implemented.");
        code2ErrorMap.put("503", "Bad sequence of commands.");
        code2ErrorMap.put("504", "Command not implemented for that parameter.");
        code2ErrorMap.put("530", "Not logged in.");
        code2ErrorMap.put("532", "Need account for storing files.");
        code2ErrorMap.put("550", "Requested action not taken.");
        code2ErrorMap.put("551", "Requested action aborted. Page type unknown.");
        code2ErrorMap.put("552", "Requested file action aborted.");
        code2ErrorMap.put("553", "Requested action not taken.File name not allowed.");
        code2ErrorMap.put("554", "");
        code2ErrorMap.put("555", "");
        code2ErrorMap.put("556", "");
    }

    /**
     * メッセージと種類を指定して構築する。
     * @param message	メッセージ文字列
     * @param code	種類
     */
    public FTPException(String message, int code) {
        super(message + code + " " + (String)code2ErrorMap.get(Integer.toString(code)));
        this.code = code;
        this.rawMessage = message;    // Add(05.06.16)
    }
    /**
     *  種類を返す
     * Result:
     *	@return int	種類
     */
    public int getType() {
        return code;
    }
    /**
     * Add(05.06.16)
     * @return String
     */
    public String getRawMessage() {
        return rawMessage;
    }

    public FTPException(String message) {
        super(message);
    }

    public FTPException(Throwable cause) {
        super(cause.getMessage());
    }

}
