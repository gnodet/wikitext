/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.bridge.bugs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.internal.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.internal.tasks.ui.editors.ContentOutlineTools;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTaskOutlineNode;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTaskSelection;
import org.eclipse.ui.views.markers.internal.ProblemMarker;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class BugzillaStructureBridge extends AbstractContextStructureBridge {

	public final static String CONTENT_TYPE = "bugzilla";

	public List<AbstractRelationProvider> providers;

	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}

	public BugzillaStructureBridge() {
		super();
		providers = new ArrayList<AbstractRelationProvider>();
	}

	/**
	 * Handle format: <server-name:port>;<bug-taskId>;<comment#>
	 * 
	 * Use: OutlineTools ???
	 */
	@Override
	public String getHandleIdentifier(Object object) {
		if (object instanceof RepositoryTaskOutlineNode) {
			RepositoryTaskOutlineNode node = (RepositoryTaskOutlineNode) object;
			return ContentOutlineTools.getHandle(node);
		} else if (object instanceof RepositoryTaskSelection) {
			RepositoryTaskSelection n = (RepositoryTaskSelection) object;
			return ContentOutlineTools.getHandle(n);
		}
		return null;
	}

//	private BugzillaReport result;

	/**
	 * TODO: this will not return a non-cached handle
	 */
	@Override
	public Object getObjectForHandle(final String handle) {
		return null;
	}

	@Override
	public String getParentHandle(String handle) {

		// check so that we don't need to try to get the parent if we are
		// already at the bug report
		if (!handle.matches(".*;.*;.*")) {
			return null;
		}

		RepositoryTaskOutlineNode bon = (RepositoryTaskOutlineNode) getObjectForHandle(handle);
		if (bon != null && bon.getParent() != null) {
			return ContentOutlineTools.getHandle(bon.getParent());
		} else {
			return null;
		}
	}

	@Override
	public String getLabel(Object object) {
		if (object instanceof RepositoryTaskOutlineNode) {
			RepositoryTaskOutlineNode b = (RepositoryTaskOutlineNode) object;
			return ContentOutlineTools.getName(b);
		} else if (object instanceof BugzillaReportInfo) {
			BugzillaTask hit = ((BugzillaReportInfo) object).getHit();
			return hit.getRepositoryUrl() + ": Bug#: " + hit.getTaskId() + ": " + hit.getSummary();
		}
		return "";
	}

	@Override
	public boolean canBeLandmark(String handle) {
		return false;
	}

	@Override
	public boolean acceptsObject(Object object) {
		return object instanceof RepositoryTaskOutlineNode || object instanceof RepositoryTaskSelection;
	}

	@Override
	public boolean canFilter(Object element) {
		return true;
	}

	@Override
	public boolean isDocument(String handle) {
		return (handle.indexOf(';') == handle.lastIndexOf(';') && handle.indexOf(";") != -1);
	}

	public String getHandleForMarker(ProblemMarker marker) {
		return null;
	}

	@Override
	public String getContentType(String elementHandle) {
		return getContentType();
	}

	@Override
	public String getHandleForOffsetInObject(Object resource, int offset) {
		return null;
	}

	@Override
	public List<String> getChildHandles(String handle) {
		return Collections.emptyList();
	}
}
