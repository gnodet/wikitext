/**
 *
 */
package org.eclipse.mylyn.sandbox.dev.spy.adapters;

import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author maarten
 */
public class AbstractTaskPropertiesSource implements IPropertySource {
	/**
	 *
	 */
	private static final String NULL_MSG = "<null>";
	private static final String LAST_READ = "last_read";
	private static final String STATE = "state";
	private static final String STATUS = "status";
	private static final String URL = "url";
	private static final String KIND = "kind";
	private static final String OWNER = "owner";
	private static final String PARENT = "parent";
	private static final String SCHEDULED = "scheduled";
	private static final String SUMMARY = "summary";

	private AbstractTask task;
	/**
	 * @param adaptableObject
	 */
	public AbstractTaskPropertiesSource(AbstractTask adaptableObject) {
		this.task = adaptableObject;
	}

	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {

		TextPropertyDescriptor summary = new TextPropertyDescriptor(SUMMARY, "Summary");
		summary.setCategory(task.getClass().getName());
		TextPropertyDescriptor owner = new TextPropertyDescriptor(OWNER, "Owner");
		owner.setCategory(task.getClass().getName());
		TextPropertyDescriptor scheduled = new TextPropertyDescriptor(SCHEDULED, "Scheduled for");
		scheduled.setCategory(task.getClass().getName());
		TextPropertyDescriptor parent = new TextPropertyDescriptor(PARENT, "Parent Containers");
		parent.setCategory(task.getClass().getName());
		TextPropertyDescriptor kind = new TextPropertyDescriptor(KIND, "Connector Kind");
		kind.setCategory(task.getClass().getName());
		TextPropertyDescriptor url = new TextPropertyDescriptor(URL, "Repository URL");
		url.setCategory(task.getClass().getName());
		TextPropertyDescriptor status = new TextPropertyDescriptor(STATUS, "Synchronization Status");
		status.setCategory(task.getClass().getName());
		TextPropertyDescriptor state = new TextPropertyDescriptor(STATE, "Synchronization State");
		state.setCategory(task.getClass().getName());
		TextPropertyDescriptor lastRead = new TextPropertyDescriptor(LAST_READ, "Last Read Timestamp");
		lastRead.setCategory(task.getClass().getName());
		return new IPropertyDescriptor[] {
				summary,
				owner,
				scheduled,
				parent,
				kind,
				url,
				status,
				state,
				lastRead
		};
	}

	public Object getPropertyValue(Object id) {
		if(SUMMARY.equals(id)) {
			return task.getSummary();
		} else if(OWNER.equals(id)) {
			return task.getOwner();
		} else if(SCHEDULED.equals(id)) {
			return task.getScheduledForDate() == null ? NULL_MSG : task.getScheduledForDate();
		} else if(PARENT.equals(id)) {
			return task.getParentContainers() == null ? NULL_MSG : task.getParentContainers().toString();
		} else if(KIND.equals(id)) {
			return task.getConnectorKind();
		} else if(URL.equals(id)) {
			return task.getRepositoryUrl();
		} else if(STATE.equals(id)) {
			return task.getSynchronizationState() == null ? NULL_MSG : task.getSynchronizationState().toString();
		} else if(STATUS.equals(id)) {
			return task.getSynchronizationStatus() == null ? NULL_MSG : task.getSynchronizationStatus().toString();
		} else if(LAST_READ.equals(id)) {
			return task.getLastReadTimeStamp() == null ? NULL_MSG : task.getLastReadTimeStamp().toString();
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub

	}

	public void setPropertyValue(Object id, Object value) {
		// TODO Auto-generated method stub

	}

}
