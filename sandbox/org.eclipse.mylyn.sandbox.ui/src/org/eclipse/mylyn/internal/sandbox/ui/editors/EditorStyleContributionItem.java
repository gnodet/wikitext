/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.mylyn.internal.sandbox.ui.editors.TaskEditorExtensions.RegisteredTaskEditorExtension;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.actions.CompoundContributionItem;

/**
 * Contributes a menu which can be used to select editor styles in the Task Repositories view.
 * 
 * @author Jingwen Ou
 */
public class EditorStyleContributionItem extends CompoundContributionItem {

	private class EditorStyleContributionAction extends Action {

		private final RegisteredTaskEditorExtension editorExtension;

		public EditorStyleContributionAction(RegisteredTaskEditorExtension editorExtension) {
			super(editorExtension.getName());
			setId(editorExtension.getId() + ".action");
			this.editorExtension = editorExtension;
		}

		// default constructor only needs specifying id and name
		public EditorStyleContributionAction(String id, String name) {
			super(name);
			setId(id);
			this.editorExtension = null;
		}

		@Override
		public void run() {
			if (taskRepository != null) {
				TaskEditorExtensions.setTaskEditorExtensionId(taskRepository,
						editorExtension != null ? editorExtension.getId() : "");
				setChecked(true);
			}
		}
	}

	private final TaskRepository taskRepository;

	public EditorStyleContributionItem() {
		this(null);
	}

	public EditorStyleContributionItem(String id) {
		// returns the selecting TaskRepository
		this(id, TasksUiUtil.getSelectedRepository());
	}

	public EditorStyleContributionItem(String id, TaskRepository taskRepository) {
		super(id);
		this.taskRepository = taskRepository;
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		List<IContributionItem> items = new ArrayList<IContributionItem>();

		EditorStyleContributionAction noEditorStyleContributionAction = new EditorStyleContributionAction(
				"org.eclipse.mylyn.sandbox.ui.action.none", "None");
		items.add(new ActionContributionItem(noEditorStyleContributionAction));
		String defaultExtensionId = TaskEditorExtensions.getTaskEditorExtensionId(taskRepository);
		if (defaultExtensionId == null || defaultExtensionId.equals("")) {
			noEditorStyleContributionAction.setChecked(true);
		}

		SortedSet<RegisteredTaskEditorExtension> allEditorExtensions = TaskEditorExtensions.getTaskEditorExtensions();
		for (RegisteredTaskEditorExtension editorExtension : allEditorExtensions) {
			Action editorStyleAction = new EditorStyleContributionAction(editorExtension);
			ActionContributionItem item = new ActionContributionItem(editorStyleAction);
			if (editorExtension.getId().equals(defaultExtensionId)) {
				editorStyleAction.setChecked(true);
			}

			items.add(item);
		}

		return items.toArray(new IContributionItem[items.size()]);
	}
}
