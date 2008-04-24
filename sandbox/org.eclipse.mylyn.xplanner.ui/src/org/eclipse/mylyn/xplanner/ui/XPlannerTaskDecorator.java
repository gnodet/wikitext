/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerTaskDecorator {

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof XPlannerTask) {
			XPlannerTask task = (XPlannerTask) element;
			if (XPlannerTask.Kind.ITERATION.toString().equals(task.getTaskKind())) {
				decoration.addOverlay(XPlannerImages.OVERLAY_ITERATION);
			} else if (XPlannerTask.Kind.USER_STORY.toString().equals(task.getTaskKind())) {
				decoration.addOverlay(XPlannerImages.OVERLAY_USER_STORY);
			} else if (XPlannerTask.Kind.TASK.toString().equals(task.getTaskKind())) {
				decoration.addOverlay(XPlannerImages.OVERLAY_TASK);
			}
		}
	}

	public void addListener(ILabelProviderListener listener) {
		// ignore
	}

	public void dispose() {
		// ignore
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// ignore
	}
}
