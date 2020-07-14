package jp.co.canon.ckbs.eec.fs.collect.model;

import java.util.Map;

public interface ToolLogCollectUrlMapModel {
	
	/**
	 * 装置名を返す。
	 *
	 * Parameters/Result:
	 *　 @return	装置名を示す文字列
	 */
	public String getToolName();
	/**
	 * 装置種別を返す。
	 *
	 * Parameters/Result:
	 *　 @return	装置種別を示す文字列
	 */
	public String getToolType();
	/**
	 * ログIDとURLをマッピングしたMapを返す。
	 *
	 * Parameters/Result:
	 *　 @return	キーとしてログID、値として、URL情報を保持するMapを返す。
	 */
	public Map<String, LogUrlModel> getLogUrlMap();
	/**
	 * 装置ログ収集ファイルのタイムスタンプを返す。
	 *
	 * Parameters/Result:
	 *　 @return	ファイル更新時間を示すlong値
	 */
	public long getTimestamp();
}
