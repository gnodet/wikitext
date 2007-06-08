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

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;

/**
 * Action used to copy selected text from a bug editor to the clipboard.
 */
public class RepositoryTaskEditorCopyAction extends Action {
	/** The editor to copy text selections from. */
	private MylarTaskEditor bugEditor;

	private ISelection selection;
	
	/**
	 * Creates a new <code>RepositoryTaskEditorCopyAction</code>.
	 * 
	 * @param editor
	 *            The editor that this action is copying text selections from.
	 */
	public RepositoryTaskEditorCopyAction(MylarTaskEditor editor) {
		bugEditor = editor;
		setText("AbstractRepositoryTaskEditor.copy.text");
	}

	@Override
	public void run() {
		bugEditor.copyToClipboard(selection);		
		//bugEditor.getCurrentText().copy();
	}
	
	public void selectionChanged(ISelection selection) {
		this.selection = selection;
		setEnabled(bugEditor.canCopy(selection));
	}

}
