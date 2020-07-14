package jp.co.canon.cks.ees.service.lastpoint;

import java.util.Date;

/**
 * 
 * ログを表すインターフェースです。
 * 
 * @author Mitsuhiro Masuda
 *
 */
public interface ILogFile extends Comparable<ILogFile> {

	/**
	 * 
	 * ファイル名称を設定します。
	 * 
	 * @param fileName ファイル名称
	 * @throws Exception ファイル名称解析中にエラーが発生した
	 */
	public void setFName(String fileName) throws Exception;
	
	/**
	 * 
	 * ファイル名称を取得します。
	 * 
	 */
	public String getFName();

	/**
	 * 
	 * ファイル名称から特定される日時を返します。
	 * 
	 * @return ファイル名称から特定される日時
	 */
	public Date getFNameDate();
	
}
