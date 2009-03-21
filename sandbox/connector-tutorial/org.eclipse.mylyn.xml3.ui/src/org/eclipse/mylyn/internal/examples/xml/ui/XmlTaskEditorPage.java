/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.examples.xml.ui;

import org.eclipse.mylyn.internal.examples.xml.core.XmlCorePlugin;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;

/**
 * @author Steffen Pingel
 */
public class XmlTaskEditorPage extends AbstractTaskEditorPage {

	public XmlTaskEditorPage(TaskEditor editor) {
		super(editor, "xmlTaskEditorPage", "XML", XmlCorePlugin.CONNECTOR_KIND);
	}

}
