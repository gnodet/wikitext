/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.xplanner.ui.XPlannerMylynUIPlugin;

public class XPlannerMylynUIPluginTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testStopWithoutFacadeInitialization() {
		XPlannerMylynUIPlugin plugin = XPlannerMylynUIPlugin.getDefault();

		assertNotNull(plugin);

		try {
			plugin.stop(plugin.getBundle().getBundleContext());
		} catch (Exception e) {
			fail("could not stop plugin: " + e.getMessage());
		}
	}
}
