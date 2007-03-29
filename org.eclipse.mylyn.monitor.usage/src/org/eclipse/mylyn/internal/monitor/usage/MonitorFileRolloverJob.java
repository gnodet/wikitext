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
package org.eclipse.mylar.internal.monitor.usage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.internal.monitor.core.collection.IUsageCollector;
import org.eclipse.mylar.internal.monitor.usage.editors.UsageStatsEditorInput;
import org.eclipse.mylar.internal.monitor.usage.editors.UsageSummaryReportEditorPart;
import org.eclipse.mylar.monitor.core.InteractionEvent;
import org.eclipse.mylar.monitor.usage.ReportGenerator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Job that performs the rollover of the mylar monitor interaction history log
 * file. Overwrites destination if exists!
 * 
 * @author Meghan Allen (modelled after
 *         org.eclipse.mylar.internal.tasks.ui.util.TaskDataExportJob)
 * 
 */
public class MonitorFileRolloverJob extends Job {

	private static final String JOB_LABEL = "Mylar Monitor Log Rollover";

	// needs to be the same as NAME_DATA_DIR in
	// org.eclipse.mylar.tasks.ui.TasksUIPlugin
	private static final String NAME_DATA_DIR = ".mylar";

	private static final String DIRECTORY_MONITOR_BACKUP = "monitor";

	private static final String BACKUP_FILE_PREFIX = "monitor-history-backup-";

	private static final String ZIP_EXTENSION = ".zip";

	List<IUsageCollector> collectors;

	public MonitorFileRolloverJob(List<IUsageCollector> collectors) {
		super(JOB_LABEL);
		this.collectors = collectors;

	}

	@SuppressWarnings("deprecation")
	private String getYear(InteractionEvent event) {
		return "" + (event.getDate().getYear() + 1900);
	}

	private String getMonth(int month) {
		switch (month) {
		case 0:
			return "01";
		case 1:
			return "02";
		case 2:
			return "03";
		case 3:
			return "04";
		case 4:
			return "05";
		case 5:
			return "06";
		case 6:
			return "07";
		case 7:
			return "08";
		case 8:
			return "09";
		case 9:
			return "10";
		case 10:
			return "11";
		case 11:
			return "12";
		default:
			return "";

		}
	}

	@SuppressWarnings("deprecation")
	public IStatus run(final IProgressMonitor progressMonitor) {

		progressMonitor.beginTask(JOB_LABEL, IProgressMonitor.UNKNOWN);

		final File monitorFile = MylarUsageMonitorPlugin.getDefault().getMonitorLogFile();
		InteractionEventLogger logger = MylarUsageMonitorPlugin.getDefault().getInteractionLogger();

		logger.stopMonitoring();

		List<InteractionEvent> events = logger.getHistoryFromFile(monitorFile);
		progressMonitor.worked(1);

		int nowMonth = Calendar.getInstance().get(Calendar.MONTH);
		if (events.size() > 0 && events.get(0).getDate().getMonth() != nowMonth) {
			int currMonth = events.get(0).getDate().getMonth();

			String fileName = BACKUP_FILE_PREFIX + this.getMonth(currMonth) + "-" + this.getYear(events.get(0));

			File dir = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + File.separatorChar
					+ NAME_DATA_DIR + File.separatorChar + DIRECTORY_MONITOR_BACKUP);

			if (!dir.exists()) {
				dir.mkdir();
			}
			try {
				File currBackupZipFile = new File(dir, fileName + ZIP_EXTENSION);
				if (!currBackupZipFile.exists()) {
					currBackupZipFile.createNewFile();
				}
				ZipOutputStream zipFileStream;

				zipFileStream = new ZipOutputStream(new FileOutputStream(currBackupZipFile));
				zipFileStream.putNextEntry(new ZipEntry(MylarUsageMonitorPlugin.getDefault().getMonitorLogFile()
						.getName()));

				for (InteractionEvent event : events) {
					int monthOfCurrEvent = event.getDate().getMonth();
					if (monthOfCurrEvent == currMonth) {
						// put in curr zip
						String xml = logger.writeLegacyEvent(event);

						zipFileStream.write(xml.getBytes());

					} else if (monthOfCurrEvent != nowMonth) {
						// we are finished backing up currMonth, but now need to
						// start backing up monthOfCurrEvent
						progressMonitor.worked(1);
						zipFileStream.closeEntry();
						zipFileStream.close();

						fileName = BACKUP_FILE_PREFIX + this.getMonth(monthOfCurrEvent) + "-" + this.getYear(event);
						currBackupZipFile = new File(dir, fileName + ZIP_EXTENSION);
						if (!currBackupZipFile.exists()) {

							currBackupZipFile.createNewFile();

						}
						zipFileStream = new ZipOutputStream(new FileOutputStream(currBackupZipFile));
						zipFileStream.putNextEntry(new ZipEntry(MylarUsageMonitorPlugin.getDefault()
								.getMonitorLogFile().getName()));
						currMonth = monthOfCurrEvent;
						String xml = logger.writeLegacyEvent(event);
						zipFileStream.write(xml.getBytes());
					} else if (monthOfCurrEvent == nowMonth) {
						// if these events are from the current event, just put
						// them
						// back in the current log (first clear the log, since
						// we are
						// putting them all back)
						logger.clearInteractionHistory(false);
						logger.interactionObserved(event);
					}
				}
				zipFileStream.closeEntry();
				zipFileStream.close();
			} catch (FileNotFoundException e) {
				MylarStatusHandler.log("Mylar monitor log rollover failed - " + e.getMessage(), this);

			} catch (IOException e) {
				MylarStatusHandler.log("Mylar monitor log rollover failed - " + e.getMessage(), this);
			}

		}
		progressMonitor.worked(1);
		logger.startMonitoring();

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			public void run() {
				try {
					final IWorkbenchPage page = MylarUsageMonitorPlugin.getDefault().getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					if (page == null) {
						return;
					}

					ReportGenerator generator = new ReportGenerator(MylarUsageMonitorPlugin.getDefault()
							.getInteractionLogger(), collectors);
					progressMonitor.worked(1);
					List<File> files = new ArrayList<File>();

					files.add(monitorFile);
					final IEditorInput input = new UsageStatsEditorInput(files, generator);

					page.openEditor(input, UsageSummaryReportEditorPart.ID);
					progressMonitor.worked(1);
				} catch (PartInitException e1) {
					MylarStatusHandler.fail(e1, "Could not show usage summary", true);
				}

			}
		});
		progressMonitor.done();
		return new Status(Status.OK, MylarUsageMonitorPlugin.PLUGIN_ID, "Mylar Usage Summary");
	}

}
