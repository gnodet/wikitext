/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.tests;

import java.util.Locale;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.internal.xplanner.ui.XPlannerAttributeMapper;
import org.eclipse.mylyn.internal.xplanner.ui.XPlannerRepositoryUtils;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

public class XPlannerRepositoryUtilsTest extends TestCase {

	private static XPlannerClient client;

	private Locale defaultLocale;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (client == null) { // only create data once per run
			client = XPlannerTestUtils.getXPlannerClient();
			XPlannerTestUtils.clearTestData(client);
			XPlannerTestUtils.setUpTestData(client);
		}
		defaultLocale = Locale.getDefault();
		Locale.setDefault(Locale.US);
	}

	@Override
	protected void tearDown() throws Exception {
		Locale.setDefault(defaultLocale);
		super.tearDown();
	}

	public void testSetupTaskAttributes() {
		try {
			org.xplanner.soap.TaskData taskData = XPlannerTestUtils.findTestTask(client);
			ITask task = XPlannerTestUtils.getTestXPlannerTask(client);
			org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData = XPlannerTestUtils.getNewXPlannerTaskData(task);
			XPlannerRepositoryUtils.setupTaskAttributes(taskData, repositoryTaskData);

			assertEquals(taskData.getDescription(), XPlannerRepositoryUtils.getDescription(repositoryTaskData));
		} catch (Exception e) {
			fail("could not set up task attributes");
		}
	}

	public void testGetNewRepositoryTaskData() {
		try {
			UserStoryData userStoryData = XPlannerTestUtils.findTestUserStory(client);
			TaskRepository taskRepository = XPlannerTestUtils.getRepository();
			org.eclipse.mylyn.tasks.core.data.TaskData newRepositoryTaskData = XPlannerRepositoryUtils.getNewRepositoryTaskData(
					taskRepository, userStoryData);
			assertNotNull(newRepositoryTaskData);
			assertTrue(newRepositoryTaskData.isNew());
			assertEquals("" + userStoryData.getId(), XPlannerRepositoryUtils.getAttributeValue(newRepositoryTaskData,
					XPlannerAttributeMapper.ATTRIBUTE_USER_STORY_ID));
		} catch (Exception e) {
			fail("could not set up task attributes: " + e.getMessage());
		}
	}

	public void testSetupUserStoryAttributes() {
		try {
			UserStoryData userStory = XPlannerTestUtils.findTestUserStory(client);
			ITask task = XPlannerTestUtils.getTestXPlannerUserStoryTask(client);
			org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData = XPlannerTestUtils.getNewXPlannerTaskData(task);
			XPlannerRepositoryUtils.setupUserStoryAttributes(userStory, repositoryTaskData);

			assertEquals(userStory.getName(), XPlannerRepositoryUtils.getAttributeValue(repositoryTaskData,
					TaskAttribute.SUMMARY));
		} catch (Exception e) {
			fail("could not set up user story attributes");
		}
	}

	public void testIsCompleted() {
		try {
			ITask task = XPlannerTestUtils.getTestXPlannerTask(client);
			org.eclipse.mylyn.tasks.core.data.TaskData repositoryTaskData = XPlannerTestUtils.getNewXPlannerTaskData(task);
			assertFalse(XPlannerRepositoryUtils.isCompleted(repositoryTaskData));
		} catch (Exception e) {
			fail("Coule not check if task is completed");
		}
	}

	public void testValidateRepository() {
		TaskRepository repository = XPlannerTestUtils.getRepository();
		try {
			XPlannerRepositoryUtils.validateRepository(repository);
		} catch (CoreException e) {
			fail("Could not validate repository");
		}
	}

	public void testEnsureNewTaskDataValid() {
		TaskData taskData = new TaskData();
		XPlannerRepositoryUtils.ensureTaskDataValid(taskData);
		assertTrue(taskData.getName() != null && taskData.getName().length() > 0);
		assertTrue(taskData.getDispositionName() != null && taskData.getDispositionName().length() > 0);
	}

	/**
	 * Formatting tests
	 */
	public void testFormatHoursRoundValueNoRoundLocaleFrench() {
		Locale.setDefault(Locale.FRENCH);
		double inputValue = 1.0d;
		String output = XPlannerRepositoryUtils.formatHours(inputValue, false);
		assertEquals(output, "1,0");
	}

	public void testFormatHoursRoundValueNoRound() {
		double inputValue = 1.0d;
		String output = XPlannerRepositoryUtils.formatHours(inputValue, false);
		assertEquals(output, "1.0");
	}

	public void testFormatHoursRoundValueRound() {
		double inputValue = 1.0d;
		String output = XPlannerRepositoryUtils.formatHours(inputValue, true);
		assertEquals(output, "1.0");
	}

	public void testFormatHoursNotRoundValueRoundHalf() {
		double inputValue = 1.5d;
		String output = XPlannerRepositoryUtils.formatHours(inputValue, true);
		assertEquals(output, "1.5");
	}

	public void testFormatHoursNotRoundValueRound_1_4() {
		double inputValue = 1.4d;
		String output = XPlannerRepositoryUtils.formatHours(inputValue, true);
		assertEquals(output, "1.5");
	}

	public void testFormatHoursNotRoundValueRound_1_2() {
		double inputValue = 1.2d;
		String output = XPlannerRepositoryUtils.formatHours(inputValue, true);
		assertEquals(output, "1.0");
	}

	public void testFormatHoursNotRoundValueRound_1_6() {
		double inputValue = 1.6d;
		String output = XPlannerRepositoryUtils.formatHours(inputValue, true);
		assertEquals(output, "1.5");
	}

	public void testFormatHoursNotRoundValueRound_1_8() {
		double inputValue = 1.8d;
		String output = XPlannerRepositoryUtils.formatHours(inputValue, true);
		assertEquals(output, "2.0");
	}

	public void testFormatHoursNotRoundValueNotRound() {
		double inputValue = 1.3d;
		String output = XPlannerRepositoryUtils.formatHours(inputValue, false);
		assertEquals(output, "1.3");
	}

	public void testFormatHoursNotRoundLongValueNotRound() {
		double inputValue = 1.345678d;
		String output = XPlannerRepositoryUtils.formatHours(inputValue, false);
		assertEquals(output, "1.3");
	}

	public void testFormatSingleFractionHoursNotRoundLongValue() {
		double inputValue = 1.366666d;
		String output = XPlannerRepositoryUtils.formatSingleFractionHours(inputValue);
		assertEquals(output, "1.4");
	}

	public void testFormatSingleFractionHoursRoundValue() {
		double inputValue = 1d;
		String output = XPlannerRepositoryUtils.formatSingleFractionHours(inputValue);
		assertEquals(output, "1.0");
	}

}
