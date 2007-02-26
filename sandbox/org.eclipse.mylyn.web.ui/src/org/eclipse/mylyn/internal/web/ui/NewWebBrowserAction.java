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

package org.eclipse.mylar.internal.web.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Mik Kersten
 */
public class NewWebBrowserAction implements IWorkbenchWindowActionDelegate {

	public void run(IAction action) {
		String clipboardUrl = WebUiUtil.getUrlFromClipboard();
		if (clipboardUrl != null) {
			TasksUiUtil.openBrowser(clipboardUrl);
		} else {
			TasksUiUtil.openBrowser("http://google.com");
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

	public void dispose() {
		// ignore
	}

	public void init(IWorkbenchWindow window) {
		// ignore
	}

}
