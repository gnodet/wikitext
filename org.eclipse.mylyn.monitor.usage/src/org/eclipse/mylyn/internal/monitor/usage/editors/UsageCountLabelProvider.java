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

package org.eclipse.mylyn.internal.monitor.usage.editors;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.internal.monitor.core.collection.InteractionEventSummary;
import org.eclipse.swt.graphics.Image;

/**
 * Provides labels for TableViewer 'viewer'.
 * 
 * @author Leah Findlater and Mik Kersten
 */
class UsageCountLabelProvider extends LabelProvider implements ITableLabelProvider {

	public String getColumnText(Object obj, int index) {
		String labelText;
		switch (index) {
		case 0:
			labelText = ((InteractionEventSummary) obj).getType();
			break;
		case 1:
			labelText = ((InteractionEventSummary) obj).getName();
			break;
		case 2:
			labelText = String.valueOf(((InteractionEventSummary) obj).getUsageCount());
			break;
		// case 3:
		// labelText = String.valueOf(((InteractionEventSummary)
		// obj).getInterestContribution());
		// break;
		case 3:
			labelText = String.valueOf(((InteractionEventSummary) obj).getDelta());
			break;
		case 4:
			labelText = ((InteractionEventSummary) obj).getUserIds().toString();
			break;
		default:
			labelText = "";
		}
		return labelText;
	}

	public Image getColumnImage(Object obj, int index) {
		return null;
	}
}
