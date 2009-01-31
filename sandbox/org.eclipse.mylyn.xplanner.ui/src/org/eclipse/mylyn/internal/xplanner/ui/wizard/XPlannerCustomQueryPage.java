/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.xplanner.ui.wizard;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylyn.internal.xplanner.core.service.XPlannerClient;
import org.eclipse.mylyn.internal.xplanner.ui.XPlannerAttributeMapper;
import org.eclipse.mylyn.internal.xplanner.ui.XPlannerClientFacade;
import org.eclipse.mylyn.internal.xplanner.ui.XPlannerTaskListMigrator;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.xplanner.soap.IterationData;
import org.xplanner.soap.ProjectData;
import org.xplanner.soap.TaskData;
import org.xplanner.soap.UserStoryData;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerCustomQueryPage extends AbstractXPlannerQueryWizardPage implements MultipleQueryPage {

	private static final boolean DEFAULT_IS_USE_TASKS = true;

	private static final boolean DEFAULT_SELECT_MY_CURRENT_TASKS = true;

	private final XPlannerClient client;

	private Text nameText;

	private TreeViewer projectsViewer;

	private Button allTasksOrStoriesButton;

	private Button myTasksOrStoriesButton;

	private Button tasksButton;

	private Button storiesButton;

	private Button myTasksButton;

	private Button selectedTasksButton;

	private Label queryNameLabel;

	private Label projectTreeLabel;

	private Label typeLabel;

	private Label scopeLabel;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public XPlannerCustomQueryPage(TaskRepository repository, IRepositoryQuery existingQuery) {
		super(repository, existingQuery);
		try {
			this.client = XPlannerClientFacade.getDefault().getXPlannerClient(repository);
			setPageComplete(false);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}

	}

	public void createControl(Composite parent) {
		Composite dataComposite = new Composite(parent, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		dataComposite.setLayoutData(gd);

		dataComposite.setLayout(new GridLayout());

		createNameControls(dataComposite);
		createMyTasksControls(dataComposite);

		Composite detailsComposite = new Composite(dataComposite, SWT.NONE);
		detailsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout detailsLayout = new GridLayout(2, false);
		detailsLayout.marginTop = 0;
		detailsLayout.marginLeft = 10;
		detailsLayout.marginHeight = 0;
		detailsComposite.setLayout(detailsLayout);
		createProjectsView(detailsComposite);
		createButtons(detailsComposite);

		if (getExistingQuery() == null) {
			loadFromDefaults();
		} else {
			loadFromExistingQuery();
		}

		updateSelectionControls();
//	don't validate in beginning, so first time dialog comes up, no error message		
//		validatePage();

		if (getSearchContainer() != null) {
			getSearchContainer().setPerformActionEnabled(isPageComplete());
		}

		setControl(dataComposite);
	}

	protected XPlannerClient getClient() {
		return this.client;
	}

	protected boolean isContentTypeTask() {
		boolean isContentTypeTask = DEFAULT_IS_USE_TASKS;

		if (tasksButton == null) {
			if (getExistingQuery() != null) {
				isContentTypeTask = XPlannerTaskListMigrator.isUseTasks(getExistingQuery());
			}
		} else {
			isContentTypeTask = tasksButton.getSelection();
		}

		return isContentTypeTask;
	}

	private void createNameControls(Composite parent) {
		if (inSearchContainer()) {
			return;
		}

		Composite nameComposite = new Composite(parent, SWT.NONE);
		nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		GridLayout nameLayout = new GridLayout(2, false);
		nameLayout.marginWidth = 0;
		nameComposite.setLayout(nameLayout);
		queryNameLabel = new Label(nameComposite, SWT.NONE);
		queryNameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		queryNameLabel.setText(Messages.XPlannerCustomQueryPage_QUERY_NAME);

		nameText = new Text(nameComposite, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validatePage();
			}
		});
		nameText.setFocus();
	}

	private void createMyTasksControls(Composite parent) {
		Composite myTasksComposite = new Composite(parent, SWT.NONE);
		myTasksComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		GridLayout myTasksGridLayout = new GridLayout(1, false);
		myTasksGridLayout.marginWidth = 0;
		myTasksGridLayout.marginTop = 5;
		myTasksGridLayout.marginBottom = 0;
		myTasksGridLayout.marginHeight = 0;
		myTasksComposite.setLayout(myTasksGridLayout);

		myTasksButton = new Button(myTasksComposite, SWT.RADIO);
		myTasksButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		myTasksButton.setText(Messages.XPlannerCustomQueryPage_ALL_MY_CURRENT_TASKS);

		selectedTasksButton = new Button(myTasksComposite, SWT.RADIO);
		selectedTasksButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		selectedTasksButton.setText(Messages.XPlannerCustomQueryPage_SELECTED_TASKS);

		// if user selects or unselects tasks, need to enable/disable other controls
		myTasksButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updateSelectionControls();
				validatePage();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// if user selects or unselects tasks, need to enable/disable other controls
		selectedTasksButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updateSelectionControls();
				validatePage();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

	}

	private void updateSelectionControls() {
		boolean getMyCurrentTasks = myTasksButton.getSelection();

		projectTreeLabel.setEnabled(!getMyCurrentTasks);
		projectsViewer.getTree().setEnabled(!getMyCurrentTasks);
		typeLabel.setEnabled(!getMyCurrentTasks);
		tasksButton.setEnabled(!getMyCurrentTasks);
		storiesButton.setEnabled(!getMyCurrentTasks);
		scopeLabel.setEnabled(!getMyCurrentTasks);
		allTasksOrStoriesButton.setEnabled(!getMyCurrentTasks);
		myTasksOrStoriesButton.setEnabled(!getMyCurrentTasks);

		if (!getMyCurrentTasks && projectsViewer.getExpandedElements().length == 0) {
			projectsViewer.expandToLevel(2);
		}
	}

	private void createProjectsView(Composite parent) {
		Composite projectsComposite = new Composite(parent, SWT.NONE);
		projectsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout projectsLayout = new GridLayout(1, false);
		projectsLayout.marginTop = 0;
		projectsLayout.marginHeight = 0;
		projectsComposite.setLayout(projectsLayout);
		projectTreeLabel = new Label(projectsComposite, SWT.NONE);
		projectTreeLabel.setData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		projectTreeLabel.setText(Messages.XPlannerCustomQueryPage_PROJECTS_TREE_TITLE);

		projectsViewer = new TreeViewer(projectsComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
		projectsViewer.setContentProvider(new ProjectsViewerContentProvider(client));
		projectsViewer.setLabelProvider(new ProjectsViewerLabelProvider());
		GridData projectsViewerGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		projectsViewerGridData.heightHint = 100;
		projectsViewerGridData.widthHint = 200;

		projectsViewer.getTree().setLayoutData(projectsViewerGridData);
		projectsViewer.setInput(client);
		projectsViewer.refresh();
		projectsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				ensureSingleTypeSelected(e);
				validatePage();
			}

		});
	}

	@SuppressWarnings("unchecked")
	private void ensureSingleTypeSelected(SelectionChangedEvent e) {
		StructuredSelection selection = (StructuredSelection) e.getSelection();
		ArrayList selectedElements = new ArrayList(selection.toList());
		if (selectedElements.size() > 1) {
			Object firstElement = selection.getFirstElement();
			int originalSelectionSize = selection.size();
			for (Iterator iter = selectedElements.iterator(); iter.hasNext();) {
				Object element = iter.next();

				if (!element.getClass().equals(firstElement.getClass())) {
					iter.remove();
				}
			}
			if (selectedElements.size() < originalSelectionSize) {
				e.getSelectionProvider().setSelection(new StructuredSelection(selectedElements));
			}
		}
	}

	private void createButtons(Composite parent) {
		Composite buttonsComposite = new Composite(parent, SWT.NONE);
		buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		GridLayout buttonsLayout = new GridLayout();
		buttonsLayout.marginTop = 0;
		buttonsLayout.marginHeight = 0;
		buttonsComposite.setLayout(buttonsLayout);
		createTypeButtons(buttonsComposite);
		createScopeButtons(buttonsComposite);
	}

	private void createTypeButtons(Composite parent) {
		Composite typeComposite = new Composite(parent, SWT.NONE);
		typeComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		GridLayout typeLayout = new GridLayout();
		typeLayout.marginTop = 0;
		typeLayout.marginHeight = 0;
		typeComposite.setLayout(typeLayout);
		typeLabel = new Label(typeComposite, SWT.NONE);
		typeLabel.setData(new GridData(SWT.BEGINNING, SWT.BEGINNING));
		typeLabel.setText(Messages.XPlannerCustomQueryPage_GROUPING_TITLE);

		tasksButton = new Button(typeComposite, SWT.RADIO);
		GridData tasksLayoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		tasksLayoutData.horizontalIndent = 10;
		tasksButton.setLayoutData(tasksLayoutData);
		tasksButton.setText(Messages.XPlannerCustomQueryPage_TASKS_BUTTON);
		// if user selects or unselects tasks, need to update tree to show stories or not
		tasksButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				refreshProjects();
				validatePage();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		tasksButton.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				validatePage();
			}
		});

		storiesButton = new Button(typeComposite, SWT.RADIO);
		storiesButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		storiesButton.setText(Messages.XPlannerCustomQueryPage_USER_STORIES_BUTTON);
		GridData storiesLayoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		storiesLayoutData.horizontalIndent = 10;
		storiesButton.setLayoutData(storiesLayoutData);

		// if user selects or unselects tasks, need to update tree to show stories or not
		storiesButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				refreshProjects();
				validatePage();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		storiesButton.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				validatePage();
			}
		});

	}

	protected void addContentTypeListener(SelectionListener listener) {
		tasksButton.addSelectionListener(listener);
	}

	protected void removeContentTypeListener(SelectionListener listener) {
		tasksButton.removeSelectionListener(listener);
	}

	private void createScopeButtons(Composite parent) {
		Composite scopeComposite = new Composite(parent, SWT.NONE);
		scopeComposite.setLayout(new GridLayout());
		GridData scopeCompositeLayoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		scopeCompositeLayoutData.verticalIndent = 10;
		scopeComposite.setLayoutData(scopeCompositeLayoutData);
		scopeLabel = new Label(scopeComposite, SWT.NONE);
		scopeLabel.setData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		scopeLabel.setText(Messages.XPlannerCustomQueryPage_SCOPE_LABEL);

		allTasksOrStoriesButton = new Button(scopeComposite, SWT.RADIO);
		GridData allTasksOrStoriesLayoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		allTasksOrStoriesLayoutData.horizontalIndent = 10;
		allTasksOrStoriesButton.setLayoutData(allTasksOrStoriesLayoutData);
		allTasksOrStoriesButton.setText(Messages.XPlannerCustomQueryPage_ALL_BUTTON);
		myTasksOrStoriesButton = new Button(scopeComposite, SWT.RADIO);
		GridData myTasksOrStoriesLayoutData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		myTasksOrStoriesLayoutData.horizontalIndent = 10;
		myTasksOrStoriesButton.setLayoutData(myTasksOrStoriesLayoutData);
		myTasksOrStoriesButton.setText(Messages.XPlannerCustomQueryPage_MY_BUTTON);

	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public IWizardPage getNextPage() {
		return null;
	}

	public void validatePage() {
		String errorMessage = null;
		// need query name
		if (getQueryTitle().length() == 0) {
			errorMessage = Messages.XPlannerCustomQueryPage_QUERY_NAME_NEEDED;
		}

		if (errorMessage == null && selectedTasksButton.getSelection()) {
			StructuredSelection selection = (StructuredSelection) projectsViewer.getSelection();
			if (selection == null || selection.isEmpty()) {
				errorMessage = Messages.XPlannerCustomQueryPage_PROJECT_ELEMENT_NEEDED;
			}
		}

		setErrorMessage(errorMessage);
		setPageComplete(errorMessage == null);
		if (getSearchContainer() != null) {
			getSearchContainer().setPerformActionEnabled(isPageComplete());
		}
	}

	@Override
	public boolean isPageComplete() {
		boolean complete = getErrorMessage() == null;

		if (complete) {
			if (nameText != null && nameText.getText().length() > 0) {
				complete = true;
				setErrorMessage(null);
			} else if (!inSearchContainer()) {
				complete = false;
				setErrorMessage(Messages.XPlannerCustomQueryPage_QUERY_NAME_NEEDED);
			}
		}

		return complete;
	}

	private void refreshProjects() {
		projectsViewer.refresh();
	}

	private void loadFromDefaults() {
		myTasksButton.setSelection(DEFAULT_SELECT_MY_CURRENT_TASKS);
		tasksButton.setSelection(true);
		myTasksOrStoriesButton.setSelection(true);
		projectsViewer.expandToLevel(2);
	}

	private void loadFromExistingQuery() {
		// name
		if (getExistingQuery().getSummary() != null) {
			nameText.setText(getExistingQuery().getSummary());
		}

		boolean isMyCurrentTasks = XPlannerTaskListMigrator.isMyCurrentTasks(getExistingQuery());
		myTasksButton.setSelection(isMyCurrentTasks);
		selectedTasksButton.setSelection(!isMyCurrentTasks);

		// tasks or user stories?
		int personId = XPlannerTaskListMigrator.getPersonId(getExistingQuery());
		if (personId != XPlannerAttributeMapper.INVALID_ID) {
			myTasksOrStoriesButton.setSelection(true);
		} else {
			allTasksOrStoriesButton.setSelection(true);
		}

		// use tasks
		boolean isUseTasks = XPlannerTaskListMigrator.isUseTasks(getExistingQuery());
		if (isUseTasks) {
			tasksButton.setSelection(true);
		} else {
			storiesButton.setSelection(true);
		}

		// select appropriate tree item
		List<Integer> contentIds = XPlannerTaskListMigrator.getContentIds(getExistingQuery());
		if (contentIds != XPlannerTaskListMigrator.INVALID_IDS) {
			List<Object> selection = getProjectElementsToSelect();

			projectsViewer.setSelection(new StructuredSelection(selection));
		}
	}

	private List<Object> getProjectElementsToSelect() {
		ArrayList<Object> selection = new ArrayList<Object>();

		XPlannerTaskListMigrator.ContentIdType contentIdType = XPlannerTaskListMigrator.getContentIdType(getExistingQuery());
		List<Integer> contentIds = XPlannerTaskListMigrator.getContentIds(getExistingQuery());

		for (int contentId : contentIds) {
			try {
				if (contentIdType == XPlannerTaskListMigrator.ContentIdType.PROJECT) {
					selection.add(client.getProject(contentId));
				} else if (contentIdType == XPlannerTaskListMigrator.ContentIdType.ITERATION) {
					selection.add(client.getIteration(contentId));
				} else if (contentIdType == XPlannerTaskListMigrator.ContentIdType.USER_STORY) {
					selection.add(client.getUserStory(contentId));
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		return selection;
	}

	//TODO -- should be in client interface
	private boolean isUseAll() {
		return allTasksOrStoriesButton.getSelection();
	}

	void applyChanges(IRepositoryQuery query) {
		if (query == null) {
			return;
		}
		// name
		query.setSummary(getQueryTitle());

		// my current tasks?
		if (myTasksButton.getSelection()) {
			XPlannerTaskListMigrator.setMyCurrentTasks(query, true);
		} else {
			XPlannerTaskListMigrator.setMyCurrentTasks(query, false);

			// use tasks?
			XPlannerTaskListMigrator.setUseTasks(query, tasksButton.getSelection());

			// use all?
			if (!isUseAll()) {
				XPlannerTaskListMigrator.setPersonId(query, client.getCurrentPersonId());
			} else {
				XPlannerTaskListMigrator.setPersonId(query, XPlannerAttributeMapper.INVALID_ID);
			}

			// content id
			XPlannerTaskListMigrator.setContentIds(query, getSelectedContentIds());

			// content id type
			XPlannerTaskListMigrator.setContentIdType(query, getSelectedContentIdType());
		}
	}

	private XPlannerTaskListMigrator.ContentIdType getSelectedContentIdType() {
		XPlannerTaskListMigrator.ContentIdType contentIdType = XPlannerTaskListMigrator.ContentIdType.USER_STORY;

		StructuredSelection selection = (StructuredSelection) projectsViewer.getSelection();
		Object selectedElement = selection.getFirstElement();

		if (selectedElement instanceof ProjectData) {
			contentIdType = XPlannerTaskListMigrator.ContentIdType.PROJECT;
		} else if (selectedElement instanceof IterationData) {
			contentIdType = XPlannerTaskListMigrator.ContentIdType.ITERATION;
		} else if (selectedElement instanceof UserStoryData) {
			contentIdType = XPlannerTaskListMigrator.ContentIdType.USER_STORY;
		}

		return contentIdType;
	}

	@SuppressWarnings("unchecked")
	private List<Integer> getSelectedContentIds() {
		ArrayList<Integer> selectedIds = new ArrayList<Integer>();
		StructuredSelection selection = (StructuredSelection) projectsViewer.getSelection();

		if (selection.size() == 0) {
			selectedIds.add(XPlannerAttributeMapper.INVALID_ID);
		} else {
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object selectedElement = iter.next();

				if (selectedElement instanceof ProjectData) {
					selectedIds.add(((ProjectData) selectedElement).getId());
				} else if (selectedElement instanceof IterationData) {
					selectedIds.add(((IterationData) selectedElement).getId());
				} else if (selectedElement instanceof UserStoryData) {
					selectedIds.add(((UserStoryData) selectedElement).getId());
				}
			}
		}

		return selectedIds;
	}

//TODO -- no longer used -- remove if really unnecessary	
//	private UserStoryData getSelectedUserStory() {
//    UserStoryData userStory = null;
//    
//		StructuredSelection selection = (StructuredSelection)projectsViewer.getSelection();
//		Object selectedElement = selection.getFirstElement();
//
//		if (selectedElement instanceof UserStoryData) {
//			int userStoryId = ((UserStoryData)selectedElement).getId();
//			if (userStoryId != XPlannerCustomQuery.INVALID_ID) {
//				try {
//					userStory = client.getUserStory(userStoryId);
//				}
//				catch (RemoteException e) {
//					e.printStackTrace();
//				}
//			}	
//		}
//		
//		return userStory;
//	}

	@Override
	public String getQueryTitle() {
		return (nameText != null) ? nameText.getText() : "<search>"; //$NON-NLS-1$
	}

	@Override
	public IRepositoryQuery getQuery() {
		if (getExistingQuery() == null) {
			setExistingQuery(TasksUi.getRepositoryModel().createRepositoryQuery(getRepository()));
		}

		applyChanges(getExistingQuery());
		return getExistingQuery();
	}

	/**
	 * if creating user story query, create a set of queries for the user story tasks
	 * 
	 * @return
	 */
	public List<IRepositoryQuery> getQueries() {
		List<IRepositoryQuery> queries = new ArrayList<IRepositoryQuery>();

		if (isContentTypeTask()) {
			// if don't have existing query, create one
			if (getExistingQuery() == null) {
				setExistingQuery(TasksUi.getRepositoryModel().createRepositoryQuery(getRepository()));
			}

			applyChanges(getExistingQuery());
			queries.add(getExistingQuery());
		} else {
			// existing query will get deleted in wizard's performFinish()
			queries = createTaskQueriesForUserStories(getSelectedUserStories());
		}

		return queries;
	}

	private List<UserStoryData> getSelectedUserStories() {
		ArrayList<UserStoryData> userStories = new ArrayList<UserStoryData>();

		StructuredSelection selection = (StructuredSelection) projectsViewer.getSelection();
		Object selectedElement = selection.getFirstElement();

		try {
			if (selectedElement instanceof ProjectData) {
				IterationData[] iterations = client.getIterations(((ProjectData) selectedElement).getId());
				for (IterationData iteration : iterations) {
					userStories.addAll(Arrays.asList(client.getUserStories(iteration.getId())));
				}
			} else if (selectedElement instanceof IterationData) {
				userStories.addAll(Arrays.asList(client.getUserStories(((IterationData) selectedElement).getId())));
			} else if (selectedElement instanceof UserStoryData) {
				userStories.add((UserStoryData) selectedElement);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return userStories;
	}

	public List<IRepositoryQuery> createTaskQueriesForUserStories(List<UserStoryData> userStories) {

		if (userStories == null || userStories.size() == 0) {
			System.err.println(Messages.XPlannerCustomQueryPage_NO_USER_STORIES_SELECTED);
			return new ArrayList<IRepositoryQuery>();
		}

		ArrayList<IRepositoryQuery> queries = new ArrayList<IRepositoryQuery>();
		int personId = client.getCurrentPersonId();
		for (UserStoryData userStory : userStories) {
			boolean createQuery = true;
			// if want user specific tasks, only create queries for stories with tasks for
			// that person
			if (!isUseAll()) {
				TaskData[] personUserStoryTasks = client.getUserStoryTasksForPerson(personId, userStory.getId());
				if (personUserStoryTasks == null || personUserStoryTasks.length == 0) {
					createQuery = false;
				}
			}

			if (createQuery) {
				String nameSuffix = Messages.XPlannerCustomQueryPage_USER_STORY + userStory.getName();
				String queryName = getQueryTitle();
				if (!queryName.contains(nameSuffix)) {
					queryName += nameSuffix;
				}

				IRepositoryQuery query = TasksUi.getRepositoryModel().createRepositoryQuery(getTaskRepository());

				applyChanges(query);
				query.setSummary(queryName);
				XPlannerTaskListMigrator.setContentIds(query, Arrays.asList(new Integer[] { userStory.getId() }));
				XPlannerTaskListMigrator.setContentIdType(query, XPlannerTaskListMigrator.ContentIdType.USER_STORY);
				XPlannerTaskListMigrator.setUseTasks(query, true);
				queries.add(query);
			}
		}

		return queries;
	}

	public TreeViewer getProjectsViewer() {
		return this.projectsViewer;
	}

	@Override
	public void setControlsEnabled(boolean enabled) {
		super.setControlsEnabled(enabled);
		updateSelectionControls();
		validatePage();
	}

	@Override
	public void applyTo(IRepositoryQuery query) {
		applyChanges(query);
	}

}
