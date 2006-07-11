/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.sandbox.web;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
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
import org.eclipse.swt.widgets.Display;
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
	private UpdatePreviewJob updatePreviewJob;

	public WebQueryWizardPage(TaskRepository repository) {
		this(repository, null);
	}

	public WebQueryWizardPage(TaskRepository repository, WebQuery query) {
		super("New web query");
		this.repository = repository;
		this.query = query;

		setTitle("Create web query");
		setDescription("Specify URL for web page that show query results and regexp to extract id and description " +
				repository.getUrl());
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
		new Label(composite, SWT.NONE);

		Label regexpLabel = new Label(composite, SWT.NONE);
		regexpLabel.setText("Regexp:");
		regexpLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true));

		regexpText = new Text(composite, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
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

		new ContentProposalAdapter(regexpText, new TextContentAdapter(),
				new RegExpProposalProvider(), KeyStroke.getInstance(SWT.CTRL, ' '), null);

		Button preview = new Button(composite, SWT.NONE);
		preview.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true));
		preview.setText("Preview");
		preview.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent e) {
				webPage = null;
				updatePreview();
			}
		});

		Label taskPrefixLabel = new Label(composite, SWT.NONE);
		taskPrefixLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		taskPrefixLabel.setText("Task prefix:");

		taskPrefixText = new Text(composite, SWT.BORDER);
		taskPrefixText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		new Label(composite, SWT.NONE);
		
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
			regexpText.setText(query.getRegexp());
			taskPrefixText.setText(query.getTaskPrefix());
		}
		sashForm.setWeights(new int[] {141, 172 });
	}

	public AbstractRepositoryQuery getQuery() {
		String description = descriptionText.getText();
		String queryUrl = queryUrlText.getText();
		String taskPrefix = taskPrefixText.getText();
		String regexp = regexpText.getText();
		return new WebQuery(description, queryUrl, taskPrefix, regexp, 
				MylarTaskListPlugin.getTaskListManager().getTaskList(), repository.getUrl());
	}

	synchronized void updatePreview() {
		if(updatePreviewJob==null) {
			updatePreviewJob = new UpdatePreviewJob("Updating preview");
			updatePreviewJob.setPriority(Job.DECORATE);
		}
		updatePreviewJob.setParams(queryUrlText.getText(), regexpText.getText());
		if(!updatePreviewJob.isActive()) {
			updatePreviewJob.schedule();
		}
		
	}
	
	void updatePreviewTable(List<AbstractQueryHit> hits, MultiStatus queryStatus) {
		if(previewTable.isDisposed()) {
			return;
		}
		
		previewTable.removeAll();

		if(hits!=null) {
			for (AbstractQueryHit hit : hits) {
				TableItem item = new TableItem(previewTable, SWT.NONE);
				if(hit.getId()!=null) {
					item.setText(0, hit.getId());
					if(hit.getDescription()!=null) {
						item.setText(1, hit.getDescription());
					}
				}
			}
		}

		if(queryStatus.isOK()) {
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
		private volatile boolean active = false;

		private UpdatePreviewJob(String name) {
			super(name);
		}
		
		public boolean isActive() {
			return active;
		}

		public void setParams(String url, String regexp) {
			this.url = url;
			this.regexp = regexp;
		}

		protected IStatus run(IProgressMonitor monitor) {
			String currentUrl = url;
			String currentRegexp = regexp;
			active = true;
			do {
				final MultiStatus queryStatus = new MultiStatus(MylarTaskListPlugin.PLUGIN_ID, IStatus.OK, "Query result", null);
				List<AbstractQueryHit> hits = null;
				try {
					if(webPage==null) {
						webPage = WebRepositoryConnector.fetchResource(currentUrl);
					}

					hits = WebRepositoryConnector.performQuery(webPage, currentRegexp, null, null, monitor, queryStatus);
					
				} catch (final IOException ex) {
					queryStatus.add(new Status(IStatus.ERROR, MylarTaskListPlugin.PLUGIN_ID, IStatus.ERROR,
							"Unable to fetch resource: "+ex.getMessage(), null));
				} catch (final Exception ex) {
					queryStatus.add(new Status(IStatus.ERROR, MylarTaskListPlugin.PLUGIN_ID, IStatus.ERROR,
							"Parsing error: "+ex.getMessage(), null));
				}
				
				final List<AbstractQueryHit> queryHits = hits;
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						updatePreviewTable(queryHits, queryStatus);
					}
				});
			} while(currentRegexp!=regexp && !monitor.isCanceled());
			active = false;
			return Status.OK_STATUS;
		}
	}


	/**
	 * Simple proposal provider for regexps  
	 */
	private static final class RegExpProposalProvider implements IContentProposalProvider {
		private static final String[] LABELS = {
				"IssueZilla",
				"GForge",
				"Trac",
				"Jira",
				"vBulletin"
			};
		private static final String[] PROPOSALS = {
				"<a href=\"show_bug.cgi\\?id\\=(.+?)\">.+?<span class=\"summary\">(.+?)</span>",
				"<a class=\"tracker\" href=\"/tracker/index.php\\?func=detail&aid=(.+?)&group_id=GROUP&atid=ATID\">(.+?)</a></td>",
				"<td class=\"summary\"><a title=\"View ticket\" href=\"/project/ticket/(.+?)\">(.+?)</a></td>",
				"<td class=\"nav summary\">\\s+?<a href=\"/browse/(.+?)\".+?>(.+?)</a>",
				"<a href=\"showthread.php\\?.+?t=(\\d+?)\" id=\"thread_title_\\1\">(.+?)</a>"
			};

		public IContentProposal[] getProposals(String contents, int position) {
			IContentProposal[] res = new IContentProposal[LABELS.length]; 
			for (int i = 0; i < LABELS.length; i++) {
				final String label = LABELS[i];
				final String content = PROPOSALS[i];
				res[i] = new IContentProposal() {
						public String getContent() {
							return content;
						}
						public int getCursorPosition() {
							return content.length();
						}
						public String getDescription() {
							return content;
						}
						public String getLabel() {
							return label;
						}
					};
			}
			return res;
		}
	}
	
}

