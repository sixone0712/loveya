/**
 * RequestInfoModel.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package jp.co.canon.cks.eec.fs.manage;

public class RequestInfoModel  implements java.io.Serializable {
    private java.lang.String comment;

    private java.util.Calendar dateTime;

    private jp.co.canon.cks.eec.fs.manage.FileInfoModel[] files;

    private java.lang.String requestNo;

    private java.lang.String requestUser;

    private int status;

    private java.lang.String toolId;

    public RequestInfoModel() {
    }

    public RequestInfoModel(
           java.lang.String comment,
           java.util.Calendar dateTime,
           jp.co.canon.cks.eec.fs.manage.FileInfoModel[] files,
           java.lang.String requestNo,
           java.lang.String requestUser,
           int status,
           java.lang.String toolId) {
           this.comment = comment;
           this.dateTime = dateTime;
           this.files = files;
           this.requestNo = requestNo;
           this.requestUser = requestUser;
           this.status = status;
           this.toolId = toolId;
    }


    /**
     * Gets the comment value for this RequestInfoModel.
     * 
     * @return comment
     */
    public java.lang.String getComment() {
        return comment;
    }


    /**
     * Sets the comment value for this RequestInfoModel.
     * 
     * @param comment
     */
    public void setComment(java.lang.String comment) {
        this.comment = comment;
    }


    /**
     * Gets the dateTime value for this RequestInfoModel.
     * 
     * @return dateTime
     */
    public java.util.Calendar getDateTime() {
        return dateTime;
    }


    /**
     * Sets the dateTime value for this RequestInfoModel.
     * 
     * @param dateTime
     */
    public void setDateTime(java.util.Calendar dateTime) {
        this.dateTime = dateTime;
    }


    /**
     * Gets the files value for this RequestInfoModel.
     * 
     * @return files
     */
    public jp.co.canon.cks.eec.fs.manage.FileInfoModel[] getFiles() {
        return files;
    }


    /**
     * Sets the files value for this RequestInfoModel.
     * 
     * @param files
     */
    public void setFiles(jp.co.canon.cks.eec.fs.manage.FileInfoModel[] files) {
        this.files = files;
    }


    /**
     * Gets the requestNo value for this RequestInfoModel.
     * 
     * @return requestNo
     */
    public java.lang.String getRequestNo() {
        return requestNo;
    }


    /**
     * Sets the requestNo value for this RequestInfoModel.
     * 
     * @param requestNo
     */
    public void setRequestNo(java.lang.String requestNo) {
        this.requestNo = requestNo;
    }


    /**
     * Gets the requestUser value for this RequestInfoModel.
     * 
     * @return requestUser
     */
    public java.lang.String getRequestUser() {
        return requestUser;
    }


    /**
     * Sets the requestUser value for this RequestInfoModel.
     * 
     * @param requestUser
     */
    public void setRequestUser(java.lang.String requestUser) {
        this.requestUser = requestUser;
    }


    /**
     * Gets the status value for this RequestInfoModel.
     * 
     * @return status
     */
    public int getStatus() {
        return status;
    }


    /**
     * Sets the status value for this RequestInfoModel.
     * 
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }


    /**
     * Gets the toolId value for this RequestInfoModel.
     * 
     * @return toolId
     */
    public java.lang.String getToolId() {
        return toolId;
    }


    /**
     * Sets the toolId value for this RequestInfoModel.
     * 
     * @param toolId
     */
    public void setToolId(java.lang.String toolId) {
        this.toolId = toolId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RequestInfoModel)) return false;
        RequestInfoModel other = (RequestInfoModel) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.comment==null && other.getComment()==null) || 
             (this.comment!=null &&
              this.comment.equals(other.getComment()))) &&
            ((this.dateTime==null && other.getDateTime()==null) || 
             (this.dateTime!=null &&
              this.dateTime.equals(other.getDateTime()))) &&
            ((this.files==null && other.getFiles()==null) || 
             (this.files!=null &&
              java.util.Arrays.equals(this.files, other.getFiles()))) &&
            ((this.requestNo==null && other.getRequestNo()==null) || 
             (this.requestNo!=null &&
              this.requestNo.equals(other.getRequestNo()))) &&
            ((this.requestUser==null && other.getRequestUser()==null) || 
             (this.requestUser!=null &&
              this.requestUser.equals(other.getRequestUser()))) &&
            this.status == other.getStatus() &&
            ((this.toolId==null && other.getToolId()==null) || 
             (this.toolId!=null &&
              this.toolId.equals(other.getToolId())));
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
        if (getComment() != null) {
            _hashCode += getComment().hashCode();
        }
        if (getDateTime() != null) {
            _hashCode += getDateTime().hashCode();
        }
        if (getFiles() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFiles());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFiles(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRequestNo() != null) {
            _hashCode += getRequestNo().hashCode();
        }
        if (getRequestUser() != null) {
            _hashCode += getRequestUser().hashCode();
        }
        _hashCode += getStatus();
        if (getToolId() != null) {
            _hashCode += getToolId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestInfoModel.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://manage.fs.eec.cks.canon.co.jp", "RequestInfoModel"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comment");
        elemField.setXmlName(new javax.xml.namespace.QName("", "comment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("files");
        elemField.setXmlName(new javax.xml.namespace.QName("", "files"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://manage.fs.eec.cks.canon.co.jp", "FileInfoModel"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestNo");
        elemField.setXmlName(new javax.xml.namespace.QName("", "requestNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestUser");
        elemField.setXmlName(new javax.xml.namespace.QName("", "requestUser"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("toolId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "toolId"));
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
