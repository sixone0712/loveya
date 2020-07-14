package jp.co.canon.ckbs.eec.fs.collect.model;

public interface LogCommandDefinitionModel {
	/**
	 * 装置ログを取得するコマンド文字列を返す。
	 *
	 * Parameters/Result:
	 *　 @return
	 */
	public String getCommand();
	/**
	 * 定期収集実行時の装置ログを取得するコマンド文字列を返す。
	 *
	 * Parameters/Result:
	 *　 @return
	 */
	public String getCommandCA();
	/**
	 * 装置ログIDを返す
	 *
	 * Parameters/Result:
	 *　 @return
	 */
	public String getLogId();
	/**
	 * ログファイルのルートURL情報を返す。
	 *
	 * Parameters/Result:
	 *　 @return	ログファイルのルートURL情報をLogUrlModelで返す。
	 */
	public LogUrlModel getLogUrl();

}
