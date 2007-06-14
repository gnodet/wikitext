/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskFactory;
import org.eclipse.mylyn.tasks.core.QueryHitCollector;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.search.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Wizard page for configuring and preview web query
 * 
 * @author Eugene Kuleshov
 */
public class WebQueryWizardPage extends AbstractRepositoryQueryPage {
	private Text queryUrlText;

	private Text queryPatternText;

	private Table previewTable;

	private String webPage;

	private TaskRepository repository;

	private WebQuery query;

	private UpdatePreviewJob updatePreviewJob;

	private FormToolkit toolkit = new FormToolkit(Display.getCurrent());

	private ParametersEditor parametersEditor;

	private Map<String, String> oldProperties;

	public WebQueryWizardPage(TaskRepository repository) {
		this(repository, null);
	}

	public WebQueryWizardPage(TaskRepository repository, WebQuery query) {
		super("New web query", query == null ? getDefaultQueryTitle(repository) : query.getSummary());
		this.repository = repository;
		this.query = query;
		setTitle("Create web query");
		setDescription("Specify query parameters for " + repository.getUrl());
	}

	private static String getDefaultQueryTitle(TaskRepository repository) {
		String label = repository.getRepositoryLabel();
		String title = label;
		Set<AbstractRepositoryQuery> queries = TasksUiPlugin.getTaskListManager().getTaskList().getRepositoryQueries(
				repository.getUrl());
		for (int n = 1; true; n++) {
			for (AbstractRepositoryQuery query : queries) {
				if (query.getSummary().equals(title)) {
					title = label + " " + n;
				}
			}
			return title;
		}
	}

	@Override
	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.numColumns = 1;
		composite.setLayout(gridLayout);

		super.createControl(composite);

// Label descriptionLabel = new Label(composite, SWT.NONE);
// descriptionLabel.setLayoutData(new GridData());
// descriptionLabel.setText("Query Title:");

// queryTitleText = new Text(composite, SWT.BORDER);
// queryTitleText.addModifyListener(new ModifyListener() {
// public void modifyText(ModifyEvent e) {
// setPageComplete(isPageComplete());
// }
// });
// queryTitleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
// false));

		parametersEditor = new ParametersEditor(composite, SWT.NONE);
		GridData gridData1 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData1.minimumHeight = 90;
		parametersEditor.setLayoutData(gridData1);

