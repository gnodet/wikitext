/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.ui;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylyn.internal.bugzilla.ui.editor.AbstractBugEditor;
import org.eclipse.mylyn.internal.bugzilla.ui.editor.ExistingBugEditorInput;
import org.eclipse.mylyn.internal.core.util.MylarStatusHandler;
import org.eclipse.mylyn.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylyn.provisional.tasklist.TaskRepository;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * Action performed when the bugs are supposed to be displayed in the editor
 * window from the favorites list
 */
@Deprecated
public class ViewBugzillaAction extends UIJob {

	/** List of bugs to be displayed */
	private List<BugzillaOpenStructure> bugs;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The job name
	 * @param bugs
	 *            List of bugs to be displayed
	 */
	public ViewBugzillaAction(String name, List<BugzillaOpenStructure> bugs) {
		super(name);
		this.bugs = bugs;
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		// go through each bug and get its id
		for (Iterator<BugzillaOpenStructure> it = bugs.iterator(); it.hasNext();) {
			BugzillaOpenStructure bos = it.next();

		}
		return new Status(IStatus.OK, BugzillaUiPlugin.PLUGIN_ID, IStatus.OK, "", null);
	}
}
