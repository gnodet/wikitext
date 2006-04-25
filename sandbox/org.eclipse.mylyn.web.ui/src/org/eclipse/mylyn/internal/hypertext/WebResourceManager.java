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
import java.util.List;
import java.util.Map;

/**
 * @author Mik Kersten
 */
public class WebResourceManager {

	private Map<String, List<String>> sitesMap = new HashMap<String, List<String>>();

	public Map<String, List<String>> getSitesMap() {
		return sitesMap;
	}

	
}
