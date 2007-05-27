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

import org.eclipse.mylar.context.core.IInteractionContext;
import org.eclipse.mylar.context.core.IInteractionContextListener;
import org.eclipse.mylar.context.core.IInteractionElement;

/**
 * @author Mik Kersten
 */
public class AspectJUiUpdateBridge implements IInteractionContextListener {

	public void contextActivated(IInteractionContext context) {
		// TODO Auto-generated method stub

	}

	public void contextDeactivated(IInteractionContext context) {
		// TODO Auto-generated method stub

	}
	
	public void contextCleared(IInteractionContext context) {
		// ignore
	}

	public void interestChanged(List<IInteractionElement> nodes) {
		// TODO Auto-generated method stub

	}

	public void elementDeleted(IInteractionElement node) {
		// TODO Auto-generated method stub

	}

	public void landmarkAdded(IInteractionElement node) {
		// TODO Auto-generated method stub

	}

	public void landmarkRemoved(IInteractionElement node) {
		// TODO Auto-generated method stub

	}

	public void relationsChanged(IInteractionElement node) {
		// TODO Auto-generated method stub

	}

}