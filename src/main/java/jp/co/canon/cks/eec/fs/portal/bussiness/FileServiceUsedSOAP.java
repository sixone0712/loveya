/**
 *
 * Title: Equipment Engineering Support System.
 *		   - Log Upload Service
 *
 * File : FileServiceUsedSOAP.java
 *
 * Author: Tomomitsu TATEYAMA
 * Date: 2011/11/27
 *
 * Copyright(C) Canon Inc. All right reserved.
 *
 */

package jp.co.canon.cks.eec.fs.portal.bussiness;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jp.co.canon.cks.eec.fs.manage.DownloadInfoModel;
import jp.co.canon.cks.eec.fs.manage.DownloadListModel;
import jp.co.canon.cks.eec.fs.manage.FileInfoModel;
import jp.co.canon.cks.eec.fs.manage.FileServiceManage;
import jp.co.canon.cks.eec.fs.manage.FileServiceManageServiceLocator;
import jp.co.canon.cks.eec.fs.manage.FileTypeModel;
import jp.co.canon.cks.eec.fs.manage.RequestInfoModel;
import jp.co.canon.cks.eec.fs.manage.RequestListModel;
import jp.co.canon.cks.eec.fs.manage.ServerErrorInfo;
import jp.co.canon.cks.eec.fs.manage.ToolInfoModel;
import jp.co.canon.cks.eec.fs.portal.bean.FileInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.LogInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.RequestInfoBean;
import jp.co.canon.cks.eec.fs.portal.bean.RequestListBean;
import jp.co.canon.cks.eec.fs.portal.bean.ToolInfo;

/**
 * <p>Title:</p> File upload Service
 * <p>Description:</p>
 *
 *　ファイルサービス管理へSOAPを使って問合せを行い、FileServiceModelの機能を実現する。
 *
 * @author Tomomitsu TATEYAMA
 * ====================================================================
 * Change History:
 * 2011-11-30 「暫定対応」createDownloadList()にて、配列がヌルの場合の対応を追加した。
 * 2011-12-01	T.OKUMURA	createFileList()で、「/」が「\」に置き換わるため、setPath(src[i].getName())に変更した。
 * 2011-12-02	J.Tsuruta	AXIS1.1で生成したWSDLから生成したクライアントへ対応して配列の取得箇所を修正
 * 2011-12-05	T.OKUMURA	createRequestList()で、オブジェクトのヌル判定間違いを修正した。　 結合不具合
 * 2012-03-07	T.Tateyama	ポートをコンストラクタで指定できる様に修正
 * 2012-10-31	T.TATEYAMA	コメントの追記, 完了一覧の作成でアーカイブ情報を修正
 * 2012-11-26	T.TATEYAMA	完了一覧でファイルの数にアーカイブファイルが含まれる不具合の修正
 * 2013-06-30	T.TATEYAMA	ファイルサービス管理のサービスインタフェース変更に伴う修正
 * 2013-07-17	T.TATEYAMA	Arrays→ArrayListに変更　（未実装のメソッドでエラーが発生）
 */
public class FileServiceUsedSOAP implements FileServiceModel {

	private FileServiceManage manager = null;
	private static final String FILE_SELECT_IN_DIR_PAGE = "FileListSelectInDirectory";
	private static final String FILE_SELECT_PAGE = "FileListSelect";
	private String ipaddress = null;
	
	/**
	 * デフォルトコンストラクタ
	 */
	public FileServiceUsedSOAP(String addr) {
		ipaddress = addr;
	}
	/**
	 * サービスのURLを返す。必要に応じ手オーバライドする。
	 *
	 * Parameters/Result:
	 *　 @return	サービスのURL
	 *　 @throws MalformedURLException	URL不正の場合
	 */
	protected URL getServiceURL() throws MalformedURLException {
		if (ipaddress == null || ipaddress.length() == 0) {
			return new URL("http://localhost/FileServiceManage/services/FileServiceManage");
		} else {
			StringBuffer b = new StringBuffer();
			b.append("http://").append(ipaddress)
					.append("/FileServiceManage/services/FileServiceManage");
			return new URL(b.toString());
		}
	}
	/**
	 *　ファイルサービス管理のSOAPサービスを返す。
	 *
	 * Parameters/Result:
	 *　 @return　FileServiceManageを返す。
	 *　 @throws MalformedURLException	URL不正の場合
	 *　 @throws javax.xml.rpc.ServiceException	RPC呼び出しで例外が発生した場合
	 */
	protected synchronized FileServiceManage getFileServiceManage() throws MalformedURLException, javax.xml.rpc.ServiceException {
		if (manager == null) {
			FileServiceManageServiceLocator serviceLocator = new FileServiceManageServiceLocator();
			manager = serviceLocator.getFileServiceManage(getServiceURL());
		}
		return manager;
	}
	/*
	 * logoutの実装
	 * @see jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel#logout(java.lang.String)
	 */
	public void logout(String user) throws ServiceException {
		try {
			 getFileServiceManage().logout(user);
		} catch (Throwable ex) {
			throw new ServiceException(ex);
		}
	}

