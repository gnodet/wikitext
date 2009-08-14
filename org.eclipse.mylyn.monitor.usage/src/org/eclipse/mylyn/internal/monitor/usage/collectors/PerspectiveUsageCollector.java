/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Leah Findlater - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.usage.collectors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.monitor.core.collection.IUsageCollector;
import org.eclipse.mylyn.internal.monitor.core.collection.PercentUsageComparator;
import org.eclipse.mylyn.internal.monitor.ui.PerspectiveChangeMonitor;
import org.eclipse.mylyn.internal.monitor.usage.UiUsageMonitorPlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.osgi.util.NLS;

/**
 * @author Mik Kersten
 * @author Leah Findlater TODO: put unclassified events in dummy perspective
 */
public class PerspectiveUsageCollector implements IUsageCollector {

	private final Map<String, Integer> perspectiveUsage = new HashMap<String, Integer>();

	private String currentPerspective = ""; //$NON-NLS-1$

	private int numUnassociatedEvents = 0;

	private int numEvents = 0;

	public void consumeEvent(InteractionEvent event, int userId) {
		numEvents++;
		if (event.getKind().equals(InteractionEvent.Kind.PREFERENCE)) {
			if (event.getDelta().equals(PerspectiveChangeMonitor.PERSPECTIVE_ACTIVATED)) {
				currentPerspective = event.getOriginId();
				if (!perspectiveUsage.containsKey(event.getOriginId())) {
					perspectiveUsage.put(event.getOriginId(), 1);
				}
			}
		}

		if (!perspectiveUsage.containsKey(currentPerspective)) {
			numUnassociatedEvents++;
			return;
		}

		perspectiveUsage.put(currentPerspective, perspectiveUsage.get(currentPerspective) + 1);
	}

	public List<String> getReport() {
		return getReport(true);
	}

	public String getReportTitle() {
		return Messages.PerspectiveUsageCollector_Perspective_Usage;
	}

	public void exportAsCSVFile(String directory) {
		String filename = directory + File.separator + "PerspectiveUsage.csv"; //$NON-NLS-1$

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)));

			// Write header
			writer.write("Perspective"); //$NON-NLS-1$
			writer.write(","); //$NON-NLS-1$
			writer.write("Events"); //$NON-NLS-1$
			writer.newLine();

			// Write Data
			for (String perspective : perspectiveUsage.keySet()) {
				writer.write(perspective);
				writer.write(","); //$NON-NLS-1$
				writer.write(new Integer(perspectiveUsage.get(perspective)).toString());
				writer.newLine();
			}

			writer.write("Unclassified"); //$NON-NLS-1$
			writer.write(","); //$NON-NLS-1$
			writer.write(numUnassociatedEvents);
			writer.newLine();

			writer.flush();
			writer.close();

		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, "Unable to write CSV file <" //$NON-NLS-1$
					+ filename + ">", e)); //$NON-NLS-1$
		}

	}

	public List<String> getPlainTextReport() {
		return getReport(false);
	}

	private List<String> getReport(boolean html) {
		List<String> summaries = new ArrayList<String>();
		summaries.add(NLS.bind(Messages.PerspectiveUsageCollector_Perspectives_Based_On_Total_User_Events,
				numUnassociatedEvents));
		summaries.add(" "); //$NON-NLS-1$

		List<String> perspectiveUsageList = new ArrayList<String>();
		for (String perspective : perspectiveUsage.keySet()) {
			float perspectiveUse = 100 * perspectiveUsage.get(perspective) / (numEvents);
			String formattedPerspectiveUse = ("" + perspectiveUse); //$NON-NLS-1$
			int indexOf2ndDecimal = formattedPerspectiveUse.indexOf('.') + 3;
			if (indexOf2ndDecimal <= formattedPerspectiveUse.length()) {
				formattedPerspectiveUse = formattedPerspectiveUse.substring(0, indexOf2ndDecimal);
			}
			String perspectiveName = perspective; // .substring(perspective.lastIndexOf(".")+1,
			// perspective.length());
			if (perspectiveName.contains("Perspective")) { //$NON-NLS-1$
				perspectiveName = perspectiveName.substring(0, perspectiveName.indexOf("Perspective")); //$NON-NLS-1$
			}
			perspectiveUsageList.add(formattedPerspectiveUse + "%: " + perspectiveName + " (" //$NON-NLS-1$//$NON-NLS-2$
					+ perspectiveUsage.get(perspective) + ")"); //$NON-NLS-1$
		}
		Collections.sort(perspectiveUsageList, new PercentUsageComparator());
		for (String perspectiveUsageSummary : perspectiveUsageList) {
			if (html) {
				summaries.add("<br>" + perspectiveUsageSummary); //$NON-NLS-1$
			} else {
				summaries.add(perspectiveUsageSummary);
			}
		}

		if (perspectiveUsage.size() % 2 != 0) {
			summaries.add(" "); //$NON-NLS-1$
		}
		return summaries;

	}
}
