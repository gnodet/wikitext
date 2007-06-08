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

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.internal.hypertext.MylarHypertextPlugin;
import org.eclipse.mylyn.internal.hypertext.ui.HypertextImages;
import org.eclipse.mylyn.internal.tasklist.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.ui.MylarImages;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class WebResourceLabelProvider extends LabelProvider implements ITableLabelProvider, IColorProvider {

	public String getColumnText(Object obj, int columnIndex) {
		String result = "";
		if (obj instanceof String) {
			switch (columnIndex) {
			case 0:
				result = (String) obj;
				break;
			default:
				break;
			}
		}
		return result;
	}

	public Image getColumnImage(Object obj, int columnIndex) {
		if (columnIndex == 0) {
			if (!MylarHypertextPlugin.getWebResourceManager().getSitesMap().containsKey(obj)) {
				return MylarImages.getImage(HypertextImages.WEB_PAGE);
			}
		}
		return null;
	}

	public Color getForeground(Object element) {
		return TaskListColorsAndFonts.COLOR_HYPERLINK;
	}

	public Color getBackground(Object element) {
		return null;
	}
}