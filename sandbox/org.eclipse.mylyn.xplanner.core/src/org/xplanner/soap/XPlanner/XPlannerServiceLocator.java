/**
 * XPlannerServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package org.xplanner.soap.XPlanner;

import java.net.Proxy;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.rpc.Call;
import javax.xml.rpc.ServiceException;

import org.apache.axis.transport.http.HTTPConstants;

@SuppressWarnings({ "unchecked", "serial", "unused", "null" })
public class XPlannerServiceLocator extends org.apache.axis.client.Service implements org.xplanner.soap.XPlanner.XPlannerService {

		private Proxy proxy;
		private String httpUser;
		private String httpPassword;
		private boolean compression;
		
    public XPlannerServiceLocator() {
    }


    public XPlannerServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public XPlannerServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for XPlanner
    private java.lang.String XPlanner_address = "http://jbproxy.inprise.com:7070/soap/XPlanner"; //$NON-NLS-1$

    public java.lang.String getXPlannerAddress() {
        return XPlanner_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String XPlannerWSDDServiceName = "XPlanner"; //$NON-NLS-1$

    public java.lang.String getXPlannerWSDDServiceName() {
        return XPlannerWSDDServiceName;
    }

    public void setXPlannerWSDDServiceName(java.lang.String name) {
        XPlannerWSDDServiceName = name;
    }

    public org.xplanner.soap.XPlanner.XPlanner getXPlanner() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(XPlanner_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getXPlanner(endpoint);
    }

    public org.xplanner.soap.XPlanner.XPlanner getXPlanner(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.xplanner.soap.XPlanner.XPlannerSoapBindingStub _stub = new org.xplanner.soap.XPlanner.XPlannerSoapBindingStub(portAddress, this);
            _stub.setPortName(getXPlannerWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setXPlannerEndpointAddress(java.lang.String address) {
        XPlanner_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.xplanner.soap.XPlanner.XPlanner.class.isAssignableFrom(serviceEndpointInterface)) {
                org.xplanner.soap.XPlanner.XPlannerSoapBindingStub _stub = new org.xplanner.soap.XPlanner.XPlannerSoapBindingStub(new java.net.URL(XPlanner_address), this);
                _stub.setPortName(getXPlannerWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName())); //$NON-NLS-1$ //$NON-NLS-2$
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
        if ("XPlanner".equals(inputPortName)) { //$NON-NLS-1$
            return getXPlanner();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "XPlannerService"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://xplanner.org:8080/soap/XPlanner", "XPlanner")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("XPlanner".equals(portName)) { //$NON-NLS-1$
            setXPlannerEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName); //$NON-NLS-1$
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

  	/*
  	 * (non-Javadoc)
  	 * 
  	 * @see org.apache.axis.client.Service#createCall()
  	 */
  	public Call createCall() throws ServiceException {
  		Call call = super.createCall();
  		// JIRA does not accept compressed SOAP messages: see bug 175915
  		//call.setProperty(HTTPConstants.MC_GZIP_REQUEST, Boolean.TRUE);
  		if (compression) {
  			call.setProperty(HTTPConstants.MC_ACCEPT_GZIP, Boolean.TRUE);
  		}
  		if (httpUser != null && httpPassword != null) {
  			call.setProperty(XPlannerHttpSender.HTTP_USER, httpUser);
  			call.setProperty(XPlannerHttpSender.HTTP_PASSWORD, httpPassword);
  		}
  		if (proxy != null) {
  			call.setProperty(XPlannerHttpSender.PROXY, proxy);
  		}
  		
  		// Some clients break with a 411 Length Required when chunked encoding
  		// is used
  		Map<String, Boolean> headers = new Hashtable<String, Boolean>();
  		headers.put(HTTPConstants.HEADER_TRANSFER_ENCODING_CHUNKED, Boolean.FALSE);
  		call.setProperty(HTTPConstants.REQUEST_HEADERS, headers);
  		return call;
  	}

  	public Proxy getProxy() {
  		return proxy;
  	}

  	public void setProxy(Proxy proxy) {
  		this.proxy = proxy;
  	}

  	public String getHttpUser() {
  		return httpUser;
  	}

  	public void setHttpUser(String httpUser) {
  		this.httpUser = httpUser;
  	}

  	public String getHttpPassword() {
  		return httpPassword;
  	}

  	public void setHttpPassword(String httpPassword) {
  		this.httpPassword = httpPassword;
  	}

  	public boolean isCompression() {
  		return compression;
  	}
  	
  	public void setCompression(boolean compression) {
  		this.compression = compression;
  	}
  	
}
