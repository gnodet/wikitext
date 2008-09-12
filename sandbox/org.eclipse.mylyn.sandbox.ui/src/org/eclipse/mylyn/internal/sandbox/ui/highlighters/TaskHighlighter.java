/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.highlighters;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.mylyn.internal.sandbox.ui.SandboxUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.ITaskHighlighter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.graphics.Color;

/**
 * Decorator that provides a background color for tasks.
 * 
 * NOTE: Setting the background color through a ILightweightLabelDecorator does not seem to work on the task list.
 * 
 * TODO remove ITaskHighlighter and replace by ILightweightLabelDecorator
 * 
 * @author Steffen Pingel
 */
public class TaskHighlighter implements ILightweightLabelDecorator, ITaskHighlighter {

	public TaskHighlighter() {
		TasksUiPlugin.getDefault().setHighlighter(this);
	}

	public void addListener(ILabelProviderListener listener) {
		// ignore
	}

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof ITask) {
			ITask task = (ITask) element;
			Highlighter highlighter = SandboxUiPlugin.getDefault().getHighlighterForContextId(
					"" + task.getHandleIdentifier());
			if (highlighter != null) {
				decoration.setBackgroundColor(highlighter.getHighlightColor());
			}
		}
	}

	public void dispose() {
		// ignore
	}

	public Color getHighlightColor(ITask task) {
		Highlighter highlighter = SandboxUiPlugin.getDefault().getHighlighterForContextId(
				"" + task.getHandleIdentifier());
		if (highlighter != null) {
			return highlighter.getHighlightColor();
		}
		return null;
	}

	public boolean isLabelProperty(Object element, String property) {
		// ignore
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// ignore
	}

}
