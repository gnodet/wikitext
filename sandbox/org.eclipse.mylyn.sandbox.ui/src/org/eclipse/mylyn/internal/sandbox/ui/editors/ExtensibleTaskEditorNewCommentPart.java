/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorNewCommentPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Jingwen Ou
 */
public class ExtensibleTaskEditorNewCommentPart extends TaskEditorNewCommentPart {

	@Override
	protected void fillToolBar(ToolBarManager barManager) {
		Action toggleEditingAction = new Action("", SWT.TOGGLE) {
			@Override
			public void run() {
				toggleEditing(this);
			}
		};
		toggleEditingAction.setImageDescriptor(CommonImages.PREVIEW_WEB);
		toggleEditingAction.setToolTipText("Preview");
		toggleEditingAction.setChecked(false);

		barManager.add(toggleEditingAction);
		barManager.add(getMaximizePartAction());
		super.fillToolBar(barManager);
	}

	private void toggleEditing(Action action) {
		if (getEditor() instanceof ExtensibleRichTextAttributeEditor) {
			ExtensibleRichTextAttributeEditor editor = (ExtensibleRichTextAttributeEditor) getEditor();
			editor.toggleEditing(!action.isChecked());
		}
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		super.createControl(parent, toolkit);
		if (getControl() != null) {
			getEditor().getControl().setData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION, getMaximizePartAction());
			if (getEditor().getControl() instanceof Composite) {
				for (Control control : ((Composite) getEditor().getControl()).getChildren()) {
					control.setData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION, getMaximizePartAction());
				}
			}
		}
	}

}