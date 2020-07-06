/**
 * FileServiceManageServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package jp.co.canon.cks.eec.fs.manage;

public class FileServiceManageServiceLocator extends org.apache.axis.client.Service implements jp.co.canon.cks.eec.fs.manage.FileServiceManageService {

    public FileServiceManageServiceLocator() {
    }


    public FileServiceManageServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public FileServiceManageServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for FileServiceManage
    // CKBS [set ip address]
    private java.lang.String FileServiceManage_address = "http://localhost/FileServiceManage/services/FileServiceManage";
    //private java.lang.String FileServiceManage_address = "http://10.1.31.237/FileServiceManage/services/FileServiceManage";

    public java.lang.String getFileServiceManageAddress() {
        return FileServiceManage_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String FileServiceManageWSDDServiceName = "FileServiceManage";

    public java.lang.String getFileServiceManageWSDDServiceName() {
        return FileServiceManageWSDDServiceName;
    }

    public void setFileServiceManageWSDDServiceName(java.lang.String name) {
        FileServiceManageWSDDServiceName = name;
    }

    public jp.co.canon.cks.eec.fs.manage.FileServiceManage getFileServiceManage() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(FileServiceManage_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getFileServiceManage(endpoint);
    }

    public jp.co.canon.cks.eec.fs.manage.FileServiceManage getFileServiceManage(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            jp.co.canon.cks.eec.fs.manage.FileServiceManageSoapBindingStub _stub = new jp.co.canon.cks.eec.fs.manage.FileServiceManageSoapBindingStub(portAddress, this);
            _stub.setPortName(getFileServiceManageWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setFileServiceManageEndpointAddress(java.lang.String address) {
        FileServiceManage_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (jp.co.canon.cks.eec.fs.manage.FileServiceManage.class.isAssignableFrom(serviceEndpointInterface)) {
                jp.co.canon.cks.eec.fs.manage.FileServiceManageSoapBindingStub _stub = new jp.co.canon.cks.eec.fs.manage.FileServiceManageSoapBindingStub(new java.net.URL(FileServiceManage_address), this);
                _stub.setPortName(getFileServiceManageWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("FileServiceManage".equals(inputPortName)) {
            return getFileServiceManage();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://manage.fs.eec.cks.canon.co.jp", "FileServiceManageService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://manage.fs.eec.cks.canon.co.jp", "FileServiceManage"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

        if ("FileServiceManage".equals(portName)) {
            setFileServiceManageEndpointAddress(address);
        }
        else
        { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
