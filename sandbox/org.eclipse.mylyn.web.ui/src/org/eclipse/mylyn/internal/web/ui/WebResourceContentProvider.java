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

package org.eclipse.mylyn.internal.web.ui;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;

/**
 * @author Mik Kersten
 */
public class WebResourceContentProvider extends BaseWorkbenchContentProvider implements ICommonContentProvider {

	private ICommonContentExtensionSite extensionSite;

	private Viewer viewer;

	private final IWebResourceListener WEB_RESOURCE_LISTENER = new IWebResourceListener() {

		public void webSiteUpdated(WebSite site) {
			refresh(site);
		}

		public void webContextUpdated() {
			refresh(null);
		}

		public void webPageUpdated(WebPage page) {
			refresh(page);
		}
	};

	public WebResourceContentProvider() {
		super();
	}

	public void init(ICommonContentExtensionSite extensionSite) {
		this.extensionSite = extensionSite;
		WebUiBridgePlugin.getWebResourceManager().addListener(WEB_RESOURCE_LISTENER);
	}

	@Override
	public void dispose() {
		super.dispose();
		WebUiBridgePlugin.getWebResourceManager().removeListener(WEB_RESOURCE_LISTENER);
	}

	public void restoreState(IMemento aMemento) {
		// Nothing to do
	}

	public void saveState(IMemento aMemento) {
		// Nothing to do
	}

	public ICommonContentExtensionSite getExtensionSite() {
		return extensionSite;
	}

	@Override
	public Object[] getElements(Object element) {
		if (WebUiBridgePlugin.getWebResourceManager() != null
				&& !WebUiBridgePlugin.getWebResourceManager().isWebContextEnabled()) {
			return null;
		} else {
			if (element instanceof IWorkspaceRoot) {
				Object[] root = { WebUiBridgePlugin.getWebResourceManager().getWebRoot() };
				return root;
			} else {
				return super.getElements(element);
			}
		}
	}

	@Override
	public Object getParent(Object element) {
		return super.getParent(element);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		super.inputChanged(viewer, oldInput, newInput);
	}

	public void refresh(final WebResource webResource) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!WebResourceContentProvider.this.viewer.getControl().isDisposed()) {
					AbstractTreeViewer viewer = (AbstractTreeViewer) WebResourceContentProvider.this.viewer;
					if (webResource == null) {
						viewer.refresh(true);
					} else {
						viewer.refresh(WebUiBridgePlugin.getWebResourceManager().getWebRoot(), true);
					}
				}
			}
		});
	}
}
