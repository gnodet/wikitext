/*******************************************************************************
 * Copyright (c) 2004, 2009 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.xplanner.ui.editor;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

public interface XPlannerEditorAttributeProvider {
	public void xplannerAttributeChanged(TaskAttribute attribute);
}
