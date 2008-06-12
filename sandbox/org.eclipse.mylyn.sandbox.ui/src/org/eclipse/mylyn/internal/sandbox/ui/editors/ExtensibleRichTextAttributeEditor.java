/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.editors;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonThemes;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextAttributeEditor;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.themes.IThemeManager;

/**
 * A multitab source viewer that can edit and preview Textile wikitext markup.
 * 
 * TODO generalize RichTextAttributeEditor: add a protected method to create the SourceViewer then subclasses can then
 * create its own SourceViewer without overriding createControl and copying the code in it
 * 
 * @author Jingwen Ou
 */
public class ExtensibleRichTextAttributeEditor extends RichTextAttributeEditor {

	private SourceViewer source;

	private final TaskRepository taskRepository;

	private final int styles;

	private final IContextService contextService;

	private IContextActivation contextActivation;

	private final AbstractTaskEditorExtension extension;

	public ExtensibleRichTextAttributeEditor(IContextService contextService, TaskDataModel manager,
			TaskRepository taskRepository, AbstractTaskEditorExtension extension, TaskAttribute taskAttribute,
			int styles) {
		super(manager, taskRepository, taskAttribute, styles);
		this.contextService = contextService;
		this.taskRepository = taskRepository;
		this.extension = extension;
		this.styles = styles;
	}

	@Override
	public SourceViewer getViewer() {
		return source;
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		if (isReadOnly()) {
			source = extension.createViewer(taskRepository, parent, styles);
			source.setDocument(new Document(getValue()));

			setControl(source instanceof Viewer ? ((Viewer) source).getControl() : source.getTextWidget());
		} else {
			CTabFolder folder = new CTabFolder(parent, SWT.FLAT | SWT.BOTTOM);

			/** wikitext markup editor **/

			CTabItem viewerItem = new CTabItem(folder, SWT.NONE);
			viewerItem.setText("Source");
			viewerItem.setToolTipText("Edit Source");

			source = extension.createEditor(taskRepository, folder, styles | SWT.V_SCROLL);
			if (source.getDocument() == null) {
				source.setDocument(new Document(getValue()));
			}
			source.addTextListener(new ITextListener() {
				public void textChanged(TextEvent event) {
					// filter out events caused by text presentation changes, e.g. annotation drawing
					String value = source.getTextWidget().getText();
					if (!getValue().equals(value)) {
						setValue(value);
					}
				}
			});
			source.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

			//setting up focus stuff, copied from Daivd's solution
			FocusListener focusListener = new FocusListener() {

				public void focusGained(FocusEvent e) {
					setContext();
				}

				public void focusLost(FocusEvent e) {
					unsetContext();
				}
			};
			source.getTextWidget().addFocusListener(focusListener);
			DisposeListener disposeListener = new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					unsetContext();
				}
			};
			source.getTextWidget().addDisposeListener(disposeListener);

			viewerItem.setControl(source instanceof Viewer ? ((Viewer) source).getControl() : source.getTextWidget());
			folder.setSelection(viewerItem);

			/** wikitext markup viewer **/

			CTabItem previewItem = new CTabItem(folder, SWT.NONE);
			previewItem.setText("Preview");
			previewItem.setToolTipText("Preview Source");

			final SourceViewer preview = extension.createViewer(taskRepository, folder, styles);

			previewItem.setControl(preview instanceof Viewer ? ((Viewer) preview).getControl()
					: preview.getTextWidget());

			folder.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent selectionevent) {
					widgetSelected(selectionevent);
				}

				public void widgetSelected(SelectionEvent selectionevent) {
					Document document = new Document(source.getDocument().get());
					preview.setDocument(document);
				}
			});

			setControl(folder);
		}

		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		Font font = themeManager.getCurrentTheme().getFontRegistry().get(CommonThemes.FONT_EDITOR_COMMENT);
		source.getTextWidget().setFont(font);
		toolkit.adapt(source.getControl(), true, false);

	}

	protected void unsetContext() {
		if (contextActivation != null) {
			contextService.deactivateContext(contextActivation);
			contextActivation = null;
		}
	}

	protected void setContext() {
		if (contextActivation != null) {
			contextService.deactivateContext(contextActivation);
			contextActivation = null;
		}
		if (contextService != null && extension.getEditorContextId() != null) {
			contextActivation = contextService.activateContext(extension.getEditorContextId());
		}
	}
}
