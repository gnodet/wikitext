/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.xplanner.ui.editor.XPlannerTaskEditorExtraControls;

public class XPlannerTaskEditorTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testFormatHoursRoundValueNoRound() {
		float inputValue = 1.0f;
		String output = XPlannerTaskEditorExtraControls.formatHours(inputValue, false);
		assertEquals(output, "1.0");
	}

	public void testFormatHoursRoundValueRound() {
		float inputValue = 1.0f;
		String output = XPlannerTaskEditorExtraControls.formatHours(inputValue, true);
		assertEquals(output, "1.0");
	}

	public void testFormatHoursNotRoundValueRoundHalf() {
		float inputValue = 1.5f;
		String output = XPlannerTaskEditorExtraControls.formatHours(inputValue, true);
		assertEquals(output, "1.5");
	}

	public void testFormatHoursNotRoundValueRound_1_4() {
		float inputValue = 1.4f;
		String output = XPlannerTaskEditorExtraControls.formatHours(inputValue, true);
		assertEquals(output, "1.5");
	}

	public void testFormatHoursNotRoundValueRound_1_2() {
		float inputValue = 1.2f;
		String output = XPlannerTaskEditorExtraControls.formatHours(inputValue, true);
		assertEquals(output, "1.0");
	}

	public void testFormatHoursNotRoundValueRound_1_6() {
		float inputValue = 1.6f;
		String output = XPlannerTaskEditorExtraControls.formatHours(inputValue, true);
		assertEquals(output, "1.5");
	}

	public void testFormatHoursNotRoundValueRound_1_8() {
		float inputValue = 1.8f;
		String output = XPlannerTaskEditorExtraControls.formatHours(inputValue, true);
		assertEquals(output, "2.0");
	}

	public void testFormatHoursNotRoundValueNotRound() {
		float inputValue = 1.3f;
		String output = XPlannerTaskEditorExtraControls.formatHours(inputValue, false);
		assertEquals(output, "1.3");
	}

	public void testFormatHoursNotRoundLongValueNotRound() {
		float inputValue = 1.345678f;
		String output = XPlannerTaskEditorExtraControls.formatHours(inputValue, false);
		assertEquals(output, "1.3");
	}

	public void testFormatSingleFractionHoursNotRoundLongValue() {
		float inputValue = 1.366666f;
		String output = XPlannerTaskEditorExtraControls.formatSingleFractionHours(inputValue);
		assertEquals(output, "1.4");
	}

	public void testFormatSingleFractionHoursRoundValue() {
		float inputValue = 1f;
		String output = XPlannerTaskEditorExtraControls.formatSingleFractionHours(inputValue);
		assertEquals(output, "1.0");
	}

}