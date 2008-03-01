/**
 * XPlannerService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.3 Oct 05, 2005 (05:23:37 EDT) WSDL2Java emitter.
 */

package org.xplanner.soap.XPlanner;

public interface XPlannerService extends javax.xml.rpc.Service {
	public java.lang.String getXPlannerAddress();

	public org.xplanner.soap.XPlanner.XPlanner getXPlanner() throws javax.xml.rpc.ServiceException;

	public org.xplanner.soap.XPlanner.XPlanner getXPlanner(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException;
}
