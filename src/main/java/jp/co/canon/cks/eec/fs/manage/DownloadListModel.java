/**
 * DownloadListModel.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package jp.co.canon.cks.eec.fs.manage;

public class DownloadListModel  implements java.io.Serializable {
    private jp.co.canon.cks.eec.fs.manage.DownloadInfoModel[] downloadInfos;

    private jp.co.canon.cks.eec.fs.manage.ServerErrorInfo[] serverErrors;

    public DownloadListModel() {
    }

    public DownloadListModel(
           jp.co.canon.cks.eec.fs.manage.DownloadInfoModel[] downloadInfos,
           jp.co.canon.cks.eec.fs.manage.ServerErrorInfo[] serverErrors) {
           this.downloadInfos = downloadInfos;
           this.serverErrors = serverErrors;
    }


    /**
     * Gets the downloadInfos value for this DownloadListModel.
     * 
     * @return downloadInfos
     */
    public jp.co.canon.cks.eec.fs.manage.DownloadInfoModel[] getDownloadInfos() {
        return downloadInfos;
    }


    /**
     * Sets the downloadInfos value for this DownloadListModel.
     * 
     * @param downloadInfos
     */
    public void setDownloadInfos(jp.co.canon.cks.eec.fs.manage.DownloadInfoModel[] downloadInfos) {
        this.downloadInfos = downloadInfos;
    }


    /**
     * Gets the serverErrors value for this DownloadListModel.
     * 
     * @return serverErrors
     */
    public jp.co.canon.cks.eec.fs.manage.ServerErrorInfo[] getServerErrors() {
        return serverErrors;
    }


    /**
     * Sets the serverErrors value for this DownloadListModel.
     * 
     * @param serverErrors
     */
    public void setServerErrors(jp.co.canon.cks.eec.fs.manage.ServerErrorInfo[] serverErrors) {
        this.serverErrors = serverErrors;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DownloadListModel)) return false;
        DownloadListModel other = (DownloadListModel) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.downloadInfos==null && other.getDownloadInfos()==null) || 
             (this.downloadInfos!=null &&
              java.util.Arrays.equals(this.downloadInfos, other.getDownloadInfos()))) &&
            ((this.serverErrors==null && other.getServerErrors()==null) || 
             (this.serverErrors!=null &&
              java.util.Arrays.equals(this.serverErrors, other.getServerErrors())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getDownloadInfos() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDownloadInfos());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDownloadInfos(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getServerErrors() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getServerErrors());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getServerErrors(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DownloadListModel.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://manage.fs.eec.cks.canon.co.jp", "DownloadListModel"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("downloadInfos");
        elemField.setXmlName(new javax.xml.namespace.QName("", "downloadInfos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://manage.fs.eec.cks.canon.co.jp", "DownloadInfoModel"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serverErrors");
        elemField.setXmlName(new javax.xml.namespace.QName("", "serverErrors"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://manage.fs.eec.cks.canon.co.jp", "ServerErrorInfo"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
