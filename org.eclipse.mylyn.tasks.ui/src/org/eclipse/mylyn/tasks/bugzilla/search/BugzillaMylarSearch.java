/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Oct 13, 2004
 */
package org.eclipse.mylar.tasks.bugzilla.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.mylar.core.search.IActiveSearchListener;
import org.eclipse.mylar.core.search.IMylarSearchOperation;
import org.eclipse.mylar.tasks.MylarTasksPlugin;
import org.eclipse.mylar.tasks.bugzilla.BugzillaMylarBridge;
import org.eclipse.mylar.tasks.bugzilla.BugzillaReportNode;
import org.eclipse.mylar.tasks.bugzilla.Util;


/**
 * Used to facilitate bugzilla searches based on IJavaElements
 * 
 * @author Shawn Minto
 */
public class BugzillaMylarSearch implements IMylarSearchOperation {

	// scope identifiers
    public static final int LOCAL_QUAL = 1; // local implies a bugzilla task, not just an offline report
    public static final int LOCAL_UNQUAL = 2; 
	public static final int FULLY_QUAL = 3;
	public static final int UNQUAL = 4;

	private int scope;
	
	private IJavaElement element;
	
    private String handle = "";
    
	/**
	 * Constructor
	 * @param scope The scope of this search
	 */
	public BugzillaMylarSearch(int scope, IJavaElement element) {
		this.scope = scope;
		this.element = element;
	}

	public IStatus run(IProgressMonitor monitor) {
        handle = element.getHandleIdentifier() + " " + scope;
        List<IJavaElement> landmarks = new ArrayList<IJavaElement>();
        landmarks.add(element);

        if (!BugzillaMylarBridge.doesJobExist(handle)) {

        	// perform the bugzilla search
        	// get only the useful landmarks (IMember)
            List<IMember> members = Util.getMemberLandmarks(landmarks);

            // go through all of the landmarks that we are given and perform a
            // search on them
            for(IMember m : members){

            	// FIXME: decide whether to do leave the caching in for now or not
                // check if we have the info cached
                List<BugzillaReportNode> landmarkDoi = MylarTasksPlugin.getBridge()
                        .getFromLandmarksHash(m, scope);

                if (landmarkDoi != null) {
                    //TODO decide when to queue up and do a refresh search
                    notifySearchCompleted(landmarkDoi);
                    continue;
                }

                // create a search operation so that we can search
                BugzillaMylarSearchOperation op = new BugzillaMylarSearchOperation(
                        this, m, scope);

                // create a new search job so that it can be scheduled and
                // run as a background thread
                Job searchJob = new BugzillaMylarSearchJob(
                        "Querying Bugzilla Server - Mylar - "
                                + op.getSearchMemberName(), op);

                // schedule the new search job
                searchJob.schedule();

                // save this searchJobs handle so that we can cancel it if need be
                BugzillaMylarBridge.addJob(handle, searchJob);
            }
        }
		return Status.OK_STATUS;
	}

    /** List of listeners wanting to know about the searches */
    private List<IActiveSearchListener> listeners = new ArrayList<IActiveSearchListener>();
    
    /**
     * Add a listener for when the bugzilla search is completed
     * 
     * @param l
     *            The listener to add
     */
    public void addListener(IActiveSearchListener l) {
        // add the listener to the list
        listeners.add(l);
    }

    /**
     * Remove a listener for when the bugzilla search is completed
     * 
     * @param l
     *            The listener to remove
     */
    public void removeListener(IActiveSearchListener l) {
        // remove the listener from the list
        listeners.remove(l);
    }

    /**
     * Notify all of the listeners that the bugzilla search is completed
     * 
     * @param doiList
     *            A list of BugzillaSearchHitDoiInfo
     * @param member
     *            The IMember that the search was performed on
     */
    public void notifySearchCompleted(List<BugzillaReportNode> doiList) {
        // go through all of the listeners and call searchCompleted(colelctor,
        // member)
        BugzillaMylarBridge.removeSearchJob(handle);
        for (IActiveSearchListener listener : listeners) {
            listener.searchCompleted(doiList);
        }
    }
	
}