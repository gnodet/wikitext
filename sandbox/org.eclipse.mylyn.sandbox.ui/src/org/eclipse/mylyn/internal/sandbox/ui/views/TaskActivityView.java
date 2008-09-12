/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.views;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonThemes;
import org.eclipse.mylyn.internal.sandbox.ui.planner.ActivityReportAction;
import org.eclipse.mylyn.internal.sandbox.ui.planner.TaskActivityLabelProvider;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.DateRange;
import org.eclipse.mylyn.internal.tasks.core.ITaskListChangeListener;
import org.eclipse.mylyn.internal.tasks.core.ScheduledTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.TaskContainerDelta;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.OpenTaskListElementAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskActivationListener;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.core.TaskActivationAdapter;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Rob Elves
 */
public class TaskActivityView extends ViewPart {

	private static final String MEMENTO_KEY_WIDTH = "colwidth";

	public static final String ID = "org.eclipse.mylyn.tasklist.activity";

	public static final String columnWidthIdentifier = "org.eclipse.mylyn.tasklist.ui.views.taskactivity.columnwidth";

	public static final String tableSortIdentifier = "org.eclipse.mylyn.tasklist.ui.views.taskactivity.sortIndex";

	private static TaskActivityView INSTANCE;

	private IMemento taskActivityMemento;

	private OpenTaskListElementAction openTaskEditor;

	private final String[] columnNames = new String[] { " ", " !", "Description", "Elapsed", "Estimated", "Scheduled",
	/*"Last Active"*/};

	private final String[] ESTIMATE_TIMES = new String[] { "0 Hours", "1 Hours", "2 Hours", "3 Hours", "4 Hours",
			"5 Hours", "6 Hours", "7 Hours", "8 Hours", "9 Hours", "10 Hours" };

	private final int[] columnWidths = new int[] { 60, 12, 160, 60, 70, 100 /*, 100*/};

	private TreeColumn[] columns;

	private TaskActivityLabelProvider taskHistoryTreeLabelProvider;

	private TreeViewer treeViewer;

	private TaskActivityViewContentProvider taskActivityTableContentProvider;

//	private TaskScheduleContentProvider taskActivityTableContentProvider;

	private IThemeManager themeManager;

