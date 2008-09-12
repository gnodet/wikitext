/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jingwen Ou - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.mylyn.internal.bugzilla.ui.editor.BugzillaTaskEditorPage;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.AttributeEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * A bugzilla task editor page that has wiki facilities.
 * 
 * @author Jingwen Ou
 */
public class ExtensibleBugzillaTaskEditorPage extends BugzillaTaskEditorPage {

	private Action toggleFindAction;

	private static final Color HIGHLIGHTER_YELLOW = new Color(Display.getDefault(), 255, 238, 99);

	private static final StyleRange HIGHLIGHT_STYLE_RANGE = new StyleRange(0, 0, null, HIGHLIGHTER_YELLOW);

	public ExtensibleBugzillaTaskEditorPage(TaskEditor editor) {
		super(editor);
	}

	private void addFindAction(IToolBarManager toolBarManager) {
		toolBarManager.add(new Separator("find"));

		if (toggleFindAction != null && toggleFindAction.isChecked()) {
			ControlContribution findTextboxControl = new ControlContribution("Find") {

				@Override
				protected Control createControl(Composite parent) {
					FormToolkit toolkit = getTaskEditor().getHeaderForm().getToolkit();
					Composite findComposite = toolkit.createComposite(parent);
					findComposite.setLayout(new RowLayout());
					findComposite.setBackground(null);

					final Text findText = toolkit.createText(findComposite, "", SWT.FLAT);
					findText.setLayoutData(new RowData(100, SWT.DEFAULT));
					findText.setFocus();
					toolkit.adapt(findText, false, false);
					findText.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetDefaultSelected(SelectionEvent event) {
							try {
								setReflow(false);
								findAndHighlight(ExtensibleBugzillaTaskEditorPage.this, findText.getText());
								// always toggle attachment part close after every search, since all ExpandableComposites are open
								AbstractTaskEditorPart attachmentsPart = getPart(AbstractTaskEditorPage.ID_PART_ATTACHMENTS);
								EditorUtil.toggleExpandableComposite(false,
										(ExpandableComposite) attachmentsPart.getControl());
							} finally {
								setReflow(true);
							}
							reflow();
						}
					});

					return findComposite;
				}

			};
			toolBarManager.add(findTextboxControl);
		}

		if (toggleFindAction == null) {
			toggleFindAction = new Action("", SWT.TOGGLE) {
				@Override
				public void run() {
					getTaskEditor().updateHeaderToolBar();
				}

			};
			toggleFindAction.setImageDescriptor(CommonImages.FIND);
			toggleFindAction.setToolTipText("Find");
			//getManagedForm().getForm().setData(TaskEditorFindHandler.KEY_FIND_ACTION, toggleFindAction);
		}

		toolBarManager.add(toggleFindAction);

	}

	@Override
	public boolean canPerformAction(String actionId) {
		if (actionId.equals(ActionFactory.FIND.getId())) {
			return true;
		}

		return super.canPerformAction(actionId);
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
			if (taskEditorPartDescriptor.getId().equals(ID_PART_COMMENTS)) {
				descriptors.remove(taskEditorPartDescriptor);
				break;
			}
		}

		descriptors.add(new TaskEditorPartDescriptor(ID_PART_COMMENTS) {
			@Override
			public AbstractTaskEditorPart createPart() {
				return new ExtensibleTaskEditorCommentPart();
			}
		}.setPath(PATH_COMMENTS));

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

	@Override
	public void doAction(String actionId) {
		if (actionId.equals(ActionFactory.FIND.getId())) {
			if (toggleFindAction != null) {
				toggleFindAction.setChecked(!toggleFindAction.isChecked());
				toggleFindAction.run();
			}
		}
		super.doAction(actionId);
	}

	@Override
	public void fillToolBar(IToolBarManager toolBarManager) {
		super.fillToolBar(toolBarManager);

		addFindAction(toolBarManager);
	}

	private static void findTextViewerControl(Composite composite, List<TextViewer> found) {
		if (!composite.isDisposed()) {
			for (Control child : composite.getChildren()) {
				TextViewer viewer = EditorUtil.getTextViewer(child);
				if (viewer != null && viewer.getDocument().get().length() > 0) {
					found.add(viewer);
				}

				// have to do this since TaskEditorCommentPart.expendComment(..) will dispose the TextViewer when the ExpandableComposite is close
				if (child instanceof ExpandableComposite) {
					EditorUtil.toggleExpandableComposite(true, (ExpandableComposite) child);
				}

				if (child instanceof Composite) {
					findTextViewerControl((Composite) child, found);
				}
			}
		}
	}

	private static boolean findAndHighlightTextViewer(TextViewer viewer, FindReplaceDocumentAdapter adapter,
			String findString, int startOffset) throws BadLocationException {
		IRegion matchRegion = adapter.find(startOffset, findString, true, false, false, false);

		if (matchRegion != null) {
			int widgetOffset = matchRegion.getOffset();
			int length = matchRegion.getLength();
			HIGHLIGHT_STYLE_RANGE.start = widgetOffset;
			HIGHLIGHT_STYLE_RANGE.length = length;
			viewer.getTextWidget().setStyleRange(HIGHLIGHT_STYLE_RANGE);

			findAndHighlightTextViewer(viewer, adapter, findString, widgetOffset + length);

			return true;
		}

		return false;
	}

	public static void findAndHighlight(IFormPage page, String findString) {
		IManagedForm form = page.getManagedForm();
		if (form == null) {
			return;
		}
		ScrolledForm scrolledForm = form.getForm();
		if (scrolledForm == null) {
			return;
		}

		List<TextViewer> found = new ArrayList<TextViewer>();
		findTextViewerControl(scrolledForm.getBody(), found);

		for (TextViewer viewer : found) {
			try {
				// Wiping previous highlighted element
				viewer.setRedraw(false);
				viewer.refresh();
				viewer.setRedraw(true);

				FindReplaceDocumentAdapter adapter = new FindReplaceDocumentAdapter(viewer.getDocument());

				if (!findAndHighlightTextViewer(viewer, adapter, findString, -1)) {
					// toggle close if can't match the keyword
					Composite comp = viewer.getControl().getParent();
					while (comp != null) {
						if (comp instanceof ExpandableComposite) {
							ExpandableComposite ex = (ExpandableComposite) comp;
							EditorUtil.toggleExpandableComposite(false, ex);
							break;
						}
						comp = comp.getParent();
					}
				}
			} catch (BadLocationException e) {
				//ignore
			}
		}
	}

}
