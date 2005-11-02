/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.monitor.reports;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.monitor.reports.collectors.CommandUsageCollector;
import org.eclipse.mylar.tasklist.ui.actions.TaskActivateAction;
import org.eclipse.mylar.tasklist.ui.actions.TaskDeactivateAction;

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
	public void consumeEvent(InteractionEvent event, int userId, String phase) {
		super.consumeEvent(event, userId, phase);
		userIds.add(userId);
		if (event.getKind().equals(InteractionEvent.Kind.COMMAND)) {
			if (event.getOriginId().equals(TaskActivateAction.ID)) {
				mylarUserIds.add(userId);
				mylarInactiveUserIds.remove(userId);
			} else if (event.getOriginId().equals(TaskDeactivateAction.ID)) {
				mylarInactiveUserIds.add(userId);
			}
		}
	}
}
