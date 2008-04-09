/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.bridge.bugs;

import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.AbstractContextLabelProvider;
import org.eclipse.mylyn.internal.context.ui.ContextUiImages;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class BugzillaContextLabelProvider extends AbstractContextLabelProvider {

	@Override
	protected Image getImage(IInteractionElement node) {
		return TasksUiImages.getImage(TasksUiImages.TASK_REMOTE);
	}

	@Override
	protected Image getImage(IInteractionRelation edge) {
		return ContextUiImages.getImage(MylynBugsManager.EDGE_REF_BUGZILLA);
	}

	@Override
	protected Image getImageForObject(Object object) {
		return TasksUiImages.getImage(TasksUiImages.TASK_REMOTE);
	}

	@Override
	protected String getTextForObject(Object node) {
		return "" + node;
	}

	/**
	 * TODO: slow?
	 */
	@Override
	protected String getText(IInteractionElement node) {
		// try to get from the cache before downloading
		Object report;
		BugzillaReportInfo reportNode = MylynBugsManager.getReferenceProvider().getCached(node.getHandleIdentifier());
		AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(
				BugzillaStructureBridge.CONTENT_TYPE);

		if (reportNode != null) {
			report = reportNode;
		} else {
			report = bridge.getObjectForHandle(node.getHandleIdentifier());
		}
		return bridge.getLabel(report);
	}

	@Override
	protected String getText(IInteractionRelation edge) {
		return BugzillaReferencesProvider.NAME;
	}
}
