/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.core.service;

/**
 * Factory class for creating ta XPlanner Service imlementation. TODO finish documenting this. Explain the extension
 * point.
 * 
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public interface XPlannerServiceFactory {
	/**
	 * Create a new service that will communicate to the supplied <code>client</code>. How the service talks to the
	 * client is up to the implementation.
	 * 
	 * @param client
	 *            XPlannerClient instance to communicate with
	 * @return Configured XPlanner Service
	 */
	public abstract XPlannerService createService(XPlannerClient client);
}
