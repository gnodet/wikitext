/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.core;

import java.io.Serializable;
import java.util.List;

import org.eclipse.mylar.internal.trac.model.TracComponent;
import org.eclipse.mylar.internal.trac.model.TracMilestone;
import org.eclipse.mylar.internal.trac.model.TracPriority;
import org.eclipse.mylar.internal.trac.model.TracSeverity;
import org.eclipse.mylar.internal.trac.model.TracTicketResolution;
import org.eclipse.mylar.internal.trac.model.TracTicketStatus;
import org.eclipse.mylar.internal.trac.model.TracTicketType;
import org.eclipse.mylar.internal.trac.model.TracVersion;

public class TracClientData implements Serializable {

	private static final long serialVersionUID = 6891961984245981675L;

	List<TracComponent> components;
	
	List<TracMilestone> milestones;
	
	List<TracPriority> priorities;
	
	List<TracSeverity> severities;
	
	List<TracTicketResolution> ticketResolutions;
	
	List<TracTicketStatus> ticketStatus;
	
	List<TracTicketType> ticketTypes;
	
	List<TracVersion> versions;

	long lastUpdate;

}
