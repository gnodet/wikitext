/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.xplanner.ui.wizard.AbstractXPlannerQueryWizardPage;
import org.eclipse.mylyn.xplanner.ui.wizard.ErrorQueryPage;
import org.eclipse.mylyn.xplanner.ui.wizard.NewXPlannerQueryWizard;
import org.eclipse.mylyn.xplanner.ui.wizard.XPlannerCustomQueryPage;
import org.eclipse.mylyn.xplanner.ui.wizard.XPlannerQuerySelectionWizardPage;
import org.eclipse.mylyn.xplanner.ui.wizard.XPlannerQueryWizardUtils;

public class XPlannerQueryWizardUtilsTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		XPlannerTestUtils.removeXPlannerRepository();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testBadCredentialsPage() {
		try {
			TaskRepository badRepository = XPlannerTestUtils.getRepository("baduser", "badpassword");
			NewXPlannerQueryWizard wizard = new NewXPlannerQueryWizard(badRepository);
			AbstractXPlannerQueryWizardPage queryPage = XPlannerQueryWizardUtils.addQueryWizardFirstPage(wizard,
					badRepository, null);
			assertNotNull(queryPage);
			assertTrue(queryPage instanceof ErrorQueryPage);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testGoodCredentialsPage() {
		try {
			TaskRepository goodRepository = XPlannerTestUtils.getRepository();
			NewXPlannerQueryWizard wizard = new NewXPlannerQueryWizard(goodRepository);
			AbstractXPlannerQueryWizardPage queryPage = XPlannerQueryWizardUtils.addQueryWizardFirstPage(wizard,
					goodRepository, null);
			assertNotNull(queryPage);
			assertTrue(queryPage instanceof XPlannerCustomQueryPage
					|| queryPage instanceof XPlannerQuerySelectionWizardPage);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testNoConnectionPage() {
		try {
			NewXPlannerQueryWizard wizard = new NewXPlannerQueryWizard(null);
			XPlannerQueryWizardUtils.addQueryWizardFirstPage(wizard, null, null);
			fail("no exception thrown on bad query page creation");

		} catch (Exception e) {
			assertFalse(e.getCause() instanceof CoreException);
		}
	}

}
