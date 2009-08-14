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
package org.eclipse.mylyn.internal.monitor.usage;

/**
 * NOTE: this needs to be a separate class in order to avoid loading ..mylyn.context.core on eager startup
 */
public class LogMoveUtility {

	// TODO resurrect this?

//		private final IContextStoreListener DATA_DIR_MOVE_LISTENER = new IContextStoreListener() {
//
//			public void contextStoreMoved(File file) {
//				if (!isPerformingUpload()) {
//					for (IInteractionEventListener listener : MonitorUiPlugin.getDefault().getInteractionListeners()) {
//						listener.stopMonitoring();
//					}
//					interactionLogger.moveOutputFile(getMonitorLogFile().getAbsolutePath());
//					for (IInteractionEventListener listener : MonitorUiPlugin.getDefault().getInteractionListeners()) {
//						listener.startMonitoring();
//					}
//				}
//			}
//		};

	void start() {
//			ContextCore.getContextStore().addListener(DATA_DIR_MOVE_LISTENER);
	}

	void stop() {
//			ContextCore.getContextStore().removeListener(DATA_DIR_MOVE_LISTENER);
	}
}