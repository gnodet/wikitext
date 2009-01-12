/*******************************************************************************
 * Copyright (c) 2004, 2008 Jingwen Ou and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jingwen Ou - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorExtension;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;

/**
 * Contributes a menu which can be used to select editor styles in the Task Repositories view.
 * 
 * @author Jingwen Ou
 */
public class EditorStyleContributionItem extends CompoundContributionItem {

	public EditorStyleContributionItem() {
		this(null);
	}

	public EditorStyleContributionItem(String id) {
		// returns the selecting TaskRepository
		this(id, TasksUiUtil.getSelectedRepository());
	}

	public EditorStyleContributionItem(String id, TaskRepository taskRepository) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		List<IContributionItem> items = new ArrayList<IContributionItem>();
		items.add(new ActionContributionItem(new InteranlLinkAction()));
		return items.toArray(new IContributionItem[items.size()]);
	}

	private class InteranlLinkAction extends Action {
		public InteranlLinkAction() {
			super("Set Internal Link...");
			setToolTipText("Set the internal link pattern for choosen markup language");
		}

		@Override
		public void run() {
			String title = "Internal link pattern setting";
			String message = "Set the internal link pattern for choosen markup language,\ne.g. http://wiki.eclipse.org/{0}";

			TaskRepository taskRepository = TasksUiUtil.getSelectedRepository();
			String initialValue = taskRepository.getProperty(AbstractTaskEditorExtension.INTERNAL_WIKI_LINK_PATTERN);
			if (initialValue == null) {
				initialValue = "";
			}

			InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					title, message, initialValue, new IInputValidator() {
						public String isValid(String newText) {
							return newText.indexOf("://") == -1 ? "Enter a valid URL" : null;
						}
					});

			if (dialog.open() == Window.OK) {
				setInternalLink(dialog.getValue());
			}
		}
	}

	private static void setInternalLink(String internalLink) {
		TaskRepository taskRepository = TasksUiUtil.getSelectedRepository();
		taskRepository.setProperty(AbstractTaskEditorExtension.INTERNAL_WIKI_LINK_PATTERN, internalLink);
	}

}
