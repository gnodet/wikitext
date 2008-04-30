/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.tests;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.commons.core.IStatusHandler;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.xplanner.ui.XPlannerClientFacade;

public class XPlannerClientFacadeTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetXPlannerClientWithBadConnectionEnsureNoStatus() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		String goodUrl = repository.getRepositoryUrl();
		repository.setRepositoryUrl(XPlannerTestUtils.BAD_REPOSITORY_LOCATION);

		IStatusHandler fakeHandler = new IStatusHandler() {

			public void displayStatus(String title, IStatus status) {
				Assert.fail("testGetXPlannerClientWithBadConnectionEnsureNoStatus() -- status handler called");
			}

			public void fail(IStatus status, boolean informUser) {
				Assert.fail("testGetXPlannerClientWithBadConnectionEnsureNoStatus() -- status handler called");
			}

		};

		StatusHandler.addStatusHandler(fakeHandler);
		try {
			// ensure no status message gets displayed during this call
			XPlannerClientFacade.getDefault().getXPlannerClient(repository);
		} catch (CoreException e) {
			assertTrue(e.getMessage() != null && e.getMessage().contains("Error connecting"));
		} finally {
			StatusHandler.removeStatusHandler(fakeHandler);
			repository.setRepositoryUrl(goodUrl);
		}
	}

}