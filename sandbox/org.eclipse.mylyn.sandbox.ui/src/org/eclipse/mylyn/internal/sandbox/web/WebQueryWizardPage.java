/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.sandbox.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Subclipse (IssueZilla)
 *   url: http://subclipse.tigris.org/issues/buglist.cgi?issue_status=NEW;issue_status=STARTED;issue_status=REOPENED&order=issues.issue_id
 *   regexp: <a href="show_bug.cgi\?id\=(.+?)">.+?<span class="summary">(.+?)</span>
 *   task prefix: http://subclipse.tigris.org/issues/show_bug.cgi?id=
 * 
 * ASM (GForge)
 *   url: http://forge.objectweb.org/tracker/?group_id=23&atid=350023
 *   regexp: <a class="tracker" href="/tracker/index.php\?func=detail&aid=(.+?)&group_id=23&atid=350023">(.+?)</a></td>
 *   task prefix: http://forge.objectweb.org/tracker/index.php?func=detail&group_id=23&atid=350023&aid=
 * 
 * @author Eugene Kuleshov
 */
public class WebQueryWizardPage extends WizardPage {
	private Text taskPrefixText;
	private Text descriptionText;
	private Text queryUrlText;
	private Text regexpText;
	private Table previewTable;
	
	private StringBuffer webPage;
	
	private TaskRepository repository;
	private WebQuery query;

	public WebQueryWizardPage(TaskRepository repository) {
		this(repository, null);
	}

	public WebQueryWizardPage(TaskRepository repository, WebQuery query) {
		super("New web query");
		this.repository = repository;
		this.query = query;

		setTitle("Create web query");
		setDescription("http://subclipse.tigris.org/issues/buglist.cgi?issue_status=NEW;issue_status=STARTED;issue_status=REOPENED&order=issues.issue_id\n" +
			"<a href=\"show_bug.cgi\\?id\\=(.+?)\">.+?<span class=\"summary\">(.+?)</span>");
	}

	public void createControl(Composite parent) {
		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);

		Composite composite = new Composite(sashForm, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		composite.setLayout(gridLayout);

		Label descriptionLabel = new Label(composite, SWT.NONE);
		descriptionLabel.setLayoutData(new GridData());
		descriptionLabel.setText("Description:");

		descriptionText = new Text(composite, SWT.BORDER);
		descriptionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		new Label(composite, SWT.NONE);

		Label queryUrlLabel = new Label(composite, SWT.NONE);
		queryUrlLabel.setText("URL:");

		queryUrlText = new Text(composite, SWT.BORDER);
		queryUrlText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				webPage = null;
			}
		});
		queryUrlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Button preview = new Button(composite, SWT.NONE);
		preview.setText("Preview");
		preview.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				updatePreview();
			}
		});

		Label taskPrefixLabel = new Label(composite, SWT.NONE);
		taskPrefixLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		taskPrefixLabel.setText("Task prefix:");

		taskPrefixText = new Text(composite, SWT.BORDER);
		taskPrefixText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		new Label(composite, SWT.NONE);

		Label regexpLabel = new Label(composite, SWT.NONE);
		regexpLabel.setText("Regexp:");
		regexpLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true));

		regexpText = new Text(composite, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 39;
		regexpText.setLayoutData(gridData);
		regexpText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				if(webPage!=null) {
					updatePreview();
				}
			}
		});

		previewTable = new Table(sashForm, SWT.BORDER);
		previewTable.setLinesVisible(true);
		previewTable.setHeaderVisible(true);

		TableColumn colId = new TableColumn(previewTable, SWT.NONE);
		colId.setWidth(100);
		colId.setText("Id");

		TableColumn colDescription = new TableColumn(previewTable, SWT.NONE);
		colDescription.setWidth(328);
		colDescription.setText("Description");

		setControl(sashForm);
		
		if(query!=null) {
			descriptionText.setText(query.getDescription());
			queryUrlText.setText(query.getQueryUrl());
			taskPrefixText.setText(query.getTaskPrefix());
			regexpText.setText(query.getRegexp());
		}
		new Label(composite, SWT.NONE);
		sashForm.setWeights(new int[] {123, 166 });
	}

	public AbstractRepositoryQuery getQuery() {
		String description = descriptionText.getText();
		String queryUrl = queryUrlText.getText();
		String taskPrefix = taskPrefixText.getText();
		String regexp = regexpText.getText();
		return new WebQuery(description, queryUrl, taskPrefix, regexp, 
				MylarTaskListPlugin.getTaskListManager().getTaskList(), repository.getUrl());
	}

	// TODO run asynchronously
	synchronized void updatePreview() {
		String regexp = regexpText.getText();
		try {
		    Pattern p = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL | Pattern.UNICODE_CASE | Pattern.CANON_EQ);
		    Matcher matcher = p.matcher(getWebPage());
		    
		    previewTable.removeAll();

		    while(matcher.find()) {
		    	if(matcher.groupCount()>0) {
		    		TableItem item = new TableItem(previewTable, SWT.NONE);
		    		for (int i = 0; i < matcher.groupCount(); i++) {
		    			item.setText(i, matcher.group(i+1));
					}
		    	}
		    	
		    	if(matcher.groupCount()<2) {
		    		setErrorMessage("Require two matching groups (id and description)");
					setPageComplete(false);
		    	} else {
		    		setErrorMessage(null);
		    		setPageComplete(true);
		    	}
		    }
			
		} catch(Exception ex) {
			setErrorMessage("Parsing error: "+ex.getMessage());
			setPageComplete(false);
		}
	}
	
	private StringBuffer getWebPage() {
		if(webPage==null) {
			try {
				webPage = WebRepositoryConnector.fetchResource(queryUrlText.getText());
			} catch(Exception ex) {
				setErrorMessage("Unable to fetch resource: "+ex.getMessage());
				setPageComplete(false);
			}
		}
		return webPage;
	}
	
}
