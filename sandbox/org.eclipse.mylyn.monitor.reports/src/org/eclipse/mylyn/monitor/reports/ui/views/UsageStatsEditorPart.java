/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.monitor.reports.ui.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.monitor.reports.IUsageStatsCollector;
import org.eclipse.mylar.monitor.reports.internal.InteractionEventSummarySorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.EditorPart;

/**
 * @author Mik Kersten
 */
public class UsageStatsEditorPart extends EditorPart {

	private UsageStatsEditorInput editorInput;
	private Table table;
	private TableViewer tableViewer;
	private String[] columnNames = new String[] { "Kind", "ID", "Num", "Last Delta"};
	
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		editorInput = (UsageStatsEditorInput)input;
		setPartName(editorInput.getName());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm sform = toolkit.createScrolledForm(parent);
		sform.getBody().setLayout(new TableWrapLayout());
		Composite editorComposite = sform.getBody();
		
		createSummaryStatsSection(editorComposite, toolkit);
		if (editorInput.getReportGenerator().getLastParsedSummary().getSingleSummaries().size() > 0) {
			createUsageSection(editorComposite, toolkit);	
			createActionSection(editorComposite, toolkit);
		}
	}

	@Override
	public void setFocus() {
	}

	private void createActionSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		section.setText("Actions");
		section.setLayout(new TableWrapLayout());
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		Composite container = toolkit.createComposite(section);
		section.setClient(container);		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;						
		container.setLayout(layout);
		
		Button export  = toolkit.createButton(container, "Export CSV file",  SWT.PUSH | SWT.CENTER);
		export.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				exportToCSV();
			}
		});
	}
	
	private void createSummaryStatsSection(Composite parent, FormToolkit toolkit) {
		for (IUsageStatsCollector collector : editorInput.getReportGenerator().getLastParsedSummary().getCollectors()) {
			List<String> summary = collector.getReport();
			if (!summary.isEmpty()) {
				Section summarySection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
				summarySection.setText(collector.getReportTitle());			
				summarySection.setLayout(new TableWrapLayout());
				summarySection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));	
				Composite summaryContainer = toolkit.createComposite(summarySection);
				summarySection.setClient(summaryContainer);		
				TableWrapLayout layout = new TableWrapLayout();
				layout.numColumns = 2;						
				summaryContainer.setLayout(layout);
				
				for (String description : summary) {
					Label label = toolkit.createLabel(summaryContainer, description);
					label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));	
					label.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
				}
			}
		}
	}

	private void createUsageSection(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText("Usage Details");	
		section.setLayout(new TableWrapLayout());
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		Composite container = toolkit.createComposite(section);
		section.setClient(container);		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;						
		container.setLayout(layout);
		
		createTable(container, toolkit);
		createTableViewer();
		toolkit.paintBordersFor(container);
	}
	
	private void createTable(Composite parent, FormToolkit toolkit) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		table = toolkit.createTable(parent, style );		
		TableLayout tlayout = new TableLayout();
		table.setLayout(tlayout);
		TableWrapData wd = new TableWrapData(TableWrapData.FILL_GRAB);
		wd.heightHint = 300;
		wd.grabVertical = true;
		table.setLayoutData(wd);
				
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText(columnNames[0]);
		column.setWidth(60);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new InteractionEventSummarySorter(InteractionEventSummarySorter.TYPE));

			}
		});

		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(columnNames[1]);
		column.setWidth(370);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new InteractionEventSummarySorter(InteractionEventSummarySorter.NAME));
			}
		});

		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText(columnNames[2]);
		column.setWidth(50);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new InteractionEventSummarySorter(InteractionEventSummarySorter.USAGE_COUNT));
			}
		});

//		column = new TableColumn(table, SWT.LEFT, 3);
//		column.setText(columnNames[3]);
//		column.setWidth(60);
		
		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText(columnNames[3]);
		column.setWidth(60);
	}
	
	private void createTableViewer() {
		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(columnNames);
		
		tableViewer.setContentProvider(new UsageCountContentProvider(editorInput.getReportGenerator()));
		tableViewer.setLabelProvider(new UsageCountLabelProvider());
		tableViewer.setInput(editorInput);
	}
	
	private void exportToCSV() {
	    File outputFile;
	    FileOutputStream outputStream;
	    
	    try {
            FileDialog dialog = new FileDialog(Workbench.getInstance().getActiveWorkbenchWindow().getShell());
	    	dialog.setText("Specify a file name");
            dialog.setFilterExtensions(new String[] { "*.csv", "*.*" });

            String filename = dialog.open();
            
	    	outputFile = new File(filename);
	    	outputStream = new FileOutputStream(outputFile, true);
	    	
	    	int columnCount = table.getColumnCount();
	    	for (TableItem item : table.getItems()) {
	      		for (int i = 0; i < columnCount - 1; i++) {
	    			outputStream.write(((String) item.getText(i) + ",").getBytes());
	    		}
	      		outputStream.write(item.getText(columnCount - 1).getBytes());
	      		outputStream.write(((String) "\n").getBytes());
	    	}

	    	outputStream.close();
        } catch (FileNotFoundException e) {
            MylarPlugin.log(e, "could not resolve file");
	    } catch (IOException e) {
	    	MylarPlugin.log(e, "could not write to file");
	    }
	}
}
