/**
 *
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : test.java
 *
 * Author: Tomomitsu TATEYAMA
 * Date: 2012/01/19
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.fs.portal.bussiness;

import java.net.MalformedURLException;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *
 *　URM文字列を解析して、プロトコル、ホスト名、ポート番号、ファイル名をそれぞれ返す。
 *
 * @author Tomomitsu TATEYAMA
 * ============================================================================
 * 2012-10-31 T.TATEYAMA	 ユーザ、パスワード指定の追加
 */
public class CustomURL {
	/** URL先頭のデリミタ文字　 */
	protected static final String URL_DELIM1 = "://";
	/** URLデリミタ文字 */
	protected static final String URL_DELIM = "/";
	/** ポート番号指定デリミタ文字 */
	protected static final String PORT_DELIM = ":";
	/**  */
	private int port = -1;
	/**  */
	private String protocol = null;
	/**  */
	private String file = null;
	/**  */
	private String host;
	/** ログイン時ユーザ 2012.10.31 add */
	private String loginUser = null;
	/** ログイン時のパスワード 2012.10.31 add */
	private String loginPassword = null;

	// Add(19.4.19) <FSポータルのActive対応(CITS)>
	private String ftpMode = "";
	protected static final String ACTIVE_VAL = "active";
	protected static final String PASSIVE_VAL = "passive";

	/**
	 * URL文字列を指定して構築する
	 * @param urlStr
	 * @throws MalformedURLException
	 */
	public CustomURL(String urlStr) throws MalformedURLException {
		int pos1 = urlStr.indexOf(URL_DELIM1);
		protocol = urlStr.substring(0, pos1);
		pos1 += URL_DELIM1.length();
		int pos2 = urlStr.indexOf(URL_DELIM, pos1);
		String temp = urlStr.substring(pos1, pos2);
		pos1 = temp.indexOf(PORT_DELIM);
		if (pos1 != -1) {
			host = temp.substring(0, pos1);
			port = Integer.parseInt(temp.substring(pos1+1));
		} else {
			host = temp;
		}
		file = urlStr.substring(pos2);
		int authStrPos = file.lastIndexOf("<");
		if (authStrPos != -1) {
			String userpass = file.substring(authStrPos);
			file = file.substring(0, authStrPos);
			authStrPos = userpass.indexOf("/");
			loginUser = userpass.substring(1, authStrPos);
			loginPassword = userpass.substring(authStrPos + 1, userpass.indexOf(">"));

			// Add(19.4.19) <FSポータルのActive対応(CITS)>
			// ・従来、引数urlStrの末尾に"<user/password>"形式でauth情報が付加されていた.
			// ・<FS管理の定義ファイル読み込み対応>により、更にftpmode指定有の場合はその情報も付加される.
			// 　　"<user/password/ftpmmode>"
			int ftpmodeStrPos = loginPassword.lastIndexOf("/");
			if(ftpmodeStrPos != -1) {
				ftpMode = loginPassword.substring(ftpmodeStrPos + 1);
				if(!ACTIVE_VAL.equals(ftpMode) && !PASSIVE_VAL.equals(ftpMode))
					throw new MalformedURLException(urlStr);
				loginPassword = loginPassword.substring(0, ftpmodeStrPos);
			}
		}
	}
	/**
	 * プロトコルを返す。
	 *
	 * Parameters/Result:
	 *　 @return	プロトコルを示す文字列
	 */
	public String getProtocol() {
		return protocol;
	}
	/**
	 * ファイル名を返す。
	 *
	 * Parameters/Result:
	 *　 @return　パスを含んだファイル名を返す。
	 */
	public String getFile() {
		return file;
	}
	/**
	 * ホスト名を返す。
	 *
	 * Parameters/Result:
	 *　 @return	ホスト名
	 */
	public String getHost() {
		return host;
	}
	/**
	 * ポート番号を返す。　
	 * URLにポート番号が指定されていない場合は-1を返す。
	 *
	 * Parameters/Result:
	 *　 @return	ポート番号　未指定の場合は-1
	 */
	public int getPort() {
		return port;
	}

	/**
	 * ログインユーザを返す。 2012.10.31 add
	 *
	 * Parameters/Result:
	 * @return　ユーザ名
	 */
	public String getLoginUser() {
		return loginUser;
	}

	/**
	 * ログインパスワードを返す。 2012.10.31 add
	 *
	 * Parameters/Result:
	 * @return　パスワード文字列
	 */
	public String getLoginPassword() {
		return loginPassword;
	}

	/**
	 * パスを除いたファイル名を返す。
	 *
	 * Parameters/Result:
	 * @return
	 */
	public String getLastFileName() {
		int pos = getFile().lastIndexOf("/");
		if (pos != -1) {
			return getFile().substring(pos + 1);
		} else {
			return getFile();
		}
	}
	// Add(19.4.19) <FSポータルのActive対応(CITS)>
	/**
	 * FTP転送モードを返す。
	 *
	 * Parameters/Result:
	 *　 @return　FTP転送モード。
	 */
	public String getFtpMode() {
		return ftpMode;
	}
}
