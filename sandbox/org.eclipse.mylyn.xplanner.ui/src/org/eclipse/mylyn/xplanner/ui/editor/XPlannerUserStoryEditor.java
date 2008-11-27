/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui.editor;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.GregorianCalendar;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.RichTextAttributeEditor;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.mylyn.xplanner.core.XPlannerCorePlugin;
import org.eclipse.mylyn.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.xplanner.ui.XPlannerAttributeMapper;
import org.eclipse.mylyn.xplanner.ui.XPlannerClientFacade;
import org.eclipse.mylyn.xplanner.ui.XPlannerMylynUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.xplanner.soap.IterationData;
import org.xplanner.soap.PersonData;
import org.xplanner.soap.ProjectData;
import org.xplanner.soap.UserStoryData;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
@SuppressWarnings("restriction")
public class XPlannerUserStoryEditor extends FormPage {
	private static final String NO_PROJECT_NAME = Messages.XPlannerTaskEditor_NO_PROJECT_NAME;

	private static final String NO_ITERATION_NAME = Messages.XPlannerTaskEditor_NO_ITERATION_NAME;

	private static final String NO_STORY_NAME = Messages.XPlannerTaskEditor_NO_STORY_NAME;

	private static final String NO_TRACKER_NAME = Messages.XPlannerUserStoryEditor_NO_TRACKER_NAME;

	private TaskEditorInput input;

	private boolean isDirty = false;

	private XPlannerClient client;

	private UserStoryData userStoryData;

	private TaskData repositoryTaskData;

