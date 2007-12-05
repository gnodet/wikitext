/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.dev.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class TestUiAction implements IWorkbenchWindowActionDelegate {

	public void dispose() {
		// ignore

	}

	public void init(IWorkbenchWindow window) {
		// ignore

	}

	public void run(IAction action) {
		TasksUiPlugin.getTaskListNotificationManager().showPopup();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

}
