/**
 *
 */
package org.eclipse.mylyn.internal.sandbox.dev.properties;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Display various {@link AbstractRepositoryQuery} properties in the Eclipse Properties View.<br />
 * See <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=210639">Bug 210639</a> and <a
 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=208275">Bug 208275</a><br />
 *
 * @author Maarten Meijer
 */
public class RepositoryQueryPropertySource extends AbstractTaskContainerPropertySource implements IPropertySource {

	private static final String NULL_MSG = "<null>";

	private static final String LAST_READ = "last_read";

	private static final String STATUS = "status";

	private static final String REPOSITORY = "repository";

	private static final String QUERY = "query";

	private static final String KIND = "kind";

	public RepositoryQueryPropertySource(AbstractRepositoryQuery adaptableObject) {
		super(adaptableObject);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		TextPropertyDescriptor summary = new TextPropertyDescriptor(SUMMARY, "Query Summary");
		summary.setCategory(description);
		TextPropertyDescriptor kind = new TextPropertyDescriptor(KIND, "Repository Connector Kind");
		kind.setCategory(description);
		TextPropertyDescriptor url = new TextPropertyDescriptor(REPOSITORY, "Repository URL");
		url.setCategory(description);
		TextPropertyDescriptor query = new TextPropertyDescriptor(QUERY, "Query URL");
		query.setCategory(description);
		TextPropertyDescriptor status = new TextPropertyDescriptor(STATUS, "Synchronization Status");
		status.setCategory(description);
		TextPropertyDescriptor lastRead = new TextPropertyDescriptor(LAST_READ, "Synchronization Timestamp");
		lastRead.setCategory(description);
		IPropertyDescriptor[] specific = new IPropertyDescriptor[] { summary, kind, url, query, status, lastRead, };
		return super.appendSpecifics(specific, super.getPropertyDescriptors());
	}

	@Override
	public Object getPropertyValue(Object id) {
		AbstractRepositoryQuery query = (AbstractRepositoryQuery) container;
		if (SUMMARY.equals(id)) {
			return query.getSummary();
		} else if (KIND.equals(id)) {
			return query.getConnectorKind();
		} else if (REPOSITORY.equals(id)) {
			return query.getRepositoryUrl();
		} else if (QUERY.equals(id)) {
			return query.getUrl();
		} else if (STATUS.equals(id)) {
			return query.getSynchronizationStatus() == null ? NULL_MSG : query.getSynchronizationStatus().toString();
		} else if (LAST_READ.equals(id)) {
			return query.getLastSynchronizedTimeStamp() == null ? NULL_MSG : query.getLastSynchronizedTimeStamp()
					.toString();
		}
		return super.getPropertyValue(id);
	}
}
