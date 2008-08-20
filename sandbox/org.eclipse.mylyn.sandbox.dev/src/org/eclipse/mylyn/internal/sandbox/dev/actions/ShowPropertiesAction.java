/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.dev.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.sandbox.dev.MylynDevPlugin;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Maarten Meijer
 */
public class ShowPropertiesAction implements IViewActionDelegate {

	private static final String PROPERTIES_ID = "org.eclipse.ui.views.PropertySheet";

	private IViewPart view;

	public ShowPropertiesAction() {
	}

	public void init(IViewPart view) {
		this.view = view;
	}

	public void run(IAction action) {
		IWorkbenchPage page = view.getSite().getPage();
		IViewPart props;
		try {
			props = page.showView(PROPERTIES_ID);
			page.activate(props);
		} catch (PartInitException e) {
			StatusHandler.log(new Status(IStatus.ERROR, MylynDevPlugin.ID_PLUGIN,
					"Unexpected exception while opening properties view", e));
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
