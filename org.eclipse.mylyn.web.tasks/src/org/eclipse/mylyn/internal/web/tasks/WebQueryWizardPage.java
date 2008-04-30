/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.tasks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.ITaskFactory;
import org.eclipse.mylyn.tasks.core.QueryHitCollector;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Wizard page for configuring and preview web query
 * 
 * @author Eugene Kuleshov
 */
public class WebQueryWizardPage extends AbstractRepositoryQueryPage {
	private Text queryUrlText;

	private Text queryPatternText;

	private TableViewer previewTable;

	private String webPage;

	private final TaskRepository repository;

	private final WebQuery query;

	private UpdatePreviewJob updatePreviewJob;

	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());

	private ParametersEditor parametersEditor;

	private Map<String, String> oldProperties;

	private final ArrayList<ControlDecoration> decorations = new ArrayList<ControlDecoration>();

	public WebQueryWizardPage(TaskRepository repository) {
		this(repository, null);
	}

	public WebQueryWizardPage(TaskRepository repository, WebQuery query) {
		super("New web query", query == null ? getDefaultQueryTitle(repository) : query.getSummary());
		this.repository = repository;
		this.query = query;
		setTitle("Create web query");
		setDescription("Specify query parameters for " + repository.getRepositoryUrl());
	}

	@Override
	public void dispose() {
		for (ControlDecoration decoration : decorations) {
			decoration.dispose();
		}
		super.dispose();
	}

	private static String getDefaultQueryTitle(TaskRepository repository) {
		String label = repository.getRepositoryLabel();
		String title = label;
		Set<AbstractRepositoryQuery> queries = TasksUiPlugin.getTaskListManager().getTaskList().getRepositoryQueries(
				repository.getRepositoryUrl());
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

		parametersEditor = new ParametersEditor(composite, SWT.NONE);
		GridData gridData1 = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData1.minimumHeight = 90;
		parametersEditor.setLayoutData(gridData1);

		ExpandableComposite expComposite = toolkit.createExpandableComposite(composite, ExpandableComposite.COMPACT
				| ExpandableComposite.TWISTIE);
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
		decorations.add(WebContentProposalProvider.createDecoration(queryUrlText, parametersEditor, false));

		Button button = new Button(composite1, SWT.NONE);
		button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		button.setText("&Open");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openBrowser();
			}
		});

		Label queryPatternLabel = toolkit.createLabel(composite1, "Query &Pattern:", SWT.NONE);
		queryPatternLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

		queryPatternText = new Text(composite1, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		gridData.widthHint = 200;
		gridData.heightHint = 60;
		queryPatternText.setLayoutData(gridData);
		decorations.add(WebContentProposalProvider.createDecoration(queryPatternText, parametersEditor, true));

		Button preview = new Button(composite1, SWT.NONE);
		preview.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		preview.setText("Previe&w");
		preview.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				webPage = null;
				updatePreview();
			}
		});

		previewTable = new TableViewer(composite1, SWT.BORDER);
		// previewTable = new Table(composite1, SWT.BORDER);
		GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1);
		gridData2.heightHint = 60;

		Table table = previewTable.getTable();
		table.setLayoutData(gridData2);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn colId = new TableColumn(table, SWT.NONE);
		colId.setWidth(100);
		colId.setText("Id");

		TableColumn colDescription = new TableColumn(table, SWT.NONE);
		colDescription.setWidth(200);
		colDescription.setText("Description");

		TableColumn colStatus = new TableColumn(table, SWT.NONE);
		colStatus.setText("Status");
		colStatus.setWidth(80);

		TableColumn colType = new TableColumn(table, SWT.NONE);
		colType.setText("Type");
		colType.setWidth(80);

		TableColumn colOwner = new TableColumn(table, SWT.NONE);
		colOwner.setText("Owner");
		colOwner.setWidth(120);

		previewTable.setColumnProperties(new String[] { "Id", "Description", "Status", "Type", "Owner" });
		previewTable.setContentProvider(new IStructuredContentProvider() {

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof Collection) {
					@SuppressWarnings("unchecked")
					Collection<AbstractTask> tasks = (Collection<AbstractTask>) inputElement;
					return tasks.toArray();
				}
				return new Object[0];
			}
		});

		previewTable.setLabelProvider(new ITableLabelProvider() {

			public String getColumnText(Object element, int columnIndex) {
				AbstractTask task = (AbstractTask) element;
				switch (columnIndex) {
				case 0:
					return task.getTaskId();
				case 1:
					return task.getSummary();
				case 2:
					return task.isCompleted() ? "complete" : "incomplete";
				case 3:
					return task.getTaskKind();
				case 4:
					return task.getOwner();
				}
				return null;
			}

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
			}
		});

		setControl(composite);

		LinkedHashMap<String, String> vars = new LinkedHashMap<String, String>();
		Map<String, String> params = new LinkedHashMap<String, String>();
		if (repository != null) {
			queryUrlText.setText(addVars(vars, repository.getProperty(WebRepositoryConnector.PROPERTY_QUERY_URL)));
			queryPatternText.setText(addVars(vars, repository.getProperty(WebRepositoryConnector.PROPERTY_QUERY_REGEXP)));

			oldProperties = repository.getProperties();
			params.putAll(oldProperties);
		}
		if (query != null) {
			setTitle(query.getSummary());
			queryUrlText.setText(addVars(vars, query.getQueryUrlTemplate()));
			queryPatternText.setText(addVars(vars, query.getQueryPattern()));
			params.putAll(query.getQueryParameters());
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
				repository.getProperty(WebRepositoryConnector.PROPERTY_TASK_URL), repository.getRepositoryUrl(), params);
	}

	synchronized void updatePreview() {
		if (updatePreviewJob == null) {
			updatePreviewJob = new UpdatePreviewJob("Updating preview");
			updatePreviewJob.setPriority(Job.DECORATE);
		}
		updatePreviewJob.setParams(queryUrlText.getText(), queryPatternText.getText(), parametersEditor.getParameters());
		if (!updatePreviewJob.isActive()) {
			updatePreviewJob.schedule();
		}
	}

	protected void openBrowser() {
		final String url = queryUrlText.getText();
		final Map<String, String> params = parametersEditor.getParameters();

		new Job("Opening Browser") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				String evaluatedUrl = WebRepositoryConnector.evaluateParams(url, params, repository);

				try {
					String webPage = WebRepositoryConnector.fetchResource(evaluatedUrl, params, repository);
					File webPageFile = File.createTempFile("mylyn-web-connector", ".html");
					webPageFile.deleteOnExit();

					FileWriter w = new FileWriter(webPageFile);
					w.write(webPage);
					w.flush();
					w.close();

					IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
					IWebBrowser browser = browserSupport.getExternalBrowser();
					browser.openURL(webPageFile.toURL());

				} catch (final Exception e) {
					Display.getCurrent().asyncExec(new Runnable() {
						public void run() {
							setMessage(e.toString());
						}
					});
				}

				return Status.OK_STATUS;
			}
		}.schedule();
	}

	@Override
	public boolean isPageComplete() {
		return super.isPageComplete();
	}

	void updatePreviewTable(List<AbstractTask> tasks, MultiStatus queryStatus) {
		previewTable.setInput(tasks);

		if (queryStatus.isOK()) {
			setMessage(null, IMessageProvider.WARNING);
		} else {
			StringBuffer sb = new StringBuffer();
			for (IStatus status : queryStatus.getChildren()) {
				sb.append(status.getMessage()).append("\n");
			}
			setMessage(sb.toString(), IMessageProvider.WARNING);
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
			String taskPrefix = WebRepositoryConnector.evaluateParams(
					repository.getProperty(WebRepositoryConnector.PROPERTY_TASK_URL), params, repository);
			active = true;
			do {
				final MultiStatus queryStatus = new MultiStatus(TasksUiPlugin.ID_PLUGIN, IStatus.OK, "Query result",
						null);
				final List<RepositoryTaskData> queryHits = new ArrayList<RepositoryTaskData>();
				try {
					if (webPage == null) {
						webPage = WebRepositoryConnector.fetchResource(evaluatedUrl, params, repository);
					}

					ITaskFactory taskFactory = new ITaskFactory() {
						public AbstractTask createTask(RepositoryTaskData taskData, IProgressMonitor monitor)
								throws CoreException {
							return null;
						}
					};
					QueryHitCollector collector = new QueryHitCollector(taskFactory) {
						@Override
						public void accept(RepositoryTaskData hit) {
							queryHits.add(hit);
						}
					};

					IStatus status;
					if (queryPattern != null && queryPattern.trim().length() > 0) {
						status = WebRepositoryConnector.performQuery(webPage, queryPattern, taskPrefix, monitor,
								collector, repository);
					} else {
						status = WebRepositoryConnector.performRssQuery(evaluatedUrl, monitor, collector, repository);
					}

					if (!status.isOK()) {
						queryStatus.add(status);
					} else if (queryHits.size() == 0) {
						queryStatus.add(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, 0,
								"No matching results. Check query regexp", null));
					}

				} catch (IOException ex) {
					queryStatus.add(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, IStatus.ERROR, //
							"Unable to fetch resource: " + ex.getMessage(), null));
				} catch (Exception ex) {
					queryStatus.add(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, IStatus.ERROR, //
							"Parsing error: " + ex.getMessage(), null));
				}

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						AbstractRepositoryConnector connector = TasksUi.getRepositoryManager().getRepositoryConnector(
								repository.getConnectorKind());
						List<AbstractTask> tasks = new ArrayList<AbstractTask>();
						for (RepositoryTaskData hit : queryHits) {
							AbstractTask task = connector.createTask(repository.getRepositoryUrl(), hit.getTaskId(), "");
							connector.updateTaskFromTaskData(repository, task, hit);
							tasks.add(task);
						}
						updatePreviewTable(tasks, queryStatus);
					}
				});
			} while (!currentRegexp.equals(currentRegexp) && !monitor.isCanceled());
			active = false;
			return Status.OK_STATUS;
		}
	}

}
