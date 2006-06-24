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

package org.eclipse.mylar.internal.sandbox.devtools;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.IMylarStructureBridge;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
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
				IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(object);
				IMylarElement node = MylarPlugin.getContextManager().getElement(bridge.getHandleIdentifier(object));
				if (node != null) {
					text += "Interest value: " + node.getInterest().getValue() + "\n";
					text += node.getInterest().toString();
				}
			} catch (Throwable t) {
				text += "<no structure bridge>";
			}

			if (object instanceof AbstractRepositoryTask || object instanceof AbstractQueryHit) {
				
				AbstractRepositoryTask task;
				if (object instanceof AbstractRepositoryTask) {
					task = (AbstractRepositoryTask)object;
				} else {
					task = ((AbstractQueryHit)object).getCorrespondingTask();
				}
				if (task != null) {
					TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(task.getRepositoryKind(), task.getRepositoryUrl());
					text += "\nlast modified: " + task.getLastModifiedDateStamp();
					text += "\nrepository synch time stamp: " + repository.getSyncTimeStamp();
					text += "\n sync state: "+ task.getSyncState();
				}
			}
			
			MessageDialog.openInformation(null, "Mylar Sandbox", text);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.currentSelection = selection;
	}

}