	/*
	 * downloadの実装
	 * @see jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel#download(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String download(String user, String system, String tool, String reqNo, String fileName) throws ServiceException {
		try {
			 return getFileServiceManage().download(user, system, tool, reqNo, fileName);
		} catch (Throwable ex) {
			throw new ServiceException(ex);
		}
	}

	/*
	 * checkAuthの実装
	 * @see jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel#checkAuth(java.lang.String, java.lang.String, java.lang.String)
	 */
	public int checkAuth(String user, String password, String passType, String compId) throws ServiceException {
		try {
			return getFileServiceManage().checkAuth(user, password, passType, compId);
		} catch (Throwable ex) {
			throw new ServiceException(ex);
		}
	}

	/*
	 * createToolListの実装
	 * @see jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel#createToolList()
	 */
	public Map createToolList() throws ServiceException {
		try {
			HashMap map = new HashMap();
			ToolInfoModel[] toolModels = getFileServiceManage().createToolList();

			if (toolModels == null) toolModels = new ToolInfoModel[0];	// 2011.11.29 add by J,Tsuruta

			ToolInfo[] tools = new ToolInfo[toolModels.length];
			for (int i = 0; i < toolModels.length; i++) {
				tools[i] = new ToolInfo();
				tools[i].setStructId(toolModels[i].getStructId());	// 2011.11.29 modify by J.Tsuruta
				tools[i].setToolName(toolModels[i].getName());
				tools[i].setToolType(toolModels[i].getType());
				if (!map.containsKey(toolModels[i].getStructId())) {
					map.put(toolModels[i].getStructId(), new ArrayList());
				}
				((List)map.get(toolModels[i].getStructId())).add(tools[i]);
			}
			return map;
		} catch (Throwable ex) {
			throw new ServiceException(ex);
		}
	}

	/*
	 * createFileTypeListの実装
	 * @see jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel#createFileTypeList(java.lang.String)
	 */
	public LogInfoBean[] createFileTypeList(String tool) throws ServiceException {
		try {
			 FileTypeModel[] ftList = getFileServiceManage().createFileTypeList(tool);

			 if (ftList == null) ftList = new FileTypeModel[0];	// 2011.11.29 add by J,Tsuruta

			 LogInfoBean[] r = new LogInfoBean[ftList.length];
			 for (int i = 0; i < ftList.length; i++) {
				 r[i] = new LogInfoBean();
				 r[i].setCode(ftList[i].getLogType());
				 r[i].setName(ftList[i].getDataName());
				 int v = Integer.parseInt(ftList[i].getSearchType());
				 switch((v & 0x03)) {
				 case 3:
					 r[i].setType(0);
					 break;
				 case 1:
					 r[i].setType(1);
					 break;
				 case 2:
					 r[i].setType(2);
					 break;
				 default:
					 r[i].setType(3);
				 }
				 if ((v & 0x10) == 0x10) {
					 r[i].setFileListForwarding(FILE_SELECT_IN_DIR_PAGE);
				 } else {
					 r[i].setFileListForwarding(FILE_SELECT_PAGE);
				 }
			 }
			 return r;
		} catch (Throwable ex) {
			throw new ServiceException(ex);
		}
	}
	/**
	 * 日付フォーマットを作成し返す。
	 *
	 * Parameters/Result:
	 * @return　DateFormatのインスタンス
	 */
	private DateFormat createDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	/*
	 * createFileListの実装
	 * @see jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel#createFileList(java.lang.String, java.lang.String, java.util.Calendar, java.util.Calendar, java.lang.String, java.lang.String)
	 */
	public FileInfoBean[] createFileList(String tool, String logType, Calendar calFrom, Calendar calTo, String queryStr,
			String dir) throws ServiceException {
		try {
			DateFormat frm = createDateFormat();
			FileInfoModel[] src = getFileServiceManage().createFileList( tool,  logType,  calFrom,  calTo,  queryStr, dir);

			if (src == null) src = new FileInfoModel[0];	// 2011.11.29 add by J,Tsuruta

			FileInfoBean[] dest = new FileInfoBean[src.length];
			for (int i = 0; i < dest.length; i++) {
				File f = new File(src[i].getName());
				dest[i] = new FileInfoBean();
				dest[i].setFile(src[i].getType().equals("F"));
				dest[i].setId(0);
				dest[i].setLogId(logType);
				dest[i].setName(f.getName());
				// 2011.12.01 「/」から「\」に置換されるため文字列そのまま設定する。
				//dest[i].setPath(f.getPath());
				dest[i].setPath(src[i].getName());
				dest[i].setSize(src[i].getSize());
				dest[i].setTimestamp(frm.format(src[i].getTimestamp().getTime()));
				dest[i].setStatus("");
			}
			return dest;
		} catch (Throwable ex) {
			throw new ServiceException(ex);
		}
	}

