/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests.performance;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.mylyn.resources.tests.performance.ResourceChangeMonitorPerformanceTest;

/**
 * @author Shawn Minto
 */
public class AllResourcesPerformanceTests {
	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.mylyn.resources.tests.performance");
		suite.addTestSuite(ResourceChangeMonitorPerformanceTest.class);
		return suite;
	}
}
