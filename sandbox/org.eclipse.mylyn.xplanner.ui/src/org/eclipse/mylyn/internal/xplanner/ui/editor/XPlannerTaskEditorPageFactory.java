/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.xplanner.ui.editor;

import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.internal.xplanner.ui.XPlannerAttributeMapper;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.FormPage;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerTaskEditorPageFactory extends AbstractTaskEditorPageFactory {
	@Override
	public boolean canCreatePageFor(TaskEditorInput input) {
		if (input.getTask().getConnectorKind().equals(XPlannerCorePlugin.CONNECTOR_KIND)
				|| TasksUiUtil.isOutgoingNewTask(input.getTask(), XPlannerCorePlugin.CONNECTOR_KIND)) {
			return true;
		}
		return false;
	}

	@Override
	public FormPage createPage(TaskEditor parentEditor) {
		FormPage editor = null;
		if (!(parentEditor.getEditorInput() instanceof TaskEditorInput)) {
			return null;
		}

		TaskEditorInput editorInput = (TaskEditorInput) parentEditor.getEditorInput();

		ITask task = editorInput.getTask();
		String kind = task.getTaskKind();

		if (XPlannerAttributeMapper.XPlannerTaskKind.TASK.toString().equals(kind)
				|| XPlannerAttributeMapper.DEFAULT_REPOSITORY_TASK_KIND.equals(kind)) {
			if (TasksUiUtil.isOutgoingNewTask(editorInput.getTask(), XPlannerCorePlugin.CONNECTOR_KIND)) {
				editor = new XPlannerTaskEditor(parentEditor, true);
			} else {
				editor = new XPlannerTaskEditor(parentEditor);
			}
		} else if (XPlannerAttributeMapper.XPlannerTaskKind.USER_STORY.toString().equals(kind)) {
			editor = new XPlannerUserStoryEditor(parentEditor);
		}

		return editor;
	}

	@Override
	public Image getPageImage() {
		return CommonImages.getImage(TasksUiImages.REPOSITORY_SMALL);
	}

	@Override
	public String getPageText() {
		return Messages.XPlannerTaskEditorFactory_TITLE;
	}

	@Override
	public int getPriority() {
		return PRIORITY_TASK;
	}

}
