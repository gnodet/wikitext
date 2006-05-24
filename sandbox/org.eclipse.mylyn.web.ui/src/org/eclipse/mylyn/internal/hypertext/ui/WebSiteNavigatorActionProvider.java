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

package org.eclipse.mylar.internal.hypertext.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylar.internal.hypertext.WebSiteResource;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

/**
 * @author Mik Kersten
 */
public class WebSiteNavigatorActionProvider extends CommonActionProvider {

	private Action newModAction;
	private Action newFolderAction;
	private Action deleteAction;

	public WebSiteNavigatorActionProvider() {
		super();
	}
	
	public void init(ICommonActionExtensionSite aSite) {
		super.init(aSite);
		createActions();
	}

	private void createActions() {
		deleteAction = new Action("Delete") {
			public void run() {
//				IStructuredSelection selection = (IStructuredSelection)getContext().getSelection();
			}
		};
		newFolderAction = new Action("Create Folder") {
			public void run() {
				WebSiteResource container = getSelectedContainer();
				if (container != null) {
					String name = promptForName();
					if (name == null)
						return;
				}
			}

			private String promptForName() {
				InputDialog dialog = new InputDialog(getShell(), "Enter Name", "Enter the name of the new folder", "New Folder", null);
				int result = dialog.open();
				if (result == Window.OK) {
					return dialog.getValue();
				}
				return null;
			}
		};
	}
	
	protected Shell getShell() {
		return getActionSite().getViewSite().getShell();
	}

	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		menu.add(deleteAction);
		WebSiteResource container = getSelectedContainer();
		if (container != null) {
			menu.add(newFolderAction);
			menu.add(newModAction);
		}
	}

	WebSiteResource getSelectedContainer() {
		IStructuredSelection selection = (IStructuredSelection)getContext().getSelection();
		if (selection.size() == 1) {
			Object o = selection.getFirstElement();
			if (o instanceof WebSiteResource) {
				WebSiteResource mc = (WebSiteResource) o;
				return mc;
			}
		}
		return null;
	}

}
