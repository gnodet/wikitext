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

package org.eclipse.mylyn.sandbox.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.tasks.tests.web.HtmlDecodeEntityTest;
import org.eclipse.mylyn.tasks.tests.web.NamedPatternTest;
import org.eclipse.mylyn.tasks.tests.web.WebRepositoryConnectorTest;
import org.eclipse.mylyn.tasks.tests.web.WebRepositoryTest;

/**
 * @author Mik Kersten
 */
public class AllSandboxTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.sandbox.tests");

		suite.addTestSuite(TaskReportGeneratorTest.class);
		suite.addTestSuite(PredictedErrorInterestTest.class);
		suite.addTestSuite(ActiveHierarchyTest.class);
		suite.addTestSuite(ActiveSearchTest.class);
		suite.addTestSuite(StatisticsReportingTest.class);
		suite.addTestSuite(SharedTaskFolderTest.class);

		// web connector tests
		suite.addTestSuite(NamedPatternTest.class);
		suite.addTestSuite(HtmlDecodeEntityTest.class);
		suite.addTestSuite(WebRepositoryTest.class);
		suite.addTestSuite(WebRepositoryConnectorTest.class);

		return suite;
	}
}
