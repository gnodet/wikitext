/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.usage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IAction;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.monitor.ui.ActionExecutionMonitor;
import org.eclipse.mylyn.internal.monitor.ui.ActivityChangeMonitor;
import org.eclipse.mylyn.internal.monitor.ui.KeybindingCommandMonitor;
import org.eclipse.mylyn.internal.monitor.ui.MenuCommandMonitor;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.mylyn.internal.monitor.ui.PerspectiveChangeMonitor;
import org.eclipse.mylyn.internal.monitor.ui.PreferenceChangeMonitor;
import org.eclipse.mylyn.internal.monitor.ui.WindowChangeMonitor;
import org.eclipse.mylyn.monitor.core.IInteractionEventListener;
import org.eclipse.mylyn.monitor.ui.AbstractCommandMonitor;
import org.eclipse.mylyn.monitor.ui.IActionExecutionListener;
import org.eclipse.mylyn.monitor.ui.IMonitorLifecycleListener;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class UiUsageMonitorPlugin extends AbstractUIPlugin {

	private static final int MINUTE = 60 * 1000;

	public static final long HOUR = 60 * MINUTE;

	public static final long DAY = HOUR * 24;

	public static final long DELAY_ON_USER_REQUEST = 5 * DAY;

	public static final long DEFAULT_DELAY_DAYS_BETWEEN_TRANSMITS = 21;

	public static final long DEFAULT_DELAY_BETWEEN_TRANSMITS = DEFAULT_DELAY_DAYS_BETWEEN_TRANSMITS * DAY;

	private static final long START_CHECK_UPLOAD_JOB_DELAY = 2 * MINUTE;

	private static final long CHECK_UPLOAD_JOB_INTERVAL = 25 * HOUR;

	private static final String METADATA_MYLYN_DIR = "/.metadata/.mylyn"; //$NON-NLS-1$

	public static final String MONITOR_LOG_NAME = "monitor-log"; //$NON-NLS-1$

	public static final String ID_PLUGIN = "org.eclipse.mylyn.monitor.usage"; //$NON-NLS-1$

	private InteractionEventLogger interactionLogger;

	private PreferenceChangeMonitor preferenceMonitor;

	private PerspectiveChangeMonitor perspectiveMonitor;

	private ActivityChangeMonitor activityMonitor;

	private MenuCommandMonitor menuMonitor;

	private WindowChangeMonitor windowMonitor;

	private KeybindingCommandMonitor keybindingCommandMonitor;

	private static UiUsageMonitorPlugin plugin;

	private final List<IActionExecutionListener> actionExecutionListeners = new ArrayList<IActionExecutionListener>();

	private final List<AbstractCommandMonitor> commandMonitors = new ArrayList<AbstractCommandMonitor>();

	private static boolean performingUpload = false;

	private StudyParameters studyParameters = new StudyParameters();

	private final ListenerList lifecycleListeners = new ListenerList();

	private final UsageUploadManager uploadManager = new UsageUploadManager();

	private LogMoveUtility logMoveUtility;

	private Job checkForUploadJob;

	private boolean isSubmissionWizardOpen;

	public static class UiUsageMonitorStartup implements IStartup {

		public void earlyStartup() {
			// everything happens on normal start
		}
	}

	public UiUsageMonitorPlugin() {
		plugin = this;

	}

	private void initDefaultPrefs() {
		getPreferenceStore().setDefault(MonitorPreferenceConstants.PREF_MONITORING_OBFUSCATE, true);

		if (!getPreferenceStore().contains(MonitorPreferenceConstants.PREF_MONITORING_INITIALLY_ENABLED)) {
			getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_MONITORING_INITIALLY_ENABLED, true);
			getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_MONITORING_ENABLED, true);
		}

		if (!getPreferenceStore().contains(
				MonitorPreferenceConstants.PREF_MONITORING_ENABLE_SUBMISSION_INITITALLY_ENABLED)) {
			getPreferenceStore().setValue(
					MonitorPreferenceConstants.PREF_MONITORING_ENABLE_SUBMISSION_INITITALLY_ENABLED, true);
			getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_MONITORING_ENABLE_SUBMISSION, true);

		}

		getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_MONITORING_STARTED, false);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initDefaultPrefs();
		final IWorkbench workbench = PlatformUI.getWorkbench();
		Display display = workbench.getDisplay();

		if (!getPreferenceStore().contains(MonitorPreferenceConstants.PREF_NUM_USER_EVENTS_SINCE_LAST_UPLOAD)) {
			getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_NUM_USER_EVENTS_SINCE_LAST_UPLOAD,
					getPreferenceStore().getInt(MonitorPreferenceConstants.PREF_NUM_USER_EVENTS));
		}

		checkForUploadJob = new CheckForUploadJob(display);
		checkForUploadJob.setSystem(true);
		checkForUploadJob.schedule(START_CHECK_UPLOAD_JOB_DELAY);
		checkForUploadJob.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				if (event.getResult() != null && event.getResult().isOK()) {
					checkForUploadJob.schedule(CHECK_UPLOAD_JOB_INTERVAL);
				}
			}
		});

		display.asyncExec(new Runnable() {
			public void run() {
				try {
					// ------- moved from synch start
					UiUsageMonitorExtensionPointReader uiUsageMonitorExtensionPointReader = new UiUsageMonitorExtensionPointReader();
					studyParameters = uiUsageMonitorExtensionPointReader.getStudyParameters();

					if (studyParameters == null || studyParameters.isEmpty()) {

						initializeDefaultStudyParameters();

					}

					if (preferenceMonitor == null) {
						preferenceMonitor = new PreferenceChangeMonitor();
					}

					interactionLogger = new InteractionEventLogger(getMonitorLogFile());
					perspectiveMonitor = new PerspectiveChangeMonitor();
					activityMonitor = new ActivityChangeMonitor();
					windowMonitor = new WindowChangeMonitor();
					menuMonitor = new MenuCommandMonitor();
					keybindingCommandMonitor = new KeybindingCommandMonitor();

					// ------- moved from synch start

					if (getPreferenceStore().getBoolean(MonitorPreferenceConstants.PREF_MONITORING_ENABLED)) {
						startMonitoring();
					}

				} catch (Throwable t) {
					StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
							"Monitor failed to start", t)); //$NON-NLS-1$
				}
			}
		});
	}

	public void startMonitoring() {
		if (studyParameters == null || !studyParameters.isComplete()) {
			return;
		}
		if (getPreferenceStore().contains(MonitorPreferenceConstants.PREF_MONITORING_STARTED)) {
			return;
		}
		interactionLogger.startMonitoring();
		for (IInteractionEventListener listener : MonitorUiPlugin.getDefault().getInteractionListeners()) {
			listener.startMonitoring();
		}

		IWorkbench workbench = PlatformUI.getWorkbench();
		MonitorUi.addInteractionListener(interactionLogger);
		getCommandMonitors().add(keybindingCommandMonitor);

		getActionExecutionListeners().add(new ActionExecutionMonitor());

		if (logMoveUtility == null) {
			logMoveUtility = new LogMoveUtility();
		}
		logMoveUtility.start();

		MonitorUiPlugin.getDefault().addWindowPerspectiveListener(perspectiveMonitor);
		workbench.getActivitySupport().getActivityManager().addActivityManagerListener(activityMonitor);
		workbench.getDisplay().addFilter(SWT.Selection, menuMonitor);
		workbench.addWindowListener(windowMonitor);

		// installBrowserMonitor(workbench);

		for (Object listener : lifecycleListeners.getListeners()) {
			((IMonitorLifecycleListener) listener).startMonitoring();
		}

		getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_MONITORING_STARTED, true);
	}

	@SuppressWarnings("deprecation")
	public void addMonitoredPreferences(Preferences preferences) {
		if (preferenceMonitor == null) {
			preferenceMonitor = new PreferenceChangeMonitor();
		}
		preferences.addPropertyChangeListener(preferenceMonitor);
	}

	@SuppressWarnings("deprecation")
	public void removeMonitoredPreferences(Preferences preferences) {
		if (preferenceMonitor != null) {
			preferences.removePropertyChangeListener(preferenceMonitor);
		} else {
			StatusHandler.log(new Status(IStatus.WARNING, UiUsageMonitorPlugin.ID_PLUGIN,
					"UI Usage Monitor not started", new Exception())); //$NON-NLS-1$
		}
	}

	public boolean isObfuscationEnabled() {
		return UiUsageMonitorPlugin.getDefault().getPreferenceStore().getBoolean(
				MonitorPreferenceConstants.PREF_MONITORING_OBFUSCATE)
				|| (studyParameters != null && studyParameters.forceObfuscation());
	}

	public void stopMonitoring() {
		if (!getPreferenceStore().contains(MonitorPreferenceConstants.PREF_MONITORING_STARTED)) {
			return;
		}

		for (Object listener : lifecycleListeners.getListeners()) {
			((IMonitorLifecycleListener) listener).stopMonitoring();
		}

		for (IInteractionEventListener listener : MonitorUiPlugin.getDefault().getInteractionListeners()) {
			listener.stopMonitoring();
		}

		IWorkbench workbench = PlatformUI.getWorkbench();
		MonitorUi.removeInteractionListener(interactionLogger);

		getCommandMonitors().remove(keybindingCommandMonitor);
		getActionExecutionListeners().remove(new ActionExecutionMonitor());

		logMoveUtility.stop();
		// ContextCore.getPluginPreferences().removePropertyChangeListener(DATA_DIR_MOVE_LISTENER);

		MonitorUiPlugin.getDefault().removeWindowPerspectiveListener(perspectiveMonitor);
		workbench.getActivitySupport().getActivityManager().removeActivityManagerListener(activityMonitor);
		workbench.getDisplay().removeFilter(SWT.Selection, menuMonitor);
		workbench.removeWindowListener(windowMonitor);

		// uninstallBrowserMonitor(workbench);
		interactionLogger.stopMonitoring();

		getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_MONITORING_STARTED, false);
	}

	public void addMonitoringLifecycleListener(IMonitorLifecycleListener listener) {
		lifecycleListeners.add(listener);
		if (isMonitoringEnabled()) {
			listener.startMonitoring();
		}
	}

	public void removeMonitoringLifecycleListener(IMonitorLifecycleListener listener) {
		lifecycleListeners.remove(listener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		stopMonitoring();
		super.stop(context);
		plugin = null;
	}

	public void actionObserved(IAction action, String info) {
		for (IActionExecutionListener listener : actionExecutionListeners) {
			listener.actionObserved(action);
		}
	}

	public List<IActionExecutionListener> getActionExecutionListeners() {
		return actionExecutionListeners;
	}

	public List<AbstractCommandMonitor> getCommandMonitors() {
		return commandMonitors;
	}

	/**
	 * Parallels TasksUiPlugin.getDefaultDataDirectory()
	 */
	public File getMonitorLogFile() {
		File rootDir = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + METADATA_MYLYN_DIR);
		File file = new File(rootDir, MONITOR_LOG_NAME + InteractionContextManager.CONTEXT_FILE_EXTENSION_OLD);
		if (!file.exists() || !file.canWrite()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
						"Could not create monitor file", e)); //$NON-NLS-1$
			}
		}
		return file;
	}

	public void userCancelSubmitFeedback(Date currentTime, boolean delay) {
		Date lastTransmit = getLastTransmitDate();
		if (delay) {
			// wait X days
			lastTransmit.setTime(currentTime.getTime() + DELAY_ON_USER_REQUEST
					- studyParameters.getTransmitPromptPeriod());
			plugin.getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_PREVIOUS_TRANSMIT_DATE,
					lastTransmit.getTime());
		} else {
			// it was canceled so ask in 1 day
			long day = HOUR * 24;
			lastTransmit.setTime(currentTime.getTime() + day - studyParameters.getTransmitPromptPeriod());
			plugin.getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_PREVIOUS_TRANSMIT_DATE,
					lastTransmit.getTime());
		}
	}

	public Date getLastTransmitDate() {
		Date lastTransmit;
		if (UiUsageMonitorPlugin.getDefault().getPreferenceStore().contains(
				MonitorPreferenceConstants.PREF_PREVIOUS_TRANSMIT_DATE)) {

			lastTransmit = new Date(UiUsageMonitorPlugin.getDefault().getPreferenceStore().getLong(
					MonitorPreferenceConstants.PREF_PREVIOUS_TRANSMIT_DATE));
		} else {
			lastTransmit = new Date();
			UiUsageMonitorPlugin.getDefault().getPreferenceStore().setValue(
					MonitorPreferenceConstants.PREF_PREVIOUS_TRANSMIT_DATE, lastTransmit.getTime());
		}
		return lastTransmit;
	}

	/**
	 * Returns the shared instance.
	 */
	public static UiUsageMonitorPlugin getDefault() {
		return plugin;
	}

	public void incrementObservedEvents(int increment) {
		int numEvents = getPreferenceStore().getInt(MonitorPreferenceConstants.PREF_NUM_USER_EVENTS);
		numEvents += increment;
		getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_NUM_USER_EVENTS, numEvents);

		numEvents = getPreferenceStore().getInt(MonitorPreferenceConstants.PREF_NUM_USER_EVENTS_SINCE_LAST_UPLOAD);
		numEvents += increment;
		getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_NUM_USER_EVENTS_SINCE_LAST_UPLOAD, numEvents);
		savePluginPreferences();
	}

	public void resetEventsSinceUpload() {
		getPreferenceStore().setValue(MonitorPreferenceConstants.PREF_NUM_USER_EVENTS_SINCE_LAST_UPLOAD, 0);
		savePluginPreferences();
	}

	public static boolean isPerformingUpload() {
		return performingUpload;
	}

	public static void setPerformingUpload(boolean performingUpload) {
		UiUsageMonitorPlugin.performingUpload = performingUpload;
	}

	public InteractionEventLogger getInteractionLogger() {
		return interactionLogger;
	}

	public StudyParameters getStudyParameters() {
		return studyParameters;
	}

	public boolean isMonitoringEnabled() {
		return getPreferenceStore().getBoolean(MonitorPreferenceConstants.PREF_MONITORING_ENABLED);
	}

	private void initializeDefaultStudyParameters() {
		studyParameters = new StudyParameters();
		studyParameters.setVersion(""); //$NON-NLS-1$
		studyParameters.setUploadServletUrl("http://mylyn.eclipse.org/monitor/upload/MylarUsageUploadServlet"); //$NON-NLS-1$
		studyParameters.setUserIdServletUrl("http://mylyn.eclipse.org/monitor/upload/GetUserIDServlet"); //$NON-NLS-1$

		studyParameters.setTitle(Messages.UiUsageMonitorPlugin_Mylyn_Feedback);
		studyParameters.setDescription(Messages.UiUsageMonitorPlugin_Fill_Out_Form);
		studyParameters.setTransmitPromptPeriod(DEFAULT_DELAY_BETWEEN_TRANSMITS);
		studyParameters.setUseContactField("false"); //$NON-NLS-1$
		studyParameters.setAcceptedUrlList(""); //$NON-NLS-1$
		studyParameters.setFormsConsent("/doc/study-ethics.html"); //$NON-NLS-1$
		studyParameters.setUsagePageUrl("http://mylyn.eclipse.org/monitor/upload/usageSummary.html"); //$NON-NLS-1$
		studyParameters.setStudyName(Messages.UiUsageMonitorPlugin_Eclipse_Mylyn);
		studyParameters.addFilteredIdPattern("org.eclipse."); //$NON-NLS-1$
	}

	public UsageUploadManager getUploadManager() {
		return uploadManager;
	}

	public boolean isSubmissionWizardOpen() {
		return isSubmissionWizardOpen;
	}

	public void setSubmissionWizardOpen(boolean isSubmissionWizardOpen) {
		this.isSubmissionWizardOpen = isSubmissionWizardOpen;
	}
}
