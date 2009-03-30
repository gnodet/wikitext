/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.tests;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.xplanner.ui.XPlannerAttributeMapper;
import org.eclipse.mylyn.tasks.core.AbstractTaskListMigrator;

/**
 * @author Helen Bershadskaya
 */
public class XPlannerAttributeMapperTest extends TestCase {

	private Locale defaultLocale;

	private TimeZone defaultTimeZone;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		defaultLocale = Locale.getDefault();
		defaultTimeZone = TimeZone.getDefault();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Locale.setDefault(Locale.US);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TimeZone.setDefault(defaultTimeZone);
		Locale.setDefault(defaultLocale);
	}

	public void testGetDateForAttributeTypeXPlannerAttribute() {
		Date dateBad = XPlannerAttributeMapper.getDateForAttributeType(
				XPlannerAttributeMapper.ATTRIBUTE_ACT_HOURS_NAME, "2008-07-28 16:01:36");

		assertNull(dateBad);

		Date dateGood = XPlannerAttributeMapper.getDateForAttributeType(
				XPlannerAttributeMapper.ATTRIBUTE_ACT_HOURS_NAME, "Aug 28, 2007");

		assertNotNull(dateGood);
		assertEquals(dateGood.toString(), "Tue Aug 28 00:00:00 UTC 2007");
	}

	public void testGetDateForAttributeTypeMylynAttribute() {
		Date dateBad = XPlannerAttributeMapper.getDateForAttributeType(AbstractTaskListMigrator.KEY_LAST_MOD_DATE,
				"Aug 28, 2007");
		assertNull(dateBad);

		Date dateGood = XPlannerAttributeMapper.getDateForAttributeType(AbstractTaskListMigrator.KEY_LAST_MOD_DATE,
				"2008-07-28 16:01:36");

		assertNotNull(dateGood);
		assertEquals(dateGood.toString(), "Mon Jul 28 16:01:36 UTC 2008");
	}
}
