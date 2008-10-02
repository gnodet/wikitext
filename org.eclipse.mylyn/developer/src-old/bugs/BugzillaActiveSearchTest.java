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

package org.eclipse.mylyn.sandbox.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.context.core.IActiveSearchListener;
import org.eclipse.mylyn.internal.sandbox.bridge.bugs.BugzillaMylynSearch;
import org.eclipse.mylyn.internal.sandbox.bridge.bugs.BugzillaReportInfo;
import org.eclipse.mylyn.internal.sandbox.bridge.bugs.MylynBugsManager;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.TaskListManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.java.tests.search.SearchPluginTestHelper;
import org.eclipse.mylyn.java.tests.search.WorkspaceSetupHelper;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/*
 * TEST CASES TO HANDLE 1. what is here 2. different scopes ( local and remote )
 * 3. no bugs 4. offline bugs
 * 
 * DEGREE OF SEPARATIONS 1 Local bug, qualified reference 2 local bug,
 * unqualified reference 3 remote bug, qualified reference 4 remote bug,
 * unqualified reference 5 NONE
 */

/**
 * Test the bugzilla search functionality of the bridge
 * 
 * @author Shawn Minto
 */
public class BugzillaActiveSearchTest extends TestCase {

	private TaskRepository repository;

	// SHAWNTODO Add tests for the different types of searches (local qual,
	// local unqual, fully qual, unqual) and mock up a bugs db for testing

	/** The expected number of results when searching for astNode */
	// SHAWNTODO add back in when we have a test server mocked up
	// private static final int NUM_AST_RESULTS = 302;
	//	
	// private static final int NUM_AST_SETSOURCERANGE_RESULTS = 15;
	/** list to add collectors to when notified */
	private final List<List<?>> lists = new ArrayList<List<?>>();

	private IType astNodeType;

	@Override
	protected void setUp() throws Exception {
		WorkspaceSetupHelper.setupWorkspace();
		repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
		TasksUiPlugin.getRepositoryManager().addRepository(repository);

		IJavaProject jp = WorkspaceSetupHelper.getJdtCoreDomProject();
		astNodeType = WorkspaceSetupHelper.getType(jp, "org.eclipse.jdt.core.dom.ASTNode");
	}

	@Override
	protected void tearDown() throws Exception {
		WorkspaceSetupHelper.clearDoiModel();
		TasksUiPlugin.getRepositoryManager().removeRepository(repository,
				TasksUiPlugin.getDefault().getRepositoriesFilePath());
	}

	/**
	 * Test adding and removing ISearchCompletedListeners
	 */
	public void testSearchCompletedListenerAddAndRemove() {
		lists.clear();

		// create 2 listeners
		IActiveSearchListener l1 = new IActiveSearchListener() {
			private boolean gathered = false;

			public void searchCompleted(List<?> l) {
				lists.add(l);
				gathered = true;
			}

			public boolean resultsGathered() {
				return gathered;
			}
		};
		IActiveSearchListener l2 = new IActiveSearchListener() {
			private boolean gathered = false;

			public void searchCompleted(List<?> l) {
				lists.add(l);
				gathered = true;
			}

			public boolean resultsGathered() {
				return gathered;
			}
		};

		BugzillaMylynSearch s = new BugzillaMylynSearch(BugzillaMylynSearch.UNQUAL, astNodeType,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL);

		// add the first listener
		s.addListener(l1);
		// remove the first listener
		s.removeListener(l1);

		// perform the search
		SearchPluginTestHelper.search(s, l2);

		// make sure that only the second listener added has any results left
		assertTrue("listener was not removed", lists.size() >= 1 && !l1.resultsGathered());
		assertTrue("listener was not added", lists.size() == 1);

		// display the time it took for the search
		MylynBugsManager.getBridge().removeFromLandmarksHash(astNodeType);
	}

	/**
	 * Tests that the bridge gets the right data for us This test is wierd because it waits on results.
	 */
	public void testBridge() {
		lists.clear();
		BugzillaMylynSearch s = new BugzillaMylynSearch(BugzillaMylynSearch.UNQUAL, astNodeType,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL);

		IActiveSearchListener l = new IActiveSearchListener() {
			private boolean gathered = false;

			public void searchCompleted(List<?> results) {
				lists.add(results);
				gathered = true;
			}

			public boolean resultsGathered() {
				return gathered;
			}
		};

		// perform the search
		SearchPluginTestHelper.search(s, l);

		// make sure we got the right number of bugs back
		assertTrue("No collector returned", lists.size() != 0);
		List<?> c = lists.get(0);
		assertTrue("Results not the right size", c.size() > 0); // TODO should
		// be
		// assertEquals
		// on expected
		// size

		// display the time it took for the search and the results returned
		MylynBugsManager.getBridge().removeFromLandmarksHash(astNodeType);

	}

