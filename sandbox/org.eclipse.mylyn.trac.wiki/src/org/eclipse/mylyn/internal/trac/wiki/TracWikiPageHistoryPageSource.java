/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Xiaoyang Guan - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.wiki;

import org.eclipse.mylyn.internal.trac.wiki.editor.TracWikiPageEditorInput;
import org.eclipse.team.ui.history.HistoryPageSource;
import org.eclipse.ui.part.Page;

/**
 * @author Xiaoyang Guan
 */
public class TracWikiPageHistoryPageSource extends HistoryPageSource {

	public boolean canShowHistoryFor(Object object) {
		if (object instanceof TracWikiPageEditorInput) {
			return true;
		}
		return false;
	}

	public Page createPage(Object object) {
		return new TracWikiPageHistoryPage();
	}

}
