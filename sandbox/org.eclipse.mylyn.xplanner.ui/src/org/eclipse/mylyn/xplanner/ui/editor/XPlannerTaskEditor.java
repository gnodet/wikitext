/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui.editor;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylyn.tasks.core.*;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.xplanner.ui.XPlannerMylynUIPlugin;
import org.eclipse.mylyn.xplanner.ui.XPlannerRepositoryUtils;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerTaskEditor extends AbstractRepositoryTaskEditor 
	implements XPlannerEditorAttributeProvider {
	
	private XPlannerTaskEditorExtraControls extraControls;
	
	public XPlannerTaskEditor(FormEditor editor) {
		super(editor);
	}

	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		updateEditorTitle();
		extraControls = new XPlannerTaskEditorExtraControls(this, getRepositoryTaskData());
		setExpandAttributeSection(true);
	}

	@Override
	protected void addRadioButtons(Composite buttonComposite) {
		//TODO -- ?
	}
	
	@Override
	protected void createPeopleLayout(Composite composite) {
		// disabled
	}

	@Override
	protected void addActionButtons(Composite buttonComposite) {
		super.addActionButtons(buttonComposite);
		//TODO -- ok with submit only, and no compare?
	}
	
	protected void validateInput() {
		submitButton.setEnabled(true);
	}

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
	protected void createCommentLayout(Composite composite) {
		// don't want this
	}

	@Override
	protected void createNewCommentLayout(Composite composite) {
		// don't want this
	}

	protected ImageHyperlink createReplyHyperlink(final int commentNum, Composite composite, final String commentBody) {
		// don't want this for now -- don't support comments in XPlanner
		return null;
	}
	
	@Override
	protected void createCustomAttributeLayout(Composite composite) {
		// make sure we only use one column
		if (composite.getLayout() instanceof GridLayout) {
			GridLayout layout = (GridLayout)composite.getLayout();
			layout.numColumns = 1;
		}

		extraControls.createPartControlCustom(composite, true);
	}

	  // just in case, leave in method -- before had to get from editorInput
	public RepositoryTaskData getRepositoryTaskData() {
		return taskData;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public String getFormTitle() {
	  return MessageFormat.format(Messages.XPlannerTaskEditor_FORM_TASK_TITLE, 
	  		XPlannerRepositoryUtils.getName(getRepositoryTaskData()),
		getRepositoryTaskData().getId() + "");  // so doesn't get formatted as number with a comma	 //$NON-NLS-1$
	}
	
	public void setFocus() {
	}

	public String getPluginId() {
		return XPlannerMylynUIPlugin.PLUGIN_ID;
	}
	
	public boolean xplannerAttributeChanged(RepositoryTaskAttribute attribute) {
		return attributeChanged(attribute);
	}
	
	@Override
	public void submitToRepository() {
		String errorMessage = null;
		Control errorControl = null;
		
		if (summaryText.getText().equals("")) {
			errorMessage = "Task name cannot be empty.";
			errorControl = summaryText;
		}
		if (errorMessage == null) {
			errorMessage = extraControls.validate();
		}
		
		if (errorMessage != null) {
			MessageDialog.openInformation(this.getSite().getShell(), "Submit Error",
				errorMessage);
			if (errorControl != null) {
				errorControl.setFocus();
			}
		}
		
		if (errorMessage == null) {
			super.submitToRepository();
		}
	}

}