	private final IPropertyChangeListener THEME_CHANGE_LISTENER = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(IThemeManager.CHANGE_CURRENT_THEME)
					|| event.getProperty().equals(CommonThemes.COLOR_CATEGORY)) {
				taskHistoryTreeLabelProvider.setCategoryBackgroundColor(themeManager.getCurrentTheme()
						.getColorRegistry()
						.get(CommonThemes.COLOR_CATEGORY));
				refresh();
			}
		}
	};

	/**
	 * TODO: need lazier refresh policy.
	 */
	private final ITaskActivationListener TASK_ACTIVATION_LISTENER = new TaskActivationAdapter() {

		@Override
		public void taskActivated(ITask task) {
			refresh();
		}
	};

	private final ITaskListChangeListener TASKLIST_CHANGE_LISTENER = new ITaskListChangeListener() {

		public void containersChanged(Set<TaskContainerDelta> containers) {
			refresh();
		}

	};

	private TaskActivityViewSorter sorter;

	public static TaskActivityView openInActivePerspective() {
		try {
			return (TaskActivityView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ID);
		} catch (Exception e) {
			return null;
		}
	}

	public TaskActivityView() {
		INSTANCE = this;
		TasksUi.getTaskActivityManager().addActivationListener(TASK_ACTIVATION_LISTENER);
		TasksUiInternal.getTaskList().addChangeListener(TASKLIST_CHANGE_LISTENER);
	}

	@Override
	public void dispose() {
		super.dispose();
		TasksUi.getTaskActivityManager().removeActivationListener(TASK_ACTIVATION_LISTENER);
		TasksUiInternal.getTaskList().removeChangeListener(TASKLIST_CHANGE_LISTENER);
	}

	@Override
	public void createPartControl(Composite parent) {
		themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		themeManager.addPropertyChangeListener(THEME_CHANGE_LISTENER);

		int treeStyle = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;
		treeViewer = new TreeViewer(parent, treeStyle);

		getViewer().getTree().setHeaderVisible(true);
		getViewer().getTree().setLinesVisible(true);
		getViewer().setColumnProperties(columnNames);
		getViewer().setUseHashlookup(true);

		columns = new TreeColumn[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			columns[i] = new TreeColumn(getViewer().getTree(), SWT.LEFT);
			columns[i].setText(columnNames[i]);
			columns[i].setWidth(columnWidths[i]);

			final int index = i;
			columns[i].addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					int column = index - 1;
					if (column == sorter.getSortColumn()) {
						sorter.reverseDirection();
					} else {
						sorter.setSortColumn(column);
					}
					getViewer().refresh();
					// if(taskActivityMemento != null) {
					// sorter.saveState(taskActivityMemento);
					// }
				}
			});

			columns[i].addControlListener(new ControlListener() {
				public void controlResized(ControlEvent e) {
					for (int j = 0; j < columnWidths.length; j++) {
						if (columns[j].equals(e.getSource())) {
							columnWidths[j] = columns[j].getWidth();
						}
					}
				}

				public void controlMoved(ControlEvent e) {
					// don't care if the control is moved
				}
			});
		}

		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		Color categoryBackground = themeManager.getCurrentTheme().getColorRegistry().get(CommonThemes.COLOR_CATEGORY);

		sorter = new TaskActivityViewSorter();
		getViewer().setSorter(sorter);
		taskActivityTableContentProvider = new TaskActivityViewContentProvider(TasksUiPlugin.getTaskActivityManager());
		taskHistoryTreeLabelProvider = new TaskActivityLabelProvider(new TaskElementLabelProvider(false),
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), categoryBackground,
				taskActivityTableContentProvider);

		getViewer().setContentProvider(taskActivityTableContentProvider);
		getViewer().setLabelProvider(taskHistoryTreeLabelProvider);
		getViewer().setInput(getViewSite());
		restoreState();
		createCellEditorListener();
		makeActions();
		initDrop();
		hookOpenAction();
		hookContextMenu();
		contributeToActionBars();
		getSite().setSelectionProvider(getViewer());
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(new ActivityReportAction());
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void initDrop() {
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };

		treeViewer.addDropSupport(DND.DROP_MOVE, types, new ViewerDropAdapter(treeViewer) {
			{
				setFeedbackEnabled(false);
			}

			@Override
			public boolean performDrop(Object data) {

				IStructuredSelection selection = ((IStructuredSelection) TaskListView.getFromActivePerspective()
						.getViewer()
						.getSelection());

				Object target = getCurrentTarget();
				ScheduledTaskContainer container;
				DateRange reminderCalendar;
				if (target instanceof ScheduledTaskContainer) {
					container = (ScheduledTaskContainer) target;
					reminderCalendar = container.getDateRange();
				} else if (target instanceof ITask) {
					reminderCalendar = ((AbstractTask) target).getScheduledForDate();
				} else {
					return false;
				}

				for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
					Object selectedObject = iter.next();
					AbstractTask task = null;
					if (selectedObject instanceof ITask) {
						task = (AbstractTask) selectedObject;
					}
					if (task != null) {
						TasksUiPlugin.getTaskActivityManager().setScheduledFor(task, reminderCalendar);
					}
				}
				return true;
			}

			@Override
			public boolean validateDrop(Object targetObject, int operation, TransferData transferType) {
				Object selectedObject = ((IStructuredSelection) TaskListView.getFromActivePerspective()
						.getViewer()
						.getSelection()).getFirstElement();

				if (selectedObject instanceof ITaskContainer) {
					return false;
				}

				Object target = getCurrentTarget();
				ScheduledTaskContainer dateRangeContainer = null;
				if (target instanceof ScheduledTaskContainer) {
					dateRangeContainer = (ScheduledTaskContainer) target;
				} else if (target instanceof ITask) {
					AbstractTask task = ((AbstractTask) target);
					dateRangeContainer = new ScheduledTaskContainer(TasksUiPlugin.getTaskActivityManager(),
							task.getScheduledForDate());
				}

				if (dateRangeContainer != null && (dateRangeContainer.isPresent() || dateRangeContainer.isFuture())) {
					return true;
				}
				return false;
			}
		});
	}

	private void makeActions() {
		openTaskEditor = new OpenTaskListElementAction(this.getViewer());
		// openUrlInExternal = new OpenTaskInExternalBrowserAction();
	}

	private void hookOpenAction() {
		getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				openTaskEditor.run();
			}
		});
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TaskActivityView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(getViewer().getControl());
		getViewer().getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, getViewer());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	public static TaskActivityView getDefault() {
		return INSTANCE;
	}

	private TreeViewer getViewer() {
		return treeViewer;
	}

	private void refresh() {
		if (PlatformUI.getWorkbench() != null && !PlatformUI.getWorkbench().getDisplay().isDisposed()) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (getViewer().getControl() != null && !getViewer().getControl().isDisposed()) {
						TaskActivityView.this.treeViewer.refresh();
					}
				}
			});
		}
	}

	public ITask getSelectedTask() {
		ISelection selection = getViewer().getSelection();
		if (selection.isEmpty()) {
			return null;
		}
		if (selection instanceof StructuredSelection) {
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Object element = structuredSelection.getFirstElement();
			if (element instanceof ITask) {
				return (ITask) structuredSelection.getFirstElement();
			}
		}
		return null;
	}

	@Override
	public void setFocus() {
		// ignore
	}

	private void createCellEditorListener() {
		CellEditor[] editors = new CellEditor[columnNames.length];
		final ComboBoxCellEditor estimateEditor = new ComboBoxCellEditor(treeViewer.getTree(), ESTIMATE_TIMES,
				SWT.READ_ONLY);
//		final ReminderCellEditor reminderEditor = new ReminderCellEditor(treeViewer.getTree());
		editors[0] = null; // not used
		editors[1] = null;// not used
		editors[2] = null;// not used
		editors[3] = null;// not used
		editors[4] = estimateEditor;
		editors[5] = null;//reminderEditor;
//		reminderEditor.addListener(new ICellEditorListener() {
//			public void applyEditorValue() {
//				Object selection = ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();
//				if (selection instanceof ITask) {
//					Date newReminder = reminderEditor.getReminderDate();
//					if (newReminder != null) {
//						TasksUiPlugin.getTaskActivityManager().setScheduledFor(
//								dateRangeActivityDelegate.getCorrespondingTask(), newReminder);
//					}
//				}
//			}
//
//			public void cancelEditor() {
//			}
//
//			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
//			}
//
//		});
		estimateEditor.addListener(new ICellEditorListener() {
			public void applyEditorValue() {
				Object selection = ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();
				if (selection instanceof AbstractTask) {
					AbstractTask task = (AbstractTask) selection;
					int estimate = (Integer) estimateEditor.getValue();
					if (estimate == -1) {
						estimate = 0;
					}
					task.setEstimatedTimeHours(estimate);
					refresh();
				}
			}

			public void cancelEditor() {
			}

			public void editorValueChanged(boolean oldValidState, boolean newValidState) {
			}

		});
		treeViewer.setCellEditors(editors);
		getViewer().setCellModifier(new TaskActivityCellModifier(treeViewer));
	}

	private class TaskActivityCellModifier implements ICellModifier {

		private final TreeViewer treeViewer;

		public TaskActivityCellModifier(TreeViewer tableViewer) {
			this.treeViewer = tableViewer;
		}

		public boolean canModify(Object element, String property) {
			if (element instanceof ITask) {
				int columnIndex = Arrays.asList(columnNames).indexOf(property);
				if (columnIndex == 4 /*|| columnIndex == 5*/) {
					return true;
				}
			}
			return false;
		}

		public Object getValue(Object element, String property) {
			if (element instanceof ITask) {
				AbstractTask task = (AbstractTask) element;
				int columnIndex = Arrays.asList(columnNames).indexOf(property);
				/*if (columnIndex == 5) {
					if (task.getScheduledForDate() != null) {
						return DateFormat.getDateInstance(DateFormat.MEDIUM).format(task.getScheduledForDate());
					} else {
						return null;
					}
				} else*/if (columnIndex == 4) {
					return new Integer(Arrays.asList(ESTIMATE_TIMES).indexOf(task.getEstimatedTimeHours()));
				}
			}
			return null;
		}

		public void modify(Object element, String property, Object value) {
			int columnIndex = Arrays.asList(columnNames).indexOf(property);
			if (element instanceof AbstractTask) {
				AbstractTask task = (AbstractTask) element;
				if (columnIndex == 4) {
					if (value instanceof Integer) {
						task.setEstimatedTimeHours(((Integer) value).intValue() * 10);
						treeViewer.refresh();
					}
				}
			}
		}
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.taskActivityMemento = memento;
	}

	@Override
	public void saveState(IMemento memento) {
		IMemento colMemento = memento.createChild(columnWidthIdentifier);
		for (int i = 0; i < columnWidths.length; i++) {
			IMemento m = colMemento.createChild("col" + i);
			m.putInteger(MEMENTO_KEY_WIDTH, columnWidths[i]);
		}

		sorter.saveState(memento);
	}

	private void restoreState() {

		if (taskActivityMemento != null) {
			IMemento taskActivityWidth = taskActivityMemento.getChild(columnWidthIdentifier);
			if (taskActivityWidth != null) {
				for (int i = 0; i < columnWidths.length; i++) {
					IMemento m = taskActivityWidth.getChild("col" + i);
					if (m != null) {
						int width = m.getInteger(MEMENTO_KEY_WIDTH);
						columnWidths[i] = width;
						columns[i].setWidth(width);
					}
				}
			}

			sorter.restoreState(taskActivityMemento);

		}
		getViewer().refresh();
	}
}
