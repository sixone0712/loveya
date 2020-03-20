/**
 * Title: Equipment Engineering Support System.
 *         - Log Upload Service
 *
 * File : FileListInfoEx.java
 *
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/01/07
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */
package jp.co.canon.cks.eec.fs.portal.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	FileListInfoにページの概念を追加した拡張クラス。
 *
 * @author Tomomitsu TATEYAMA
 */
public class FileListInfoEx extends FileListInfo {
	/**　シリアライズバージョン番号 */
	private static final long serialVersionUID = -2963257894414990201L;
	private int currentPage = 0;
	private int maxPage = 0;
	private int lineOfPage = 10;

	/**
	 * デフォルトコンストラクタ
	 */
	public FileListInfoEx() {
		setFileList(new ArrayList());
	}
	/**
	 * ページ数をカウントアップする
	 */
	public void nextPage() {
		if (currentPage + 1 < maxPage) {
			currentPage++;
		}
	}
	/**
	 * ページ数をカウントダウンする
	 */
	public void previousPage() {
		if (currentPage > 0) {
			currentPage--;
		}
	}
	/**
	 * ページを設定する
	 * Parameter:
	 * 	@param	newVal	新しいページ番号
	 */
	public void setPage(int newVal) {
		if (newVal  < 0 || newVal >= getMaxPage()) {
			return;
		}
		currentPage = newVal;
	}
	/**
	 * 現在のページ数を返す
	 * Result:
	 *	@return int	ページ数を返す　ページ数は０始まり
	 */
	public int getPage() {
		return currentPage;
	}
	/**
	 * 最大ページを返す。
	 * Result:
	 *	@return int	最大ページ数
	 */
	public int getMaxPage() {
		return maxPage;
	}
	/**
	 * １ページの行数を返す
	 * Result:
	 *	@return int	行数
	 */
	public int getLineOfPage() {
		return lineOfPage;
	}
	/**
	 * １ページの行数を設定する。
	 * Parameter:
	 * 	@param	newVal	１ページ毎の行数
	 */
	public void setLineOfPage(int newVal) {
		if (newVal <= 0) {
			throw new IllegalArgumentException("Lines in Page is invalid value." + newVal);
		}
		lineOfPage = newVal;
		update();
	}
	/**
	 * 内部データを更新する
	 */
	protected void update() {
		currentPage = 0;
		maxPage = (int) Math.ceil(getFileListCount() / (double) getLineOfPage());
	}
	/*
	 * setFileListの実装
	 * @see jp.co.canon.cks.eec.fs.portal.bean.FileListInfo#setFileList(java.util.List)
	 */
	public final void setFileList(List filelist) {
		super.setFileList(filelist);
		update();
	}
	/**
	 * ナビゲータを返す。
	 * Parameter:
	 * 	@param	url 遷移先のURLを設定
	 *  @param	max 最大表示数
	 *  @param	jscript	Java Scriptかどうかを返す。
	 * Result:
	 *	@return String　ナビゲータの文字列を返す。
	 */
	public String getNavigator(String url, int max, boolean jscript) {
		StringBuffer b = new StringBuffer();
		if (getFileListCount() > 0) {
			int half = max / 2;
			int firstP = 0, lastP = 0;
			if (getPage() > half) {
				if (getMaxPage() - half <= getPage()) {
					firstP = getMaxPage() - max;
					lastP = getMaxPage();
				} else {
					firstP = getPage() - half;
					lastP = getPage() + half + 1;
				}
			} else {
				firstP = 0;
				lastP = max;
			}
			// --- 最初
			if (firstP > 0) {
				b.append("<A href=\"").append(url).append("0");
				if (jscript) b.append(")");
				b.append("\">1</A>");
				if (firstP > 1) b.append("...");
				else b.append(" | ");
			}
			boolean first = true;
			for (int i = Math.max(firstP, 0); i < getMaxPage() && i < lastP; i++) {
				if (!first) b.append(" | ");
				if (getPage() == i) {
					b.append(i + 1);
				} else {
					b.append("<A href=\"").append(url).append(i);
					if (jscript) b.append(")");
					b.append("\">").append(i + 1).append("</A>");
				}
				first = false;
			}
			// --- 最後
			if (lastP < getMaxPage()) {
				if (lastP < getMaxPage() - 1) b.append("...");
				else b.append(" | ");
				b.append("<A href=\"").append(url).append(getMaxPage() - 1);
				if (jscript) b.append(")");
				b.append("\">").append(getMaxPage()).append("</A> ");
			}
		}
		return b.toString();
	}
}
