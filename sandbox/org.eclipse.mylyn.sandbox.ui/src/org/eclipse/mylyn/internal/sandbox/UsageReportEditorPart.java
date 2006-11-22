/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.sandbox;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.internal.monitor.reports.ui.views.UsageEditorPart;
import org.eclipse.mylar.internal.monitor.usage.ui.FileDisplayDialog;
import org.eclipse.mylar.monitor.usage.MylarUsageMonitorPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Meghan Allen
 */
public class UsageReportEditorPart extends UsageEditorPart {

	private static final String DATE_FORMAT_STRING = "h:mm a z, MMMMM d, yyyy";

	@Override
	protected void createActionSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		section.setText(editorInput.getName() + " at " + new SimpleDateFormat(DATE_FORMAT_STRING).format(new Date()));
		section.setLayout(new TableWrapLayout());
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		Composite container = toolkit.createComposite(section);
		section.setClient(container);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 3;
		container.setLayout(layout);

		Button viewFile = toolkit.createButton(container, "View File", SWT.PUSH | SWT.CENTER);
		viewFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewFile();
			}
		});
	}

	private void viewFile() {

		File monitorFile = MylarUsageMonitorPlugin.getDefault().getMonitorLogFile();
		try {
			FileDisplayDialog.openShowFile(null, "Mylar - Usage History", "", monitorFile);

		} catch (FileNotFoundException e) {
			MylarStatusHandler.log(e, "Couldn't display the monitor history file");

		}

	}
}
