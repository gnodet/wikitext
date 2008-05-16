/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.highlighters;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.mylyn.internal.context.ui.HighlighterImageDescriptor;
import org.eclipse.mylyn.internal.sandbox.ui.SandboxUiPlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;

/**
 * @author Mik Kersten
 */
public class TaskHighlighterMenuContributor implements IDynamicSubMenuContributor {

	private static final String CHOOSE_HIGHLIGHTER = "Highlighter";

	public MenuManager getSubMenuManager(final List<ITaskElement> selectedElements) {
		final MenuManager subMenuManager = new MenuManager(CHOOSE_HIGHLIGHTER);
		for (final Highlighter highlighter : SandboxUiPlugin.getDefault().getHighlighters()) {
			Action action = new Action() {
				@Override
				public void run() {
					AbstractTask task = null;
					for (ITaskElement selectedElement : selectedElements) {
						if (selectedElement instanceof ITask) {
							task = (AbstractTask) selectedElement;
						}
						if (task != null) {
							SandboxUiPlugin.getDefault().setHighlighterMapping(task.getHandleIdentifier(),
									highlighter.getName());
							TasksUiInternal.getTaskList().notifyElementChanged(task);
						}
					}
				}
			};
			if (highlighter.isGradient()) {
				action.setImageDescriptor(new HighlighterImageDescriptor(highlighter.getBase(),
						highlighter.getHighlightColor()));
			} else {
				action.setImageDescriptor(new HighlighterImageDescriptor(highlighter.getHighlightColor(),
						highlighter.getHighlightColor()));
			}
			action.setText(highlighter.toString());
			subMenuManager.add(action);
		}
		subMenuManager.add(new Separator());
		subMenuManager.add(new EditHighlightersAction());
		return subMenuManager;
	}
}
