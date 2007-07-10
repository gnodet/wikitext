/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui.editor;

import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.*;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.*;
import org.eclipse.mylyn.xplanner.ui.XPlannerMylynUIPlugin;
import org.eclipse.mylyn.xplanner.ui.XPlannerTask;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerTaskEditorFactory extends AbstractTaskEditorFactory {

	public boolean canCreateEditorFor(AbstractTask task) {
		return task instanceof XPlannerTask;
	}

	public IEditorPart createEditor(TaskEditor parentEditor, IEditorInput editorInput) {
		IEditorPart editor = null;

		String kind = null;
		if (editorInput instanceof RepositoryTaskEditorInput) {
			RepositoryTaskData taskData = ((RepositoryTaskEditorInput)editorInput).getTaskData();
			kind = taskData.getTaskKind();
		}
		else if (editorInput instanceof TaskEditorInput) {
			AbstractTask task = ((TaskEditorInput)editorInput).getTask();
			kind = task.getTaskKind();
		}
		
	  if (XPlannerTask.Kind.TASK.toString().equals(kind)) {
		  editor = new XPlannerTaskEditor(parentEditor);
		  if (editor != null) {
		  	((XPlannerTaskEditor)editor).setParentEditor(parentEditor);
		  }
	  }
	  else if (XPlannerTask.Kind.USER_STORY.toString().equals(kind)) {
	  	editor = new XPlannerUserStoryEditor(parentEditor);
	  }
		
	  return editor;
	}

	public IEditorInput createEditorInput(AbstractTask task) {
		IEditorInput input = null;
		
		if (task instanceof XPlannerTask) {
			XPlannerTask xplannerTask = (XPlannerTask) task;
			
			final TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					xplannerTask.getConnectorKind(), xplannerTask.getRepositoryUrl());
			try {
				input = new RepositoryTaskEditorInput(repository, 
					xplannerTask.getTaskId(), xplannerTask.getUrl()); 
			} 
			catch (Exception e) {
				StatusHandler.fail(e, Messages.XPlannerTaskEditorFactory_COULD_NOT_CREATE_EDITOR_INPUT, true);
			}
		
		}
	
		return input;
	}

	public String getTitle() {
		return Messages.XPlannerTaskEditorFactory_TITLE;
	}

	public boolean providesOutline() {
		return true;
	}

	public boolean canCreateEditorFor(IEditorInput input) {
		if (input instanceof RepositoryTaskEditorInput) {
			RepositoryTaskEditorInput existingInput = (RepositoryTaskEditorInput) input;
			return existingInput.getTaskData() != null
					&& XPlannerMylynUIPlugin.REPOSITORY_KIND.equals(existingInput.getRepository().getConnectorKind());
		} 
		return false;
	}

}
