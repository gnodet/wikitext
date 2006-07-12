/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.sandbox.tests;

import java.util.Arrays;
import java.util.List;

import junit.extensions.ActiveTestSuite;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.internal.sandbox.web.WebRepositoryConnector;
import org.eclipse.mylar.internal.sandbox.web.WebRepositoryTemplate;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;

/**
 * @author Eugene Kuleshov 
 */
public class WebRepositoryConnectorTest extends TestCase {

	private final WebRepositoryTemplate template;
	
	public WebRepositoryConnectorTest(WebRepositoryTemplate template) {
		super("testRepositoryTemplate");
		this.template = template;
	}

	public void testRepositoryTemplate() throws Exception {
		StringBuffer buffer = WebRepositoryConnector.fetchResource(template.query);
		
		IProgressMonitor monitor = new NullProgressMonitor();
		MultiStatus queryStatus = new MultiStatus(MylarTaskListPlugin.PLUGIN_ID, IStatus.OK, "Query result", null);
		
		List<AbstractQueryHit> hits = WebRepositoryConnector.performQuery(buffer, template.regexp, template.prefix, template.url, monitor, queryStatus);
		
		assertTrue(Arrays.asList(queryStatus.getChildren()).toString(), queryStatus.isOK());
		assertTrue("Expected non-empty query result", hits.size()>0);
		
	}

	public String getName() {
		return template.label;
	}
	
	public static TestSuite suite() {
		TestSuite suite = new ActiveTestSuite(WebRepositoryConnectorTest.class.getName());
		// TestSuite suite = new TestSuite(WebRepositoryConnectorTest.class.getName());
		
		for (WebRepositoryTemplate template : WebRepositoryConnector.REPOSITORY_TEMPLATES) {
			suite.addTest(new WebRepositoryConnectorTest(template));
		}
		
		return suite;
	}

}