		ExpandableComposite expComposite = toolkit.createExpandableComposite(composite, Section.COMPACT
				| Section.TWISTIE);
		expComposite.setFont(parent.getFont());
		GridData gridData_1 = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData_1.heightHint = 150;
		gridData_1.minimumHeight = 150;
		expComposite.setLayoutData(gridData_1);
		expComposite.setBackground(parent.getBackground());
		expComposite.setText("Advanced &Configuration");
		expComposite.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				getControl().getShell().pack();
			}
		});
		toolkit.paintBordersFor(expComposite);

		Composite composite1 = toolkit.createComposite(expComposite, SWT.BORDER);
		composite1.setLayout(new GridLayout(3, false));
		expComposite.setClient(composite1);

		toolkit.createLabel(composite1, "&Query URL:", SWT.NONE);

		queryUrlText = new Text(composite1, SWT.BORDER);
		final GridData gridData_2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData_2.widthHint = 200;
		queryUrlText.setLayoutData(gridData_2);
		queryUrlText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				webPage = null;
			}
		});
		new Label(composite1, SWT.NONE);

		/*
		 * final Button openButton = new Button(composite1, SWT.NONE);
		 * openButton.addSelectionListener(new SelectionAdapter() { public void
		 * widgetSelected(SelectionEvent e) { String url =
		 * WebRepositoryConnector.evaluateParams(queryUrlText.getText(),
		 * parametersEditor.getParameters(), repository);
		 * IWorkbenchBrowserSupport support =
		 * PlatformUI.getWorkbench().getBrowserSupport(); try {
		 * support.getExternalBrowser().openURL(new URL(url)); } catch
		 * (Exception ex) { // XXX log the error } } });
		 * openButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
		 * false)); toolkit.adapt(openButton, true, true);
		 * openButton.setText("Open");
		 */

		Label queryPatternLabel = toolkit.createLabel(composite1, "Query &Pattern:", SWT.NONE);
		queryPatternLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

		queryPatternText = new Text(composite1, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.widthHint = 200;
		gridData.heightHint = 45;
		queryPatternText.setLayoutData(gridData);

// regexpText.addModifyListener(new ModifyListener() {
// public void modifyText(final ModifyEvent e) {
// if(webPage!=null) {
// updatePreview();
// }
// }
// });

		Button preview = new Button(composite1, SWT.NONE);
		preview.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		preview.setText("Preview");
		preview.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				webPage = null;
				updatePreview();
			}
		});

		previewTable = new Table(composite1, SWT.BORDER);
		GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1);
		gridData2.heightHint = 60;
		previewTable.setLayoutData(gridData2);
		previewTable.setLinesVisible(true);
		previewTable.setHeaderVisible(true);

		TableColumn colId = new TableColumn(previewTable, SWT.NONE);
		colId.setWidth(100);
		colId.setText("Id");

		TableColumn colDescription = new TableColumn(previewTable, SWT.NONE);
		colDescription.setWidth(328);
		colDescription.setText("Description");

		setControl(composite);

		LinkedHashMap<String, String> vars = new LinkedHashMap<String, String>();
		Map<String, String> params = new LinkedHashMap<String, String>();
		if (repository != null) {
			queryUrlText.setText(addVars(vars, repository.getProperty(WebRepositoryConnector.PROPERTY_QUERY_URL)));
			queryPatternText
					.setText(addVars(vars, repository.getProperty(WebRepositoryConnector.PROPERTY_QUERY_REGEXP)));

			oldProperties = repository.getProperties();
			params.putAll(oldProperties);
		}
		if (query != null) {
			setTitle(query.getSummary());
			queryUrlText.setText(addVars(vars, query.getQueryUrlTemplate()));
			queryPatternText.setText(addVars(vars, query.getQueryPattern()));
			params.putAll((query).getQueryParameters());
		}
		parametersEditor.addParams(params, vars);
	}

	private static String addVars(LinkedHashMap<String, String> vars, String property) {
		if (property == null) {
			return "";
		}
		for (String var : WebRepositoryConnector.getTemplateVariables(property)) {
			vars.put(var, "");
		}
		return property;
	}

	@Override
	public AbstractRepositoryQuery getQuery() {
		String description = getQueryTitle();
		String queryUrlTemplate = queryUrlText.getText();
		String queryPattern = queryPatternText.getText();
		Map<String, String> params = parametersEditor.getParameters();

		String queryUrl = WebRepositoryConnector.evaluateParams(queryUrlTemplate, params, repository);

		return new WebQuery(description, queryUrl, queryUrlTemplate, queryPattern,
				repository.getProperty(WebRepositoryConnector.PROPERTY_TASK_URL), repository.getUrl(), params);
	}

	synchronized void updatePreview() {
		if (updatePreviewJob == null) {
			updatePreviewJob = new UpdatePreviewJob("Updating preview");
			updatePreviewJob.setPriority(Job.DECORATE);
		}
		updatePreviewJob
				.setParams(queryUrlText.getText(), queryPatternText.getText(), parametersEditor.getParameters());
		if (!updatePreviewJob.isActive()) {
			updatePreviewJob.schedule();
		}
	}

	@Override
	public boolean isPageComplete() {
		if (getErrorMessage() != null) {
			return false;
		}
		return super.isPageComplete();
	}

	void updatePreviewTable(List<AbstractTask> hits, MultiStatus queryStatus) {
		if (previewTable.isDisposed()) {
			return;
		}

		previewTable.removeAll();

		if (hits != null) {
			for (AbstractTask hit : hits) {
				TableItem item = new TableItem(previewTable, SWT.NONE);
				if (hit.getTaskId() != null) {
					item.setText(0, hit.getTaskId());
					if (hit.getSummary() != null) {
						item.setText(1, hit.getSummary());
					}
				}
			}
		}

		if (queryStatus.isOK()) {
			setErrorMessage(null);
			setPageComplete(true);
		} else {
			StringBuffer sb = new StringBuffer();
			for (IStatus status : queryStatus.getChildren()) {
				sb.append(status.getMessage()).append("\n");
			}
			setErrorMessage(sb.toString());
			setPageComplete(false);
		}
	}

	private final class UpdatePreviewJob extends Job {
		private volatile String url;

		private volatile String regexp;

		private volatile Map<String, String> params;

		private volatile boolean active = false;

		private UpdatePreviewJob(String name) {
			super(name);
		}

		public boolean isActive() {
			return active;
		}

		public void setParams(String url, String regexp, Map<String, String> params) {
			this.url = url;
			this.regexp = regexp;
			this.params = params;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			String currentRegexp = regexp;
			String queryPattern = WebRepositoryConnector.evaluateParams(currentRegexp, params, repository);
			String evaluatedUrl = WebRepositoryConnector.evaluateParams(url, params, repository);
			active = true;
			do {
				final MultiStatus queryStatus = new MultiStatus(TasksUiPlugin.PLUGIN_ID, IStatus.OK, "Query result",
						null);
				final List<AbstractTask> queryHits = new ArrayList<AbstractTask>();
				try {
					if (webPage == null) {
						webPage = WebRepositoryConnector.fetchResource(evaluatedUrl, params, repository);
					}

					QueryHitCollector collector = new QueryHitCollector(TasksUiPlugin.getTaskListManager()
							.getTaskList(), new ITaskFactory() {

						public AbstractTask createTask(RepositoryTaskData taskData, IProgressMonitor monitor) throws CoreException {
							return null;
						}
					}) {

						@Override
						public void accept(AbstractTask hit) {
							queryHits.add(hit);
						}
					};

					IStatus status;
					if (queryPattern != null && queryPattern.trim().length() > 0) {
						status = WebRepositoryConnector.performQuery(webPage, queryPattern, null, monitor, collector,
								repository);
					} else {
						status = WebRepositoryConnector.performRssQuery(evaluatedUrl, monitor, collector, repository);
					}

					if (!status.isOK()) {
						queryStatus.add(status);
					}

				} catch (final IOException ex) {
					queryStatus.add(new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, IStatus.ERROR,
							"Unable to fetch resource: " + ex.getMessage(), null));
				} catch (final Exception ex) {
					queryStatus.add(new Status(IStatus.ERROR, TasksUiPlugin.PLUGIN_ID, IStatus.ERROR, "Parsing error: "
							+ ex.getMessage(), null));
				}

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						updatePreviewTable(queryHits, queryStatus);
					}
				});
			} while (!currentRegexp.equals(currentRegexp) && !monitor.isCanceled());
			active = false;
			return Status.OK_STATUS;
		}
	}

}
