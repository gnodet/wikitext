/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.sandbox.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllSandboxTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.sandbox.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(PredictedErrorInterestTest.class);
		suite.addTestSuite(ActiveHierarchyTest.class);
		suite.addTestSuite(ActiveSearchTest.class);
		suite.addTestSuite(StatisticsReportingTest.class);
		suite.addTestSuite(SharedTaskFolderTest.class);
		suite.addTestSuite(BugzillaActiveSearchTest.class);
		//$JUnit-END$
		return suite;
	}

}
