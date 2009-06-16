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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.tests.ui.util.CompoundCondition;
import org.eclipse.mylyn.tests.ui.util.HasId;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.matchers.WidgetMatcherFactory;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * tests that drive the Connector Discovery UI
 *  
 * @author David Green
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class DiscoverySystemTest {

	private static final String KEY_CONNECTOR_ID = "connectorId";
	
	private static SWTWorkbenchBot bot;

	@BeforeClass
	public static void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
		try {
			bot.viewByTitle("Welcome").close();
		} catch (WidgetNotFoundException e) {
			// ignore
		}
	}
	
	@After
	public void after() {
		Shell mainShell = UIThreadRunnable.syncExec(new Result<Shell>() {
			public Shell run() {
				return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			}
		});
		SWTBotShell[] shells = bot.shells();
		for (SWTBotShell shell: shells) {
			if (shell.widget != mainShell) {
				shell.close();
			}
		}
	}

	@Test
	public void testSelectOneConnectorAndActivateP2Installer() {
		activateDiscoveryUi();
		bot.checkBoxWithId(KEY_CONNECTOR_ID, "org.eclipse.mylyn.trac_feature").click();
		
		bot.button("&Finish").click();
		
		// P2 takes over here.  Just verify that the P2 installer is activated and that the selected connector is
		// visible and checked.
		SWTBotShell shell = bot.shell("Install");
		shell.activate();
		
		final SWTBotTable table = bot.table();
		int rowCount = table.rowCount();
		assertTrue(rowCount > 0);
		int tracRow = -1;
		for (int x = 0;x<rowCount;++x) {
			String cellData = table.cell(x, 0);
			if (cellData.indexOf("Trac") != -1) {
				tracRow = x;
				break;
			}
		}
		assertTrue("Trac wasn't in the p2 ui",tracRow >= 0);
		
		bot.button("Cancel").click();
	}
	
	@Test
	public void testAllConnectorsEnabled() {
		activateDiscoveryUi();
		List<? extends Widget> widgets = allConnectorCheckboxes();
		assertFalse(widgets.isEmpty());
		List<String> disabledWidgets = new ArrayList<String>();
		for (final Widget widget: widgets) {
			boolean enabled = new SWTBotCheckBox((Button) widget).isEnabled();
			if (!enabled) {
				String data = UIThreadRunnable.syncExec(new Result<String>() {
					public String run() {
						return (String) ((Widget) widget).getData(KEY_CONNECTOR_ID);
					}
				});
				disabledWidgets.add(data);
			}
		}
		if (!disabledWidgets.isEmpty()) {
			fail(String.format("%s connectors were disabled: %s",disabledWidgets.size(),disabledWidgets));
		}
	}

	@SuppressWarnings("unchecked")
	private List<? extends Widget> allConnectorCheckboxes() {
		return bot.widgets(WidgetMatcherFactory.allOf(WidgetMatcherFactory.widgetOfType(Button.class),WidgetMatcherFactory.withStyle(SWT.CHECK, "SWT.CHECK"),new HasId(KEY_CONNECTOR_ID)));
	}


	private void activateDiscoveryUi() {
		showTaskRepositoriesView();
		
		bot.viewByTitle("Task Repositories").toolbarButton("Add Task Repository...").click();
		
		SWTBotShell shell = bot.shell("Add Task Repository");
		shell.activate();
		
		bot.button("Install More Connectors...").click();
		
		shell = bot.shell("Install Connectors");
		shell.activate();
		
		CompoundCondition completeOrErrorCondition = CompoundCondition.or(
				Conditions.waitForWidget(WidgetMatcherFactory.withId("discoveryComplete", "true"),shell.widget),
				Conditions.waitForShell(WidgetMatcherFactory.withText("Connector Discovery Error")));
		bot.waitUntil(completeOrErrorCondition,10000L);
		
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
