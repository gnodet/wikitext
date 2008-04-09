/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.sandbox.ui.views.RelatedElementsPopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * Class to activate the inplace ActiveSearch view, via the key binding defined in the plugin.xml.
 * 
 * @author Tracy Mew
 */
public class OpenRelatedElementsPopupDialogAction implements IWorkbenchWindowActionDelegate {

	private static RelatedElementsPopupDialog inplaceDialog;

	public void dispose() {
		inplaceDialog = null;
	}

	public void init(IWorkbenchWindow window) {
		// ignore
	}

	public void run(IAction action) {
		Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		inplaceDialog = new RelatedElementsPopupDialog(parent, SWT.NULL);
		inplaceDialog.open();
		inplaceDialog.setFocus();

	}

	public static RelatedElementsPopupDialog getFromRelatedDialogPerspective() {
		return inplaceDialog;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

}
