/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui;

import java.util.Arrays;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiPrefContstants;
import org.eclipse.mylyn.internal.context.ui.Highlighter;
import org.eclipse.mylyn.internal.context.ui.HighlighterImageDescriptor;
import org.eclipse.mylyn.internal.context.ui.HighlighterList;
import org.eclipse.mylyn.internal.java.ui.InterestInducingProblemListener;
import org.eclipse.mylyn.internal.java.ui.JavaUiBridgePlugin;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Ken Sueda
 * @author Mik Kersten
 * @author Eugene Kuleshov
 */
public class SandboxUiPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, ICellEditorListener {

	private Table table;

	private TableViewer tableViewer;

	private Button enableErrorInterest = null;

	private ColorCellEditor colorDialogEditor;

	private Button incomingOverlaysButton = null;

	private Button activateOnOpen;

	private Button showTaskTrimButton;
	
	private Highlighter selection = null;

	private HighlighterContentProvider contentProvider = null;


	private static final String LABEL_COLUMN = "Label";

	private static final String COLOR_COLUMN = "Color";

	private static final String TYPE_COLUMN = "Type";

	private static String[] columnNames = new String[] { LABEL_COLUMN, COLOR_COLUMN, TYPE_COLUMN, };

	static final String[] TYPE_ARRAY = { "Gradient", "Solid" };

	public SandboxUiPreferencePage() {
		super();
		setPreferenceStore(ContextUiPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		createTaskNavigationGroup(container);
		createTaskListGroup(container);
		createJavaGroup(container);
		
		createHighlightersTable(container);
		createTableViewer();
		
		contentProvider = new HighlighterContentProvider();
		tableViewer.setContentProvider(contentProvider);
		tableViewer.setLabelProvider(new HighlighterLabelProvider());
		tableViewer.setInput(ContextUiPlugin.getDefault().getHighlighterList());

		return container;
	}

	public void init(IWorkbench workbench) {
		// ignore
	}

	private void createTaskNavigationGroup(Composite parent) {
		Group navigationGroup = new Group(parent, SWT.NONE);
		navigationGroup.setText("Task Navigation");
		navigationGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		navigationGroup.setLayout(new GridLayout());
		
		IPreferenceStore uiPreferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
		
		showTaskTrimButton = new Button(navigationGroup, SWT.CHECK);
		showTaskTrimButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		showTaskTrimButton.setText("Show Task Trim widget");
		showTaskTrimButton.setSelection(uiPreferenceStore.getBoolean(
				TasksUiPreferenceConstants.SHOW_TRIM));
	}
	
	private void createTaskListGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Task List");
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		IPreferenceStore uiPreferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
		
		incomingOverlaysButton = new Button(group, SWT.CHECK);
		incomingOverlaysButton.setText("Use Synchronize View style incoming overlays and placement");
		incomingOverlaysButton.setSelection(uiPreferenceStore.getBoolean(
				TasksUiPreferenceConstants.OVERLAYS_INCOMING_TIGHT));
		
		activateOnOpen = new Button(group, SWT.CHECK);
		activateOnOpen.setLayoutData(new GridData());
		activateOnOpen.setText("Activate tasks on open");
		activateOnOpen.setSelection(uiPreferenceStore.getBoolean(TasksUiPreferenceConstants.ACTIVATE_WHEN_OPENED));
	}

	private void createJavaGroup(Composite parent) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		group.setText("Java");
		GridLayout gl = new GridLayout(1, false);
		group.setLayout(gl);

