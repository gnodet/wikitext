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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Shawn Minto
 */
public class TaskEditorPersonalPart extends AbstractTaskEditorPart {

	private final PersonalPart part = new PersonalPart(ExpandableComposite.TWISTIE, false);

	@Override
	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		super.initialize(taskEditorPage);
		boolean needsDueDate = !taskEditorPage.getConnector().hasRepositoryDueDate(taskEditorPage.getTaskRepository(),
				taskEditorPage.getTask(), getTaskData());
		part.initialize(taskEditorPage.getManagedForm(), taskEditorPage.getTaskRepository(),
				(AbstractTask) taskEditorPage.getTask(), needsDueDate, taskEditorPage.getEditorSite());
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		part.createControl(parent, toolkit);
		setSection(toolkit, part.getSection());
	}

	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		part.commit(onSave);
	}

	@Override
	public boolean isDirty() {
		return super.isDirty() || part.isDirty();
	}

}