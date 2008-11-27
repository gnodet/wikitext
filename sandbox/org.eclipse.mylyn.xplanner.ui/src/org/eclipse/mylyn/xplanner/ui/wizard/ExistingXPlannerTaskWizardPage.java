/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui.wizard;

import org.eclipse.mylyn.internal.tasks.ui.wizards.ExistingTaskWizardPage;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
//TODO 3.1 remove class
@Deprecated
@SuppressWarnings("restriction")
public class ExistingXPlannerTaskWizardPage extends ExistingTaskWizardPage {

	private static final String DESCRIPTION = Messages.ExistingXPlannerTaskWizardPage_ENTER_XPLANNER_IDENTIFIER
			+ Messages.ExistingXPlannerTaskWizardPage_PREFIX_MESSAGE;

	private static final String DESCRIPTION_TEMP = Messages.ExistingXPlannerTaskWizardPage_ENTER_TASK_ID;

	public ExistingXPlannerTaskWizardPage() {
		super();
		setDescription(DESCRIPTION);
		setDescription(DESCRIPTION_TEMP);
	}

}
