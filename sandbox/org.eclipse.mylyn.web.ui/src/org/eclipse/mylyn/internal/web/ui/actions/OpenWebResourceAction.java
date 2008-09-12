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

package org.eclipse.mylyn.internal.web.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.web.ui.WebResource;
import org.eclipse.mylyn.internal.web.ui.WebUiUtil;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 */
public class OpenWebResourceAction extends BaseSelectionListenerAction {

	public OpenWebResourceAction(String text) {
		super(text);
	}

	@Override
	public void run() {
		IStructuredSelection selection = super.getStructuredSelection();
		Object selectedElement = selection.getFirstElement();
		if (selectedElement instanceof WebResource) {
			WebUiUtil.openUrl(((WebResource) selectedElement));
		}
	}
}
