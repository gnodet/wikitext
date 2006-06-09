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

import java.util.List;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.internal.web.WebPage;
import org.eclipse.mylar.internal.web.WebResource;
import org.eclipse.mylar.internal.web.WebSite;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.IMylarStructureBridge;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.ui.IMylarUiBridge;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 */
public class WebResourceUiBridge implements IMylarUiBridge {

	public void open(IMylarElement element) {
		IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(element.getContentType());
		if (bridge == null) {
			return;
		} else {
			WebResource webResource = (WebResource)bridge.getObjectForHandle(element.getHandleIdentifier());
			if (webResource instanceof WebPage || webResource instanceof WebSite) {
				WebUiUtil.openUrlInInternalBrowser(webResource);
			}
		}
	} 

	public void restoreEditor(IMylarElement document) {
		open(document);
	}
	
	public void close(IMylarElement node) {
		// ignore
	}

	public boolean acceptsEditor(IEditorPart editorPart) {
		return false;
	}

	public List<TreeViewer> getContentOutlineViewers(IEditorPart editorPart) {
		return null;
	}

	public Object getObjectForTextSelection(TextSelection selection, IEditorPart editor) {
		return null;
	}
	
	public IMylarElement getElement(IEditorInput input) {
		return null;
	}
}
