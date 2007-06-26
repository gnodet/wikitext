/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.ui;

import java.util.List;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 */
public class WebResourceUiBridge extends AbstractContextUiBridge {

	@Override
	public void open(IInteractionElement element) {
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(
				element.getContentType());
		if (bridge == null) {
			return;
		} else {
			WebResource webResource = (WebResource) bridge.getObjectForHandle(element.getHandleIdentifier());
			if (webResource instanceof WebPage || webResource instanceof WebSite) {
				WebUiUtil.openUrl(webResource);
			}
		}
	}

	@Override
	public void close(IInteractionElement node) {
		// ignore
	}

	@Override
	public boolean acceptsEditor(IEditorPart editorPart) {
		return false;
	}

	@Override
	public List<TreeViewer> getContentOutlineViewers(IEditorPart editorPart) {
		return null;
	}

	@Override
	public Object getObjectForTextSelection(TextSelection selection, IEditorPart editor) {
		return null;
	}

	@Override
	public IInteractionElement getElement(IEditorInput input) {
		return null;
	}

	@Override
	public String getContentType() {
		return WebResourceStructureBridge.CONTENT_TYPE;
	}
}
