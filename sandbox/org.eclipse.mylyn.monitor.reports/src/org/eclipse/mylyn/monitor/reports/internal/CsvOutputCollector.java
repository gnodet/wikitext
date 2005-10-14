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

package org.eclipse.mylar.monitor.reports.internal;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.monitor.reports.IUsageStatsCollector;

/**
 * @author Mik Kersten and Leah Findlater
 */
public class CsvOutputCollector implements IUsageStatsCollector {

	public CsvOutputCollector() {
		
	}
	
	public String getReportTitle() {
		return "CSV Output Collector";
	}

	public void consumeEvent(InteractionEvent event, int userId, String phase) {
		// TODO: write to CSV
	}

	public List<String> getReport() {
		return Collections.emptyList();
	}

	public void generateCsvFile(File file) {
	}

}
