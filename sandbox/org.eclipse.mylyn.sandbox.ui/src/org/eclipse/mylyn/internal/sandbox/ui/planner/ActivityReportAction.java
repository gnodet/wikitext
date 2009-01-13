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

package org.eclipse.mylyn.internal.sandbox.ui.planner;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Rob Elves
 */
public class ActivityReportAction extends Action {

	private static final String LABEL = "Activity Report";

	public static final String ID = "org.eclipse.mylyn.tasklist.actions.report.activity";

	public ActivityReportAction() {
		super(LABEL);
		setId(ID);
		setText(LABEL);
		setToolTipText(LABEL);
		setImageDescriptor(TasksUiImages.TASKS_VIEW);
	}

	@Override
	public void run() {
		TaskActivityWizard wizard = new TaskActivityWizard();

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		if (shell != null && !shell.isDisposed()) {

			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.setBlockOnOpen(true);
			dialog.open();

		} else {
			// ignore
		}
	}
}
