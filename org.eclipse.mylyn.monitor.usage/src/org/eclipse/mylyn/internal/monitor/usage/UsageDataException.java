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

/**
 * @author Shawn Minto
 */
public class UsageDataException extends Exception {

	private static final long serialVersionUID = 3037413397893076406L;

	public UsageDataException(String message) {
		this(message, null);
	}

	public UsageDataException(String message, Exception cause) {
		super(message, cause);
	}

}