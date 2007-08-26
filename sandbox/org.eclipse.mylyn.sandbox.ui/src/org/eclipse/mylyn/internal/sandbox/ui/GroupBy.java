/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.sandbox.ui;

import java.util.Date;

import org.eclipse.mylyn.tasks.core.AbstractTask;

/**
 * @author Eugene Kuleshov
 */
public enum GroupBy {

	None() {
		public String getKey(AbstractTask task) {
			return null;
		}
	},

	Owner() {
		public String getKey(AbstractTask task) {
			return task.getOwner();
		}
	},

	Priority() {
		public String getKey(AbstractTask task) {
			return task.getPriority();
		}
	},
	Kind() {
		public String getKey(AbstractTask task) {
			return task.getTaskKind();
		}

	},
	Repository() {
		public String getKey(AbstractTask task) {
			return task.getRepositoryUrl();
		}
	},
	Due() {
		public String getKey(AbstractTask task) {
			Date date = task.getDueDate();
			return date == null ? null : date.toString();
		}
	},
	Sheduled() {
		public String getKey(AbstractTask task) {
			Date date = task.getScheduledForDate();
			return date == null ? null : date.toString();
		}
	};

	public abstract String getKey(AbstractTask task);

}
