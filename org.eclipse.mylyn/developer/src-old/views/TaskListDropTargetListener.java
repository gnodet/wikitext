/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Feb 9, 2005
  */
package org.eclipse.mylyn.tasklist.ui.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.mylyn.tasklist.ITask;
import org.eclipse.mylyn.tasklist.TaskListPlugin;


final class TaskListDropTargetListener implements DropTargetListener {
    private final Composite component;
    private boolean addMode;
    final Viewer viewer;
    final TextTransfer textTransfer;

    TaskListDropTargetListener(Composite component, Viewer viewer, TextTransfer textTransfer, boolean addMode) {
        super();
        this.component = component;
        this.viewer = viewer;
        this.textTransfer = textTransfer;
        this.addMode = addMode;
    }

    public void dragEnter(DropTargetEvent event) {
        if (event.detail == DND.DROP_DEFAULT) event.detail = DND.DROP_COPY;
    }

    public void dragLeave(DropTargetEvent event) { }

    public void dragOperationChanged(DropTargetEvent event) { }

    public void dragOver(DropTargetEvent event) {
        if (textTransfer.isSupportedType(event.currentDataType)) {
            // NOTE: on unsupported platforms this will return null
            String t = (String) (textTransfer.nativeToJava(event.currentDataType));
            if (t != null) { }
        }
    }

    public void drop(DropTargetEvent event) {  
        if (textTransfer.isSupportedType(event.currentDataType)) {
            String text = (String) event.data;
            int id = Integer.parseInt(text);
            ITask task = TaskListPlugin.getTaskListManager().getTaskList().getTaskForId(id);
            if (task == null) return;  
            if (viewer != null && addMode && !TaskListPlugin.getTaskListManager().getTaskList().getActiveTasks().contains(task)) {
                TaskListPlugin.getTaskListManager().activateTask(task);
                viewer.refresh();
            } else {
                TaskListPlugin.getTaskListManager().deactivateTask(task);
            }
        } 
    }

    public void dropAccept(DropTargetEvent event) { }
}