/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.planner;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;

/**
 * @author Rob Elves
 */
public class TaskActivityContentProvider implements ITreeContentProvider, ITaskPlannerContentProvider {

	private final TaskActivityEditorInput editorInput;

	public TaskActivityContentProvider(TaskActivityEditorInput editorInput) {
		this.editorInput = editorInput;
	}

	public Object[] getElements(Object inputElement) {
		return editorInput.getCategories().toArray();
//		List<AbstractTask> allTasks = new ArrayList<AbstractTask>();
//		allTasks.addAll(editorInput.getCompletedTasks());
//		allTasks.addAll(editorInput.getInProgressTasks());
//		return allTasks.toArray();
	}

	public void removeTask(ITask task) {
		editorInput.removeCompletedTask(task);
		editorInput.removeInProgressTask(task);
	}

	public void addTask(AbstractTask task) {
		// ignore
	}

	public Object[] getChildren(Object parentElement) {
		Set<ITask> result = new HashSet<ITask>();
		if (parentElement instanceof ITaskElement) {
			ITaskElement parent = (ITaskElement) parentElement;
			Set<ITask> completedChildren = new HashSet<ITask>();
			completedChildren.addAll(editorInput.getCompletedTasks());
			completedChildren.retainAll(parent.getChildren());
			result.addAll(completedChildren);

			Set<ITask> inProgressChildren = new HashSet<ITask>();
			inProgressChildren.addAll(editorInput.getInProgressTasks());
			inProgressChildren.retainAll(parent.getChildren());
			result.addAll(inProgressChildren);
		}
		return result.toArray();
	}

	public Object getParent(Object element) {
		// ignore
		return null;
	}

	public boolean hasChildren(Object element) {
		return (getChildren(element).length > 0);
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// ignore
	}

	public void dispose() {
		// ignore
	}

}
