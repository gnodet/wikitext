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
/*
 * Created on Apr 6, 2005
 */
package org.eclipse.mylyn.internal.sandbox.bridge.bugs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.ui.AbstractContextUiBridge;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTaskOutlinePage;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class BugzillaUiBridge extends AbstractContextUiBridge {

	protected BugzillaContextLabelProvider labelProvider = new BugzillaContextLabelProvider();

	public void open(IInteractionElement node) {
		String handle = node.getHandleIdentifier();
		String bugHandle = handle;
		String server = handle.substring(0, handle.indexOf(";"));

		handle = handle.substring(handle.indexOf(";") + 1);
		int next = handle.indexOf(";");

		int bugId;
		if (next == -1) {
			bugId = Integer.parseInt(handle);
		} else {
			bugId = Integer.parseInt(handle.substring(0, handle.indexOf(";")));
			bugHandle = bugHandle.substring(0, next);
		}

		AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(handle);
		if (task != null) {
			TasksUiUtil.openEditor(task, false);
		} else {
			String bugUrl = BugzillaClient.getBugUrlWithoutLogin(server, ""+bugId);
			TasksUiUtil.openRepositoryTask(server, "" + bugId, bugUrl);
		}
	}

	public ILabelProvider getLabelProvider() {
		return labelProvider;
	}

	public void close(IInteractionElement node) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			IEditorReference[] references = page.getEditorReferences();
			for (int i = 0; i < references.length; i++) {
				IEditorPart part = references[i].getEditor(false);
				if (part != null) {
					if (part instanceof AbstractRepositoryTaskEditor) {
						((AbstractRepositoryTaskEditor) part).close();
					} else if (part instanceof TaskEditor) {
						((TaskEditor) part).close(true);
					}
				}
			}
		}
	}

	public boolean acceptsEditor(IEditorPart editorPart) {
		return editorPart instanceof AbstractRepositoryTaskEditor;
	}

	public List<TreeViewer> getContentOutlineViewers(IEditorPart editor) {
		List<TreeViewer> viewers = new ArrayList<TreeViewer>();
		TreeViewer outline = getOutlineTreeViewer(editor);
		if (outline != null)
			viewers.add(outline);
		return viewers;
	}

	protected TreeViewer getOutlineTreeViewer(IEditorPart editor) {
		if (editor instanceof AbstractRepositoryTaskEditor) {
			AbstractRepositoryTaskEditor abe = (AbstractRepositoryTaskEditor) editor;
			RepositoryTaskOutlinePage outline = abe.getOutline();
			if (outline != null)
				return outline.getOutlineTreeViewer();
		}
		return null;
	}

	public Object getObjectForTextSelection(TextSelection selection, IEditorPart editor) {
		return null;
	}

	public void restoreEditor(IInteractionElement document) {
		// ignore
	}

	public IInteractionElement getElement(IEditorInput input) {
		return null;
	}

	public String getContentType() {
		return BugzillaStructureBridge.CONTENT_TYPE;
	}
}