	/*
	 * registRequestの実装
	 * @see jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel#registRequest(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String[], long[], java.lang.String[])
	 */
	public String registRequest(String system, String user, String tool, String comment, String logType, String[] fileName,
			long[] fileSizes, String[] fileTimestamps) throws ServiceException {
		try {
			Calendar[] calList = null;
			if (fileTimestamps != null) {
				DateFormat f = createDateFormat();
				calList = new Calendar[fileTimestamps.length];
				for (int i=0; i < fileTimestamps.length; i++) {
					calList[i] = Calendar.getInstance();
					calList[i].setTime(f.parse(fileTimestamps[i]));
				}
			}
			return getFileServiceManage().registRequest(system, user, tool, comment, logType, fileName, fileSizes, calList);
		} catch (Throwable ex) {
			throw new ServiceException(ex);
		}
	}

	/*
	 * cancelRequestの実装
	 * @see jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel#cancelRequest(java.lang.String, java.lang.String, java.lang.String)
	 */
	public int cancelRequest(String user, String tool, String reqNo) throws ServiceException {
		try {
			return getFileServiceManage().cancelRequest(user, tool, reqNo);
		} catch (Throwable ex) {
			throw new ServiceException(ex);
		}
	}

	/**
	 * サーバエラー情報からエラーメッセージを作成する。
	 *
	 * Parameters/Result:
	 * @param v サーバエラー情報
	 * @return 出力行単位のエラーメッセージ
	 */
	private ToolInfo[] createErrorMessages(ServerErrorInfo[] v) {
		if (v != null && v.length > 0) {
			ArrayList<ToolInfo> l = new ArrayList<ToolInfo>();
			for (ServerErrorInfo info : v) {
				for (String tool : info.getToolList()) {
					ToolInfo x = new ToolInfo();
					x.setToolName(tool);
					x.setCollectHostName(info.getHostName());
					x.setCollectServerId(info.getId());
					l.add(x);
				}
			}
			ToolInfo[] result = l.toArray(new ToolInfo[0]);
			// ソート
			Arrays.sort(result, new Comparator<ToolInfo>() {
				public int compare(ToolInfo o1, ToolInfo o2) {
					return o1.getToolName().compareTo(o2.getToolName());
				}	
			});
			return result;
		}
		return new ToolInfo[0];
	}
	/*
	 * createRequestListの実装
	 * @see jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel#createRequestList(java.lang.String, java.lang.String, java.lang.String)
	 */
	public RequestListBean createRequestList(String system, String tool, String reqNo) throws ServiceException {
		try {
			SimpleDateFormat frm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			RequestListModel list = getFileServiceManage().createRequestList(system, tool, reqNo);
			RequestInfoModel[] src = list.getRequestInfos();

			if (src == null) src = new RequestInfoModel[0];	// 2011.11.29 add by J,Tsuruta

			RequestInfoBean[] dest = new RequestInfoBean[src.length];
			for (int i = 0; i < dest.length; i++) {
				dest[i] = new RequestInfoBean();
				dest[i].setDate(frm.format(src[i].getDateTime().getTime()));
				dest[i].setDenominator(src[i].getFiles().length);

				dest[i].setNumerator(src[i].getStatus());
				dest[i].setDescription(src[i].getComment());
				dest[i].setRequestNo(src[i].getRequestNo());
				dest[i].setTool(src[i].getToolId());
				dest[i].setUser(src[i].getRequestUser());
				if (src[i].getStatus() == 0) {
					dest[i].setStatus("Waiting");
				} else {
					dest[i].setStatus("Collecting");
				}
				long totalSize = 0;
				ArrayList l = new ArrayList();
				if (src[i].getFiles() != null && 0 < src[i].getFiles().length) {	// 2011.12.05 ヌル判定間違い
					for (int n = 0; n < src[i].getFiles().length; n++) {
						FileInfoBean bean = new FileInfoBean();
						bean.setName(src[i].getFiles()[n].getName());
						bean.setSize(src[i].getFiles()[n].getSize());
						bean.setTimestamp(frm.format(src[i].getFiles()[n].getTimestamp().getTime()));
						totalSize += bean.getSize();
						l.add(bean);
					}
				}
				dest[i].setFileList(l);
				dest[i].setArchiveFileSize(totalSize);
			}
			RequestListBean result = new RequestListBean();
			result.setRequestList(new ArrayList(Arrays.asList(dest)));
			result.setDisconnectTools(createErrorMessages(list.getServerErrors()));
			return result;
		} catch (Throwable ex) {
			throw new ServiceException(ex);
		}
	}
	/*
	 * createDownloadListの実装
	 * @see jp.co.canon.cks.eec.fs.portal.bussiness.FileServiceModel#createDownloadList(java.lang.String, java.lang.String, java.lang.String)
	 */
	public RequestListBean createDownloadList(String system, String tool, String reqNo) throws ServiceException {
		try {
			DateFormat frm = createDateFormat();
			DownloadListModel list = getFileServiceManage().createDownloadList(system, tool, reqNo);
			DownloadInfoModel[] src = list.getDownloadInfos(); // getFileServiceManage().createDownloadList(system, tool, reqNo);

			if (src == null) src = new DownloadInfoModel[0]; // 2011.11.29 add
																// by J,Tsuruta

			RequestInfoBean[] dest = new RequestInfoBean[src.length];
			for (int i = 0; i < dest.length; i++) {
				dest[i] = new RequestInfoBean();
				dest[i].setDate(frm.format(src[i].getCollectTime().getTime()));
//				if (src[i].getFiles() != null && 0 < src[i].getFiles().length) {
//					dest[i].setDenominator(src[i].getFiles().length);
//				} else {
//				    dest[i].setDenominator(0);
//				}
//                if (src[i].getFiles() != null && 0 < src[i].getFiles().length) {
//					dest[i].setNumerator(src[i].getFiles().length);
//                } else {
//                    dest[i].setNumerator(0);
//                }
				dest[i].setDescription(src[i].getComment());
				dest[i].setRequestNo(src[i].getRequestNo());
				dest[i].setTool(src[i].getToolId());
				dest[i].setErrorMsg(src[i].getErrorMessage());
				dest[i].setUser(src[i].getRequestUser());
				dest[i].setLastModifiedTime(src[i].getCollectTime().getTimeInMillis());
				dest[i].setStatus("completed");
				//long totalSize = 0; 2012-10-31 delete
				ArrayList l = new ArrayList();
				if (src[i].getFiles() != null && 0 < src[i].getFiles().length) {
					String archiveFileName = src[i].getRequestNo()+".zip";
					for (int n = 0; n < src[i].getFiles().length; n++) {
						// アーカイブファイル名であればリストに追加しない 2012.10.31 add
						if (src[i].getFiles()[n].getName().equalsIgnoreCase(archiveFileName)) {
							dest[i].setArchiveFileSize(src[i].getFiles()[n].getSize());		
							dest[i].setArchiveFileName(src[i].getFiles()[n].getName());
							continue;
						}
						// --------
						FileInfoBean bean = new FileInfoBean();
						bean.setName(src[i].getFiles()[n].getName());
						bean.setSize(src[i].getFiles()[n].getSize());
						if (src[i].getFiles()[n].getTimestamp() != null) {
							bean.setTimestamp(frm.format(src[i].getFiles()[n].getTimestamp().getTime()));
						}
						//totalSize += bean.getSize(); 2012-10-31 delete
						l.add(bean);
					}
					// 2012.11.26 bug fix
					dest[i].setDenominator(l.size());
					dest[i].setNumerator(l.size());
				} else {
				    dest[i].setDenominator(0);
                    dest[i].setNumerator(0);					
				}
				dest[i].setFileList(l);
			}
			//-----------------------
			RequestListBean result = new RequestListBean();
			result.setRequestList(new ArrayList(Arrays.asList(dest)));
			result.setDisconnectTools(createErrorMessages(list.getServerErrors()));
			return result;
		} catch (Throwable ex) {
			throw new ServiceException(ex);
		}
	}

}
