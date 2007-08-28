/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.sandbox.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTaskListContentProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTaskListPresentation;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;

/**
 * @author Eugene Kuleshov
 */
public class GroupedPresentation extends AbstractTaskListPresentation implements IExecutableExtension {

	private GroupBy groupBy;

	public GroupedPresentation() {
		super(null);
	}
	
	@Override
	protected AbstractTaskListContentProvider createContentProvider(TaskListView taskListView) {
		return new GroupedTaskListContentProvider(taskListView, groupBy);
	}

	@Override
	public String getId() {
		return "org.eclipse.mylyn.tasks.ui.groupBy" + groupBy;
	}
	
	// IExecutableExtension
	
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		try {
			groupBy = GroupBy.valueOf((String) data);
		} catch (IllegalArgumentException ex) {
			// ignore
		}
	}

}
