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

package org.eclipse.mylyn.internal.sandbox.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * This action is not persistent, in order to avoid Mylyn not working on startup.
 * 
 * @author Mik Kersten
 */
public class ContextCapturePauseAction extends Action implements IViewActionDelegate, IActionDelegate2 {

	protected IAction initAction = null;

	private final AbstractContextListener CONTEXT_LISTENER = new AbstractContextListener() {
		@Override
		public void contextActivated(IInteractionContext context) {
			resume();
			setChecked(false);
			if (initAction != null) {
				initAction.setChecked(false);
			}
		}
	};

	public ContextCapturePauseAction() {
		ContextCore.getContextManager().addListener(CONTEXT_LISTENER);
	}

	public void init(IViewPart view) {
		// ignore
	}

	public void init(IAction action) {
		// ignore
	}

	public void dispose() {
		ContextCore.getContextManager().removeListener(CONTEXT_LISTENER);
	}

	public void run(IAction action) {
		initAction = action;
		setChecked(!action.isChecked());
		if (isChecked()) {
			resume();
		} else {
			pause();
		}
	}

	public void pause() {
		ContextCore.getContextManager().setContextCapturePaused(true);
		TaskListView.getFromActivePerspective().indicatePaused(true);
	}

	public void resume() {
		ContextCore.getContextManager().setContextCapturePaused(false);
		if (TaskListView.getFromActivePerspective() != null) {
			TaskListView.getFromActivePerspective().indicatePaused(false);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

	public void runWithEvent(IAction action, Event event) {
		// ignore

	}
}
