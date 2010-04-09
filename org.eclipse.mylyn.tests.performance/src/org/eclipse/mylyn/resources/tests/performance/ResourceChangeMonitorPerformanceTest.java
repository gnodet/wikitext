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

package org.eclipse.mylyn.resources.tests.performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.internal.resources.ui.ResourceChangeMonitor;
import org.eclipse.mylyn.tests.performance.PerformanceConstants;
import org.eclipse.mylyn.tests.performance.support.CommonTestUtil;
import org.eclipse.test.performance.PerformanceTestCase;

/**
 * @author Shawn Minto
 */
public class ResourceChangeMonitorPerformanceTest extends PerformanceTestCase {

	private Set<IPath> paths;

	private Set<String> excludedPatterns;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		excludedPatterns = new HashSet<String>();
		excludedPatterns.add(ResourceChangeMonitor.createRegexFromPattern("*.class"));
		excludedPatterns.add(ResourceChangeMonitor.createRegexFromPattern("*.tmp"));
		excludedPatterns.add(ResourceChangeMonitor.createRegexFromPattern("~*"));
		excludedPatterns.add(ResourceChangeMonitor.createRegexFromPattern(".*"));
		excludedPatterns.add(ResourceChangeMonitor.createRegexFromPattern("generated"));

		paths = new HashSet<IPath>();
		File pathFile = CommonTestUtil.getFile(this, "testdata/resourceExclusionTestPaths.txt");
		assertNotNull(pathFile);

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(pathFile));
			String line;
			while ((line = br.readLine()) != null) {
				paths.add(new Path(line));
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		assertFalse(paths.isEmpty());

	}

	public void testIsExcluded() {

		for (int i = 0; i < PerformanceConstants.REPEAT; i++) {
			startMeasuring();
			boolean excluded = false;
			for (IPath path : paths) {
				excluded |= ResourceChangeMonitor.isExcluded(path, null, excludedPatterns);
			}
			assertTrue(excluded);

			stopMeasuring();
		}

		commitMeasurements();
		assertPerformance();
	}

}
