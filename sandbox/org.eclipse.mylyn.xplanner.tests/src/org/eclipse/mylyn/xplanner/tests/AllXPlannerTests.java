/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Helen Bershadskaya
 */
public class AllXPlannerTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for mylyn.xplanner.tests");

		// $JUnit-BEGIN$
		suite.addTestSuite(XPlannerRepositoryUtilsTest.class);
		suite.addTestSuite(XPlannerRepositoryConnectorTest.class);
		suite.addTestSuite(XPlannerCustomQueryTest.class);
		suite.addTestSuite(XPlannerTaskDataHandlerTest.class);
		suite.addTestSuite(XPlannerTaskEditorTest.class);
		suite.addTestSuite(XPlannerMylynUIPluginTest.class);
		suite.addTestSuite(XPlannerQueryWizardUtilsTest.class);
		// $JUnit-END$
		return suite;
	}
}
