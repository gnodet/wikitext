/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.editors;

import java.util.Set;

import org.eclipse.mylyn.internal.bugzilla.ui.editor.BugzillaTaskEditorPage;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.contexts.IContextService;

/**
 * A bugzilla task editor page that has wiki facilities.
 * 
 * @author Jingwen Ou
 */
public class ExtensibleBugzillaTaskEditorPage extends BugzillaTaskEditorPage {

	public ExtensibleBugzillaTaskEditorPage(TaskEditor editor) {
		super(editor);
	}

	@Override
	protected AttributeEditorFactory createAttributeEditorFactory() {
		final AttributeEditorFactory bugzillaFactory = super.createAttributeEditorFactory();
		AttributeEditorFactory factory = new AttributeEditorFactory(getModel(), getTaskRepository()) {
			@Override
			public AbstractAttributeEditor createEditor(String type, TaskAttribute taskAttribute) {
				// replace description part and the comment part
				AbstractTaskEditorExtension extension = TaskEditorExtensions.getTaskEditorExtension(getTaskRepository());
				if (extension != null) {
					if (TaskAttribute.TYPE_LONG_RICH_TEXT.equals(type)) {
						return new ExtensibleRichTextAttributeEditor((IContextService) getEditor().getEditorSite()
								.getService(IContextService.class), getModel(), getTaskRepository(), extension,
								taskAttribute, SWT.MULTI);
					}
				}
				return bugzillaFactory.createEditor(type, taskAttribute);
			}
		};
		return factory;
	}

	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> descriptors = super.createPartDescriptors();

		for (TaskEditorPartDescriptor taskEditorPartDescriptor : descriptors) {
			if (taskEditorPartDescriptor.getId().equals(ID_PART_NEW_COMMENT)) {
				descriptors.remove(taskEditorPartDescriptor);
				break;
			}
		}

		descriptors.add(new TaskEditorPartDescriptor(ID_PART_NEW_COMMENT) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new ExtensibleTaskEditorNewCommentPart();
			}
		}.setPath(PATH_COMMENTS));

		return descriptors;
	}
}
