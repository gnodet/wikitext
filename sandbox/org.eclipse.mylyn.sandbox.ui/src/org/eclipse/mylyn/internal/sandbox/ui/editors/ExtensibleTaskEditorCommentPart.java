/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Jingwen Ou - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.sandbox.ui.editors.CommentGroupStrategy.CommentGroup;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorCommentPart;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 * @author Jingwen Ou added comment groupings
 */
public class ExtensibleTaskEditorCommentPart extends TaskEditorCommentPart {
	private static void toggleChildren(Composite composite, boolean expended) {
		for (Control child : composite.getChildren()) {
			if (child instanceof ExpandableComposite && !child.isDisposed()) {
				EditorUtil.toggleExpandableComposite(expended, (ExpandableComposite) child);
			}
			if (child instanceof Composite) {
				toggleChildren((Composite) child, expended);
			}
		}
	}

	private CommentGroupStrategy commentGroupStrategy;

	private List<Section> subSections;

	public ExtensibleTaskEditorCommentPart() {
		setPartName("Comments");
	}

	private void createCommentSubsections(final FormToolkit toolkit, final Composite composite,
			List<TaskAttribute> comments) {
		List<CommentGroup> commentGroups = getCommentGroupStrategy().groupCommentsFromModel(getModel());

		// if there is only one subsection, then don't show it
		if (commentGroups.size() == 1) {
			for (CommentGroup commentGroup : commentGroups) {
				addComments(toolkit, composite, commentGroup.getCommentAttributes());
			}
		} else {
			subSections = new ArrayList<Section>();
			for (CommentGroup commentGroup : commentGroups) {
				createGroupSection(toolkit, composite, commentGroup);

			}
		}
	}

	private void createCurrentSubsectionToolBar(final FormToolkit toolkit, final Section section) {
		if (section == null) {
			return;
		}

		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

		Action collapseAllAction = new Action("") {
			@Override
			public void run() {
				toggleSection(section, false);
			}
		};
		collapseAllAction.setImageDescriptor(CommonImages.COLLAPSE_ALL_SMALL);
		collapseAllAction.setToolTipText("Collapse All Current Comments");
		toolBarManager.add(collapseAllAction);

		Action expandAllAction = new Action("") {
			@Override
			public void run() {
				toggleSection(section, true);
			}
		};
		expandAllAction.setImageDescriptor(CommonImages.EXPAND_ALL_SMALL);
		expandAllAction.setToolTipText("Expand All Current Comments");
		toolBarManager.add(expandAllAction);

		Composite toolbarComposite = toolkit.createComposite(section);
		toolbarComposite.setBackground(null);
		RowLayout rowLayout = new RowLayout();
		rowLayout.marginTop = 0;
		rowLayout.marginBottom = 0;
		rowLayout.marginLeft = 0;
		rowLayout.marginRight = 0;
		toolbarComposite.setLayout(rowLayout);

		toolBarManager.createControl(toolbarComposite);
		section.setTextClient(toolbarComposite);
	}

	private void createGroupSection(final FormToolkit toolkit, final Composite parent, final CommentGroup commentGroup) {
		int style = ExpandableComposite.TWISTIE | ExpandableComposite.SHORT_TITLE_BAR;
		if (commentGroup.getGroupName().equals("Current")) {
			style |= ExpandableComposite.EXPANDED;
		}

		final Section groupSection = toolkit.createSection(parent, style);
		groupSection.setBackground(null);
		groupSection.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(groupSection);
		groupSection.setText(commentGroup.getGroupName() + " (" + commentGroup.getCommentAttributes().size() + ")");

		// create toolbar only for Current section
		if (commentGroup.getGroupName().equals("Current")) {
			createCurrentSubsectionToolBar(toolkit, groupSection);
		}

		// only Current subsection will be expanded by default
		if (groupSection.isExpanded()) {
			expendSubsection(toolkit, commentGroup, groupSection);
		}
		groupSection.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				expendSubsection(toolkit, commentGroup, groupSection);
				getTaskEditorPage().reflow();
			}

		});

		subSections.add(groupSection);
	}

	@Override
	protected void expandAllComments() {
		if (section != null) {
			EditorUtil.toggleExpandableComposite(true, section);
		}
		if (subSections != null) {
			// first toggle on all subSections
			try {
				getTaskEditorPage().setReflow(false);

				if (section != null) {
					EditorUtil.toggleExpandableComposite(true, section);
				}

				for (Section subSection : subSections) {
					if (subSection.isDisposed()) {
						continue;
					}
					EditorUtil.toggleExpandableComposite(true, subSection);
				}
			} finally {
				getTaskEditorPage().setReflow(true);
			}
		}

		super.expandAllComments();
	}

	@Override
	protected void expandSection(final FormToolkit toolkit, final Section section, List<TaskAttribute> comments) {
		final Composite composite = toolkit.createComposite(section);
		section.setClient(composite);
		composite.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);

		// fewer than 12 comments then no subsections
		if (comments.size() < 12) {
			addComments(toolkit, composite, comments);
		} else {
			GridLayout layout = new GridLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			composite.setLayout(layout);
			createCommentSubsections(toolkit, composite, comments);
		}
	}

	private void expendSubsection(final FormToolkit toolkit, CommentGroup commentGroup, Section groupSection) {
		if (groupSection.getData("isInit") == null) {
			Composite groupContentComposite = toolkit.createComposite(groupSection);
			groupSection.setClient(groupContentComposite);
			GridLayout contentLayout = new GridLayout();
			contentLayout.marginHeight = 0;
			contentLayout.marginWidth = 0;
			groupContentComposite.setLayout(contentLayout);
			groupContentComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			addComments(toolkit, groupContentComposite, commentGroup.getCommentAttributes());

			groupSection.setData("isInit", true);
		}
	}

	private CommentGroupStrategy getCommentGroupStrategy() {
		if (commentGroupStrategy == null) {
			commentGroupStrategy = new CommentGroupStrategy();
		}

		return commentGroupStrategy;
	}

	private void toggleSection(Section section, boolean expended) {
		try {
			getTaskEditorPage().setReflow(false);

			if (expended && !section.isDisposed()) {
				EditorUtil.toggleExpandableComposite(true, section);
			}

			toggleChildren(section, expended);
		} finally {
			getTaskEditorPage().setReflow(true);
		}
		getTaskEditorPage().reflow();
	}
}
