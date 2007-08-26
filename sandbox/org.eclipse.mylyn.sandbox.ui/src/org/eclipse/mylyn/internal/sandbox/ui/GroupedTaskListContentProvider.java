/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui;

import java.util.TreeMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.mylyn.internal.tasks.core.TaskArchive;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListContentProvider;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.TaskGroup;

/**
 * @author Eugene Kuleshov
 */
public class GroupedTaskListContentProvider extends TaskListContentProvider implements IExecutableExtension  {

	public static final String MEMENTO_KEY_GROUP_BY = "groupBy";

	private GroupBy groupBy = GroupBy.None;

	@Override
	public Object[] getChildren(Object parent) {
		Object[] children = super.getChildren(parent);
		
		if ((parent instanceof AbstractRepositoryQuery || parent instanceof TaskArchive) 
				&& groupBy!=GroupBy.None) {
			return getGroups((AbstractTaskContainer) parent, children);
		} else if (parent instanceof TaskGroup) {
			return ((TaskGroup) parent).getChildren().toArray();
		} else {
			return children;
		}
	}

	private TaskGroup[] getGroups(AbstractTaskContainer parent, Object[] children) {
		TreeMap<String, TaskGroup> groups = new TreeMap<String, TaskGroup>();

		for (Object container : children) {
			if(container instanceof AbstractTask) {
				AbstractTask task = (AbstractTask) container;
				String key = groupBy.getKey(task);
				if (key == null || key.length() == 0) {
					key = "<unknown>";
				}
				TaskGroup group = groups.get(key);
				if (group == null) {
					group = new TaskGroup(parent.getHandleIdentifier(), key, groupBy.name());
					groups.put(key, group);
				}
				group.internalAddChild(task);
			}
		}

		return groups.values().toArray(new TaskGroup[groups.size()]);
	}

	public GroupBy getGroupBy() {
		return groupBy;
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
