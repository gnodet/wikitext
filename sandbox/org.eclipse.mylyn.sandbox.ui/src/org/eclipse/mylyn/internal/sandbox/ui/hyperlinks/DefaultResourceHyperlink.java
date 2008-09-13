/*******************************************************************************
 * Copyright (c) 2004, 2008 Jingwen Ou and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jingwen Ou - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.hyperlinks;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Jingwen Ou
 */
public class DefaultResourceHyperlink implements IHyperlink {

	private final IRegion region;

	private final String resourceName;

	public DefaultResourceHyperlink(IRegion region, String resourceName) {
		this.region = region;
		this.resourceName = resourceName;
	}

	public IRegion getHyperlinkRegion() {
		return region;
	}

	public String getHyperlinkText() {
		return "Open " + resourceName;
	}

	public String getTypeLabel() {
		return null;
	}

	public void open() {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(resourceName), true);
		if (resource instanceof IFile) {
			openEditor((IFile) resource);
		} else {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Open Resource", "Resource could not be located.");
		}
	}

	private void openEditor(final IFile file) {
		UIJob job = new UIJob("Opening resource") {

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				try {
					IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file);
					return Status.OK_STATUS;
				} catch (PartInitException e) {
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Open Resource", "Failed to open resource.");
				}
				return Status.CANCEL_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}
}
