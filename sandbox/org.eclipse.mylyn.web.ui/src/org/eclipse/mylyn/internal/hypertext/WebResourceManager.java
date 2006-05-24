/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.hypertext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Mik Kersten
 */
public class WebResourceManager {

	private Map<String, List<String>> sitesMap = new HashMap<String, List<String>>();

	private WebRoot webRoot = new WebRoot();
	
	private Set<IWebResourceListener> listeners = new HashSet<IWebResourceListener>();
	
	public WebResourceManager() {
		webRoot = new WebRoot();
		WebSite site1 = new WebSite("http://google.com", webRoot);
		WebSite site2 = new WebSite("http://nytimes.com", webRoot);
		webRoot.addSite(site1);
		webRoot.addSite(site2);
		
		site1.addPage(new WebPage("foo.bar", site1));
		for (IWebResourceListener listener : listeners) {
			listener.webSiteUpdated(site1);
		}
	}
	
	public Map<String, List<String>> getSitesMap() {
		return sitesMap;
	}

	public WebRoot getWebRoot() {
		return webRoot;
	}
	
	public void addListener(IWebResourceListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(IWebResourceListener listener) {
		listeners.remove(listener);
	}
}
