/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Shawn Minto
 */
public class PersonProposalLabelProvider extends LabelProvider {

	public Image getImage(Object element) {
		if (element instanceof PersonContentProposal) {
			return ((PersonContentProposal)element).getImage();
		}
		return null;
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof PersonContentProposal) {
			return ((PersonContentProposal)element).getLabel();
		}
		return super.getText(element);
	}
}
