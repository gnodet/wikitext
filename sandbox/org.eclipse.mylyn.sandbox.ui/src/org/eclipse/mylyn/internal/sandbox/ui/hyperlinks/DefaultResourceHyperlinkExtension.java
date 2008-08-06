/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.hyperlinks;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * @author Jingwen Ou
 */
public class DefaultResourceHyperlinkExtension extends AbstractResourceHyperlinkExtension {

	private static final String DEFAULT_PREFIX = "file\\s";

	private static final String DEFAULT_RESOURCE_QUALIFIED_NAME = "(((\\w|/)(\\w|\\.|/)*\\w)|\\w)";

	@Override
	protected String getResourceExpressions() {
		return DEFAULT_PREFIX + DEFAULT_RESOURCE_QUALIFIED_NAME;
	}

	@Override
	public boolean isResourceExists(String resourceName) {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(resourceName), true);
		if (resource != null) {
			return true;
		}
		return false;
	}

	@Override
	protected IHyperlink createHyperlinkInstance(IRegion region, String resourceName) {
		return new DefaultResourceHyperlink(region, resourceName);
	}
}
