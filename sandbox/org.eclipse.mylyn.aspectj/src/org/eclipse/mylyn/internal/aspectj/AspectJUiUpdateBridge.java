/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.aspectj;


import java.util.List;

import org.eclipse.mylar.context.core.IMylarContext;
import org.eclipse.mylar.context.core.IMylarContextListener;
import org.eclipse.mylar.context.core.IMylarElement;

/**
 * @author Mik Kersten
 */
public class AspectJUiUpdateBridge implements IMylarContextListener {

	public void contextActivated(IMylarContext context) {
		// TODO Auto-generated method stub

	}

	public void contextDeactivated(IMylarContext context) {
		// TODO Auto-generated method stub

	}
	
	public void contextCleared(IMylarContext context) {
		// ignore
	}

	public void interestChanged(List<IMylarElement> nodes) {
		// TODO Auto-generated method stub

	}

	public void elementDeleted(IMylarElement node) {
		// TODO Auto-generated method stub

	}

	public void landmarkAdded(IMylarElement node) {
		// TODO Auto-generated method stub

	}

	public void landmarkRemoved(IMylarElement node) {
		// TODO Auto-generated method stub

	}

	public void relationsChanged(IMylarElement node) {
		// TODO Auto-generated method stub

	}

}