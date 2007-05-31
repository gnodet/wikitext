/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.core.service.soap;

import org.eclipse.mylar.xplanner.core.service.XPlannerServer;
import org.eclipse.mylar.xplanner.core.service.XPlannerService;
import org.eclipse.mylar.xplanner.core.service.XPlannerServiceFactory;

/**
 * @author Ravi Kumar 
 * @author Helen Bershadskaya 
 */
public class SoapXPlannerServiceFactory implements XPlannerServiceFactory {

	public XPlannerService createService(XPlannerServer server) {
		// TODO Auto-generated method stub
		return new SoapXPlannerService(server);
	}

}
