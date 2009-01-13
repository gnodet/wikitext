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

import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Display various {@link AbstractTaskCategory} properties in the Eclipse Properties View.<br />
 * See <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=210639">Bug 210639</a> and <a
 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=208275">Bug 208275</a><br />
 * 
 * @author Maarten Meijer
 */
public class AbstractTaskCategoryPropertySource extends AbstractTaskContainerPropertySource implements IPropertySource {

	/**
	 * @param adaptableObject
	 *            to create source for
	 */
	public AbstractTaskCategoryPropertySource(AbstractTaskCategory adaptableObject) {
		super(adaptableObject);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		TextPropertyDescriptor summary = new TextPropertyDescriptor(SUMMARY, "Summary");
		summary.setCategory(description);
		IPropertyDescriptor[] specific = new IPropertyDescriptor[] { summary, };
		return super.appendSpecifics(specific, super.getPropertyDescriptors());
	}

	@Override
	public Object getPropertyValue(Object id) {
		AbstractTaskCategory category = (AbstractTaskCategory) container;
		if (SUMMARY.equals(id)) {
			return category.getSummary();
		}
		return super.getPropertyValue(id);
	}
}
