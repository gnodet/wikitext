/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.xplanner.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.internal.xplanner.ui.editor.XPlannerTaskEditorExtraControls;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Event;

public class XPlannerTaskEditorTest extends TestCase {

	public void testExtraControlsVerifyTextFullText() throws Exception {
		XPlannerTaskEditorExtraControls extraControls = new XPlannerTaskEditorExtraControls(null);
		XPlannerTaskEditorExtraControls.HoursVerifyListener verifyListener = extraControls.new HoursVerifyListener();

		VerifyEvent verifyEvent = createVerifyEvent();
		verifyEvent.character = '\0';
		verifyEvent.text = "123";
		verifyListener.verifyText(verifyEvent);

		assertTrue(verifyEvent.doit);
	}

	private VerifyEvent createVerifyEvent() {
		Event event = new Event();
//		Dialog d = new MessageDialog(Display.getDefault().getActiveShell(), "", null, null, MessageDialog.NONE, null, 0);
		event.widget = WorkbenchUtil.getShell();
		return new VerifyEvent(event);
	}

	public void testExtraControlsVerifyTextOneCharacter() throws Exception {
		XPlannerTaskEditorExtraControls extraControls = new XPlannerTaskEditorExtraControls(null);
		XPlannerTaskEditorExtraControls.HoursVerifyListener verifyListener = extraControls.new HoursVerifyListener();

		VerifyEvent verifyEvent = createVerifyEvent();
		verifyEvent.character = '1';
		verifyListener.verifyText(verifyEvent);

		assertTrue(verifyEvent.doit);
	}

	public void testExtraControlsVerifyTextBadCharacter() throws Exception {
		XPlannerTaskEditorExtraControls extraControls = new XPlannerTaskEditorExtraControls(null);
		XPlannerTaskEditorExtraControls.HoursVerifyListener verifyListener = extraControls.new HoursVerifyListener();

		VerifyEvent verifyEvent = createVerifyEvent();
		verifyEvent.character = ';';
		verifyListener.verifyText(verifyEvent);

		assertFalse(verifyEvent.doit);

		verifyEvent.character = 'a';
		verifyListener.verifyText(verifyEvent);

		assertFalse(verifyEvent.doit);
	}
}
