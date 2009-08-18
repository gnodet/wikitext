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

package org.eclipse.mylyn.internal.monitor.usage.common;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.monitor.core.collection.InteractionEventSummary;
import org.eclipse.mylyn.internal.monitor.usage.ReportGenerator;

/**
 * Acts as proxy to the Taskscape model.
 * 
 * @author Mik Kersten
 */
public class UsageCountContentProvider implements IStructuredContentProvider {

	private ReportGenerator parser;

	public UsageCountContentProvider() {
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		if (newInput instanceof ReportGenerator) {
			this.parser = (ReportGenerator) newInput;
		}
	}

	public void dispose() {
	}

	// Return the individual stat summaries as an array of Objects
	public Object[] getElements(Object parent) {
		if (parser == null || parser.getLastParsedSummary() == null) {
			return new Object[] {};
		} else {
			List<InteractionEventSummary> stats = parser.getLastParsedSummary().getSingleSummaries();
			return stats.toArray();
		}
	}
}
