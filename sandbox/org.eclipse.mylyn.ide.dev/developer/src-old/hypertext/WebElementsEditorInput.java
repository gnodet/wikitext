/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.hypertext.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.provisional.tasklist.ITask;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Ken Sueda
 */
public class WebElementsEditorInput implements IEditorInput {

	private static final String LABEL_EDITOR = "Web Docs";

	// private IMylarContext context;

	public WebElementsEditorInput(ITask task) {
		// this.context = context;
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return LABEL_EDITOR;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return LABEL_EDITOR;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}
}
