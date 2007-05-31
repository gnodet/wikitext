/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.ui.editor;

import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.tasks.ui.editors.ITaskEditorFactory;
import org.eclipse.mylar.tasks.ui.editors.RepositoryTaskEditorInput;
import org.eclipse.mylar.tasks.ui.editors.TaskEditor;
import org.eclipse.mylar.tasks.ui.editors.TaskEditorInput;
import org.eclipse.mylar.xplanner.ui.XPlannerMylarUIPlugin;
import org.eclipse.mylar.xplanner.ui.XPlannerTask;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerTaskEditorFactory implements ITaskEditorFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.mylar.internal.tasklist.ui.ITaskEditorFactory#canCreateEditorFor(org.eclipse.mylar.provisional.tasklist.ITask)
	 */
	public boolean canCreateEditorFor(ITask task) {
		return task instanceof XPlannerTask;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylar.internal.tasklist.ui.ITaskEditorFactory#createEditor(org.eclipse.mylar.internal.tasklist.ui.editors.MylarTaskEditor)
	 */
	public IEditorPart createEditor(TaskEditor parentEditor, IEditorInput editorInput) {
		IEditorPart editor = null;
		
		if (editorInput instanceof TaskEditorInput) {
		  ITask task = ((TaskEditorInput)editorInput).getTask();
		  if (XPlannerTask.Kind.TASK.toString().equals(task.getTaskKind())) {
			  editor = new XPlannerTaskEditor(parentEditor);
			  if (editor != null) {
			  	((XPlannerTaskEditor)editor).setParentEditor(parentEditor);
			  }
		  }
		  else if (XPlannerTask.Kind.USER_STORY.toString().equals(task.getTaskKind())) {
		  	editor = new XPlannerUserStoryEditor(parentEditor);
		  }
		}
		
	  return editor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylar.internal.tasklist.ui.ITaskEditorFactory#createEditorInput(org.eclipse.mylar.provisional.tasklist.ITask)
	 */
	public IEditorInput createEditorInput(ITask task) {
		IEditorInput input = null;
		
		if (task instanceof XPlannerTask) {
			XPlannerTask xplannerTask = (XPlannerTask) task;
			
			final TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					xplannerTask.getRepositoryKind(), xplannerTask.getRepositoryUrl());
			try {
				input = new RepositoryTaskEditorInput(repository, 
					xplannerTask.getHandleIdentifier(), xplannerTask.getTaskUrl(), 
					xplannerTask.getTaskId());
			} 
			catch (Exception e) {
				MylarStatusHandler.fail(e, Messages.XPlannerTaskEditorFactory_COULD_NOT_CREATE_EDITOR_INPUT, true);
			}
		
		}
	
		return input;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylar.internal.tasklist.ui.ITaskEditorFactory#getTitle()
	 */
	public String getTitle() {
		return Messages.XPlannerTaskEditorFactory_TITLE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylar.internal.tasklist.ui.ITaskEditorFactory#notifyEditorActivationChange(org.eclipse.ui.IEditorPart)
	 */
	public void notifyEditorActivationChange(IEditorPart editor) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylar.internal.tasklist.ui.ITaskEditorFactory#providesOutline()
	 */
	public boolean providesOutline() {
		return true;
	}

	public boolean canCreateEditorFor(IEditorInput input) {
		if (input instanceof RepositoryTaskEditorInput) {
			RepositoryTaskEditorInput existingInput = (RepositoryTaskEditorInput) input;
			return existingInput.getTaskData() != null
					&& XPlannerMylarUIPlugin.REPOSITORY_KIND.equals(existingInput.getRepository().getKind());
		} 
		return false;
	}

}
