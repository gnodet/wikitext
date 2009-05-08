/*******************************************************************************
 * Copyright (c) 2004, 2008 Maarten Meijer and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Maarten Meijer - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.dev.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Displays various {@link AbstractTask} properties in the Properties View.
 * 
 * @author Maarten Meijer
 */
public class AbstractTaskPropertiesSource extends AbstractTaskContainerPropertySource implements IPropertySource {

	private static final String STATE = "state";

	private static final String STATUS = "status";

	private static final String URL = "url";

	private static final String KIND = "kind";

	private static final String OWNER = "owner";

	private static final String PARENT = "parent";

	private static final String SCHEDULED = "scheduled";

	private final AbstractTask task;

	private Map<String, TaskAttribute> taskdata;

	private static final String dataCategoryName = TaskData.class.getCanonicalName();

	public AbstractTaskPropertiesSource(AbstractTask adaptableObject) {
		super(adaptableObject);
		this.task = adaptableObject;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		TextPropertyDescriptor summary = new TextPropertyDescriptor(SUMMARY, "Summary");
		summary.setCategory(description);
		TextPropertyDescriptor owner = new TextPropertyDescriptor(OWNER, "Owner");
		owner.setCategory(description);
		TextPropertyDescriptor scheduled = new TextPropertyDescriptor(SCHEDULED, "Scheduled for");
		scheduled.setCategory(description);
		TextPropertyDescriptor parent = new TextPropertyDescriptor(PARENT, "Parent Containers");
		parent.setCategory(description);
		TextPropertyDescriptor kind = new TextPropertyDescriptor(KIND, "Repository Connector Kind");
		kind.setCategory(description);
		TextPropertyDescriptor url = new TextPropertyDescriptor(URL, "Repository URL");
		url.setCategory(description);
		TextPropertyDescriptor status = new TextPropertyDescriptor(STATUS, "Synchronization Status");
		status.setCategory(description);
		TextPropertyDescriptor state = new TextPropertyDescriptor(STATE, "Synchronization State");
		state.setCategory(description);
		IPropertyDescriptor[] specific = new IPropertyDescriptor[] { summary, owner, scheduled, parent, kind, url,
				status, state };
		IPropertyDescriptor[] descriptors = super.appendSpecifics(specific, getTaskDataAsProperties());
		return super.appendSpecifics(descriptors, super.getPropertyDescriptors());
	}

	@Override
	public Object getPropertyValue(Object id) {
		AbstractTask task = (AbstractTask) container;
		if (SUMMARY.equals(id)) {
			return safeObject(task.getSummary());
		} else if (OWNER.equals(id)) {
			return safeObject(task.getOwner());
		} else if (SCHEDULED.equals(id)) {
			return safeObject(task.getScheduledForDate());
		} else if (PARENT.equals(id)) {
			return safeObject((task).getParentContainers()).toString();
		} else if (KIND.equals(id)) {
			return task.getConnectorKind();
		} else if (URL.equals(id)) {
			return task.getRepositoryUrl();
		} else if (STATE.equals(id)) {
			return safeObject(task.getSynchronizationState()).toString();
		} else if (STATUS.equals(id)) {
			return safeObject(task.getStatus()).toString();
		}
		Object dataValue = getTaskDataValue(id);
		if (null != dataValue) {
			return dataValue;
		}
		return super.getPropertyValue(id);
	}

	private Object getTaskDataValue(Object id) {
		try {
			Map<String, TaskAttribute> taskdata = getAttributes();
			if (taskdata.containsKey(id)) {
				return safeObject(taskdata.get(id)).toString();
			}
		} catch (CoreException e) {
			// fail silently
		}
		return null;
	}

	private Map<String, TaskAttribute> getAttributes() throws CoreException {
		if (taskdata == null) {
			taskdata = TasksUiPlugin.getTaskDataManager()
					.getWorkingCopy(task, false)
					.getLocalData()
					.getRoot()
					.getAttributes();
		}
		return taskdata;
	}

	private IPropertyDescriptor[] getTaskDataAsProperties() {
		List<TextPropertyDescriptor> props = new ArrayList<TextPropertyDescriptor>();
		try {
			Map<String, TaskAttribute> taskdata = getAttributes();
			for (String key : taskdata.keySet()) {
				TextPropertyDescriptor desc = new TextPropertyDescriptor(key, key);
				desc.setCategory(dataCategoryName);
				props.add(desc);
			}
		} catch (CoreException e) {
			// fail silently
		}
		return props.toArray(new TextPropertyDescriptor[0]);
	}
}
