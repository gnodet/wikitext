/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.textile.ui.editors;

import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import org.eclipse.mylyn.wikitext.ui.editor.MarkupTaskEditorExtension;

/**
 *
 *
 * @author David Green
 */
public class TextileTaskEditorExtension extends MarkupTaskEditorExtension {

	public TextileTaskEditorExtension() {
		setMarkupLanguage(new TextileLanguage());
	}

}
