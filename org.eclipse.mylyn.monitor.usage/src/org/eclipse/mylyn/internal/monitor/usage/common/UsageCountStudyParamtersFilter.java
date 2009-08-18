/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.usage.common;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.internal.monitor.core.collection.InteractionEventSummary;
import org.eclipse.mylyn.internal.monitor.usage.StudyParameters;

/**
 * @author Shawn Minto
 */
public class UsageCountStudyParamtersFilter extends ViewerFilter {

	private final Collection<String> filteredIds;

	public UsageCountStudyParamtersFilter(StudyParameters studyParameters) {
		Assert.isNotNull(studyParameters);
		filteredIds = studyParameters.getFilteredIds();
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof InteractionEventSummary) {
			return shouldIncludeEvent((InteractionEventSummary) element);
		}
		return true;
	}

	private boolean shouldIncludeEvent(InteractionEventSummary event) {
		if (filteredIds.size() == 0) {
			return true;
		}
		for (String filterId : filteredIds) {
			if (event.getName().startsWith(filterId)) {
				return true;
			}
		}
		return false;
	}

}
