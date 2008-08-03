/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.editors;

import java.util.Iterator;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonThemes;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewer;
import org.eclipse.mylyn.internal.tasks.ui.editors.RepositoryTextViewerConfiguration;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextAttributeEditor;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.themes.IThemeManager;

/**
 * A text attribute that can switch between a source editor and a preview using StackLayout.
 * 
 * @author Jingwen Ou
 * @author Steffen Pingel
 */
public class ExtensibleRichTextAttributeEditor extends RichTextAttributeEditor {

	private IContextActivation contextActivation;

	private final IContextService contextService;

	private SourceViewer defaultViewer;

	private Composite editorComposite;

	private StackLayout editorLayout;

	private final AbstractTaskEditorExtension extension;

	private SourceViewer editorViewer;

	private SourceViewer previewViewer;

	private final int styles;

	private final TaskRepository taskRepository;

	private FormToolkit toolkit;

	public ExtensibleRichTextAttributeEditor(IContextService contextService, TaskDataModel manager,
			TaskRepository taskRepository, AbstractTaskEditorExtension extension, TaskAttribute taskAttribute,
			int styles) {
		super(manager, taskRepository, taskAttribute, styles);
		this.contextService = contextService;
		this.taskRepository = taskRepository;
		this.extension = extension;
		this.styles = styles;
	}

	/** Configures annontation model for spell checking. */
	private void configureAsTextEditor(SourceViewer viewer, Document document) {
		AnnotationModel annotationModel = new AnnotationModel();
		viewer.showAnnotations(false);
		viewer.showAnnotationsOverview(false);
		IAnnotationAccess annotationAccess = new DefaultMarkerAnnotationAccess();
		final SourceViewerDecorationSupport support = new SourceViewerDecorationSupport(viewer, null, annotationAccess,
				EditorsUI.getSharedTextColors());
		Iterator<?> e = new MarkerAnnotationPreferences().getAnnotationPreferences().iterator();
		while (e.hasNext()) {
			support.setAnnotationPreference((AnnotationPreference) e.next());
		}
		support.install(EditorsUI.getPreferenceStore());
		viewer.getTextWidget().addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				support.uninstall();
			}
		});
		viewer.getTextWidget().setIndent(2);
		viewer.setDocument(document, annotationModel);
	}

	private SourceViewer configureEditor(final SourceViewer viewer, boolean readOnly) {
		Document document = new Document(getValue());
		if (readOnly) {
			viewer.setDocument(document);
		} else {
			configureAsTextEditor(viewer, document);
			viewer.addTextListener(new ITextListener() {
				public void textChanged(TextEvent event) {
					// filter out events caused by text presentation changes, e.g. annotation drawing
					String value = viewer.getTextWidget().getText();
					if (!getValue().equals(value)) {
						setValue(value);
						EditorUtil.ensureVisible(viewer.getTextWidget());
					}
				}
			});

			viewer.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

			// drop & drag support, under review
			// TaskEditorDropTarget.addDropTargetSupport(viewer);
		}

		// enable cut/copy/paste
		EditorUtil.setTextViewer(viewer.getTextWidget(), viewer);
		viewer.setEditable(!readOnly);
		viewer.getTextWidget().setFont(getFont());
		toolkit.adapt(viewer.getControl(), false, false);

		return viewer;
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		this.toolkit = toolkit;

		editorComposite = new Composite(parent, SWT.NULL);
		editorLayout = new StackLayout();
		editorComposite.setLayout(editorLayout);
		setControl(editorComposite);

		int styles = this.styles;
		if (!isReadOnly() && (styles & TasksUiInternal.SWT_NO_SCROLL) == 0) {
			styles |= SWT.V_SCROLL;
		}

		if (isReadOnly()) {
			editorViewer = extension.createViewer(taskRepository, editorComposite, styles);
		} else {
			editorViewer = extension.createEditor(taskRepository, editorComposite, styles);
			editorViewer.getTextWidget().addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					setContext();
				}

				public void focusLost(FocusEvent e) {
					unsetContext();
				}
			});
			editorViewer.getTextWidget().addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					unsetContext();
				}
			});
		}
		configureEditor(editorViewer, isReadOnly());

		show(editorViewer);
	}

	private SourceViewer createDefaultEditor(Composite parent, int styles) {
		SourceViewer defaultEditor = new RepositoryTextViewer(taskRepository, parent, styles);

		RepositoryTextViewerConfiguration viewerConfig = new RepositoryTextViewerConfiguration(taskRepository,
				isSpellCheckingEnabled());
		viewerConfig.setMode(getMode());
		defaultEditor.configure(viewerConfig);

		return defaultEditor;
	}

	private SourceViewer getDefaultViewer() {
		if (defaultViewer == null) {
			defaultViewer = createDefaultEditor(editorComposite, styles);
			configureEditor(defaultViewer, isReadOnly());
			// adapt maximize action
			defaultViewer.getControl().setData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION,
					editorViewer.getControl().getData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION));
		}
		return defaultViewer;
	}

	private Font getFont() {
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		Font font = themeManager.getCurrentTheme().getFontRegistry().get(CommonThemes.FONT_EDITOR_COMMENT);
		return font;
	}

	private SourceViewer getPreviewViewer() {
		// construct as needed
		if (previewViewer == null) {
			previewViewer = extension.createViewer(taskRepository, editorComposite, styles);
			configureEditor(previewViewer, true);
			// adapt maximize action
			previewViewer.getControl().setData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION,
					editorViewer.getControl().getData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION));
		}
		Document document = new Document(editorViewer.getDocument().get());
		previewViewer.setDocument(document);
		return previewViewer;
	}

	public SourceViewer getEditorViewer() {
		return editorViewer;
	}

	@Override
	public SourceViewer getViewer() {
		if (defaultViewer != null && editorLayout.topControl == defaultViewer.getControl()) {
			return defaultViewer;
		} else if (previewViewer != null && editorLayout.topControl == previewViewer.getControl()) {
			return previewViewer;
		} else {
			return editorViewer;
		}
	}

	private void setContext() {
		if (contextActivation != null) {
			contextService.deactivateContext(contextActivation);
			contextActivation = null;
		}
		if (contextService != null && extension.getEditorContextId() != null) {
			contextActivation = contextService.activateContext(extension.getEditorContextId());
		}
	}

	/**
	 * Brings <code>viewer</code> to top.
	 */
	private void show(SourceViewer viewer) {
		editorLayout.topControl = viewer.getControl();
		editorComposite.layout();
		viewer.getControl().setFocus();
	}

	public void showDefault() {
		show(getDefaultViewer());
	}

	public void showPreview() {
		if (!isReadOnly()) {
			show(getPreviewViewer());
		}
	}

	public void showEditor() {
		show(getEditorViewer());
	}

	private void unsetContext() {
		if (contextActivation != null) {
			contextService.deactivateContext(contextActivation);
			contextActivation = null;
		}
	}

}
