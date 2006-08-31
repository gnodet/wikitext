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

import java.util.ArrayList;
import java.util.List;

import junit.extensions.ActiveTestSuite;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.internal.sandbox.web.WebRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractQueryHitCollector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.RepositoryTemplate;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * @author Eugene Kuleshov 
 */
public class WebRepositoryConnectorTest extends TestCase {

	private final RepositoryTemplate template;
	
	public WebRepositoryConnectorTest(RepositoryTemplate template) {
		super("testRepositoryTemplate");
		this.template = template;
	}

	public void testRepositoryTemplate() throws Exception {
		StringBuffer buffer = WebRepositoryConnector.fetchResource(template.taskQueryUrl);
		
		IProgressMonitor monitor = new NullProgressMonitor();
		MultiStatus queryStatus = new MultiStatus(TasksUiPlugin.PLUGIN_ID, IStatus.OK, "Query result", null);
		final List<AbstractQueryHit> hits = new ArrayList<AbstractQueryHit>();
		AbstractQueryHitCollector collector = new AbstractQueryHitCollector(TasksUiPlugin.getTaskListManager().getTaskList()) {

			@Override
			public void addMatch(AbstractQueryHit hit) {
				hits.add(hit);
				
			}
			
		};
		
		IStatus resultingStatus = WebRepositoryConnector.performQuery(buffer, template.getAttribute(WebRepositoryConnector.TASK_REGEXP), template.taskPrefixUrl, template.repositoryUrl, monitor, collector);		
		assertTrue(template.taskQueryUrl+"\n"+template.getAttribute(WebRepositoryConnector.TASK_REGEXP)+"\n"+resultingStatus.toString(), queryStatus.isOK());
		assertTrue("Expected non-empty query result\n"+template.taskQueryUrl+"\n"+template.getAttribute(WebRepositoryConnector.TASK_REGEXP), hits.size()>0);
	}

	public String getName() {
		return template.label;
	}
	
	public static TestSuite suite() {
		TestSuite suite = new ActiveTestSuite(WebRepositoryConnectorTest.class.getName());
		
		AbstractRepositoryConnector repositoryConnector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(WebRepositoryConnector.REPOSITORY_TYPE);
		for (RepositoryTemplate template : repositoryConnector.getTemplates()) {
			suite.addTest(new WebRepositoryConnectorTest(template));
		}
		
		return suite;
	}

}
