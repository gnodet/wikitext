/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


/**
 * Wizard page for web-based new XPlanner task wizard
 * 
 * @author Ravi Kumar 
 * @author Helen Bershadskaya
 */
public class NewXPlannerTaskPage extends WizardPage {
	
	public NewXPlannerTaskPage() {
		super(Messages.NewXPlannerTaskPage_NEW_TASK);
		setTitle(Messages.NewXPlannerTaskPage_CREATE_VIA_BROWSER);
		setDescription(Messages.NewXPlannerTaskPage_ONCE_SUBMITTED_SYNCHRONIZE_QUERIES
				+ Messages.NewXPlannerTaskPage_NOTE_TO_LOG_IN);
	}

	public void createControl(Composite parent) {
		Label label = new Label(parent, SWT.NULL);
		setControl(label);
//		Text text = new Text(parent, SWT.WRAP);
//		text.setEditable(false);
//		text.setText("\nThis will open a web browser that can be used to create new task.\n" +
//				"Once submitted you can refresh a corresponding query or add the task to a category.");
//		setControl(text);
//		parent.setFocus();
	}
	
}

