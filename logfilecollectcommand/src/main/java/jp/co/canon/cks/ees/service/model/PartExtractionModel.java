/**
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : PartExtractionModel.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2010/12/03
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.ees.service.model;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *	場所を特定化するための情報を保持するインタフェース
 *
 * @author Tomomitsu TATEYAMA
 */
public interface PartExtractionModel {
	public static final int NORMAL_DIRECTION = 1;
	public static final int REVERSE_DIRECTION = 2;
	
	/**
	 * ディレクトリ位置を示す
	 * @return index	ディレクトリの位置を示す数値
	 */
	public int getTreeIndex();
	/**
	 * 方向を返す
	 * @return direction	NORMAL_DIRECTION　か　REVERSE_DIRECTION
	 */
	public int getDirection();
	/**
	 * 開始位置を返す。NORMAL_DIRECTIONであれば、行頭からの位置、REVERSE_DIRECTIONであれば
	 * 行末からの位置を返す。REVERSE_DIRECTIONの場合でもここで指定される位置は、getEndPos()よりも
	 * 左側の位置（値は大きい）となる。
	 * @return startPos	抽出する文字の開始位置
	 */
	public int getStartPos();
	/**
	 * 終了位置を返す。NORMAL_DIRECTIONであれば、行頭からの位置、REVERSE_DIRECTIONであれば
	 * 行末からの位置を返す。値が-1であれば、方向に関係なく行末指定となる。
	 * @return endPos	抽出する文字の終了位置
	 */
	public int getEndPos();
	/**
	 * 区切り文字を返す。
	 * これは、複数のPartExtractionModelを指定して各の違う文字を結合した際に
	 * それがわかるように、デリミタを挿入する機能である。最後に結合する文字に関しては、
	 * デリミタは、適用されない。
	 * @return delimiter	デリミタの文字を返す。
	 */
	public String getDelimiter();
}
