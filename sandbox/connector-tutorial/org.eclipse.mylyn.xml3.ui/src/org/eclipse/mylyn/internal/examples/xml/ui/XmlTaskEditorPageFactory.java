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

package org.eclipse.mylyn.internal.examples.xml.ui;

import org.eclipse.mylyn.internal.examples.xml.core.XmlCorePlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * @author Steffen Pingel
 */
public class XmlTaskEditorPageFactory extends AbstractTaskEditorPageFactory {

	@Override
	public boolean canCreatePageFor(TaskEditorInput input) {
		return (input.getTask().getConnectorKind().equals(XmlCorePlugin.CONNECTOR_KIND) || TasksUiUtil.isOutgoingNewTask(
				input.getTask(), XmlCorePlugin.CONNECTOR_KIND));
	}

	@Override
	public IFormPage createPage(TaskEditor editor) {
		return new XmlTaskEditorPage(editor);
	}

	@Override
	public String[] getConflictingIds(TaskEditorInput input) {
		if (!input.getTask().getConnectorKind().equals(XmlCorePlugin.CONNECTOR_KIND)) {
			return new String[] { ITasksUiConstants.ID_PAGE_PLANNING };
		}
		return null;
	}

	@Override
	public Image getPageImage() {
		return CommonImages.getImage(TasksUiImages.REPOSITORY_SMALL);
	}

	@Override
	public String getPageText() {
		return "XML";
	}

	@Override
	public int getPriority() {
		return PRIORITY_TASK;
	}

}