	public XPlannerUserStoryEditor(FormEditor parent) {
		super(parent, "id", "label"); //$NON-NLS-1$//$NON-NLS-2$
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		//@TODO -- update with XPlanner doSave
		isDirty = false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		if (!(input instanceof TaskEditorInput)) {
			return;
		}

		TaskEditorInput repositoryInput = (TaskEditorInput) input;
		if (!XPlannerCorePlugin.CONNECTOR_KIND.equals(repositoryInput.getTaskRepository().getConnectorKind())) {
			return;
		}

		this.input = repositoryInput;
		setSite(site);
		setInput(input);
		setPartName(this.input.getName());
		try {
			repositoryTaskData = TasksUi.getTaskDataManager().getTaskData(repositoryInput.getTask());
			TaskRepository repository = TasksUi.getRepositoryManager().getRepository(
					repositoryTaskData.getConnectorKind(), repositoryTaskData.getRepositoryUrl());
			client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);
			String id = repositoryTaskData.getTaskId();
			if (id == null || id.trim().equals("")) { //$NON-NLS-1$
				StatusHandler.log(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.ID_PLUGIN,
						Messages.XPlannerTaskEditor_NO_TASK_KEY_EXCEPTION));
			} else {
				setUserStoryData(id);
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.ID_PLUGIN,
					"Error initializing task editor", e)); //$NON-NLS-1$
		}
	}

	public void setUserStoryData(String key) {
		try {
			this.userStoryData = client.getUserStory(Integer.valueOf(key).intValue());
		} catch (NumberFormatException e) {
			StatusHandler.log(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.ID_PLUGIN,
					"Error formatting user story key", e)); //$NON-NLS-1$
		} catch (RemoteException e) {
			StatusHandler.log(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.ID_PLUGIN,
					"Error retrieving user story: " + key, e)); //$NON-NLS-1$
		}
	}

	public UserStoryData getUserStoryData() {
		return this.userStoryData;
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	public String getFormTitle() {
		return MessageFormat.format(Messages.XPlannerUserStoryEditor_STORY_TITLE, getUserStoryData().getName(),
				getUserStoryData().getId() + "", String.valueOf(getUserStoryData().getActualHours())); // so doesn't get formatted as number with a comma	 //$NON-NLS-1$
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();

		form.setText(getFormTitle());

		final Composite formBody = form.getBody();
		formBody.setLayout(new GridLayout(1, false));

		// hierarchy
		createHierarchySection(toolkit, formBody);

		// description
		createDescriptionSection(toolkit, formBody);

		// data
		createDataSection(toolkit, formBody);

		form.reflow(true);
	}

	private void createHierarchySection(FormToolkit toolkit, final Composite formBody) {
		Section hierarchySection = toolkit.createSection(formBody, ExpandableComposite.TITLE_BAR
				| ExpandableComposite.TWISTIE);
		hierarchySection.setText(Messages.XPlannerTaskEditor_HIERARCHY_SECTION_TITLE);
		hierarchySection.setLayout(new GridLayout(1, true));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(hierarchySection);
		hierarchySection.setExpanded(true);
		Composite hierarchyComposite = toolkit.createComposite(hierarchySection, SWT.NONE);
		GridLayout hierarchyGridLayout = new GridLayout(1, false);
		hierarchyGridLayout.marginWidth = 0;

		hierarchyComposite.setLayout(hierarchyGridLayout);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(hierarchyComposite);

		hierarchySection.setClient(hierarchyComposite);
		Tree hierarchyTree = toolkit.createTree(hierarchyComposite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).minSize(200, 80).applyTo(hierarchyTree);

		TreeItem projectItem = new TreeItem(hierarchyTree, SWT.NONE);
		projectItem.setText(getProjectName());

		TreeItem iterationItem = new TreeItem(projectItem, SWT.NONE);
		iterationItem.setText(getIterationName());

		TreeItem storyItem = new TreeItem(iterationItem, SWT.NONE);
		storyItem.setText(getStoryName());

		hierarchyTree.showItem(storyItem);
	}

	private void createDescriptionSection(FormToolkit toolkit, final Composite formBody) {
		Section descriptionSection = toolkit.createSection(formBody, ExpandableComposite.TITLE_BAR
				| ExpandableComposite.TWISTIE);
		descriptionSection.setText(Messages.XPlannerTaskEditor_DESCRIPTION_SECTION_TITLE);
		descriptionSection.setLayout(new GridLayout(1, true));
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).applyTo(descriptionSection);
		descriptionSection.setExpanded(true);
		Composite descriptionComposite = toolkit.createComposite(descriptionSection, SWT.NONE);
		GridLayout descriptionCompositeLayout = new GridLayout(1, true);
		descriptionCompositeLayout.marginWidth = 0;
		descriptionComposite.setLayout(descriptionCompositeLayout);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(descriptionComposite);

		createDescriptionEditor(descriptionComposite, toolkit);
		descriptionSection.setClient(descriptionComposite);
	}

	private void createDescriptionEditor(Composite composite, FormToolkit toolkit) {
		try {
			AbstractAttributeEditor editor = new RichTextAttributeEditor(createModel(), input.getTaskRepository(),
					repositoryTaskData.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION), SWT.READ_ONLY
							| SWT.BORDER);
			if (editor != null) {
				editor.setReadOnly(true);
				editor.createControl(composite, toolkit);
				GridDataFactory.fillDefaults().grab(true, true).applyTo(editor.getControl());
				toolkit.adapt(editor.getControl(), true, true);
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, XPlannerMylynUIPlugin.ID_PLUGIN,
					"Could not create editor model", e)); //$NON-NLS-1$
		}
	}

	protected TaskDataModel createModel() throws CoreException {
		ITaskDataWorkingCopy taskDataState = TasksUiPlugin.getTaskDataManager().getWorkingCopy(input.getTask());
		return new TaskDataModel(input.getTaskRepository(), input.getTask(), taskDataState);
	}

	private void createDataSection(FormToolkit toolkit, final Composite formBody) {
		Section dataSection = toolkit.createSection(formBody, ExpandableComposite.TITLE_BAR
				| ExpandableComposite.TWISTIE);
		dataSection.setText(Messages.XPlannerTaskEditor_DATA_SECTION_TITLE);
		dataSection.setLayout(new GridLayout(1, true));
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.TOP).applyTo(dataSection);
		dataSection.setExpanded(true);
		Composite dataComposite = toolkit.createComposite(dataSection, SWT.BORDER);
		dataComposite.setLayout(new GridLayout(4, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(dataComposite);

		dataSection.setClient(dataComposite);
		// priority label
		Label priorityLabel = toolkit.createLabel(dataComposite, Messages.XPlannerUserStoryEditor_PRIORITY_LABEL);
		priorityLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		GridDataFactory.fillDefaults().grab(false, false).span(1, 2).applyTo(priorityLabel);

		// priority text
		Label priorityValue = toolkit.createLabel(dataComposite, ""); //$NON-NLS-1$
		GridDataFactory.fillDefaults().grab(true, false).span(1, 2).applyTo(priorityValue);
		priorityValue.setText(String.valueOf(getUserStoryData().getPriority()));

		// estimated hours label
		Label estimatedHoursLabel = toolkit.createLabel(dataComposite, Messages.XPlannerTaskEditor_ESTIMATED_HOURS_TEXT);
		estimatedHoursLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));

		// estimated hours text
		toolkit.createLabel(dataComposite, getUserStoryData().getAdjustedEstimatedHours()
				+ " (" + getUserStoryData().getEstimatedOriginalHours() + ")"); //$NON-NLS-1$ //$NON-NLS-2$

		// actual time label
		Label actualTimeLabel = toolkit.createLabel(dataComposite, Messages.XPlannerTaskEditor_ACTUAL_HOURS_TEXT);
		actualTimeLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));

		// actual time text
		toolkit.createLabel(dataComposite, getUserStoryData().getActualHours() + ""); //$NON-NLS-1$

		// tracker label
		Label trackerLabel = toolkit.createLabel(dataComposite, Messages.XPlannerUserStoryEditor_TRACKER_LABEL);
		trackerLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		GridDataFactory.fillDefaults().grab(false, false).applyTo(trackerLabel);

		// tracker text
		Label trackerValue = toolkit.createLabel(dataComposite, getTrackerName());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(trackerValue);

		// remaining hours label
		Label remainingHoursLabel = toolkit.createLabel(dataComposite,
				Messages.XPlannerUserStoryEditor_REMAINING_HOURS_LABEL);
		remainingHoursLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));

		// remaining hours text
		toolkit.createLabel(dataComposite, String.valueOf(getUserStoryData().getRemainingHours()));

		// last updated time label
		Label lastUpdatedTimeLabel = toolkit.createLabel(dataComposite,
				Messages.XPlannerUserStoryEditor_LAST_UPDATE_LABEL);
		lastUpdatedTimeLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		GridDataFactory.fillDefaults().grab(false, false).applyTo(lastUpdatedTimeLabel);

		// last updated time text
		DateFormat dateFormat = DateFormat.getDateInstance(XPlannerAttributeMapper.DATE_FORMAT_STYLE);
		Label lastUpdatedTimeValue = toolkit.createLabel(dataComposite,
				dateFormat.format(((GregorianCalendar) getUserStoryData().getLastUpdateTime()).getTime()) + ""); //$NON-NLS-1$
		GridDataFactory.fillDefaults().grab(true, false).applyTo(lastUpdatedTimeValue);

		// disposition label
		Label dispositionLabel = toolkit.createLabel(dataComposite, Messages.XPlannerUserStoryEditor_DISPOSITION_LABEL);
		dispositionLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));

		// disposition text
		toolkit.createLabel(dataComposite, getUserStoryData().getDispositionName());

		Button completedButton = toolkit.createButton(dataComposite, Messages.XPlannerTaskEditor_COMPLETED_BUTTON,
				SWT.CHECK);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(completedButton);
		completedButton.setSelection(userStoryData.isCompleted());
		completedButton.setEnabled(false);

