/**
 *
 */
package org.eclipse.mylyn.internal.sandbox.dev.properties;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author maarten
 */
public class RepositoryQueryPropertySource implements IPropertySource {
	/**
	 *
	 */
	private static final String NULL_MSG = "<null>";
	private static final String LAST_READ = "last_read";
	private static final String STATUS = "status";
	private static final String URL = "url";
	private static final String KIND = "kind";
	private static final String SUMMARY = "summary";

	private AbstractRepositoryQuery query;
	/**
	 * @param adaptableObject
	 */
	public RepositoryQueryPropertySource(AbstractRepositoryQuery adaptableObject) {
		this.query = adaptableObject;
	}

	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		TextPropertyDescriptor summary = new TextPropertyDescriptor(SUMMARY, "Summary");
		summary.setCategory(query.getClass().getName());
		TextPropertyDescriptor kind = new TextPropertyDescriptor(KIND, "Connector Kind");
		kind.setCategory(query.getClass().getName());
		TextPropertyDescriptor url = new TextPropertyDescriptor(URL, "Repository URL");
		url.setCategory(query.getClass().getName());
		TextPropertyDescriptor status = new TextPropertyDescriptor(STATUS, "Synchronization Status");
		status.setCategory(query.getClass().getName());
		TextPropertyDescriptor lastRead = new TextPropertyDescriptor(LAST_READ, "Last Synchronized Timestamp");
		lastRead.setCategory(query.getClass().getName());
		return new IPropertyDescriptor[] {
				summary,
				kind,
				url,
				status,
				lastRead
		};
	}

	public Object getPropertyValue(Object id) {
		if(SUMMARY.equals(id)) {
			return query.getSummary();
		} else if(KIND.equals(id)) {
			return query.getRepositoryKind();
		} else if(URL.equals(id)) {
			return query.getRepositoryUrl();
		} else if(STATUS.equals(id)) {
			return query.getSynchronizationStatus() == null ? NULL_MSG : query.getSynchronizationStatus().toString();
		} else if(LAST_READ.equals(id)) {
			return query.getLastSynchronizedTimeStamp() == null ? NULL_MSG : query.getLastSynchronizedTimeStamp().toString();
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
