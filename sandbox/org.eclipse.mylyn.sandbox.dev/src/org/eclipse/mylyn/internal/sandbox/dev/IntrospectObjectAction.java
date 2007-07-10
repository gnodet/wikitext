/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.dev;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 */
public class IntrospectObjectAction implements IViewActionDelegate {

	private ISelection currentSelection;

	public void init(IViewPart view) {

	}

	public void run(IAction action) {
		if (currentSelection instanceof StructuredSelection) {
			StructuredSelection selection = (StructuredSelection) currentSelection;
			Object object = selection.getFirstElement();

			String text = "Object class: " + object.getClass() + "\n\n";

			try {
				AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(object);
				IInteractionElement node = ContextCorePlugin.getContextManager().getElement(
						bridge.getHandleIdentifier(object));
				if (node != null) {
					text += "Interest value: " + node.getInterest().getValue() + "\n";
					text += node.getInterest().toString();
				}
			} catch (Throwable t) {
				text += "<no structure bridge>";
			}

			if (object instanceof IAdaptable) {
				Object resourceAdapter = ((IAdaptable) object).getAdapter(IResource.class);
				if (resourceAdapter != null) {
					text += "\nResource adapter: " + ((IResource) resourceAdapter).getFullPath().toOSString();
				}
			}

			if (object instanceof AbstractTask) {

				AbstractTask task = null;
				if (object instanceof AbstractTask) {
					task = (AbstractTask) object;
				}
				if (task != null) {
					TaskRepository repository = TasksUiPlugin.getRepositoryManager().getRepository(
							task.getConnectorKind(), task.getRepositoryUrl());
					text += "\nLast time in SYNCHRONIZED state: " + task.getLastReadTimeStamp();
					text += "\nRepository synch time stamp: " + repository.getSynchronizationTimeStamp();
					text += "\nSync state: " + task.getSynchronizationState();
					text += "\nParents: " + task.getParentContainers();
					if (task.getChildren() != null && !task.getChildren().isEmpty()) {
						text += "\nChildren: ";
						for (AbstractTask subTask : task.getChildren()) {
							text += "\n" + subTask;
						}
					}
				}
			}
			text += "\n\nNum tasks: " + TasksUiPlugin.getTaskListManager().getTaskList().getAllTasks().size();
			text += "\nNum queries: " + TasksUiPlugin.getTaskListManager().getTaskList().getQueries().size();

			MessageDialog.openInformation(null, "Mylyn Sandbox", text);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.currentSelection = selection;
	}

}
