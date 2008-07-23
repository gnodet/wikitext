/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.hyperlinks;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * Truncated class of JavaStackTraceFileHyperlink, open a hyperlink using OpenTypeAction
 * 
 * @author Rob Elves
 * @author Jingwe Ou
 */
public class JavaResourceHyperlink implements IHyperlink {

	private final IRegion region;

	private final String typeName;

	public JavaResourceHyperlink(IRegion region, String typeName) {
		this.region = region;
		this.typeName = typeName;
	}

	public IRegion getHyperlinkRegion() {
		return region;
	}

	public String getHyperlinkText() {
		return "Open " + typeName;
	}

	public String getTypeLabel() {
		return null;
	}

	public void open() {
		startSourceSearch(typeName);
	}

	/**
	 * Starts a search for the type with the given name. Reports back to 'searchCompleted(...)'.
	 * 
	 * @param typeName
	 *            the type to search for
	 */
	protected void startSourceSearch(final String typeName) {
		Job search = new Job("Searching...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					// search for the type in the workspace
					Object result = OpenTypeAction.findTypeInWorkspace(typeName);
					searchCompleted(result, typeName, null);
				} catch (CoreException e) {
					searchCompleted(null, typeName, e.getStatus());
				}
				return Status.OK_STATUS;
			}

		};
		search.schedule();
	}

	protected void searchCompleted(final Object source, final String typeName, final IStatus status) {
		UIJob job = new UIJob("link search complete") { //$NON-NLS-1$
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (source == null) {
					// did not find source
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Open Type", "Type could not be located.");
				} else {
					processSearchResult(source, typeName);
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	/**
	 * The search succeeded with the given result
	 * 
	 * @param source
	 *            resolved source object for the search
	 * @param typeName
	 *            type name searched for
	 */
	protected void processSearchResult(Object source, String typeName) {
		IDebugModelPresentation presentation = JDIDebugUIPlugin.getDefault().getModelPresentation();
		IEditorInput editorInput = presentation.getEditorInput(source);
		if (editorInput != null) {
			String editorId = presentation.getEditorId(editorInput, source);
			if (editorId != null) {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput,
							editorId);

				} catch (CoreException e) {
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							"Open Type", "Failed to open type.");
				}
			}
		}
	}
}
