/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Feb 2, 2005
 */
package org.eclipse.mylyn.internal.sandbox.bridge.bugs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.internal.context.core.DegreeOfSeparation;
import org.eclipse.mylyn.internal.context.core.IActiveSearchListener;
import org.eclipse.mylyn.internal.context.core.IActiveSearchOperation;
import org.eclipse.mylyn.internal.context.core.IDegreeOfSeparation;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.PlatformUI;

/**
 * @author Shawn Minto
 */
public class BugzillaReferencesProvider extends AbstractRelationProvider {

	public static final String ID = "org.eclipse.mylyn.bugs.search.references";

	public static final String NAME = "referenced by";

	public static final int DEFAULT_DEGREE = 0;

	public BugzillaReferencesProvider() {
		super(BugzillaStructureBridge.CONTENT_TYPE, ID);
	}

	@Override
	public List<IDegreeOfSeparation> getDegreesOfSeparation() {
		List<IDegreeOfSeparation> separations = new ArrayList<IDegreeOfSeparation>();
		separations.add(new DegreeOfSeparation("disabled", 0));
		separations.add(new DegreeOfSeparation("local, fully qualified matches", 1));
		separations.add(new DegreeOfSeparation("local, unqualified matches", 2));
		separations.add(new DegreeOfSeparation("server, fully quaified matches", 3));
		separations.add(new DegreeOfSeparation("server, unqualified matches", 4));

		return separations;
	}

	protected boolean acceptElement(IJavaElement javaElement) {
		return javaElement != null && (javaElement instanceof IMember || javaElement instanceof IType)
				&& javaElement.exists();
	}

	/**
	 * HACK: checking kind as string - don't want the dependancy to mylar.java
	 */
	@Override
	protected void findRelated(final IInteractionElement node, int degreeOfSeparation) {
		if (!node.getContentType().equals("java")) {
			return;
		}
		IJavaElement javaElement = JavaCore.create(node.getHandleIdentifier());
		if (!acceptElement(javaElement)) {
			return;
		}
		runJob(node, degreeOfSeparation);
	}

	@Override
	public IActiveSearchOperation getSearchOperation(IInteractionElement node, int limitTo, int degreeOfSepatation) {
		IJavaElement javaElement = JavaCore.create(node.getHandleIdentifier());

		ITask task = TasksUiPlugin.getTaskListManager().getActiveTask();
		TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		return new BugzillaMylynSearch(degreeOfSepatation, javaElement, repository.getRepositoryUrl());
	}

	private void runJob(final IInteractionElement node, final int degreeOfSeparation) {
		BugzillaMylynSearch search = (BugzillaMylynSearch) getSearchOperation(node, 0, degreeOfSeparation);

		search.addListener(new IActiveSearchListener() {

			private boolean gathered = false;

			public void searchCompleted(List<?> nodes) {
				Iterator<?> itr = nodes.iterator();

				if (MylynBugsManager.getDefault() == null) {
					return;
				}

				while (itr.hasNext()) {
					Object o = itr.next();
					if (o instanceof BugzillaReportInfo) {
						BugzillaReportInfo bugzillaNode = (BugzillaReportInfo) o;
						final String handle = bugzillaNode.getElementHandle();
//						if (MylarBugsPlugin.getDefault().getCache().getCached(handle) == null)
//							cache(handle, bugzillaNode);

						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								incrementInterest(node, BugzillaStructureBridge.CONTENT_TYPE, handle,
										degreeOfSeparation);
							}
						});
					}
				}
				gathered = true;
				BugzillaReferencesProvider.this.searchCompleted(node);
			}

			public boolean resultsGathered() {
				return gathered;
			}

		});
		search.run(new NullProgressMonitor(), Job.DECORATE - 10);
	}

	@Override
	public String getGenericId() {
		return ID;
	}

	@Override
	protected String getSourceId() {
		return ID;
	}

	@Override
	public String getName() {
		return NAME;
	}

	/*
	 * 
	 * STUFF FOR TEMPORARILY CACHING A PROXY REPORT
	 * 
	 * TODO remove the proxys and update the BugzillaStructureBridge cache so
	 * that on restart, we dont have to get all of the bugs
	 * 
	 */
	private static final Map<String, BugzillaReportInfo> reports = new HashMap<String, BugzillaReportInfo>();

	public BugzillaReportInfo getCached(String handle) {
		return reports.get(handle);
	}

	protected void cache(String handle, BugzillaReportInfo bugzillaNode) {
		reports.put(handle, bugzillaNode);
	}

	public void clearCachedReports() {
		reports.clear();
	}

	public Collection<? extends String> getCachedHandles() {
		return reports.keySet();
	}

	@Override
	public void stopAllRunningJobs() {
		BugzillaSearchManager.cancelAllRunningJobs();

	}

	@Override
	protected int getDefaultDegreeOfSeparation() {
		return DEFAULT_DEGREE;
	}

	public void contextPreActivated(IInteractionContext context) {
		// ignore	
	}

	public void elementsDeleted(List<IInteractionElement> elements) {
		// ignore
	}
}
