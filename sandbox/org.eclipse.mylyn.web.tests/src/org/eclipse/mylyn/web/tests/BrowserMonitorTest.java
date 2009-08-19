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

package org.eclipse.mylyn.web.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.web.ui.BrowserMonitor;

/**
 * @author Mik Kersten
 */
public class BrowserMonitorTest extends TestCase {

	private final BrowserMonitor browserMonitor = new BrowserMonitor();

	public void testUrlFilter() {
		browserMonitor.setAcceptedUrls("url1,url2,url3");
		assertEquals(3, browserMonitor.getAcceptedUrls().size());

		browserMonitor.setAcceptedUrls(null);
		assertEquals(0, browserMonitor.getAcceptedUrls().size());

		browserMonitor.setAcceptedUrls("");
		assertEquals(0, browserMonitor.getAcceptedUrls().size());
	}

}
