/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui.editor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.mylyn.xplanner.ui.XPlannerMylynUIPlugin;
import org.eclipse.mylyn.xplanner.ui.XPlannerTask;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerTaskEditorFactory extends AbstractTaskEditorFactory {

	@Override
	public boolean canCreateEditorFor(AbstractTask task) {
		return task instanceof XPlannerTask;
	}

	@Override
	public IEditorPart createEditor(TaskEditor parentEditor, IEditorInput editorInput) {
		IEditorPart editor = null;

		String kind = null;
		if (editorInput instanceof RepositoryTaskEditorInput) {
			RepositoryTaskData taskData = ((RepositoryTaskEditorInput) editorInput).getTaskData();
			kind = taskData.getTaskKind();
		} else if (editorInput instanceof TaskEditorInput) {
			AbstractTask task = ((TaskEditorInput) editorInput).getTask();
			kind = task.getTaskKind();
		}

		if (XPlannerTask.Kind.TASK.toString().equals(kind)) {
			if (editorInput instanceof RepositoryTaskEditorInput
					&& ((RepositoryTaskEditorInput) editorInput).getTaskData().isNew()) {
				editor = new NewXPlannerTaskEditor(parentEditor);
			} else {
				editor = new XPlannerTaskEditor(parentEditor);
			}

			((AbstractRepositoryTaskEditor) editor).setParentEditor(parentEditor);
		} else if (XPlannerTask.Kind.USER_STORY.toString().equals(kind)) {
			editor = new XPlannerUserStoryEditor(parentEditor);
		}

		return editor;
	}

	@Override
	public IEditorInput createEditorInput(AbstractTask task) {
		IEditorInput input = null;

		if (task instanceof XPlannerTask) {
			XPlannerTask xplannerTask = (XPlannerTask) task;

			final TaskRepository repository = TasksUi.getRepositoryManager().getRepository(
					xplannerTask.getConnectorKind(), xplannerTask.getRepositoryUrl());
			try {
				input = new RepositoryTaskEditorInput(repository, xplannerTask.getTaskId(), xplannerTask.getUrl());
			} catch (Exception e) {
				StatusHandler.fail(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.PLUGIN_ID,
						Messages.XPlannerTaskEditorFactory_COULD_NOT_CREATE_EDITOR_INPUT, e));
			}

		}

		return input;
	}

	@Override
	public String getTitle() {
		return Messages.XPlannerTaskEditorFactory_TITLE;
	}

	@Override
	public boolean providesOutline() {
		return true;
	}

	@Override
	public boolean canCreateEditorFor(IEditorInput input) {
		if (input instanceof RepositoryTaskEditorInput) {
			RepositoryTaskEditorInput existingInput = (RepositoryTaskEditorInput) input;
			return existingInput.getTaskData() != null
					&& XPlannerMylynUIPlugin.REPOSITORY_KIND.equals(existingInput.getRepository().getConnectorKind());
		}
		return false;
	}

}
