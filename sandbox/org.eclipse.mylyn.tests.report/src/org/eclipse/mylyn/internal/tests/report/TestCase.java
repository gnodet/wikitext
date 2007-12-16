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
public class TestCase {

	private final String className;

	private final String testName;

	private final TestCaseResult result;

	public TestCase(String className, String testName, TestCaseResult result) {
		this.className = className;
		this.testName = testName;
		this.result = result;
	}

	public String getClassName() {
		return className;
	}

	public String getPackageName() {
		int i = className.lastIndexOf(".");
		return i != -1 ? className.substring(0, i) : "";
	}

	public TestCaseResult getResult() {
		return result;
	}

	public String getShortClassName() {
		int i = className.lastIndexOf(".");
		return i != -1 ? className.substring(i + 1) : className;
	}

	public String getTestName() {
		return testName;
	}

	@Override
	public String toString() {
		return getClass().getName() + " [className=" + className + ",testName=" + testName + ",result=" + result + "]";
	}

}
