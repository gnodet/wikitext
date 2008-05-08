/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.sandbox.ui;

import java.util.Date;

import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Eugene Kuleshov
 */
public enum GroupBy {

	None() {
		@Override
		public String getKey(ITask task) {
			return null;
		}
	},

	Owner() {
		@Override
		public String getKey(ITask task) {
			return task.getOwner();
		}
	},

	Priority() {
		@Override
		public String getKey(ITask task) {
			return task.getPriority();
		}
	},
	Kind() {
		@Override
		public String getKey(ITask task) {
			return task.getTaskKind();
		}

	},
	Repository() {
		@Override
		public String getKey(ITask task) {
			return task.getRepositoryUrl();
		}
	},
	Due() {
		@Override
		public String getKey(ITask task) {
			Date date = task.getDueDate();
			return date == null ? null : date.toString();
		}
	},
	Sheduled() {
		@Override
		public String getKey(ITask task) {
			DateRange date = ((AbstractTask) task).getScheduledForDate();
			return date == null ? null : date.toString();
		}
	};

	public abstract String getKey(ITask task);

}
