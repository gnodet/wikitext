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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylar.core.util.ErrorLogger;
import org.eclipse.mylar.monitor.reports.IUsageCollector;
import org.eclipse.mylar.monitor.reports.internal.InteractionEventSummarySorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
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

	private String[] columnNames = new String[] { "Kind", "ID", "Num", "Last Delta", "Users" };

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		editorInput = (UsageStatsEditorInput) input;
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

		createActionSection(editorComposite, toolkit);
		createSummaryStatsSection(editorComposite, toolkit);
		if (editorInput.getReportGenerator().getLastParsedSummary().getSingleSummaries().size() > 0) {
			createUsageSection(editorComposite, toolkit);
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
		layout.numColumns = 2;
		container.setLayout(layout);

		Button exportHtml = toolkit.createButton(container, "Export as HTML", SWT.PUSH | SWT.CENTER);
		exportHtml.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				exportToHtml();
			}
		});

		Button export = toolkit.createButton(container, "Export as CSV Files", SWT.PUSH | SWT.CENTER);
		export.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				exportToCSV();
			}
		});
	}

	private void createSummaryStatsSection(Composite parent, FormToolkit toolkit) {
		for (IUsageCollector collector : editorInput.getReportGenerator().getLastParsedSummary().getCollectors()) {
			List<String> summary = collector.getReport();
			if (!summary.isEmpty()) {
				Section summarySection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
				summarySection.setText(collector.getReportTitle());
				summarySection.setLayout(new TableWrapLayout());
				summarySection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
				Composite summaryContainer = toolkit.createComposite(summarySection);
				summarySection.setClient(summaryContainer);
				TableWrapLayout layout = new TableWrapLayout();
				// layout.numColumns = 2;
				summaryContainer.setLayout(layout);

				Composite browserComposite = new Composite(summaryContainer, SWT.NULL);
				browserComposite.setLayout(new GridLayout());
				Browser browser = new Browser(browserComposite, SWT.NONE);
				GridData browserLayout = new GridData(GridData.FILL_HORIZONTAL);
				browserLayout.heightHint = 600;
				browserLayout.widthHint = 600;
				browser.setLayoutData(browserLayout);
				String htmlText = "<html><head><LINK REL=STYLESHEET HREF=\"http://eclipse.org/default_style.css\" TYPE=\"text/css\"></head><body>\n";
				for (String description : summary)
					htmlText += description;
				htmlText += "</body></html>";
				browser.setText(htmlText);
				// if (description.equals(ReportGenerator.SUMMARY_SEPARATOR)) {
				// toolkit.createLabel(summaryContainer,
				// "---------------------------------");
				// toolkit.createLabel(summaryContainer,
				// "---------------------------------");
				// } else {
				// Label label = toolkit.createLabel(summaryContainer,
				// description);
				// if (!description.startsWith("<h"));
				// label.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
				// label.setLayoutData(new
				// TableWrapData(TableWrapData.FILL_GRAB));
				// }
				// }
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
		table = toolkit.createTable(parent, style);
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

		// column = new TableColumn(table, SWT.LEFT, 3);
		// column.setText(columnNames[3]);
		// column.setWidth(60);

		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText(columnNames[3]);
		column.setWidth(60);

		column = new TableColumn(table, SWT.LEFT, 4);
		column.setText(columnNames[4]);
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

		// Ask the user to pick a directory into which to place multiple CSV
		// files
		try {
			DirectoryDialog dialog = new DirectoryDialog(Workbench.getInstance().getActiveWorkbenchWindow().getShell());
			dialog.setText("Specify a directory for the CSV files");
			String directoryName = dialog.open();

			File outputFile;
			FileOutputStream outputStream;

			String filename = directoryName + File.separator + "Usage.csv";
			outputFile = new File(filename);

			outputStream = new FileOutputStream(outputFile, false);

			// Delegate to all collectors
			for (IUsageCollector collector : editorInput.getReportGenerator().getCollectors()) {
				collector.exportAsCSVFile(directoryName);
			}

			int columnCount = table.getColumnCount();
			for (TableItem item : table.getItems()) {
				for (int i = 0; i < columnCount - 1; i++) {
					outputStream.write(((String) item.getText(i) + ",").getBytes());
				}
				outputStream.write(item.getText(columnCount - 1).getBytes());
				outputStream.write(((String) "\n").getBytes());
			}

			outputStream.flush();
			outputStream.close();

		} catch (SWTException swe) {
			ErrorLogger.log(swe, "unable to get directory name");
		} catch (FileNotFoundException e) {
			ErrorLogger.log(e, "could not resolve file");
		} catch (IOException e) {
			ErrorLogger.log(e, "could not write to file");
		}
	}

	private void exportToHtml() {
	    File outputFile;
	    try {
            FileDialog dialog = new FileDialog(Workbench.getInstance().getActiveWorkbenchWindow().getShell());
	    	dialog.setText("Specify a file name");
            dialog.setFilterExtensions(new String[] { "*.html", "*.*" });

            String filename = dialog.open();
            if (!filename.endsWith(".html")) filename += ".html";
	    	outputFile = new File(filename);
//	    	outputStream = new FileOutputStream(outputFile, true);
	    	BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
	    	writer.write(
	    			"<html><head>"
//	    			+ "<link rel=\"stylesheet\" href=\"http://eclipse.org/mylar/style.css\" type=\"text/css\"></head><body>"	
	    	);
	    	for (IUsageCollector collector : editorInput.getReportGenerator().getCollectors()) {
	    		writer.write("<h3>" + collector.getReportTitle() + "</h3>");
	    		for (String reportLine : collector.getReport()) {
	    			writer.write(reportLine);
				}
	    		writer.write("<br><hr>");
			}
	    	writer.write("</body></html>");
	    	writer.close();
        } catch (FileNotFoundException e) {
            ErrorLogger.log(e, "could not resolve file");
	    } catch (IOException e) {
	    	ErrorLogger.log(e, "could not write to file");
	    }
	}
}