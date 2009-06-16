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
package org.eclipse.mylyn.tests.ui.util;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

/**
 * a condition that dispatches to multiple delegates.
 * 
 * @author David Green
 */
public class CompoundCondition implements ICondition {

	private ICondition[] conditions;
	private boolean and;
	private boolean[] results;
	
	public static CompoundCondition and(ICondition... conditions) {
		return new CompoundCondition(conditions, true);
	}
	public static CompoundCondition or(ICondition... conditions) {
		return new CompoundCondition(conditions, false);	
	}
	
	protected CompoundCondition(ICondition[] conditions, boolean and) {
		if (conditions == null || conditions.length == 0) {
			throw new IllegalArgumentException();
		}
		this.conditions = conditions;
		results = new boolean[conditions.length];
		this.and = and;
	}
	
	public String getFailureMessage() {
		String message = "";
		for (ICondition c: conditions) {
			if (message.length() == 0) {
				message += ", ";
				message += and?"AND ":"OR ";
			}
			message += c.getFailureMessage();
		}
		return message;
	}

	public void init(SWTBot bot) {
		for (ICondition c: conditions) {
			c.init(bot);
		}
	}

	public boolean test() throws Exception {
		int success = 0;
		for (int x = 0;x<conditions.length;++x) {
			ICondition c = conditions[x];
			if (c.test()) {
				results[x] = true;
				if (!and) {
					return true;
				}
				++success;
			} else {
				results[x] = false;
				if (and) {
					return false;
				}
			}
		}
		return success == conditions.length;
	}
	
	public boolean[] getResults() {
		return results;
	}
}
