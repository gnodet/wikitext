/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.xplanner.ui.editor;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.ScheduleDatePicker;
import org.eclipse.mylyn.internal.tasks.ui.deprecated.AbstractNewRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.xplanner.ui.XPlannerRepositoryUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

public class NewXPlannerTaskEditor extends AbstractNewRepositoryTaskEditor implements XPlannerEditorAttributeProvider {

	private XPlannerTaskEditorExtraControls extraControls;

	public NewXPlannerTaskEditor(FormEditor editor) {
		super(editor);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		extraControls = new XPlannerTaskEditorExtraControls(this, getRepositoryTaskData());
		setExpandAttributeSection(true);
	}

	@Override
	protected void validateInput() {
		boolean isValid = true;

		submitButton.setEnabled(isValid);
	}

	@Override
	protected void createAttributeLayout(Composite composite) {
		// xplanner related attributes displayed in separate section
	}

	@Override
	protected void addAttachContextButton(Composite buttonComposite, AbstractTask task) {
		// disabled, see bug 155151
	}

	@Override
	protected void createAttachmentLayout(Composite parent) {
		// don't want this
	}

	@Override
	protected void createNewCommentLayout(Composite composite) {
		// TODO -- don't want this for now -- duplicates existing attributes revisit 
		// when do closer mylyn ui to xplanner values mapping

		// hack to avoid NPE in updating new task method 
		scheduledForDate = new ScheduleDatePicker(composite, null, SWT.FLAT);
		scheduledForDate.setVisible(false);

		estimatedTime = new Spinner(composite, SWT.FLAT);
		estimatedTime.setVisible(false);
	}

	@Override
	protected ImageHyperlink createReplyHyperlink(final int commentNum, Composite composite, final String commentBody) {
		// don't want this for now -- don't support comments in XPlanner
		return null;
	}

	@Override
	protected void createCustomAttributeLayout(Composite composite) {
		// make sure we only use one column
		if (composite.getLayout() instanceof GridLayout) {
			GridLayout layout = (GridLayout) composite.getLayout();
			layout.numColumns = 1;
		}

		extraControls.createPartControlCustom(composite, false);
	}

	// just in case, leave in method -- before had to get from editorInput
	public RepositoryTaskData getRepositoryTaskData() {
		return taskData;
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	public String getFormTitle() {
		return MessageFormat.format(Messages.XPlannerTaskEditor_FORM_TASK_TITLE,
				XPlannerRepositoryUtils.getName(getRepositoryTaskData()), getRepositoryTaskData().getTaskId() + ""); // so doesn't get formatted as number with a comma	 //$NON-NLS-1$
	}

	public boolean xplannerAttributeChanged(RepositoryTaskAttribute attribute) {
		return attributeChanged(attribute);
	}

	@Override
	public void submitToRepository() {
		boolean ok = true;

		if (summaryText.getText().equals("")) {
			MessageDialog.openInformation(this.getSite().getShell(), "Submit Error",
					"Please provide a name for the new task.");
			summaryText.setFocus();
			ok = false;
		}

		if (ok) {
			super.submitToRepository();
		}
	}

}
