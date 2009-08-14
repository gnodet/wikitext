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

package org.eclipse.mylyn.internal.monitor.usage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.monitor.usage.AbstractStudyBackgroundPage;
import org.eclipse.mylyn.monitor.usage.AbstractStudyQuestionnairePage;

class UiUsageMonitorExtensionPointReader {

	public static final String EXTENSION_ID_STUDY = "org.eclipse.mylyn.monitor.usage.study";

	public static final String ELEMENT_SCRIPTS = "scripts";

	public static final String ELEMENT_SCRIPTS_VERSION = "version";

	public static final String ELEMENT_SCRIPTS_SERVER_URL = "url";

	public static final String ELEMENT_SCRIPTS_UPLOAD_USAGE = "upload";

	public static final String ELEMENT_SCRIPTS_GET_USER_ID = "userId";

	public static final String ELEMENT_SCRIPTS_FORCE_OBFUSCATION = "forceObfuscation";

	public static final String ELEMENT_UI = "ui";

	public static final String ELEMENT_UI_TITLE = "title";

	public static final String ELEMENT_UI_STUDY_NAME = "studyName";

	public static final String ELEMENT_UI_DESCRIPTION = "description";

	public static final String ELEMENT_UI_UPLOAD_PROMPT = "daysBetweenUpload";

	public static final String ELEMENT_UI_QUESTIONNAIRE_PAGE = "questionnairePage";

	public static final String ELEMENT_UI_BACKGROUND_PAGE = "backgroundPage";

	public static final String ELEMENT_UI_CONSENT_FORM = "consentForm";

	public static final String ELEMENT_UI_CONTACT_CONSENT_FIELD = "useContactField";

	public static final String ELEMENT_UI_URL_USAGE_PAGE = "usagePageUrl";

	public static final String ELEMENT_MONITORS = "monitors";

	public static final String ELEMENT_MONITORS_BROWSER_URL = "browserUrlFilter";

	public static final String ELEMENT_FILTER = "filter";

	public static final String ELEMENT_FILTER_ID_PREFIX = "idPrefix";

	private boolean extensionsRead = false;

	private StudyParameters studyParameters;

	// private MonitorUsageExtensionPointReader thisReader = new
	// MonitorUsageExtensionPointReader();

	public synchronized StudyParameters getStudyParameters() {
		if (!extensionsRead) {
			initExtensions();
		}
		return studyParameters;
	}

	private void initExtensions() {
		try {
			if (!extensionsRead) {
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_ID_STUDY);
				if (extensionPoint != null) {
					IExtension[] extensions = extensionPoint.getExtensions();
					for (IExtension extension : extensions) {
						studyParameters = new StudyParameters();
						IConfigurationElement[] elements = extension.getConfigurationElements();
						for (IConfigurationElement element : elements) {
							if (element.getName().compareTo(ELEMENT_SCRIPTS) == 0) {
								readScripts(element);
							} else if (element.getName().compareTo(ELEMENT_UI) == 0) {
								readForms(element);
							} else if (element.getName().compareTo(ELEMENT_MONITORS) == 0) {
								readMonitors(element);
							} else if (element.getName().compareTo(ELEMENT_FILTER) == 0) {
								readFilter(element);
							}
						}
						studyParameters.setCustomizingPlugin(extension.getContributor().getName());

						//TODO make this support multiple studies properly
						// currently we only read the first one
						break;
					}
					extensionsRead = true;
				}
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
					"Could not read monitor extension", t));
		}
	}

	private void readFilter(IConfigurationElement element) {
		studyParameters.addFilteredIdPattern(element.getAttribute(ELEMENT_FILTER_ID_PREFIX));

	}

	private void readScripts(IConfigurationElement element) {
		studyParameters.setVersion(element.getAttribute(ELEMENT_SCRIPTS_VERSION));
		String serverUrl = element.getAttribute(ELEMENT_SCRIPTS_SERVER_URL);
		String userIdScript = element.getAttribute(ELEMENT_SCRIPTS_GET_USER_ID);
		String usageUploadScript = element.getAttribute(ELEMENT_SCRIPTS_UPLOAD_USAGE);
		String forceObfuscationString = element.getAttribute(ELEMENT_SCRIPTS_FORCE_OBFUSCATION);

		studyParameters.setUploadServletUrl(serverUrl + usageUploadScript);
		studyParameters.setUserIdServletUrl(serverUrl + userIdScript);
		studyParameters.setForceObfuscation(Boolean.parseBoolean(forceObfuscationString));

	}

	private void readForms(IConfigurationElement element) throws CoreException {
		studyParameters.setUsagePageUrl(element.getAttribute(ELEMENT_UI_URL_USAGE_PAGE));
		studyParameters.setStudyName(element.getAttribute(ELEMENT_UI_STUDY_NAME));
		studyParameters.setTitle(element.getAttribute(ELEMENT_UI_TITLE));
		studyParameters.setDescription(element.getAttribute(ELEMENT_UI_DESCRIPTION));
		if (element.getAttribute(ELEMENT_UI_UPLOAD_PROMPT) != null) {
			Integer uploadInt = new Integer(element.getAttribute(ELEMENT_UI_UPLOAD_PROMPT));
			studyParameters.setTransmitPromptPeriod(UiUsageMonitorPlugin.HOUR * 24 * uploadInt);
		}
		studyParameters.setUseContactField(element.getAttribute(ELEMENT_UI_CONTACT_CONSENT_FIELD));

		try {
			if (element.getAttribute(ELEMENT_UI_QUESTIONNAIRE_PAGE) != null) {
				Object questionnaireObject = element.createExecutableExtension(ELEMENT_UI_QUESTIONNAIRE_PAGE);
				if (questionnaireObject instanceof AbstractStudyQuestionnairePage) {
					AbstractStudyQuestionnairePage page = (AbstractStudyQuestionnairePage) questionnaireObject;
					studyParameters.setQuestionnairePage(page);
				}
			} else {
			}
		} catch (Throwable e) {
			StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, "Could not load questionaire",
					e));
		}

		try {
			if (element.getAttribute(ELEMENT_UI_BACKGROUND_PAGE) != null) {
				Object backgroundObject = element.createExecutableExtension(ELEMENT_UI_BACKGROUND_PAGE);
				if (backgroundObject instanceof AbstractStudyBackgroundPage) {
					AbstractStudyBackgroundPage page = (AbstractStudyBackgroundPage) backgroundObject;
					studyParameters.setBackgroundPage(page);
				}
			} else {
			}
		} catch (Throwable e) {
			StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
					"Could not load background page", e));
		}

		studyParameters.setFormsConsent("/" + element.getAttribute(ELEMENT_UI_CONSENT_FORM));

	}

	private void readMonitors(IConfigurationElement element) throws CoreException {
		// TODO: This should parse a list of filters but right now it takes
		// the
		// entire string as a single filter.
		// ArrayList<String> urlList = new ArrayList<String>();
		String urlList = element.getAttribute(ELEMENT_MONITORS_BROWSER_URL);
		studyParameters.setAcceptedUrlList(urlList);
	}
}