//TODO -- no API for status		
//		// status label
//		Label statusLabel = toolkit.createLabel(dataComposite, Messages.XPlannerUserStoryEditor_STATUS_LABEL);
//		statusLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
//		
//		// status text
//		toolkit.createLabel(dataComposite, Messages.XPlannerUserStoryEditor_STATUS_PLACEHOLDER); //TODO -- getUserStoryData().getDispositionName()));   

	}

	private String getProjectName() {
		String projectName = NO_PROJECT_NAME;

		try {
			IterationData iteration = client.getIteration(userStoryData.getIterationId());
			if (iteration != null) {
				ProjectData project = client.getProject(iteration.getProjectId());
				if (project != null) {
					projectName = project.getName();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return projectName;
	}

	private String getIterationName() {
		String iterationName = NO_ITERATION_NAME;

		try {
			IterationData iteration = client.getIteration(userStoryData.getIterationId());
			if (iteration != null) {
				iterationName = iteration.getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return iterationName;
	}

	private String getStoryName() {
		String storyName = NO_STORY_NAME;

		try {
			storyName = userStoryData.getName();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return storyName;
	}

	private String getTrackerName() {
		String acceptorName = NO_TRACKER_NAME;

		try {
			PersonData personData = client.getPerson(getUserStoryData().getTrackerId());
			if (personData != null) {
				acceptorName = personData.getName();
			}
		} catch (Exception e) { //RemoteException e) {
			e.printStackTrace();
		}

		return acceptorName;
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {
		super.dispose();
		// commentImage.dispose();
	}

}
