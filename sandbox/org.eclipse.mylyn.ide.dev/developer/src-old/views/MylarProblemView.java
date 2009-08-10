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

package org.eclipse.mylyn.java.ui.views;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.ProblemsLabelDecorator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.views.markers.internal.*;

import org.eclipse.mylyn.core.ITaskscapeListener;
import org.eclipse.mylyn.core.MylarPlugin;
import org.eclipse.mylyn.core.model.*;
import org.eclipse.mylyn.core.model.ITaskscapeNode;
import org.eclipse.mylyn.core.model.InterestComparator;
import org.eclipse.mylyn.java.ui.JavaUiUtil;
import org.eclipse.mylyn.java.ui.actions.ToggleAutoManagerProblemsAction;
import org.eclipse.mylyn.ui.*;

/**
 * @author Mik Kersten
 */
public class MylarProblemView extends ProblemView {

    private static TableViewer currentViewer = null;
    private final static String TAG_DIALOG_SECTION = "org.eclipse.mylyn.ui.views.problem";
    private ProblemsListFilter doiFilter = new ProblemsListFilter();
    private TableSorter sorter = null;
    
    // START: from super class
    private final static int ASCENDING = TableSorter.ASCENDING;
    private final static int DESCENDING = TableSorter.DESCENDING;
    private final static int SEVERITY = 0;
    private final static int DOI = 1;
    private final static int DESCRIPTION = 2;
    private final static int RESOURCE = 3;
    private final static int[] DEFAULT_PRIORITIES = { 
        SEVERITY, 
        DOI, 
        DESCRIPTION,
        RESOURCE };
    private final static int[] DEFAULT_DIRECTIONS = { 
        DESCENDING, // severity
        ASCENDING, // folder
        ASCENDING, // resource
        ASCENDING}; // location
    //  END: from super class
    
	private final static IField[] VISIBLE_FIELDS = { 
			new FieldSeverity(), 
			new FieldMessage(),
			new ProblemsFieldJavaElement(), 
            new ProblemsFieldDoi(),
//			new FieldFolder(), // TODO: remove
//			new FieldLineNumber() // TODO: remove 
		};
	
	private final static ColumnLayoutData[] COLUMN_LAYOUTS = { 
			new ColumnPixelData(19, false), 
			new ColumnWeightData(350), 
			new ColumnWeightData(250), 
            new ColumnWeightData(35), 
//			new ColumnWeightData(0), 
//			new ColumnWeightData(0) 
		};
	
	private static final ITaskscapeListener MODEL_LISTENER = new ITaskscapeListener() {
        public void presentationSettingsChanging(PresentationChangeKind kind) {
//            refresh();
        } 
        
        public void interestChanged(ITaskscapeNode info, IWorkbenchPart sourcePart) {
            refresh();
	    }

        public void interestChanged(List<ITaskscapeNode> nodes, IWorkbenchPart part) {
            refresh();
        }
        
        public void taskscapeActivated(ITaskscape taskscape) {
            refresh();
        }

        public void taskscapeDeactivated(ITaskscape taskscape) {
            refresh();
        } 
        
        public void landmarkAdded(ITaskscapeNode element) { }

        public void landmarkRemoved(ITaskscapeNode element) { }

        public void relationshipsChanged() {
        }
        
        private void refresh() {
            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    try {
                        if (currentViewer != null && !currentViewer.getTable().isDisposed()) {
                            currentViewer.refresh();
                        } 
                    } catch (Throwable t) {
                        ContextCorePlugin.fail(t, "Could not update viewer", false);
                    }
                }
            });
        }

        public void presentationSettingsChanged(PresentationChangeKind kind) {
            refresh();
        }

        public void nodeDeleted(ITaskscapeNode node) {
            refresh();
        }
	};
	
	public MylarProblemView() {
		super();
		ContextCorePlugin.getTaskscapeManager().addListener(MODEL_LISTENER);
	}    

	public void init(IViewSite viewSite, IMemento memento) throws PartInitException {
		super.init(viewSite, memento);
	}	
 
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		MylarLabelProvider labelProvider = new MylarLabelProvider(
			new TableViewLabelProvider(getVisibleFields()), 
			new ProblemsLabelDecorator()); 
        
		getViewer().setLabelProvider(labelProvider); 
        getViewer().addFilter(doiFilter);
//		getViewer().getTable().setHeaderVisible(false);
		getViewer().getTable().setBackground(MylarUiPlugin.getDefault().getColorMap().BACKGROUND_COLOR);
		
        IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
        ToggleAutoManagerProblemsAction autoExpandAction = new ToggleAutoManagerProblemsAction(this); 
        if (toolBarManager != null) {   
            toolBarManager.add(new Separator("StartMylar")); 
            toolBarManager.add(autoExpandAction); 
        } 
