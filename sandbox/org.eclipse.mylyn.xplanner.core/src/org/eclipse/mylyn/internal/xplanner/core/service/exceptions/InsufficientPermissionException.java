/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.xplanner.core.service.exceptions;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class InsufficientPermissionException extends RuntimeException {

	private static final long serialVersionUID = 7637802109314205626L;

	public InsufficientPermissionException(String message) {
		super(message);
	}
}
