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

import java.io.IOException;
import java.util.List;

import javax.security.auth.login.LoginException;

import junit.framework.TestCase;

import org.eclipse.mylar.core.core.tests.support.MylarTestUtils;
import org.eclipse.mylar.core.core.tests.support.MylarTestUtils.Credentials;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaException;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaServerFacade;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskRepository;

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

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		repository = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND, IBugzillaConstants.TEST_BUGZILLA_222_URL);
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

	/**
	 * @throws LoginException
	 *             Username / Password invalid
	 * @throws IOException
	 *             Network or parse failed
	 * @throws BugzillaException
	 *             Midair collision, or other bugzilla failure. See
	 *             {@link BugzillaServerFacade.parseHtmlError}
	 */
	public void testValidateCredentials() throws LoginException, IOException, BugzillaException {
		BugzillaServerFacade.validateCredentials(null, repository.getUrl(), repository.getUserName(), repository
				.getPassword());
	}

	public void testGetBug() throws Exception {
		RepositoryTaskData taskData = BugzillaServerFacade.getBug(repository.getUrl(), repository.getUserName(),
				repository.getPassword(), null, repository.getCharacterEncoding(), 1);
		assertNotNull(taskData);
		assertEquals("user@mylar.eclipse.org", taskData.getAssignedTo());

		// Notice that comment 0 is the Bug Description
		assertEquals("foo", taskData.getComments().get(0).getText());
		assertEquals("nhapke@cs.ubc.ca", taskData.getComments().get(0).getAuthor());

		// You can use the getAttributeValue to pull up the information on any
		// part of the bug
		assertEquals("P1", taskData.getAttributeValue(BugzillaReportElement.PRIORITY.getKeyString()));
	}

	public void testGetProductList() throws Exception {
		List<String> products = BugzillaServerFacade.getProductList(repository.getUrl(), null,
				repository.getUserName(), repository.getPassword(), repository.getCharacterEncoding());

		assertEquals(3, products.size());
		assertTrue(products.contains("Read Only Test Cases"));
		assertTrue(products.contains("Read Write Test Cases"));
		assertTrue(products.contains("TestProduct"));
	}

	public void testQueryBugs() throws Exception {

		// Not yet refactored. See bug 155279

		// BugzillaResultCollector collector = new BugzillaResultCollector();
		// collector.setProgressMonitor(new NullProgressMonitor());
		// BugzillaSearchOperation operation = new BugzillaSearchOperation(
		// repository,
		// "http://mylar.eclipse.org/bugs222/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=search-match-test&product=TestProduct&long_desc_type=substring&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&deadlinefrom=&deadlineto=&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&emailassigned_to1=1&emailtype1=substring&email1=&emailassigned_to2=1&emailreporter2=1&emailcc2=1&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=",
		// null, collector, "-1");
		// operation.run(new NullProgressMonitor());
		//
		// assertEquals(2, collector.getResults().size());
		// for (BugzillaSearchHit hit : collector.getResults()) {
		// assertTrue(hit.getDescription().startsWith("search-match-test"));
		// }
	}
}
