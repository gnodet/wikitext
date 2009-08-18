/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.usage.wizards;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.monitor.usage.UiUsageMonitorPlugin;
import org.eclipse.swt.widgets.Shell;

/**
 * Wizard Dialog that sets the appropriate flag when the wizard is open
 * 
 * @author Shawn Minto
 */
public class UsageSubmissionWizardDialog extends WizardDialog {
	public UsageSubmissionWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
	}

	@Override
	public int open() {
		UiUsageMonitorPlugin.getDefault().setSubmissionWizardOpen(true);
		return super.open();
	}

	@Override
	public boolean close() {
		UiUsageMonitorPlugin.getDefault().setSubmissionWizardOpen(false);
		return super.close();
	}
}