//        autoExpandAction.autoExpand(autoExpandAction.isChecked());
//        sorter = new ProblemsListDoiSorter(getFields(), DEFAULT_PRIORITIES, DEFAULT_DIRECTIONS);
//        getViewer().setSorter(sorter);
        getViewer().refresh();
		currentViewer = getViewer();
	}
	 
	protected IField[] getVisibleFields() {
		return VISIBLE_FIELDS;
	}
	
	protected ColumnLayoutData[] getDefaultColumnLayouts() {
		return COLUMN_LAYOUTS;
	}
	
	protected IDialogSettings getDialogSettings() {
		IDialogSettings workbenchSettings = JavaPlugin.getDefault().getDialogSettings();
		IDialogSettings settings = workbenchSettings.getSection(TAG_DIALOG_SECTION);
		
		if (settings == null)
			settings = workbenchSettings.addNewSection(TAG_DIALOG_SECTION);

		return settings;
	}
    
    public InterestFilter getDoiFilter() {
        return doiFilter;
    }
    
    public TableViewer getViewer() {
        return super.getViewer();
    } 
     
    protected TableSorter getSorter() {
        if (sorter == null)
            sorter = new TableSorter(VISIBLE_FIELDS, DEFAULT_PRIORITIES, DEFAULT_DIRECTIONS);
        return sorter;
    }

    public void setInterestFiltering(boolean on) {
        if (on) {
            this.getViewer().addFilter(doiFilter);
        } else {
            this.getViewer().removeFilter(doiFilter);
        }
    }
}

class ProblemsListDoiSorter extends TableSorter { 

    public ProblemsListDoiSorter(IField[] properties, int[] defaultPriorities, int[] defaultDirections) {
        super(properties, defaultPriorities, defaultDirections);
    } 

    protected InterestComparator comparator = new InterestComparator();
    
    protected int compare(Object obj1, Object obj2, int depth) {
        return super.compare(obj1, obj2, depth);
    }

    public int compare(Viewer viewer, Object e1, Object e2) {
        return super.compare(viewer, e1, e1);
//        if (e1 instanceof ProblemMarker && e2 instanceof ProblemMarker) {
//            if (((ProblemMarker)e2).getSeverity() == IMarker.SEVERITY_ERROR) {
//                return 1;
//            }
//            IJavaElement element1 = Util.getJavaElement((ProblemMarker)e1);
//            IJavaElement element2 = Util.getJavaElement((ProblemMarker)e2);
//            if (element1 != null && element2 != null) {
//                return comparator.compare(
//                    ContextCorePlugin.getTaskscapeManager().getActiveTaskscape().get(element1.getHandleIdentifier()),
//                    ContextCorePlugin.getTaskscapeManager().getActiveTaskscape().get(element1.getHandleIdentifier()));
//            }
//        } 
//        
//        return comparator.compare(e1, e2);  
    }
}

class MylarLabelProvider extends DecoratingLabelProvider implements IFontProvider, ITableLabelProvider, IColorProvider {

	TableViewLabelProvider provider;
	
	public MylarLabelProvider(TableViewLabelProvider provider, ILabelDecorator decorator) {
		super(provider, decorator);
		this.provider = provider;
	}

    public Font getFont(Object element) {
        return null;
//        ProblemMarker marker = (ProblemMarker)element;
//        IJavaElement javaElement = Util.getJavaElement(marker);
//        return UiUtil.getFontForElement(javaElement);
    }
	
	public Image getColumnImage(Object element, int columnIndex) {
		return provider.getColumnImage(element, columnIndex);
	}

	public String getColumnText(Object element, int columnIndex) {
		return provider.getColumnText(element, columnIndex);
	}
	
	public Color getForeground(Object element) {
        ProblemMarker marker = (ProblemMarker)element;
        IJavaElement javaElement = JavaUiUtil.getJavaElement(marker);
        if (javaElement != null) {
            ITaskscapeNode node = ContextCorePlugin.getTaskscapeManager().getNode(javaElement.getHandleIdentifier());
            return UiUtil.getForegroundForElement(node);
        } else {
            return null;
        }        
//		return MylarUiPlugin.getDefault().getColorMap().TEXT;
	}

	public Color getBackground(Object element) {
        ProblemMarker marker = (ProblemMarker)element;
        IJavaElement javaElement = JavaUiUtil.getJavaElement(marker);
        if (javaElement != null) {
            ITaskscapeNode node = ContextCorePlugin.getTaskscapeManager().getNode(javaElement.getHandleIdentifier());
            return UiUtil.getBackgroundForElement(node);
        } else {
            return null;
        }
	}
}