	/**
	 * Tests that the bridge saves the results of a search so that it can be used later
	 */
	public void testSaveResults() {
		lists.clear();
		BugzillaMylynSearch s = new BugzillaMylynSearch(BugzillaMylynSearch.UNQUAL, astNodeType,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL);

		IActiveSearchListener l = new IActiveSearchListener() {
			private boolean gathered = false;

			public void searchCompleted(List<?> results) {
				lists.add(results);
				gathered = true;
			}

			public boolean resultsGathered() {
				return gathered;
			}
		};

		// perform the search
		SearchPluginTestHelper.search(s, l);

		// do an inital search
		assertTrue("No collectors returned", lists.size() != 0);
		List<?> c = lists.get(0);
		assertTrue("Results not the right size", c.size() > 0);
		// TODO should be assertEquals on expected size

		// check that the search has been saved
		List<BugzillaReportInfo> saved = MylynBugsManager.getBridge().getFromLandmarksHash(astNodeType,
				BugzillaMylynSearch.UNQUAL);
		assertTrue("Results not cached", saved != null);
		if (saved == null) {
			fail();
		} else {
			assertTrue("Results not the right size", saved.size() > 0);
			// TODO should be assertEquals on expected size

			assertTrue(c.containsAll(saved) && saved.containsAll(c));
			MylynBugsManager.getBridge().removeFromLandmarksHash(astNodeType);
		}
	}

	public void testLocalBugUnqual() throws InterruptedException {
		lists.clear();

		String bugPrefix = "<server>-";

		TaskListManager manager = TasksUiPlugin.getTaskListManager();
		TaskCategory cat = new TaskCategory("Testing Category");
		manager.getTaskList().addCategory(cat);
		AbstractTask bugTask1 = new TaskTask(bugPrefix, "" + 94185, "<bugzilla info>");

		manager.getTaskList().addTask(bugTask1, cat);
		// cat.addTask(bugTask1);
		while (bugTask1.isSynchronizing()) {
			Thread.sleep(500);
		}
		AbstractTask bugTask2 = new TaskTask(bugPrefix, "" + 3692, "<bugzilla info>");
		manager.getTaskList().addTask(bugTask2, cat);
		// cat.addTask(bugTask2);
		while (bugTask2.isSynchronizing()) {
			Thread.sleep(500);
		}
		AbstractTask bugTask3 = new TaskTask(bugPrefix, "" + 3693, "<bugzilla info>");
		manager.getTaskList().addTask(bugTask3, cat);
		// cat.addTask(bugTask3);
		while (bugTask3.isSynchronizing()) {
			Thread.sleep(500);
		}

		AbstractTask bugTask4 = new TaskTask(bugPrefix, "" + 9583, "<bugzilla info>");
		manager.getTaskList().addTask(bugTask4, cat);
		// cat.addTask(bugTask4);
		while (bugTask4.isSynchronizing()) {
			Thread.sleep(500);
		}

		BugzillaMylynSearch s = new BugzillaMylynSearch(BugzillaMylynSearch.LOCAL_UNQUAL, astNodeType,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL);

		IActiveSearchListener l = new IActiveSearchListener() {
			private boolean gathered = false;

			public void searchCompleted(List<?> results) {
				lists.add(results);
				gathered = true;
			}

			public boolean resultsGathered() {
				return gathered;
			}
		};

		// perform the search
		SearchPluginTestHelper.search(s, l);

		// do an inital search
		assertTrue("No collectors returned", lists.size() != 0);
		List<?> c = lists.get(0);
		assertEquals("Results not the right size", 3, c.size());

		MylynBugsManager.getBridge().removeFromLandmarksHash(astNodeType);
		TasksUiPlugin.getTaskList().deleteCategory(cat);
	}

	// TODO need to test a bug that wraps...should fail since we can only search
	// on a single line
	public void testLocalBugFullyQual() throws InterruptedException {
		lists.clear();

		String bugPrefix = "Bugzilla-";

		TaskListManager manager = TasksUiPlugin.getTaskListManager();
		TaskCategory cat = new TaskCategory("Testing Category");
		manager.getTaskList().addCategory(cat);
		AbstractTask bugTask1 = new TaskTask(bugPrefix, "" + 94185, "<bugzilla info>");
		manager.getTaskList().addTask(bugTask1, cat);
		// cat.addTask(bugTask1);
		while (bugTask1.isSynchronizing()) {
			Thread.sleep(500);
		}

		AbstractTask bugTask2 = new TaskTask(bugPrefix, "" + 9583, "<bugzilla info>");
		manager.getTaskList().addTask(bugTask2, cat);
		// cat.addTask(bugTask2);
		while (bugTask2.isSynchronizing()) {
			Thread.sleep(500);
		}
		AbstractTask bugTask3 = new TaskTask(bugPrefix, "" + 3693, "<bugzilla info>");
		manager.getTaskList().addTask(bugTask3, cat);
		// cat.addTask(bugTask3);
		while (bugTask3.isSynchronizing()) {
			Thread.sleep(500);
		}

		BugzillaMylynSearch s = new BugzillaMylynSearch(BugzillaMylynSearch.LOCAL_QUAL, astNodeType,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL);

		IActiveSearchListener l = new IActiveSearchListener() {
			private boolean gathered = false;

			public void searchCompleted(List<?> results) {
				lists.add(results);
				gathered = true;
			}

			public boolean resultsGathered() {
				return gathered;
			}
		};

		// perform the search
		SearchPluginTestHelper.search(s, l);

		// do an inital search
		assertTrue("No collectors returned", lists.size() != 0);
		List<?> c = lists.get(0);
		assertEquals("Results not the right size", 1, c.size());

		MylynBugsManager.getBridge().removeFromLandmarksHash(astNodeType);
		TasksUiPlugin.getTaskList().deleteCategory(cat);
	}

}
