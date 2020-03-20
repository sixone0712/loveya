/**
 * FileTypeModel.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package jp.co.canon.cks.eec.fs.manage;

public class FileTypeModel  implements java.io.Serializable {
    private java.lang.String dataName;

    private java.lang.String logType;

    private java.lang.String searchType;

    public FileTypeModel() {
    }

    public FileTypeModel(
           java.lang.String dataName,
           java.lang.String logType,
           java.lang.String searchType) {
           this.dataName = dataName;
           this.logType = logType;
           this.searchType = searchType;
    }


    /**
     * Gets the dataName value for this FileTypeModel.
     * 
     * @return dataName
     */
    public java.lang.String getDataName() {
        return dataName;
    }


    /**
     * Sets the dataName value for this FileTypeModel.
     * 
     * @param dataName
     */
    public void setDataName(java.lang.String dataName) {
        this.dataName = dataName;
    }


    /**
     * Gets the logType value for this FileTypeModel.
     * 
     * @return logType
     */
    public java.lang.String getLogType() {
        return logType;
    }


    /**
     * Sets the logType value for this FileTypeModel.
     * 
     * @param logType
     */
    public void setLogType(java.lang.String logType) {
        this.logType = logType;
    }


    /**
     * Gets the searchType value for this FileTypeModel.
     * 
     * @return searchType
     */
    public java.lang.String getSearchType() {
        return searchType;
    }


    /**
     * Sets the searchType value for this FileTypeModel.
     * 
     * @param searchType
     */
    public void setSearchType(java.lang.String searchType) {
        this.searchType = searchType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FileTypeModel)) return false;
        FileTypeModel other = (FileTypeModel) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dataName==null && other.getDataName()==null) || 
             (this.dataName!=null &&
              this.dataName.equals(other.getDataName()))) &&
            ((this.logType==null && other.getLogType()==null) || 
             (this.logType!=null &&
              this.logType.equals(other.getLogType()))) &&
            ((this.searchType==null && other.getSearchType()==null) || 
             (this.searchType!=null &&
              this.searchType.equals(other.getSearchType())));
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
        if (getDataName() != null) {
            _hashCode += getDataName().hashCode();
        }
        if (getLogType() != null) {
            _hashCode += getLogType().hashCode();
        }
        if (getSearchType() != null) {
            _hashCode += getSearchType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FileTypeModel.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://manage.fs.eec.cks.canon.co.jp", "FileTypeModel"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("logType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "logType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "searchType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
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
