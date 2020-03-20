package jp.co.canon.cks.eec.fs.rssportal.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RSSToolInfo {

	/**
	 * 構成IDです。
	 */
	private String structId;	// 2011.11.29 modify by J.Tsuruta

	/**
	 * 装置名です。
	 */
	private String targetname;

	/**
	 * 装置種別です。
	 */
	private String targettype;

	/**
	 * 収集サーバのＩＤ
	 */
	private String collectServerId = "0";
	
	/**
	 * 収集サーバのホスト名
	 */
	private String collectHostName = null;
}
