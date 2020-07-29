/**
 *
 * Title: Equipment Engineering Support System. 
 *		   - Log Upload Service
 *
 * File : FileServiceDataModel.java
 * 
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/11/26
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.fs.portal.bussiness;

import jp.co.canon.cks.eec.fs.portal.bean.FileInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.LogInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.RequestListBean;

import java.util.Map;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 * 
 *　ファイルサービスは、ポータルが利用する、ファイルサービスの機能へのインタフェースを抽出したものである。
 *　機能はファイルサービス管理に要求する機能だけとなっている。
 *
 * @author Tomomitsu TATEYAMA
 * ============================================================================
 * 2013-06-30	T.TATEYAMA	ユーザ認証 コンポーネントＩＤを指定できる様に修正
 * 2013-06-30	T.TATEYAMA	要求一覧、完了一覧の戻り値をそれぞれRequestListBeanに変更
 */
@Deprecated
public interface FileServiceModel {
	/**
	 * ログアウトを実行する
	 *
	 * Parameters/Result:
	 *　 @param user	ユーザID
	 *　 @throws ServiceException	サービス要求時にエラーが例外した場合
	 */
    public void logout(String user) throws ServiceException;
    /**
     * ダウンロードする物理ファイル情報を取得する。
     *
     * Parameters/Result:
     *　 @param user		対象ユーザ
     *　 @param system	対象システム
     *　 @param tool		対象装置
     *　 @param reqNo		対象要求番号
     *　 @param fileName	対象ファイル名
     *　 @return	物理的なファイル位置を示すURLをかえす。
     *　 @throws ServiceException	サービス要求時にエラーが例外した場合
     */
    public String download(String user, String system, String tool, String reqNo, String fileName) throws ServiceException;
    /**
     * ユーザ認証を行う。
     *
     * Parameters/Result:
     *　 @param user	ユーザ名
     *　 @param password	パスワード
     *　 @param passType	パスワード文字列対応
     *　 @param compId コンポーネントID 2013-06-29 added
     *　 @return	ユーザ認証結果を数値で返す。0の場合は、正常認証　マイナスの場合はエラー
     *　 @throws ServiceException	サービス要求時にエラーが例外した場合
     */
    public int checkAuth(String user, String password, String passType, String compId) throws ServiceException;
    /**
     * 装置リストを作成する。装置リストには、OTS/DSS/ファイルサービス、共通サービス等も含まれる
     *
     * Parameters/Result:
     *　 @return	構成ID別の装置リストをマップで返す。
     *　 @throws ServiceException	サービス要求時にエラーが例外した場合
     */
    public Map createToolList() throws ServiceException;
    /**
     * 
     *	TODO This section is description for function;
     *
     * Parameters/Result:
     *　 @param tool
     *　 @return
     *　 @throws ServiceException	サービス要求時にエラーが例外した場合
     */
    public LogInfoBean[] createFileTypeList(String tool) throws ServiceException;
    /**
     * ファイルリストを作成する。
     *
     * Parameters/Result:
     *　 @param tool		装置ID
     *　 @param logType	ログ種別
     *　 @param calFrom	ファイルタイムスタンプの開始時間
     *　 @param calTo		ファイルタイムスタンプの終了時間
     *　 @param queryStr	ファイル名へのキーワード指定
     *　 @param dir		ディレクトリ指定
     *　 @return	対象装置の条件に一致するファイル一覧を返す。
     *　 @throws ServiceException	サービス要求時にエラーが例外した場合
     */
    public FileInfoBean[] createFileList(String tool, String logType, java.util.Calendar calFrom, java.util.Calendar calTo, String queryStr, String dir) throws ServiceException;
    /**
     * 収集要求を登録する
     *
     * Parameters/Result:
     *　 @param system		システムID
     *　 @param user				ユーザID
     *　 @param tool				装置ID
     *　 @param comment			コメント
     *　 @param logType			ログ種別
     *　 @param fileName			ファイル名一覧
     *　 @param fileSizes			ファイルサイズ一覧
     *　 @param fileTimestamps	ファイルタイムスタンプ一覧
     *　 @return　正常登録時に要求受付番号を返す。
     *　 @throws ServiceException	サービス要求時にエラーが例外した場合
     */
    public String registRequest(String system, String user, String tool, String comment, String logType, String[] fileName, long[] fileSizes, String[] fileTimestamps) throws ServiceException;
    /**
     * 指定した要求をキャンセルする。
     *
     * Parameters/Result:
     *　 @param user	ユーザID
     *　 @param tool	装置ID
     *　 @param reqNo	要求受付番号
     *　 @return　結果を数値で返す。　0:正常終了　マイナス数値：エラー
     *　 @throws ServiceException	サービス要求時にエラーが例外した場合
     */
    public int cancelRequest(String user, String tool, String reqNo) throws ServiceException;
    /**
     * 要求一覧を作成する。
     *
     * Parameters/Result:
     *　 @param system	対象システムID
     *　 @param tool		装置ID
     *　 @param reqNo		要求受付番号
     *　 @return	条件に一致する要求一覧を返す。  2013-06-29 型の変更
     *　 @throws ServiceException	サービス要求時にエラーが例外した場合
     */
    public RequestListBean createRequestList(String system, String tool, String reqNo) throws ServiceException;
    /**
     * 完了一覧を作成する。
     *
     * Parameters/Result:
     *　 @param system	対象システムID
     *　 @param tool		装置ID
     *　 @param reqNo		要求受付番号
     *　 @return　条件に一致する完了一覧を返す。 2013-06-29 型の変更
     *　 @throws ServiceException	サービス要求時にエラーが例外した場合
     */
    public RequestListBean createDownloadList(String system, String tool, String reqNo) throws ServiceException;
}
