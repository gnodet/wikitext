/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Jun 10, 2005
 */
package org.eclipse.mylyn.internal.monitor.monitors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.core.MylarContextManager;
import org.eclipse.mylyn.internal.monitor.HandleObfuscator;
import org.eclipse.mylyn.provisional.core.AbstractUserInteractionMonitor;
import org.eclipse.mylyn.provisional.core.IMylarElement;
import org.eclipse.mylyn.provisional.core.InteractionEvent;
import org.eclipse.mylyn.provisional.core.MylarPlugin;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;

/**
 * Limited to Java selections.
 * 
 * @author Mik Kersten
 */
public class SelectionMonitor extends AbstractUserInteractionMonitor {

	private static final String ID_JAVA_UNKNOWN = "(non-source element)";

	public static final String SELECTION_DEFAULT = "selected";

	public static final String SELECTION_NEW = "new";

	public static final String SELECTION_DECAYED = "decayed";

	public static final String SELECTION_PREDICTED = "predicted";

	private static final Object ID_JAVA_UNKNOW_OLD = "(non-existing element)";

	private IJavaElement lastSelectedElement = null;

	private HandleObfuscator obfuscator = new HandleObfuscator();
	
	@Override
	protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection) {
		// ignored, since not using context monitoring facilities
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		String structureKind = InteractionEvent.ID_UNKNOWN;
		String obfuscatedElementHandle = InteractionEvent.ID_UNKNOWN;
		String elementHandle = InteractionEvent.ID_UNKNOWN;
		InteractionEvent.Kind interactionKind = InteractionEvent.Kind.SELECTION;
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Object selectedObject = structuredSelection.getFirstElement();
			if (selectedObject == null)
				return;
			if (selectedObject instanceof IJavaElement) {
				IJavaElement javaElement = (IJavaElement) selectedObject;
				structureKind = "java:" + javaElement.getClass();
				elementHandle = javaElement.getHandleIdentifier();
				obfuscatedElementHandle = obfuscateJavaElementHandle(javaElement);
				lastSelectedElement = javaElement;
			} else {
				structureKind = InteractionEvent.ID_UNKNOWN + ": " + selectedObject.getClass();
				if (selectedObject instanceof IAdaptable) {
					IResource resource = (IResource) ((IAdaptable) selectedObject).getAdapter(IResource.class);
					if (resource != null) {
						obfuscatedElementHandle = obfuscator.obfuscateResourcePath(resource.getProjectRelativePath());
					}
				}
			}
		} else {
			if (selection instanceof TextSelection && part instanceof JavaEditor) {
				TextSelection textSelection = (TextSelection) selection;
				IJavaElement javaElement;
				try {
					javaElement = SelectionConverter.resolveEnclosingElement((JavaEditor) part, textSelection);
					if (javaElement != null) {
						structureKind = "java:" + javaElement.getClass();
						obfuscatedElementHandle = obfuscateJavaElementHandle(javaElement);
						elementHandle = javaElement.getHandleIdentifier();
						if (javaElement != null && javaElement.equals(lastSelectedElement)) {
							interactionKind = InteractionEvent.Kind.EDIT;
						}
						lastSelectedElement = javaElement;
					}
				} catch (JavaModelException e) {
					// ignore unresolved elements
				}
			} else if (part instanceof EditorPart) {
				EditorPart editorPart = (EditorPart) part;
				IEditorInput input = editorPart.getEditorInput();
				if (input instanceof IPathEditorInput) {
					structureKind = "file";
					obfuscatedElementHandle = obfuscator.obfuscateResourcePath(((IPathEditorInput) input).getPath());
				}
			}
		}
		IMylarElement node = ContextCorePlugin.getContextManager().getElement(elementHandle);
		String delta = "";
		float selectionFactor = MylarContextManager.getScalingFactors().get(InteractionEvent.Kind.SELECTION).getValue();

		// XXX: broken in 0.4?
		if (node != null) {
			if (node.getInterest().getEncodedValue() <= selectionFactor
					&& node.getInterest().getValue() > selectionFactor) {
				delta = SELECTION_PREDICTED;
			} else if (node.getInterest().getEncodedValue() < selectionFactor
					&& node.getInterest().getDecayValue() > selectionFactor) {
				delta = SELECTION_DECAYED;
			} else if (node.getInterest().getValue() == selectionFactor
					&& node.getInterest().getDecayValue() < selectionFactor) {
				delta = SELECTION_NEW;
			} else {
				delta = SELECTION_DEFAULT;
			}
		}

		InteractionEvent event = new InteractionEvent(interactionKind, structureKind, obfuscatedElementHandle, part
				.getSite().getId(), "null", delta, 0);
		ContextCorePlugin.getDefault().notifyInteractionObserved(event);
	}

	private String obfuscateJavaElementHandle(IJavaElement javaElement) {
		try {
			StringBuffer obfuscatedPath = new StringBuffer();
			IResource resource;
			resource = (IResource) javaElement.getUnderlyingResource();
			if (resource != null && (resource instanceof IFile)) {
				IFile file = (IFile) resource;
				obfuscatedPath.append(obfuscator.obfuscateResourcePath(file.getProjectRelativePath()));
				obfuscatedPath.append(':');
				obfuscatedPath.append(obfuscator.obfuscateString(javaElement.getElementName()));
				return obfuscatedPath.toString();
			} else {
				return obfuscator.obfuscateString(javaElement.getHandleIdentifier());
			}
		} catch (JavaModelException e) {
			// ignore non-existing element
		}
		return ID_JAVA_UNKNOWN;
	}

	/**
	 * Some events do not have a valid handle, e.g. hande is null or ?
	 */
	public static boolean isValidStructureHandle(InteractionEvent event) {
		String handle = event.getStructureHandle();
		return handle != null 
			&& !handle.trim().equals("") 
			&& !handle.equals(SelectionMonitor.ID_JAVA_UNKNOWN)
			&& !handle.equals(SelectionMonitor.ID_JAVA_UNKNOW_OLD)
			&& event.isValidStructureHandle();
	}
}
