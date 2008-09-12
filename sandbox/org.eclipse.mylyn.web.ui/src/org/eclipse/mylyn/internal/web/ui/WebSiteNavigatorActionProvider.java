/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.ui;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.web.ui.actions.OpenWebResourceAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * @author Mik Kersten
 */
public class WebSiteNavigatorActionProvider extends CommonActionProvider {

	// private ICommonViewerWorkbenchSite viewSite = null;

	private final OpenWebResourceAction openAction = new OpenWebResourceAction("Open");

	public WebSiteNavigatorActionProvider() {
		super();
	}

	@Override
	public void init(ICommonActionExtensionSite extensionSite) {
		super.init(extensionSite);
		// createActions();
		if (extensionSite.getViewSite() instanceof ICommonViewerWorkbenchSite) {
			// viewSite = (ICommonViewerWorkbenchSite) aConfig.getViewSite();
			// openAction = new OpenFileAction();
			// contribute = true;
			// viewSite.getActionBars().
		}

	}

	@Override
	public void fillContextMenu(IMenuManager menuManager) {
		IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();

		openAction.selectionChanged(selection);
		if (openAction.isEnabled()) {
			menuManager.insertAfter(ICommonMenuConstants.GROUP_OPEN, openAction);
		}
		menuManager.add(new Separator(ICommonMenuConstants.GROUP_ADDITIONS));
	}

	@Override
	public void fillActionBars(IActionBars actionBars) {
		IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		if (selection.size() == 1 && selection.getFirstElement() instanceof WebResource) {
			openAction.selectionChanged(selection);
			actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, openAction);
		}
	}
}