		enableErrorInterest = new Button(group, SWT.CHECK);
		enableErrorInterest.setText("Enable predicted interest of errors (significantly increases view refresh).");
		enableErrorInterest.setSelection(JavaUiBridgePlugin.getDefault().getPreferenceStore().getBoolean(
				InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS));
	}

	private void createHighlightersTable(Composite parent) {
		Group tableComposite = new Group(parent, SWT.SHADOW_ETCHED_IN);
		tableComposite.setText("Context Highlighters");
		tableComposite.setLayout(new GridLayout(2, false));
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		int style = SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		table = new Table(tableComposite, style);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		table.setLayoutData(gridData);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// 1st column with Label
		TableColumn column = new TableColumn(table, SWT.NONE, 0);
		column.setText("Label");
		column.setWidth(150);
		column.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new HighlighterTableSorter(HighlighterTableSorter.LABEL));
			}
		});

		// 2nd column with highlighter Description
		column = new TableColumn(table, SWT.LEAD, 1);
		column.setResizable(false);
		column.setText("Color");
		column.setWidth(100);
		column.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new HighlighterTableSorter(HighlighterTableSorter.COLOR));
			}
		});

		// 3rd column with Type
		column = new TableColumn(table, SWT.LEAD, 2);
		column.setResizable(false);
		column.setText("Kind");
		column.setWidth(80);
		column.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new HighlighterTableSorter(HighlighterTableSorter.TYPE));
			}
		});

		createAddRemoveButtons(tableComposite);
	}

	@Override
	public boolean performOk() {
		IPreferenceStore uiPreferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
		
		uiPreferenceStore.setValue(TasksUiPreferenceConstants.SHOW_TRIM, showTaskTrimButton.getSelection());
		
		uiPreferenceStore.setValue(TasksUiPreferenceConstants.ACTIVATE_WHEN_OPENED, activateOnOpen.getSelection());

		uiPreferenceStore.setValue(TasksUiPreferenceConstants.OVERLAYS_INCOMING_TIGHT,
				incomingOverlaysButton.getSelection());
		TaskListView view = TaskListView.getFromActivePerspective();
		if (view != null) {
			view.setSynchronizationOverlaid(incomingOverlaysButton.getSelection());
		}
		
		JavaUiBridgePlugin.getDefault().getPreferenceStore().setValue(
				InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS, enableErrorInterest.getSelection());
		
		getPreferenceStore().setValue(ContextUiPrefContstants.HIGHLIGHTER_PREFIX,
				ContextUiPlugin.getDefault().getHighlighterList().externalizeToString());
		
		return true;
	}

	@Override
	public boolean performCancel() {
		enableErrorInterest.setSelection(JavaUiBridgePlugin.getDefault().getPreferenceStore().getBoolean(
				InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS));

		String highlighters = getPreferenceStore().getString(ContextUiPrefContstants.HIGHLIGHTER_PREFIX);
		ContextUiPlugin.getDefault().getHighlighterList().internalizeFromString(highlighters);
		
		IPreferenceStore uiPreferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
		activateOnOpen.setSelection(uiPreferenceStore.getBoolean(TasksUiPreferenceConstants.ACTIVATE_WHEN_OPENED));
		showTaskTrimButton.setSelection(uiPreferenceStore.getBoolean(TasksUiPreferenceConstants.SHOW_TRIM));
		
		contentProvider = new HighlighterContentProvider();
		tableViewer.setContentProvider(contentProvider);
		return true;
	}

	@Override
	public void performDefaults() {
		super.performDefaults();
		enableErrorInterest.setSelection(JavaUiBridgePlugin.getDefault().getPreferenceStore().getDefaultBoolean(
				InterestInducingProblemListener.PREDICTED_INTEREST_ERRORS));

		contentProvider = new HighlighterContentProvider();
		tableViewer.setContentProvider(contentProvider);
		ContextUiPlugin.getDefault().getHighlighterList().setToDefaultList();
	}

	/**
	 * Class HighlighterLabelProvider - Label and image provider for tableViewer
	 */
	private static class HighlighterLabelProvider extends LabelProvider implements ITableLabelProvider {

		public HighlighterLabelProvider() {
			// don't have any initialization to do
		}

		/**
		 * getColumnText - returns text for label and combo box cells
		 */
		public String getColumnText(Object obj, int columnIndex) {
			String result = "";
			if (obj instanceof Highlighter) {
				Highlighter h = (Highlighter) obj;
				switch (columnIndex) {
				case 0:
					// return name for label column
					result = h.getName();
					break;
				case 2:
					// return type for type column
					result = h.getHighlightKind();
					break;
				default:
					break;
				}
			}
			return result;
		}

		/**
		 * getColumnImage - returns image for color column
		 */
		public Image getColumnImage(Object obj, int columnIndex) {
			if (obj instanceof Highlighter) {
				Highlighter h = (Highlighter) obj;
				switch (columnIndex) {
				case 1:
					HighlighterImageDescriptor des;
					if (h.isGradient()) {
						des = new HighlighterImageDescriptor(h.getBase(), h.getHighlightColor());
					} else {
						des = new HighlighterImageDescriptor(h.getHighlightColor(), h.getHighlightColor());
					}
					return des.getImage();
				default:
					break;
				}
			}
			return null;
		}
	}

	/**
	 * Class HighLighterContentProvider - content provider for table viewer
	 */
	private class HighlighterContentProvider implements IStructuredContentProvider {

		/**
		 * getElements - returns array of Highlighters for table
		 */
		public Object[] getElements(Object inputElement) {
			return ContextUiPlugin.getDefault().getHighlighterList().getHighlighters().toArray();
		}

		public void dispose() {
			// don't care when we are disposed
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// don't care when the input changes
		}

		/**
		 * addHighlighter - notify the tableViewer to add a highlighter called when a highlighter is added to the
		 * HighlighterList
		 */
		public void addHighlighter(Highlighter hl) {
			tableViewer.add(hl);
		}

		/**
		 * removeHighlighter - notify the tableViewer to remove a highlighter called when a highlighter is removed from
		 * the HighlighterList
		 */
		public void removeHighlighter(Highlighter hl) {
			tableViewer.remove(hl);
		}

		/**
		 * updateHighlighter - notify the tableViewer to update a highlighter called when a highlighter property has
		 * been changed
		 */
		public void updateHighlighter(Highlighter hl) {
			tableViewer.update(hl, null);
		}
	}

	/**
	 * class HighlighterCellModifier - cellModifier for tableViewer handles all modification to the table
	 */
	private class HighlighterCellModifier implements ICellModifier {

		HighlighterCellModifier() {
			super();
		}

		public boolean canModify(Object element, String property) {
			if (element instanceof Highlighter) {
				if (!((Highlighter) element).getName().equals(HighlighterList.DEFAULT_HIGHLIGHTER.getName())) {
					return true;
				}
			}
			return false;
		}

		/**
		 * getValue - returns content of the current selection
		 */
		public Object getValue(Object element, String property) {
			// Find the index of the column
			int columnIndex = Arrays.asList(columnNames).indexOf(property);
			Object res = null;
			if (element instanceof Highlighter) {
				Highlighter hl = (Highlighter) element;
				switch (columnIndex) {
				case 0: // LABEL_COLUMN
					res = hl.getName();
					break;
				case 1: // COLOR_COLUMN
					selection = hl;
					if (selection.getCore() != null) {
						return selection.getCore().getRGB();
					} else {
						return Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND).getRGB();
					}
				case 2: // KIND_COLUMN
					// return index of current value
					if (hl.isGradient()) {
						res = new Integer(0);
					} else if (hl.isIntersection()) {
						res = new Integer(2);
					} else {
						res = new Integer(1);
					}
					break;
				default:
					return null;
				}
			}
			return res;
		}

		/**
		 * modify - modifies Highlighter with new property
		 */
		public void modify(Object element, String property, Object value) {
			// Find the index of the column
			int columnIndex = Arrays.asList(columnNames).indexOf(property);

			TableItem item = (TableItem) element;
			Highlighter hl = (Highlighter) item.getData();
			switch (columnIndex) {
			case 0: // LABEL_COLUMN
				// change value of name
				if (value instanceof String) {
					// TableItem ti = (TableItem) element;
					hl.setName((String) value);

					// update contentprovider
					contentProvider.updateHighlighter(hl);
				}
				break;
			case 1: // COLOR_COLUMN
				// never gets called since color dialog is used.
				break;
			case 2: // KIND_COLUMN
				// sets new type
				if (value instanceof Integer) {
					int choice = ((Integer) value).intValue();
					switch (choice) {
					case 0:
						// Gradient
						hl.setGradient(true);
						hl.setIntersection(false);
						break;
					case 1:
						// Solid
						hl.setGradient(false);
						hl.setIntersection(false);
						break;
					case 2:
						// Instersection
						hl.setGradient(false);
						hl.setIntersection(true);
						break;
					default:
						break;
					}
					// update content provider
					contentProvider.updateHighlighter(hl);
				}
			default:
				break;
			}
			return;
		}

	}

	/**
	 * class HighlighterTableSorter - sort columns of table added to every column as a sorter
	 */
	private static class HighlighterTableSorter extends ViewerSorter {

		public final static int LABEL = 1;

		public final static int COLOR = 2;

		public final static int TYPE = 3;

		private int criteria;

		/**
		 * set the criteria
		 */
		public HighlighterTableSorter(int criteria) {
			super();
			this.criteria = criteria;
		}

		/**
		 * compare - invoked when column is selected calls the actual comparison method for particular criteria
		 */
		@Override
		public int compare(Viewer viewer, Object o1, Object o2) {
			Highlighter h1 = (Highlighter) o1;
			Highlighter h2 = (Highlighter) o2;
			switch (criteria) {
			case LABEL:
				return compareLabel(h1, h2);
			case COLOR:
				return compareImage(h1, h2);
			case TYPE:
				return compareType(h1, h2);
			default:
				return 0;
			}
		}

		/**
		 * compareLabel - compare by label
		 */
		protected int compareLabel(Highlighter h1, Highlighter h2) {
			return h1.getName().compareTo(h2.getName());
		}

		/**
		 * compareImage - do nothing
		 */
		protected int compareImage(Highlighter h1, Highlighter h2) {
			return 0;
		}

		/**
		 * compareType - compare by type
		 */
		protected int compareType(Highlighter h1, Highlighter h2) {
			return h1.getHighlightKind().compareTo(h2.getHighlightKind());
		}

		/**
		 * getCriteria
		 */
		public int getCriteria() {
			return criteria;
		}
	}

	private void createTableViewer() {

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(columnNames);

		CellEditor[] editors = new CellEditor[columnNames.length];

		TextCellEditor textEditor = new TextCellEditor(table);
		((Text) textEditor.getControl()).setTextLimit(20);
		((Text) textEditor.getControl()).setOrientation(SWT.LEFT_TO_RIGHT);
		editors[0] = textEditor;

		colorDialogEditor = new ColorCellEditor(table);
		colorDialogEditor.addListener(this);
		editors[1] = colorDialogEditor;

		editors[2] = new ComboBoxCellEditor(table, TYPE_ARRAY, SWT.READ_ONLY);

		tableViewer.setCellEditors(editors);
		tableViewer.setCellModifier(new HighlighterCellModifier());
	}

	private void createAddRemoveButtons(Composite parent) {

		Composite addRemoveComposite = new Composite(parent, SWT.LEAD);
		addRemoveComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		addRemoveComposite.setLayout(new GridLayout(2, false));

		Button add = new Button(addRemoveComposite, SWT.PUSH | SWT.CENTER);
		add.setText("Add");
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		add.setLayoutData(gridData);

		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Highlighter hl = ContextUiPlugin.getDefault().getHighlighterList().addHighlighter();
				contentProvider.addHighlighter(hl);
			}
		});

		Button delete = new Button(addRemoveComposite, SWT.PUSH | SWT.CENTER);
		delete.setText("Delete");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		delete.setLayoutData(gridData);

		delete.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Highlighter hl = (Highlighter) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();
				if (hl != null) {
					ContextUiPlugin.getDefault().getHighlighterList().removeHighlighter(hl);
					contentProvider.removeHighlighter(hl);
				}
			}
		});
	}

	/**
	 * applyEditorValue - method called when Color selected
	 */
	public void applyEditorValue() {
		Object obj = colorDialogEditor.getValue();
		if (!colorDialogEditor.isDirty() || !colorDialogEditor.isValueValid()) {
			return;
		}

		if (obj instanceof RGB) {
			// create new color
			RGB rgb = (RGB) obj;
			Color c = new Color(Display.getCurrent(), rgb.red, rgb.green, rgb.blue);
			if (selection != null) {
				selection.setCore(c);
				contentProvider.updateHighlighter(selection);
			}
		} else {
			// ignore
		}
	}

	public void cancelEditor() {
		// don't care about this
	}

	public void editorValueChanged(boolean oldValidState, boolean newValidState) {
		// don't care when the value is changed
	}

}
