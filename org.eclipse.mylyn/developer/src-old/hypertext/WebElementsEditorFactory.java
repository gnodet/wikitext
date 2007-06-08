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

import org.eclipse.mylyn.internal.tasklist.ui.ITaskEditorFactory;
import org.eclipse.mylyn.internal.tasklist.ui.editors.MylarTaskEditor;
import org.eclipse.mylyn.provisional.tasklist.ITask;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorPart;

/**
 * @author Mik Kersten
 */
public class WebElementsEditorFactory implements ITaskEditorFactory {

	public void notifyEditorActivationChange(IEditorPart editor) {
		if (editor instanceof WebElementsEditor) {
			((WebElementsEditor) editor).update();
		}
	}

	public EditorPart createEditor(MylarTaskEditor parentEditor) {
		return new WebElementsEditor();
	}

	public IEditorInput createEditorInput(ITask task) {
		return new WebElementsEditorInput(task);
	}

	public String getTitle() {
		return "Web Docs";
	}

	public boolean canCreateEditorFor(ITask task) {
		return true;
	}

	public boolean providesOutline() {
		return false;
	}
}
