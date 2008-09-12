/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Maarten Meijer - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.dev.properties;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Display various {@link AbstractTask} properties in the Eclipse Properties View.<br />
 * See <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=210639">Bug 210639</a> and <a
 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=208275">Bug 208275</a><br />
 * 
 * @author Maarten Meijer
 */
public class AbstractTaskPropertiesSource extends AbstractTaskContainerPropertySource implements IPropertySource {

	private static final String NULL_MSG = "<null>";

	private static final String LAST_READ = "last_read";

	private static final String STATE = "state";

	private static final String STATUS = "status";

	private static final String URL = "url";

	private static final String KIND = "kind";

	private static final String OWNER = "owner";

	private static final String PARENT = "parent";

	private static final String SCHEDULED = "scheduled";

	public AbstractTaskPropertiesSource(AbstractTask adaptableObject) {
		super(adaptableObject);
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
		TextPropertyDescriptor lastRead = new TextPropertyDescriptor(LAST_READ, "Last Read Timestamp");
		lastRead.setCategory(description);
		IPropertyDescriptor[] specific = new IPropertyDescriptor[] { summary, owner, scheduled, parent, kind, url,
				status, state, lastRead };
		return super.appendSpecifics(specific, super.getPropertyDescriptors());
	}

	@SuppressWarnings("deprecation")
	@Override
	public Object getPropertyValue(Object id) {
		AbstractTask task = (AbstractTask) container;
		if (SUMMARY.equals(id)) {
			return task.getSummary();
		} else if (OWNER.equals(id)) {
			return task.getOwner();
		} else if (SCHEDULED.equals(id)) {
			return task.getScheduledForDate() == null ? NULL_MSG : task.getScheduledForDate();
		} else if (PARENT.equals(id)) {
			return (task).getParentContainers() == null ? NULL_MSG : (task).getParentContainers().toString();
		} else if (KIND.equals(id)) {
			return task.getConnectorKind();
		} else if (URL.equals(id)) {
			return task.getRepositoryUrl();
		} else if (STATE.equals(id)) {
			return task.getSynchronizationState() == null ? NULL_MSG : task.getSynchronizationState().toString();
		} else if (STATUS.equals(id)) {
			return task.getStatus() == null ? NULL_MSG : task.getStatus().toString();
		} else if (LAST_READ.equals(id)) {
			return task.getLastReadTimeStamp() == null ? NULL_MSG : task.getLastReadTimeStamp().toString();
		}
		return super.getPropertyValue(id);
	}

}
