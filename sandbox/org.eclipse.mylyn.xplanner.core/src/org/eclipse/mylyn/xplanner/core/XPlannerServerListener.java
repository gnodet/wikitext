/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.core;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
import org.eclipse.mylar.xplanner.core.service.XPlannerServer;


public interface XPlannerServerListener {

	public abstract void serverAdded(XPlannerServer server);
	
	public abstract void serverRemoved(XPlannerServer server);
}
