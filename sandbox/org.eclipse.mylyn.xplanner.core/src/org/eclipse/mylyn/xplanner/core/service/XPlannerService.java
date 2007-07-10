/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.core.service;

import org.eclipse.mylyn.xplanner.core.service.exceptions.AuthenticationException;
import org.eclipse.mylyn.xplanner.core.service.exceptions.ServiceUnavailableException;
import org.xplanner.soap.XPlanner.XPlanner;


/**
 * This interface exposes the full set of services available from a XPlanner installation.
 *  
 * @author Ravi Kumar 
 * @author Helen Bershadskaya 
 */
public abstract class XPlannerService implements XPlanner {
    public String login(String username, String password) throws AuthenticationException, ServiceUnavailableException {
    	return null;
    }
    public boolean logout() throws ServiceUnavailableException {
    	return false;
    }
    
	/**
	 * Refresh any cached information with the latest values from the remote client.
	 * This operation may take a long time to complete and should not be called
	 * from a UI thread.
	 */
	public void refreshDetails() {
		// TODO Auto-generated method stub
		
	}    
}
