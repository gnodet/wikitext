/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.xplanner.ui.wizard.NewXPlannerQueryWizard;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class MylynXPlannerUtils {
	private MylynXPlannerUtils() {
		
	}
	
	public static void addNewMyTasksQuery(TaskRepository repository,
		String name) {
		
		XPlannerCustomQuery myTasksQuery = 
			new XPlannerCustomQuery(repository.getUrl(), name);
		
		myTasksQuery.setMyCurrentTasks(true);
		NewXPlannerQueryWizard.addQuery(myTasksQuery, repository);
	}
			
}
