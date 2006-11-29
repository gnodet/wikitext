///*******************************************************************************
// * Copyright (c) 2004 - 2006 Mylar committers and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *******************************************************************************/
//
//package org.eclipse.mylar.internal.sandbox.team;
//
//import org.eclipse.core.resources.IResource;
//import org.eclipse.core.runtime.IAdapterFactory;
//import org.eclipse.core.runtime.IAdapterManager;
//import org.eclipse.mylar.internal.team.ILinkedTaskInfo;
//import org.eclipse.mylar.internal.team.LinkedTaskInfo;
//import org.eclipse.mylar.tasks.core.TaskRepository;
//import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
//import org.eclipse.team.core.TeamException;
//import org.eclipse.team.internal.core.subscribers.ChangeSet;
//import org.eclipse.team.internal.ui.synchronize.ChangeSetDiffNode;
//import org.eclipse.team.internal.ui.synchronize.SyncInfoModelElement;
//import org.eclipse.team.internal.ui.synchronize.SynchronizeModelElement;
//import org.tigris.subversion.subclipse.core.ISVNRemoteResource;
//import org.tigris.subversion.subclipse.core.sync.SVNStatusSyncInfo;
//
///**
// * Wrapper class used to isolate Subclibse dependencies 
// * 
// * @author Eugene Kuleshov
// */
//public class SubclipseLinkedTaskInfoWrapper {
//
//	public static void init(IAdapterManager adapterManager, IAdapterFactory factory) {
//		// XXX put back
//		Class logEntryClass = org.tigris.subversion.subclipse.core.history.LogEntry.class;
//		adapterManager.registerAdapters(FACTORY, logEntryClass);
//	}
//
//	private static ILinkedTaskInfo adaptSubclipseChangeset(ChangeSetDiffNode diff, ChangeSet set) {
//        SynchronizeModelElement diffContainer = (SynchronizeModelElement) diff.getChildren()[0];
//
//        IResource res = diffContainer.getResource();
//
//        SyncInfoModelElement melement = (SyncInfoModelElement) diffContainer.getChildren()[0];
//
//        // Specific to Subclipse
//        SVNStatusSyncInfo info = (SVNStatusSyncInfo) melement.getSyncInfo();
//
//        ISVNRemoteResource remoteResource = (ISVNRemoteResource) info.getRemote();
//        SVNRevision rev = remoteResource.getLastChangedRevision();
//
//		String comment;
//		try {
//			ISVNLogMessage[] messages = remoteResource.getLogMessages(rev, rev, SVNRevision.START, false, false, 1);
//			comment = messages[0].getMessage();
//		} catch (TeamException ex) {
//			comment = diff.getSet().getComment();
//		}
//		return adaptFromComment(diff, res, comment);
//	}
//
//	private static ILinkedTaskInfo adaptSubclipseLogEntry(Object object) {
//		org.tigris.subversion.subclipse.core.history.LogEntry logEntry = 
//			(org.tigris.subversion.subclipse.core.history.LogEntry) object;
//
//		String comment = logEntry.getComment();
//		IResource res = logEntry.getResource().getResource();
//		
//		return adaptFromComment(object, res, comment);
//	}
//
//	private static ILinkedTaskInfo adaptFromComment(Object object, IResource res, String comment) {
//		ProjectProperties props = null;
//		try {
//			props = ProjectProperties.getProjectProperties(res);
//		} catch (TeamException ex) {
//			// ignore?
//		}
//		
//		String[] urls = null;
//		String repositoryUrl = null;
//		if(props!=null) {
//			repositoryUrl = getRepositoryUrl(props.getUrl());
//			urls = props.getLinkList(comment).getUrls();
//		}
//		if (urls == null || urls.length == 0) {
//			// we can do better job then this method
//			urls = ProjectProperties.getUrls(comment).getUrls();
//		}
//		if (urls != null && urls.length > 0) {
//			return new LinkedTaskInfo(repositoryUrl, null, urls[0]);
//		}
//		return null;
//	}
//
//	private static String getRepositoryUrl(String url) {
//		for (TaskRepository repository : TasksUiPlugin.getRepositoryManager().getAllRepositories()) {
//			if(url.startsWith(repository.getUrl())) {
//				return repository.getUrl();
//			}
//		}
//		return null;
//	}
//	
//}