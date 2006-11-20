/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.bugzilla.tests.headless;

import junit.framework.TestCase;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.context.tests.support.MylarTestUtils;
import org.eclipse.mylar.context.tests.support.MylarTestUtils.Credentials;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.QueryHitCollector;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskList;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;

/**
 * 
 * Runs headless (can be run as regular junit test without platform plugin
 * support).
 * 
 * @author Rob Elves
 * @author Nathan Hapke
 */
public class BugzillaQueryTest extends TestCase {

	private TaskRepository repository;
	private BugzillaRepositoryConnector connector;
	private IOfflineTaskHandler handler;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		connector = (BugzillaRepositoryConnector) TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				BugzillaCorePlugin.REPOSITORY_KIND);
		handler = connector.getOfflineTaskHandler();
		repository = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);
		Credentials credentials = MylarTestUtils.readCredentials();
		repository.setAuthenticationCredentials(credentials.username, credentials.password);
	}
	
	
	/**
	 * This is the first test so that the repository credentials are correctly
	 * set for the other tests
	 */
	public void testAddCredentials() {
		if (!repository.hasCredentials()) {
			Credentials credentials = MylarTestUtils.readCredentials();
			repository.setAuthenticationCredentials(credentials.username, credentials.password);

			assertTrue(repository.hasCredentials());
		}
	}

//	public void testValidateCredentials() throws IOException, BugzillaException, KeyManagementException,
//			GeneralSecurityException {
//		BugzillaClient.validateCredentials(null, repository.getUrl(), repository.getCharacterEncoding(),
//				repository.getUserName(), repository.getPassword());
//	}
//
//	public void testValidateCredentialsInvalidProxy() throws IOException, BugzillaException, KeyManagementException,
//			GeneralSecurityException {
//		BugzillaClient.validateCredentials(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 12356)),
//				repository.getUrl(), repository.getCharacterEncoding(), repository.getUserName(), repository
//						.getPassword());
//	}

//	public void testCredentialsEncoding() throws IOException, BugzillaException, KeyManagementException,
//			GeneralSecurityException {
//		String poundSignUTF8 = BugzillaClient.addCredentials(IBugzillaConstants.TEST_BUGZILLA_222_URL, "UTF-8",
//				"testUser", "\u00A3");
//		assertTrue(poundSignUTF8.endsWith("password=%C2%A3"));
//		String poundSignISO = BugzillaClient.addCredentials(IBugzillaConstants.TEST_BUGZILLA_222_URL,
//				"ISO-8859-1", "testUser", "\u00A3");
//		assertFalse(poundSignISO.contains("%C2%A3"));
//		assertTrue(poundSignISO.endsWith("password=%A3"));
//	}

	public void testGetBug() throws Exception {
		RepositoryTaskData taskData = handler.downloadTaskData(repository, "1");
		assertNotNull(taskData);
		assertEquals("user@mylar.eclipse.org", taskData.getAssignedTo());

		assertEquals("foo", taskData.getDescription());

		// You can use the getAttributeValue to pull up the information on any
		// part of the bug
		assertEquals("P1", taskData.getAttributeValue(BugzillaReportElement.PRIORITY.getKeyString()));
	}

	public void testQueryViaConnector() throws Exception {
		String queryUrlString = repository.getUrl()
				+ "/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=search-match-test&product=TestProduct&long_desc_type=substring&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&deadlinefrom=&deadlineto=&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&emailassigned_to1=1&emailtype1=substring&email1=&emailassigned_to2=1&emailreporter2=1&emailcc2=1&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=";

		// holds onto actual hit objects
		TaskList taskList = new TaskList();
		QueryHitCollector collector = new QueryHitCollector(new TaskList());
		BugzillaRepositoryConnector connector = new BugzillaRepositoryConnector();
		connector.init(taskList);
		BugzillaRepositoryQuery query = new BugzillaRepositoryQuery(repository.getUrl(), queryUrlString, "description",
				"-1", taskList);
		connector.performQuery(query, repository, new NullProgressMonitor(), collector);
		assertEquals(2, collector.getHits().size());
		for (AbstractQueryHit hit : collector.getHits()) {
			assertTrue(hit.getDescription().contains("search-match-test"));
		}
	}
}