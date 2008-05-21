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
import org.eclipse.mylyn.xplanner.ui.XPlannerClientFacade;

/**
 * @author Helen Bershadskaya
 */
public class XPlannerClientFacadeTest extends TestCase {

	public void testGetXPlannerClientWithBadConnectionEnsureNoStatus() throws Exception {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		String goodUrl = repository.getRepositoryUrl();
		try {
			repository.setRepositoryUrl(XPlannerTestUtils.BAD_REPOSITORY_LOCATION);
			// ensure no status message gets displayed during this call
			XPlannerClientFacade.getDefault().getXPlannerClient(repository);
			fail("Expected CoreException");
		} catch (CoreException e) {
			assertEquals("Error connecting", e.getMessage());
		} finally {
			repository.setRepositoryUrl(goodUrl);
		}
	}

}