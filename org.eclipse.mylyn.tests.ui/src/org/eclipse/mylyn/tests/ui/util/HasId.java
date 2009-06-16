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

import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.hamcrest.Description;

/**
 * a matcher for widgets that have an id with a specified key with any not-null value.
 * 
 * @author David Green
 */
public class HasId<T extends Widget> extends AbstractMatcher<T> {

	private String key;
	
	public HasId(String key) {
		super();
		this.key = key;
	}

	@Override
	protected boolean doMatch(final Object obj) {
		Object data = UIThreadRunnable.syncExec(new Result<Object>() {
			public Object run() {
				return ((Widget) obj).getData(key);
			}
		});
		return data != null;
	}

	public void describeTo(Description description) {
		description.appendText("with key (").appendText(key).appendText(")"); //$NON-NLS-1$ //$NON-NLS-2$	
	}


}
