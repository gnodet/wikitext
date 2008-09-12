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

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author Maarten Meijer
 */
public class MylynPropertiesSourceAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IPropertySource.class && adaptableObject instanceof TaskRepository) {
			return new TaskRepositoryPropertiesSource((TaskRepository) adaptableObject);
		}
		if (adapterType == IPropertySource.class && adaptableObject instanceof ITask) {
			return new AbstractTaskPropertiesSource((AbstractTask) adaptableObject);
		}
		if (adapterType == IPropertySource.class && adaptableObject instanceof IRepositoryQuery) {
			return new RepositoryQueryPropertySource((RepositoryQuery) adaptableObject);
		}
		if (adapterType == IPropertySource.class && adaptableObject instanceof AbstractTaskCategory) {
			return new AbstractTaskCategoryPropertySource((AbstractTaskCategory) adaptableObject);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[] { IPropertySource.class };
	}

}
