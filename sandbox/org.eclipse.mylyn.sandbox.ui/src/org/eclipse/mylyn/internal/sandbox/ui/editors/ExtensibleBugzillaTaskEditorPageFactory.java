/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jingwen Ou - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.editors;

import org.eclipse.mylyn.internal.bugzilla.ui.editor.BugzillaTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.forms.editor.FormPage;

/**
 * A bugzilla task editor page factory that invokes a task editor page that has wiki facilities
 * 
 * @author Jingwen Ou
 * @author Steffen Pingel
 */
public class ExtensibleBugzillaTaskEditorPageFactory extends BugzillaTaskEditorPageFactory {

	@Override
	public boolean canCreatePageFor(TaskEditorInput input) {
		return super.canCreatePageFor(input)
				&& TaskEditorExtensions.getTaskEditorExtension(input.getTaskRepository()) != null;
	}

	@Override
	public FormPage createPage(TaskEditor parentEditor) {
		return new ExtensibleBugzillaTaskEditorPage(parentEditor);
	}

	@Override
	public String[] getConflictingIds(TaskEditorInput input) {
		return new String[] { "org.eclipse.mylyn.bugzilla.ui.pageFactory" };
	}

}
