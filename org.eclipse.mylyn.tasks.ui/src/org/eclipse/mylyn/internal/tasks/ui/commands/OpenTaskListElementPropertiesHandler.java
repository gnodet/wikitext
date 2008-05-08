/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.commands;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskElement;

/**
 * @author Steffen Pingel
 */
public class OpenTaskListElementPropertiesHandler extends AbstractTaskListViewHandler {

	@Override
	protected void execute(ExecutionEvent event, TaskListView taskListView, ITaskElement item) {
		if (item instanceof TaskCategory || item instanceof IRepositoryQuery) {
			TasksUiInternal.refreshAndOpenTaskListElement(item);
		}
	}

}