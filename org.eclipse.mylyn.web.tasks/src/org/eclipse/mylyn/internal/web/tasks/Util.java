/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.tasks;

/**
 * Utility methods
 * 
 * @author Eugene Kuleshov
 */
public class Util {

	public static boolean isPresent(String value) {
		return value != null && value.length() > 0;
	}

	public static String nvl(String value) {
		return value == null ? "" : value;
	}

}