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

package org.eclipse.mylyn.internal.sandbox.ui;

import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTaskListContentProvider;
import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTaskListPresentation;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;

/**
 * @author Rob Elves
 */
public class IncomingPresentation extends AbstractTaskListPresentation {

	private static final String PRESENTATION_ID = "org.eclipse.mylyn.tasks.ui.incoming";

	public IncomingPresentation() {
		super(PRESENTATION_ID);
	}

	@Override
	protected AbstractTaskListContentProvider createContentProvider(TaskListView taskListView) {
		return new IncomingTaskListContentProvider(taskListView);
	}

	@Override
	public String getId() {
		return PRESENTATION_ID;
	}
}
