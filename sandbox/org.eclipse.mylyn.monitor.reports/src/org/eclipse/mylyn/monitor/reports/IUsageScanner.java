/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.monitor.reports;

import org.eclipse.mylar.core.InteractionEvent;

/**
 * @author Gail Murphy
 *
 */

/**
 * A usage scanner will see all events for a user before any consumers
 */
public interface IUsageScanner {
	
	public void scanEvent(InteractionEvent event, int userId);
	
	public boolean accept( int userId );

}
