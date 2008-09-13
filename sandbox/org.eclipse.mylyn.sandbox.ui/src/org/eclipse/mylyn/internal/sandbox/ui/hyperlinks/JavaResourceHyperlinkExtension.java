/*******************************************************************************
 * Copyright (c) 2004, 2008 Jingwen Ou and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jingwen Ou - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.hyperlinks;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * @author Jingwen Ou
 */
public class JavaResourceHyperlinkExtension extends AbstractResourceHyperlinkExtension {

	private static final String JAVA_PREFIX = "java\\sclass\\s";

	@Override
	protected String getResourceExpressions() {
		return JAVA_PREFIX + DEFAULT_QUALIFIED_NAME;
	}

	@Override
	protected boolean isResourceExists(String resourceName) {
		return true;
	}

	@Override
	protected IHyperlink createHyperlinkInstance(IRegion region, String resourceName) {
		return new JavaResourceHyperlink(region, resourceName);
	}
}
