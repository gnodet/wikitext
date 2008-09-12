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

package org.eclipse.mylyn.internal.sandbox.dev;

import org.eclipse.mylyn.internal.context.ui.actions.AbstractInterestManipulationAction;

/**
 * @author Mik Kersten
 */
public class InterestDecrementAction extends AbstractInterestManipulationAction {

	public InterestDecrementAction() {
		super.preserveUninteresting = true;
	}

	@Override
	protected boolean isIncrement() {
		return false;
	}
}
