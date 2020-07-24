/**
 * FileServiceManage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package jp.co.canon.cks.eec.fs.manage;

@Deprecated
public interface FileServiceManage extends java.rmi.Remote {
    public int checkAuth(java.lang.String user, java.lang.String password, java.lang.String passType, java.lang.String compId) throws java.rmi.RemoteException;
    public jp.co.canon.cks.eec.fs.manage.ToolInfoModel[] createToolList() throws java.rmi.RemoteException;
    public jp.co.canon.cks.eec.fs.manage.FileTypeModel[] createFileTypeList(java.lang.String tool) throws java.rmi.RemoteException;
    public jp.co.canon.cks.eec.fs.manage.FileInfoModel[] createFileList(java.lang.String tool, java.lang.String logType, java.util.Calendar calFrom, java.util.Calendar calTo, java.lang.String queryStr, java.lang.String dir) throws java.rmi.RemoteException;
    public java.lang.String registRequest(java.lang.String system, java.lang.String user, java.lang.String tool, java.lang.String comment, java.lang.String logType, java.lang.String[] fileNames, long[] fileSizes, java.util.Calendar[] fileTimestamps) throws java.rmi.RemoteException;
    public int cancelRequest(java.lang.String user, java.lang.String tool, java.lang.String reqNo) throws java.rmi.RemoteException;
    public jp.co.canon.cks.eec.fs.manage.RequestListModel createRequestList(java.lang.String system, java.lang.String tool, java.lang.String reqNo) throws java.rmi.RemoteException;
    public jp.co.canon.cks.eec.fs.manage.DownloadListModel createDownloadList(java.lang.String system, java.lang.String tool, java.lang.String reqNo) throws java.rmi.RemoteException;
    public java.lang.String download(java.lang.String user, java.lang.String system, java.lang.String tool, java.lang.String reqNo, java.lang.String fileName) throws java.rmi.RemoteException;
    public void logout(java.lang.String user) throws java.rmi.RemoteException;
}
