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

package org.eclipse.mylar.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.Task;
import org.eclipse.mylar.tasklist.TasklistImages;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class MarkTaskCompleteAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.mark.completed";
		
	private final TaskListView view;
	
	public MarkTaskCompleteAction(TaskListView view) {
		this.view = view;
		setText("Mark Complete");
        setToolTipText("Mark Complete");
        setId(ID);
        setImageDescriptor(TasklistImages.TASK_COMPLETE);
	}
	@Override
	public void run() {   
//        MylarPlugin.getDefault().actionObserved(this);
	    Object selectedObject = ((IStructuredSelection)this.view.getViewer().getSelection()).getFirstElement();
	    if(selectedObject instanceof ITask &&
	    		MylarTasklistPlugin.getDefault().getTaskHandlerForElement((ITask)selectedObject) != null) {
	    	 MylarTasklistPlugin.getDefault().getTaskHandlerForElement((ITask)selectedObject).taskCompleted((ITask)selectedObject);
	 	}else if (selectedObject instanceof Task){ 
	    	((Task)selectedObject).setCompleted(true);
	    	
	    } 
	    this.view.getViewer().refresh();
	}
}