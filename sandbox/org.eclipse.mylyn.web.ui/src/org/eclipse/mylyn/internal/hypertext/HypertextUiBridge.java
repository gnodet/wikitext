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

package org.eclipse.mylar.internal.hypertext;

import java.util.List;

import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.ui.IMylarUiBridge;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 */
public class HypertextUiBridge implements IMylarUiBridge {

	public void open(IMylarElement node) {
		// TODO Auto-generated method stub
	}

	public void close(IMylarElement node) {
		// TODO Auto-generated method stub
	}

	public boolean acceptsEditor(IEditorPart editorPart) {
		return false;
	}

	public List<TreeViewer> getContentOutlineViewers(IEditorPart editorPart) {
		return null;
	}

	public Object getObjectForTextSelection(TextSelection selection, IEditorPart editor) {
		return null;
	}

	public void restoreEditor(IMylarElement document) {
		// TODO Auto-generated method stub

	}

	public void setContextCapturePaused(boolean paused) {
		// TODO Auto-generated method stub

	}
}
