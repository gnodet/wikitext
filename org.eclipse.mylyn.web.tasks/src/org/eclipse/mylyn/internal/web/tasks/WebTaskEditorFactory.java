/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.tasks;

import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorPart;

/**
 * @author Mik Kersten
 */
public class WebTaskEditorFactory extends AbstractTaskEditorFactory {

	private static final String TITLE = "Browser";

	public IEditorPart createEditor(TaskEditor parentEditor, IEditorInput editorInput) {
		AbstractRepositoryTaskEditor editor = null;
		if (editorInput instanceof TaskEditorInput) {
			TaskEditorInput taskInput = (TaskEditorInput) editorInput;
			return createBrowser(parentEditor, taskInput.getTask().getUrl());
		}
		return editor;
	}

	public IEditorInput createEditorInput(AbstractTask task) {
		if (task instanceof WebTask) {
			final TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					WebRepositoryConnector.REPOSITORY_TYPE, task.getRepositoryUrl());
			return new RepositoryTaskEditorInput(repository, task.getUrl(),	task.getTaskId());
		} else {
			return null;
		}
	}

	private EditorPart createBrowser(TaskEditor parentEditor, String url) {
		return new BrowserFormPage(parentEditor, TITLE);
	}

	public String getTitle() {
		return TITLE;
	}

	public boolean canCreateEditorFor(AbstractTask task) {
		;
		return task instanceof WebTask;
	}

	public boolean canCreateEditorFor(IEditorInput input) {
		return input instanceof BrowserEditorInput;
	}

	public boolean providesOutline() {
		return false;
	}

}
