/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui;

import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.xplanner.wsdl.soap.domain.DomainData;
import org.xplanner.soap.IterationData;
import org.xplanner.soap.UserStoryData;

/**
 * @author Ravi Kumar 
 * @author Helen Bershadskaya 
 */
public class XPlannerTask extends AbstractTask {

	private String key = null;

	public enum Kind {
		ITERATION, USER_STORY, TASK;

		@Override
		public String toString() {
			switch (this) {
			case ITERATION:
				return "Iteration"; //$NON-NLS-1$
			case USER_STORY:
				return "User Story"; //$NON-NLS-1$
			case TASK:
				return "Task"; //$NON-NLS-1$
			default:
				return ""; //$NON-NLS-1$
			}
		}
		
		public static Kind fromString(String kindValue) {
			Kind kind = null;
		
			if (kindValue == null) {
				kind = null;
			}
			else if (kindValue.equals(TASK.toString())) {
				kind = TASK;
			}
			else if (kindValue.equals(USER_STORY.toString())) {
				kind = USER_STORY;
			}
			else if (kindValue.equals(ITERATION.toString())) {
				kind = ITERATION;
			}
			
			return kind;
		}

	}

	public void setKind(DomainData data) {
		String tempKind = Kind.TASK.toString();
		
		if (data instanceof IterationData) {
			tempKind = Kind.ITERATION.toString();
		}
		else if (data instanceof UserStoryData) {
			tempKind = Kind.USER_STORY.toString();
		}
		
		setTaskKind(tempKind);
	}
	
	/**
	 * The handle is also the task's XPlanner url
	 */
	public XPlannerTask(String repositoryUrl, String id, String label) {
		super(repositoryUrl, id, label);
	}

	public String getConnectorKind() {
		return XPlannerMylynUIPlugin.REPOSITORY_KIND;
	}

	public boolean isLocal() {
		return false;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
