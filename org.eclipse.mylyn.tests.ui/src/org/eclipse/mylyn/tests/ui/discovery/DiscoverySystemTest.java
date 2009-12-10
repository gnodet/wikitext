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
package org.eclipse.mylyn.tests.ui.discovery;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.tests.ui.util.CompoundCondition;
import org.eclipse.mylyn.tests.ui.util.HasId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBotTestCase;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.ui.PlatformUI;
import org.hamcrest.Matcher;
import org.junit.Test;

/**
 * tests that drive the Connector Discovery UI
 * 
 * @author David Green
 */
public class DiscoverySystemTest extends SWTBotTestCase {

	private static final String KEY_CONNECTOR_ID = "connectorId";

	private static SWTWorkbenchBot bot = new SWTWorkbenchBot();

	@Override
	public void setUp() throws Exception {
		try {
			bot.viewByTitle("Welcome").close();
		} catch (WidgetNotFoundException e) {
			// ignore
		}
		Shell mainShell = UIThreadRunnable.syncExec(new Result<Shell>() {
			public Shell run() {
				return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			}
		});
		SWTBotShell[] shells = bot.shells();
		for (SWTBotShell shell : shells) {
			if (shell.widget != mainShell) {
				shell.close();
			}
		}
	}

	@Override
	public void tearDown() {
	}

	@Test
	public void testSelectTwoConnectorsAndActivateP2Installer() {
		activateDiscoveryUi();
		bot.checkBoxWithId(KEY_CONNECTOR_ID, "com.itsolut.mantis_feature").click();
		bot.checkBoxWithId(KEY_CONNECTOR_ID, "com.foglyn").click();

		bot.button("&Finish").click();

		// P2 takes over here.  Just verify that the P2 installer is activated and that the selected connector is
		// visible and checked.
		bot.waitUntil(Conditions.shellIsActive("Install"), 90000L);

		final SWTBotTable table = bot.table();
		int rowCount = table.rowCount();
		assertTrue(rowCount > 0);
		int foglynRow = -1;
		int mantisRow = -1;
		for (int x = 0; x < rowCount; ++x) {
			String cellData = table.cell(x, 0);
			if (cellData.indexOf("Foglyn") != -1) {
				foglynRow = x;
			}
			if (cellData.indexOf("Mantis") != -1) {
				mantisRow = x;
			}
		}
		assertTrue("Foglyn wasn't in the p2 ui", foglynRow >= 0);
		assertTrue("Mantis wasn't in the p2 ui", mantisRow >= 0);

		bot.button("Cancel").click();
	}

	@Test
	public void testAllConnectorsEnabled() {
		activateDiscoveryUi();
		List<? extends Widget> widgets = allConnectorCheckboxes();
		assertFalse(widgets.isEmpty());
		List<String> disabledWidgets = new ArrayList<String>();
		for (final Widget widget : widgets) {
			boolean enabled = new SWTBotCheckBox((Button) widget).isEnabled();
			if (!enabled) {
				String data = UIThreadRunnable.syncExec(new Result<String>() {
					public String run() {
						return (String) (widget).getData(KEY_CONNECTOR_ID);
					}
				});
				disabledWidgets.add(data);
			}
		}
		assertEquals(String.format("Expected disablement of test contribution only, got %s", disabledWidgets), 1,
				disabledWidgets.size());
		assertEquals("org.eclipse.mylyn.discovery.tests.connectorDescriptor1", disabledWidgets.get(0));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<? extends Widget> allConnectorCheckboxes() {
		return bot.widgets(org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf(
				org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType(Button.class),
				org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withStyle(SWT.CHECK, "SWT.CHECK"),
				new HasId(KEY_CONNECTOR_ID)));
	}

	private void activateDiscoveryUi() {
		showTaskRepositoriesView();

		bot.viewByTitle("Task Repositories").toolbarButton("Add Task Repository...").click();

		SWTBotShell shell = bot.shell("Add Task Repository");
		shell.activate();

		bot.button("Install More Connectors...").click();

		shell = bot.shell("Install Connectors");
		shell.activate();

		Matcher<Shell> shellMatcher = org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withText("Connector Discovery Error");
		CompoundCondition completeOrErrorCondition = CompoundCondition.or(Conditions.waitForWidget(
				org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withId("discoveryComplete", "true"),
				shell.widget), Conditions.waitForShell(shellMatcher));
		bot.waitUntil(completeOrErrorCondition, 30000L);

		if (!completeOrErrorCondition.getResults()[0]) {
			fail("Connector discovery failed");
		}
	}

	private void showTaskRepositoriesView() {
		bot.menu("Window").menu("Show View").menu("Other...").click();

		SWTBotShell shell = bot.shell("Show View");
		shell.activate();

		bot.tree().expandNode("Tasks").select("Task Repositories");
		bot.button("OK").click();
	}

}
