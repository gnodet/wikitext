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

package org.eclipse.mylyn.internal.hypertext.ui.editors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.hypertext.HypertextStructureBridge;
import org.eclipse.mylyn.internal.hypertext.MylarHypertextPlugin;
import org.eclipse.mylyn.provisional.core.IMylarStructureBridge;
import org.eclipse.mylyn.provisional.core.MylarPlugin;

/**
 * @author Mik Kersten
 */
public class WebResourceContentProvider implements IStructuredContentProvider, ITreeContentProvider {

//	private final WebElementsEditor editor;

//	/**
//	 * @param editor
//	 */
//	WebResourceContentProvider(WebElementsEditor editor) {
//		this.editor = editor;
//	}

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object parent) {
		if (parent instanceof ArrayList) {
			List<String> webDocs = (ArrayList<String>) parent;
			IMylarStructureBridge bridge = ContextCore.getStructureBridge(
					HypertextStructureBridge.CONTENT_TYPE);
			Set<String> sites = new HashSet<String>();
			for (String link : webDocs) {
				String webSite = bridge.getParentHandle(link);
				if (webSite != null) {
					sites.add(webSite);
					List<String> pages = MylarHypertextPlugin.getWebResourceManager().getSitesMap().get(webSite);
					if (pages == null) {
						pages = new ArrayList<String>();
						MylarHypertextPlugin.getWebResourceManager().getSitesMap().put(webSite, pages);
					}
					pages.add(link);
				}
			}
			if (sites.size() > 0) {
				return sites.toArray();
			} else {
				return new String[] { "Task context not activated" };
			}
		} else {
			return getChildren(parent);
		}
	}

	public void dispose() {
		// don't care if we are disposed
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// don't care if the input chages
	}

	public boolean isEmpty() {
		return false;
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof String) {
			String site = (String) parentElement;
			List<String> pages = MylarHypertextPlugin.getWebResourceManager().getSitesMap().get(site);
			if (pages != null)
				return pages.toArray();
		}
		return null;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object parentElement) {
		if (parentElement instanceof String) {
			String site = (String) parentElement;
			List<String> pages = MylarHypertextPlugin.getWebResourceManager().getSitesMap().get(site);
			return pages != null && pages.size() > 0;
		}
		return false;
	}
}