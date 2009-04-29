/*******************************************************************************
 * Copyright (c) 2004, 2008 Maarten Meijer and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Maarten Meijer - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.dev.properties;

import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Displays {@link RepositoryQuery} properties in the Properties View.
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

	public RepositoryQueryPropertySource(RepositoryQuery adaptableObject) {
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
		RepositoryQuery query = (RepositoryQuery) container;
		if (SUMMARY.equals(id)) {
			return query.getSummary();
		} else if (KIND.equals(id)) {
			return query.getConnectorKind();
		} else if (REPOSITORY.equals(id)) {
			return query.getRepositoryUrl();
		} else if (QUERY.equals(id)) {
			return query.getUrl();
		} else if (STATUS.equals(id)) {
			return query.getStatus() == null ? NULL_MSG : query.getStatus().toString();
		} else if (LAST_READ.equals(id)) {
			return query.getLastSynchronizedTimeStamp() == null ? NULL_MSG : query.getLastSynchronizedTimeStamp()
					.toString();
		}
		return super.getPropertyValue(id);
	}
}
