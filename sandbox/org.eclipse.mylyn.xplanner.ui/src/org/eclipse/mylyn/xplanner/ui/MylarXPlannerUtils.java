/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.ui;

import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.xplanner.ui.wizard.NewXPlannerQueryWizard;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class MylarXPlannerUtils {
	private MylarXPlannerUtils() {
		
	}
	
	public static void addNewMyTasksQuery(TaskRepository repository,
		String name) {
		
		XPlannerCustomQuery myTasksQuery = 
			new XPlannerCustomQuery(repository.getUrl(), name, 
				TasksUiPlugin.getTaskListManager().getTaskList());
		
		myTasksQuery.setMyCurrentTasks(true);
		NewXPlannerQueryWizard.addQuery(myTasksQuery, repository);
	}
			
}
