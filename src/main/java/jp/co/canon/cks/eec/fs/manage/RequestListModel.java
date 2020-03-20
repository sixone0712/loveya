/**
 * RequestListModel.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package jp.co.canon.cks.eec.fs.manage;

public class RequestListModel  implements java.io.Serializable {
    private jp.co.canon.cks.eec.fs.manage.RequestInfoModel[] requestInfos;

    private jp.co.canon.cks.eec.fs.manage.ServerErrorInfo[] serverErrors;

    public RequestListModel() {
    }

    public RequestListModel(
           jp.co.canon.cks.eec.fs.manage.RequestInfoModel[] requestInfos,
           jp.co.canon.cks.eec.fs.manage.ServerErrorInfo[] serverErrors) {
           this.requestInfos = requestInfos;
           this.serverErrors = serverErrors;
    }


    /**
     * Gets the requestInfos value for this RequestListModel.
     * 
     * @return requestInfos
     */
    public jp.co.canon.cks.eec.fs.manage.RequestInfoModel[] getRequestInfos() {
        return requestInfos;
    }


    /**
     * Sets the requestInfos value for this RequestListModel.
     * 
     * @param requestInfos
     */
    public void setRequestInfos(jp.co.canon.cks.eec.fs.manage.RequestInfoModel[] requestInfos) {
        this.requestInfos = requestInfos;
    }


    /**
     * Gets the serverErrors value for this RequestListModel.
     * 
     * @return serverErrors
     */
    public jp.co.canon.cks.eec.fs.manage.ServerErrorInfo[] getServerErrors() {
        return serverErrors;
    }


    /**
     * Sets the serverErrors value for this RequestListModel.
     * 
     * @param serverErrors
     */
    public void setServerErrors(jp.co.canon.cks.eec.fs.manage.ServerErrorInfo[] serverErrors) {
        this.serverErrors = serverErrors;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RequestListModel)) return false;
        RequestListModel other = (RequestListModel) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.requestInfos==null && other.getRequestInfos()==null) || 
             (this.requestInfos!=null &&
              java.util.Arrays.equals(this.requestInfos, other.getRequestInfos()))) &&
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
        if (getRequestInfos() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRequestInfos());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRequestInfos(), i);
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
        new org.apache.axis.description.TypeDesc(RequestListModel.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://manage.fs.eec.cks.canon.co.jp", "RequestListModel"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestInfos");
        elemField.setXmlName(new javax.xml.namespace.QName("", "requestInfos"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://manage.fs.eec.cks.canon.co.jp", "RequestInfoModel"));
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
