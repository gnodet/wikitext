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

package org.eclipse.mylyn.internal.examples.xml.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Steffen Pingel
 */
public class XmlConfiguration {

	private List<String> projects = Collections.emptyList();

	long updated = -1;

	public List<String> getProjects() {
		return projects;
	}

	void setProjects(List<String> projects) {
		this.projects = Collections.unmodifiableList(new ArrayList<String>(projects));
	}

}
