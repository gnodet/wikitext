/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.report.ui;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.tasklist.ITask;

/**
 * @author Ken Sueda
 */
public class PlanningGameContentProvider implements IStructuredContentProvider {

	private List<ITask> tasks = null;
	
	public PlanningGameContentProvider(List<ITask> tasks) {
		this.tasks = tasks;
	}
	public Object[] getElements(Object inputElement) {
		return tasks.toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
