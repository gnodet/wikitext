/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.usage.editors;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.monitor.core.collection.InteractionEventSummary;
import org.eclipse.mylyn.monitor.usage.ReportGenerator;

/**
 * Acts as proxy to the Taskscape model.
 * 
 * @author Mik Kersten
 */
class UsageCountContentProvider implements IStructuredContentProvider {

	private ReportGenerator parser;

	public UsageCountContentProvider(ReportGenerator parser) {
		this.parser = parser;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		/*
		 * If we're doing real-time updating we'll need to check when the model
		 * changes.
		 */
		// if (newInput != null)
		// ((MylarModel) newInput).addChangeListener(this);
		// if (oldInput != null)
		// ((MylarModel) oldInput).removeChangeListener(this);
	}

	public void dispose() {
		// model.removeChangeListener(this);
	}

	// Return the individual stat summaries as an array of Objects

	public Object[] getElements(Object parent) {
		if (parser.getLastParsedSummary() == null) {
			return new Object[] {};
		} else {
			List<InteractionEventSummary> stats = parser.getLastParsedSummary().getSingleSummaries();
			return stats.toArray();
		}
	}
}
