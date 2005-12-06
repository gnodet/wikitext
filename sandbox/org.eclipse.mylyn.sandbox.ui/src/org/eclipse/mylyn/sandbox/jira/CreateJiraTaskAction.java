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

package org.eclipse.mylar.sandbox.jira;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 */
public class CreateJiraTaskAction extends Action implements IViewActionDelegate{
	
	private static final String LABEL = "Add Existing Jira Report";

	public static final String ID = "org.eclipse.mylar.bugs.jira.actions.create.bug";
		
	public CreateJiraTaskAction() {
		setText(LABEL);
        setToolTipText(LABEL);
        setId(ID); 
	} 
	
	@Override
	public void run() {
		if(TaskListView.getDefault() == null) return;

		MylarPlugin.fail(new RuntimeException("unimplmented"), "unimplemented", true);
//	    String bugIdString = TaskListView.getDefault().getBugIdFromUser();
//	    int bugId = -1;
//	    try {
//	    	if (bugIdString != null) {
//	    		bugId = Integer.parseInt(bugIdString);
//	    	} else {
//	    		return;
//	    	}
//	    } catch (NumberFormatException nfe) {
//	        TaskListView.getDefault().showMessage("Please enter a valid report number");
//	        return;
//	    }
//	
//	    ITask newTask = new BugzillaTask("Bugzilla-"+bugId, "<bugzilla info>", true, true);				
//	    Object selectedObject = ((IStructuredSelection)TaskListView.getDefault().getViewer().getSelection()).getFirstElement();
//    	
//	    ITaskHandler taskHandler = MylarTaskListPlugin.getDefault().getTaskHandlerForElement(newTask);
//	    if(taskHandler != null){
//	    	ITask addedTask = taskHandler.taskAdded(newTask);
//	    	if(addedTask instanceof BugzillaTask){
//		    	BugzillaTask newTask2 = (BugzillaTask)addedTask;
//	    		if(newTask2 == newTask){
//	    			((BugzillaTask)newTask).scheduleDownloadReport();
//	    		} else {
//	    			newTask = newTask2;
//	    			((BugzillaTask)newTask).updateTaskDetails();
//	    		}
//	    	}
//    	} else {
//    		((BugzillaTask)newTask).scheduleDownloadReport();
//    	}
//	    if (selectedObject instanceof TaskCategory){
//	        ((TaskCategory)selectedObject).addTask(newTask);
//	    } else { 
//	        MylarTaskListPlugin.getTaskListManager().addRootTask(newTask);
//	    }
//	    BugzillaUiPlugin.getDefault().getBugzillaTaskListManager().addToBugzillaTaskRegistry((BugzillaTask)newTask);
//
//	    if(TaskListView.getDefault() != null)
//			TaskListView.getDefault().getViewer().refresh();
	}

	public void init(IViewPart view) {
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		
	}
}