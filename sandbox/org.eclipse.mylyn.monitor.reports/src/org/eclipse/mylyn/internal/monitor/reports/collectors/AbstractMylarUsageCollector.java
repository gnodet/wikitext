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

package org.eclipse.mylyn.internal.monitor.reports.collectors;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.internal.monitor.core.collection.CommandUsageCollector;
import org.eclipse.mylyn.internal.monitor.core.collection.DelegatingUsageCollector;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public abstract class AbstractMylarUsageCollector extends DelegatingUsageCollector {

	protected Set<Integer> userIds = new HashSet<Integer>();

	protected Set<Integer> mylarUserIds = new HashSet<Integer>();

	protected Set<Integer> mylarInactiveUserIds = new HashSet<Integer>();

	protected CommandUsageCollector commandUsageCollector = new CommandUsageCollector();

	public AbstractMylarUsageCollector() {
		super.getDelegates().add(commandUsageCollector);
	}

	/**
	 * Overriders must call super.consumeEvent(..)
	 */
	@Override
	public void consumeEvent(InteractionEvent event, int userId) {
		super.consumeEvent(event, userId);
		userIds.add(userId);
		if (MylarUsageDetector.isAMylarActivateCommand(event)) {
			mylarUserIds.add(userId);
			mylarInactiveUserIds.remove(userId);
		}
		if (MylarUsageDetector.isAMylarDeactivateCommand(event)) {
			mylarInactiveUserIds.add(userId);
		}
	}

}
