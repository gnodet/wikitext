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
package org.eclipse.mylyn.monitor.views;


import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.mylyn.core.MylarPlugin;
import org.eclipse.mylyn.monitor.MonitorImages;
import org.eclipse.mylyn.monitor.MonitorPlugin;


public abstract class MylarMonitorView extends ViewPart {
    private LabelProvider labelProvider;
    private ViewerSorter viewerSorter;
	protected TableViewer viewer;
	private Action refresh;
	private Action deleteUsageFile;
	private Action linkRefresh;
	private boolean activeRefresh = ContextCorePlugin.DEBUG_MODE;
	
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
		    return refreshView(parent);
		}
	}
	
    protected abstract Object[] refreshView(Object parent);
	
    public MylarMonitorView(LabelProvider labelProvider, ViewerSorter viewerSorter) {
        this.labelProvider = labelProvider;
        this.viewerSorter = viewerSorter;
        
//        if (activeRefresh) ContextCorePlugin.getTaskscapeManager().addListener(REFRESH_UPDATE_LISTENER);
    }
    
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
//	    Composite composite = new Composite(parent, SWT.TOP);
//	    summaryLabel = new Label(composite, SWT.TOP);
//	    summaryLabel.setText("no summary info");
//	    JUnitProgressBar fProgressBar = new JUnitProgressBar(parent);
	    
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(labelProvider);
		viewer.setSorter(viewerSorter);
		viewer.setInput(getViewSite());
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				MylarMonitorView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(refresh);
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(refresh);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
	    manager.add(deleteUsageFile);
	    manager.add(linkRefresh);
	    manager.add(refresh);
	}

	private void makeActions() {
		linkRefresh = new ActiveRefreshAction();
		linkRefresh.setToolTipText("Active Refresh");
		linkRefresh.setImageDescriptor(MonitorImages.SYNCHED);  
		linkRefresh.setChecked(activeRefresh);
	    
	    refresh = new Action() {
			public void run() {
			    MonitorPlugin.getStatisticsManager().refreshCurrentSession();
				MylarMonitorView.this.viewer.refresh(); 
			}
		};
		refresh.setText("Refresh");
		refresh.setToolTipText("Refresh Statistics");
		refresh.setImageDescriptor(MonitorImages.REFRESH);  

		deleteUsageFile = new Action() {
			public void run() {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
					public void run() {
						try {
							Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
							boolean delete = MessageDialog.openQuestion(
								shell,
								"Confirm delete",
								"Delete the underlying file?");
							if (delete) resetUnderlyingModel();
						} catch (Throwable t) {
						    ContextCorePlugin.fail(t, "Could not delete usage file", true);
						}
					}
				});
				MylarMonitorView.this.viewer.refresh();
			}
		};
		deleteUsageFile.setText("Delete usage file");
		deleteUsageFile.setToolTipText("Delete usage file");
		deleteUsageFile.setImageDescriptor(MonitorImages.REMOVE);
	}

	protected abstract void resetUnderlyingModel();
	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	class ActiveRefreshAction extends Action {
	    public ActiveRefreshAction() {
	        super(null, IAction.AS_CHECK_BOX);
	    } 
	     
		public void run() {
		    activeRefresh = !activeRefresh;
		    setChecked(activeRefresh);
		    if (activeRefresh) {
//		        ContextCorePlugin.getTaskscapeManager().addListener(REFRESH_UPDATE_LISTENER);
		    } else {
//		        ContextCorePlugin.getTaskscapeManager().removeListener(REFRESH_UPDATE_LISTENER);
		    }
		}
	};
}

