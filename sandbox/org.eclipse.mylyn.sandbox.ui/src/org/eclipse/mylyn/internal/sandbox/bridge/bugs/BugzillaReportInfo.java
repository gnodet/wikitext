/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.bridge.bugs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.internal.bugzilla.ui.tasklist.StackTrace;
import org.eclipse.mylyn.tasks.core.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;

/**
 * Class to store the DoiInfo of a BugzillaSearchHit
 * 
 * TODO: refactor
 * 
 * @author Shawn Minto
 */
public class BugzillaReportInfo {

	private static final int MAX_LABEL_LENGTH = 150;

	private static final long serialVersionUID = 3257004367222419506L;

	/** The BugzillaSearchHit associated with this DoiInfo */
	private BugzillaTask hit;

	/** Whether this search hit was from an exact search like a stack trace */
	private boolean isExact = false;

	/** List of all of the StackTrace's in the given bug */
	private List<StackTrace> stackTraces;

	/** The bug report associated with this DoiInfo */
	private RepositoryTaskData bug;

	/**
	 * Constructor
	 * 
	 * @param initialValue
	 *            The initial Doi value
	 * @param hit
	 *            The BugzillaSearchHit associated with this DoiInfo
	 * @param isExact
	 *            Whether the search was exact or not
	 */
	public BugzillaReportInfo(float initialValue, BugzillaTask hit, boolean isExact) {
		this.hit = hit;
		this.isExact = isExact;
		bug = null;
		stackTraces = new ArrayList<StackTrace>();
	}

	/**
	 * Get the bugzilla search hit relating to this DoiInfo
	 * 
	 * @return The BugzillaSearchHit related to this DoiInfo
	 */
	public BugzillaTask getHit() {
		return hit;
	}

	@Override
	public String toString() {
		return hit.toString();
	}

	/**
	 * Determine if the search hit this represents is exact or not
	 * 
	 * @return <code>true</code> if the search was exact otherwise <code>false</code>
	 */
	public boolean isExact() {
		return isExact;
	}

	/**
	 * Set whether this bug has any exact elements in it - the search used was fully qualified
	 * 
	 * @param isExact -
	 *            Whether there are any exact element matches in it
	 */
	public void setExact(boolean isExact) {
		this.isExact = isExact;
	}

	/**
	 * Get the bug report associated with this DoiInfo<br>
	 * The bug is downloaded if it was not previously
	 * 
	 * @return Returns the BugReport
	 */
	public RepositoryTaskData getBug() throws CoreException {
		if (bug == null) {
			// get the bug report
			TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
					BugzillaCorePlugin.REPOSITORY_KIND, hit.getRepositoryUrl());
			BugzillaRepositoryConnector bugzillaConnector = (BugzillaRepositoryConnector) TasksUiPlugin.getRepositoryManager()
					.getRepositoryConnector(BugzillaCorePlugin.REPOSITORY_KIND);
			AbstractTaskDataHandler handler = bugzillaConnector.getTaskDataHandler();
			bug = handler.getTaskData(repository, hit.getTaskId(), new NullProgressMonitor());
		}
		return bug;
	}

	/**
	 * Set the bug report associated with this DoiInfo
	 * 
	 * @param bug -
	 *            BugReport that this is associated with
	 */
	public void setBug(RepositoryTaskData bug) {
		this.bug = bug;
	}

	/**
	 * Get all of the stack traces contained in the bug
	 * 
	 * @return Returns a list of StackTrace's
	 */
	public List<StackTrace> getStackTraces() {
		return stackTraces;
	}

	/**
	 * Determine whether the doi info has any stack traces associated with it
	 * 
	 * @return <code>true</code> if there are some stack traces else <code>false</code>
	 */
	public boolean hasStackTraces() {
		return !stackTraces.isEmpty();
	}

	/**
	 * Add a stack trace to this DoiInfo
	 * 
	 * @param stackTrace -
	 *            The StackTrace to add
	 */
	public void addStackTrace(StackTrace stackTrace) {
		this.stackTraces.add(stackTrace);
	}

	/**
	 * Add an array of stack traces to this DoiInfo
	 * 
	 * @param stackTracesToAdd -
	 *            The StackTraces to add
	 */
	public void addStackTraces(StackTrace[] stackTracesToAdd) {
		for (int i = 0; i < stackTracesToAdd.length; i++)
			this.stackTraces.add(stackTracesToAdd[i]);
	}

	/**
	 * Get the name of the bug report
	 * 
	 * @return The name of the bug report, max 20 characters
	 */
	public String getName() {
		String description = hit.getSummary();
		int length = description.length();
		if (length > MAX_LABEL_LENGTH)
			description = description.substring(0, MAX_LABEL_LENGTH) + "..";
		return "bug " + hit.getTaskId() + ": " + description;
	}

	public String getElementHandle() {
		return hit.getRepositoryUrl() + ";" + hit.getTaskId();
	}
}
