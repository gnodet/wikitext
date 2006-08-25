/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.ui.editor;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylar.internal.tasks.ui.editors.AbstractBugEditorInput;
import org.eclipse.mylar.internal.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylar.internal.tasks.ui.editors.ExistingBugEditorInput;
import org.eclipse.mylar.internal.tasks.ui.editors.RepositoryTaskOutlineNode;
import org.eclipse.mylar.internal.trac.TracRepositoryConnector;
import org.eclipse.mylar.internal.trac.TracUiPlugin;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.InvalidTicketException;
import org.eclipse.mylar.internal.trac.model.TracTicket;
import org.eclipse.mylar.internal.trac.model.TracTicket.Key;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.RepositoryOperation;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author Steffen Pingel
 */
public class TracTaskEditor extends AbstractRepositoryTaskEditor {

	private static final String SUBMIT_JOB_LABEL = "Submitting to Trac repository";

	private TracRepositoryConnector connector;

	public TracTaskEditor(FormEditor editor) {
		super(editor);
	}

	@Override
	protected void addAttachContextButton(Composite buttonComposite, ITask task) {
		// disabled, see bug 155151
	}

	@Override
	protected void addSelfToCC(Composite composite) {
		// disabled, see bug 155151
	}

	@Override
	public void createCustomAttributeLayout() {
	}

	@Override
	protected void createCustomAttributeLayout(Composite composite) {
	}

	@Override
	public RepositoryTaskData getRepositoryTaskData() {
		return editorInput.getRepositoryTaskData();
	}

	@Override
	protected String getTitleString() {
		// TODO Auto-generated method stub
		return null;
	}

	public void init(IEditorSite site, IEditorInput input) {
		if (!(input instanceof ExistingBugEditorInput))
			return;

		editorInput = (AbstractBugEditorInput) input;
		repository = editorInput.getRepository();
		connector = (TracRepositoryConnector) TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getKind());

		setSite(site);
		setInput(input);

		taskOutlineModel = RepositoryTaskOutlineNode.parseBugReport(editorInput.getRepositoryTaskData());

		isDirty = false;
		updateEditorTitle();
	}

	@Override
	protected void submitBug() {
		if (isDirty()) {
			this.doSave(new NullProgressMonitor());
		}
		updateBug();
		submitButton.setEnabled(false);
		showBusy(true);

		final TracTicket ticket;
		try {
			ticket = getTracTicket();
		} catch (InvalidTicketException e) {
			TracUiPlugin.handleTracException(e);
			return;
		}
		final String comment = getNewCommentText();
		final AbstractRepositoryTask task = (AbstractRepositoryTask) TasksUiPlugin.getTaskListManager().getTaskList()
				.getTask(AbstractRepositoryTask.getHandle(repository.getUrl(), getRepositoryTaskData().getId()));
		final boolean attachContext = false; // getAttachContext();

		JobChangeAdapter listener = new JobChangeAdapter() {
			public void done(final IJobChangeEvent event) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (event.getJob().getResult().isOK()) {
							if (attachContext) {
								// TODO should be done as part of job
								try {
									connector.attachContext(repository, (AbstractRepositoryTask) task, "",
											TasksUiPlugin.getDefault().getProxySettings());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							close();
						} else {
							// TracUiPlugin.handleTracException(event.getResult());
							submitButton.setEnabled(true);
							TracTaskEditor.this.showBusy(false);
						}
					}
				});
			}
		};

		Job submitJob = new Job(SUBMIT_JOB_LABEL) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					ITracClient server = connector.getClientManager().getRepository(repository);
					server.updateTicket(ticket, comment);
					// XXX hack to avoid message about lost changes to local
					// task
					task.setTaskData(null);
					TasksUiPlugin.getSynchronizationManager().synchronize(connector, task, true, null);
					return Status.OK_STATUS;
				} catch (Exception e) {
					return TracUiPlugin.toStatus(e);
				}
			}

		};

		submitJob.addJobChangeListener(listener);
		submitJob.schedule();

	}

	@Override
	protected void updateBug() {
		getRepositoryTaskData().setHasLocalChanges(true);
	}

	@Override
	protected void validateInput() {
	}

	TracTicket getTracTicket() throws InvalidTicketException {
		RepositoryTaskData data = getRepositoryTaskData();

		TracTicket ticket = new TracTicket(Integer.parseInt(data.getId()));

		List<RepositoryTaskAttribute> attributes = data.getAttributes();
		for (RepositoryTaskAttribute attribute : attributes) {
			if (!attribute.isReadOnly()) {
				ticket.putValue(attribute.getID(), attribute.getValue());
			}
		}
		// TODO "1" should not be hard coded here
		if ("1".equals(data.getAttributeValue(RepositoryTaskAttribute.ADD_SELF_CC))) {
			String cc = data.getAttributeValue(RepositoryTaskAttribute.USER_CC);
			ticket.putBuiltinValue(Key.CC, cc + "," + repository.getUserName());
		}

		RepositoryOperation operation = data.getSelectedOperation();
		if (operation != null) {
			String action = operation.getKnobName();
			if (!"leave".equals(action)) {
				if ("accept".equals(action)) {
					ticket.putValue("status", "assigned");
					ticket.putValue("owner", TracRepositoryConnector.getDisplayUsername(repository));
				} else if ("resolve".equals(action)) {
					ticket.putValue("status", "closed");
					ticket.putValue("resolution", operation.getOptionSelection());
				} else if ("reopen".equals(action)) {
					ticket.putValue("status", "reopened");
					ticket.putValue("resolution", "");
				} else if ("reassign".equals(operation.getKnobName())) {
					ticket.putValue("status", "new");
					ticket.putValue("owner", operation.getInputValue());
				}
			}
		}

		return ticket;
	}

}
