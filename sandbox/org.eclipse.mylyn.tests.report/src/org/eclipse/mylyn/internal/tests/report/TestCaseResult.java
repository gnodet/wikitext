/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tests.report;

/**
 * @author Steffen Pingel
 */
public class TestCaseResult {

	public enum TestCaseResultType {
		ERROR, FAILURE
	};

	private final TestCaseResultType resultType;

	private final String type;

	private final String message;

	private final String stackTrace;

	public TestCaseResult(TestCaseResultType resultType, String type, String message, String stackTrace) {
		this.resultType = resultType;
		this.type = type;
		this.message = message;
		this.stackTrace = stackTrace;
	}

	public String getMessage() {
		return message;
	}

	public TestCaseResultType getResultType() {
		return resultType;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return resultType.name();
	}
}
