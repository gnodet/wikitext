/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.ui;

/**
 * @author Mik Kersten
 */
public interface IWebResourceListener {

	public void webSiteUpdated(WebSite site);

	public void webContextUpdated();

	public void webPageUpdated(WebPage page);

}
