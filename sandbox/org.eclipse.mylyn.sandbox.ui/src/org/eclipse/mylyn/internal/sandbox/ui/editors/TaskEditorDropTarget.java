/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.editors;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.sandbox.ui.hyperlinks.ResourceHyperlinkExtensions;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.custom.StyledTextDropTargetEffect;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.part.ResourceTransfer;

/**
 * Specifies a move operation is allowed, but when the actual operation is performed, covert it to a copy operation
 * 
 * @author Jingwen Ou
 */
public class TaskEditorDropTarget extends StyledTextDropTargetEffect {

	public static final String DEFAULT_PREFIX = "file";

	public static void addDropTargetSupport(SourceViewer editor) {
		new TaskEditorDropTarget(editor);
	}

	private final SourceViewer editor;

	private TaskEditorDropTarget(SourceViewer editor) {
		super(editor.getTextWidget());
		this.editor = editor;

		DropTarget target = new DropTarget(editor.getControl(), DND.DROP_COPY | DND.DROP_MOVE);
		// TODO: may also add TaskTransfer?
		// TODO: this concern may also go to IResourceHyperlinkExtension by adding something like IResourceHyperlinkExtension.getTransfers()?
		target.setTransfer(new Transfer[] { JavaUI.getJavaElementClipboardTransfer(), ResourceTransfer.getInstance(),
				TextTransfer.getInstance() });
		target.addDropListener(this);

	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		if (event.detail == DND.DROP_MOVE || event.detail == DND.DROP_DEFAULT) {
			if ((event.operations & DND.DROP_COPY) != 0) {
				event.detail = DND.DROP_COPY;
			} else {
				event.detail = DND.DROP_NONE;
			}
		}
		super.dragEnter(event);
	}

	@Override
	public void drop(DropTargetEvent event) {
		StringBuilder shouldDropText = new StringBuilder();
		// this concern may also go to IResourceHyperlinkExtension by adding something like IResourceHyperlinkExtension.drop(DropTargetEvent)?
		if (TextTransfer.getInstance().isSupportedType(event.currentDataType) && (event.data instanceof String)) {
			shouldDropText.append((String) event.data);
		} else if (JavaUI.getJavaElementClipboardTransfer().isSupportedType(event.currentDataType)
				&& (event.data instanceof IJavaElement[])) {

			IJavaElement[] elements = (IJavaElement[]) event.data;

			for (IJavaElement element : elements) {
				shouldDropText.append(ResourceHyperlinkExtensions.getGeneratedPrefix("java") + element.getElementName()
						+ addCommaIfNeeded(elements, element));
			}

		} else if (ResourceTransfer.getInstance().isSupportedType(event.currentDataType)
				&& (event.data instanceof IResource[])) {
			IResource[] elements = (IResource[]) event.data;
			for (IResource element : elements) {
				String fullName = element.getName();
				int sep_pos = fullName.lastIndexOf(".");

				if (sep_pos != -1) {
					String fileName = fullName.substring(0, sep_pos);
					String fileType = fullName.substring(sep_pos + 1);

					// decide file type by file extension
					String generatedPrefix = ResourceHyperlinkExtensions.getGeneratedPrefix(fileType.toLowerCase());

					if (generatedPrefix != null) {
						shouldDropText.append(generatedPrefix + " " + fileName + addCommaIfNeeded(elements, element));
					} else {
						// cannot decide the file type, then use default
						shouldDropText.append(ResourceHyperlinkExtensions.getDefaultPrefix() + " "
								+ element.getFullPath().toString().substring(1) + addCommaIfNeeded(elements, element));
					}
				}

			}
		}

		if (shouldDropText.length() > 0) {
			dropTextToCurrentPosition(shouldDropText.toString());
		}
	}

	private String addCommaIfNeeded(Object[] elements, Object element) {
		return ((elements.length > 1 && element != elements[elements.length - 1]) ? ", " : "");
	}

	private void dropTextToCurrentPosition(String shouldDropText) {
		try {
			ITextSelection textSelection = ((ITextSelection) editor.getSelection());
			IDocument currentDocument = editor.getDocument();
			currentDocument.replace(textSelection.getOffset(), 0, shouldDropText);

			// move cursor to the end
			editor.getTextWidget().setFocus();
			editor.setSelection(new TextSelection(textSelection.getOffset() + shouldDropText.length(), 0));
		} catch (BadLocationException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not drop text", e));
		}
	}
}
