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

package org.eclipse.mylar.internal.sandbox.bridge.bugs;

import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.context.core.IMylarRelation;
import org.eclipse.mylar.context.core.IMylarStructureBridge;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.internal.context.ui.AbstractContextLabelProvider;
import org.eclipse.mylar.internal.context.ui.ContextUiImages;
import org.eclipse.mylar.internal.tasks.ui.TaskListImages;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class BugzillaContextLabelProvider extends AbstractContextLabelProvider {

	@Override
	protected Image getImage(IMylarElement node) {
		return TaskListImages.getImage(TaskListImages.TASK_REMOTE);
	}

	@Override
	protected Image getImage(IMylarRelation edge) {
		return ContextUiImages.getImage(MylarBugsManager.EDGE_REF_BUGZILLA);
	}

	@Override
	protected Image getImageForObject(Object object) {
		return TaskListImages.getImage(TaskListImages.TASK_REMOTE);
	}

	@Override
	protected String getTextForObject(Object node) {
		return "" + node;
	}

	/**
	 * TODO: slow?
	 */
	@Override
	protected String getText(IMylarElement node) {
		// try to get from the cache before downloading
		Object report;
		BugzillaReportInfo reportNode = MylarBugsManager.getReferenceProvider().getCached(node.getHandleIdentifier());
//		BugzillaReport cachedReport = MylarBugsPlugin.getDefault().getCache().getCached(node.getHandleIdentifier());
		IMylarStructureBridge bridge = ContextCorePlugin.getDefault()
				.getStructureBridge(BugzillaStructureBridge.CONTENT_TYPE);

		if (reportNode != null) {
			report = reportNode;
		} else {
			report = bridge.getObjectForHandle(node.getHandleIdentifier());
		}
		return bridge.getName(report);
	}

	@Override
	protected String getText(IMylarRelation edge) {
		return BugzillaReferencesProvider.NAME;
	}
}
