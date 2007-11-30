/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.sandbox.dev.properties;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author Maarten Meijer
 */
public class TaskRepositoryPropertiesSource implements IPropertySource {
	private TaskRepository repository;

	public TaskRepositoryPropertiesSource(TaskRepository repository) {
		this.repository = repository;
	}

	public Object getEditableValue() {
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		Set<String> properties = repository.getProperties().keySet();
		IPropertyDescriptor[] result = new IPropertyDescriptor[properties.size()];
		Iterator<String> keys = properties.iterator();
		int i = 0;
		while (keys.hasNext()) {
			String key = keys.next();
			PropertyDescriptor descriptor = new TextPropertyDescriptor(key, key);
			descriptor.setCategory(repository.getClass().getName());
			result[i] = descriptor;
			++i;
		}
		return result;
	}

	public Object getPropertyValue(Object id) {
		return repository.getProperty((String) id);
	}

	public boolean isPropertySet(Object id) {
		return false;
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
	}

}
