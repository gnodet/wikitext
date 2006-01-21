package org.eclipse.mylar.monitor.reports.internal;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.internal.tasklist.ui.actions.TaskActivateAction;
import org.eclipse.mylar.internal.tasklist.ui.actions.TaskDeactivateAction;

public abstract class MylarUsageDetector {

	public static boolean isAMylarActivateCommand(InteractionEvent event) {
		if (event.getKind().equals(InteractionEvent.Kind.COMMAND)) {
			if (event.getOriginId().equals(TaskActivateAction.ID)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isAMylarDeactivateCommand(InteractionEvent event) {
		if (event.getKind().equals(InteractionEvent.Kind.COMMAND)) {
			if (event.getOriginId().equals(TaskDeactivateAction.ID)) {
				return true;
			}
		}
		return false;
	}
}
