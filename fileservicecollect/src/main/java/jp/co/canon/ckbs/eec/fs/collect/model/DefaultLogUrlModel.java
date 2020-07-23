package jp.co.canon.ckbs.eec.fs.collect.model;

import lombok.Getter;
import lombok.Setter;

public class DefaultLogUrlModel implements LogUrlModel {
	/** URL文字列 */
	@Getter @Setter
	private String urlString = null;
	/** ユーザID */
	@Getter @Setter
	private String userId = null;
	/** パスワード */
	@Getter @Setter
	private String password = null;

	/** FTPモード */
	private static final String PASV = "passive";
	private static final String ACTV = "active";

	@Getter
	private String ftpmode = PASV;

	public void setFtpmode(String mode){
		if (mode == null){
			return;
		}
		if (mode.equalsIgnoreCase(PASV)){
			ftpmode = PASV;
			return;
		}
		if (mode.equalsIgnoreCase(ACTV)){
			ftpmode = ACTV;
			return;
		}
	}
}
