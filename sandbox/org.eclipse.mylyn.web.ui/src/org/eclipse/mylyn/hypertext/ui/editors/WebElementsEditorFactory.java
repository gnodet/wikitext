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

package org.eclipse.mylar.hypertext.ui.editors;

import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.tasklist.ui.IContextEditorFactory;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorPart;

/**
 * @author Mik Kersten
 */
public class WebElementsEditorFactory implements IContextEditorFactory {

	public void notifyEditorActivationChange(IEditorPart editor) {
		if (editor instanceof WebElementsEditor) {
			((WebElementsEditor)editor).update();
		}
	}

	public EditorPart createEditor() {
		return new WebElementsEditor();
	}

	public IEditorInput createEditorInput(IMylarContext context) {
		return new WebElementsEditorInput(context);
	}

	public String getTitle() {
		return "Web Docs";
	}
}
