/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : DefaultPartExtractionModel.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/04
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	PartExtractionModelのデフォルト実装
 *
 * @author Tomomitsu TATEYAMA
 */
public class DefaultPartExtractionModel implements PartExtractionModel {
	private int treeIndex = -1;
	private int direction = NORMAL_DIRECTION;
	private int startPos = 0;
	private int endPos = -1;
	private String delimiter = null;
	
	/**
	 * デフォルトコンストラクタ
	 */
	public DefaultPartExtractionModel() {
	}
	/**
	 *	設定されているtreeIndexを返す。
	 * Return:
	 * @return treeIndex
	 */
	public int getTreeIndex() {
		return treeIndex;
	}
	/**
	 * treeIndexを設定する。
	 * Parameters:
	 * @param treeIndex セットする treeIndex
	 */
	public void setTreeIndex(int treeIndex) {
		this.treeIndex = treeIndex;
	}
	/**
	 *	設定されているdirectionを返す。
	 * Return:
	 * @return direction
	 */
	public int getDirection() {
		return direction;
	}
	/**
	 * directionを設定する。
	 * Parameters:
	 * @param direction セットする direction
	 */
	public void setDirection(int direction) {
		if (direction != NORMAL_DIRECTION && direction != REVERSE_DIRECTION) {
			throw new IllegalArgumentException("parameter is not support number:" + direction);
		}
		this.direction = direction;
	}
	/**
	 *	設定されているstartPosを返す。
	 * Return:
	 * @return startPos
	 */
	public int getStartPos() {
		return startPos;
	}
	/**
	 * startPosを設定する。
	 * Parameters:
	 * @param startPos セットする startPos
	 */
	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}
	/**
	 *	設定されているendPosを返す。
	 * Return:
	 * @return endPos
	 */
	public int getEndPos() {
		return endPos;
	}
	/**
	 * endPosを設定する。
	 * Parameters:
	 * @param endPos セットする endPos
	 */
	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}
	/**
	 *	設定されているdelimiterを返す。
	 * Return:
	 * @return delimiter
	 */
	public String getDelimiter() {
		return delimiter;
	}
	/**
	 * delimiterを設定する。
	 * Parameters:
	 * @param delimiter セットする delimiter
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
}
