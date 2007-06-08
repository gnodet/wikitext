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
/*
 * Created on Jul 26, 2004
  */
package org.eclipse.mylyn.monitor.views;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import org.eclipse.mylyn.core.MylarPlugin;
import org.eclipse.mylyn.monitor.MonitorPlugin;
import org.eclipse.mylyn.monitor.stats.UsageStatistic;

/**
 * @author Mik Kersten
 */
public class UsageStatisticsView extends MylarMonitorView {
    
    private static UsageStatisticsView INSTANCE = null;
    
    public UsageStatisticsView() {
        super(new UsageViewLabelProvider(), new ViewerSorter());
        INSTANCE = this;
    }
    
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
    }
    
    protected Object[] refreshView(Object parent) {
		if (MonitorPlugin.getStatisticsManager() == null) {
			return new String[] { "No Statistics" };  
		} else { 
		    try {
		        String summary = MonitorPlugin.getStatisticsManager().getGlobalMergedSession().getSummary();
				super.setContentDescription(summary); 
                return MonitorPlugin.getStatisticsManager().getGlobalMergedSession().getStatistics().toArray();
			} catch (Throwable t) { 
			    ContextCorePlugin.fail(t, "Failed to show usage data", false);
                return new String[] { 
                        "Absent or incompatible usage data: " + t.getMessage(), 
                        "Consider resetting usage file." };
			} 
		}
    } 

    static class UsageViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object object, int index) {
		        return getText(object);
		} 
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object object) {
		    if (object instanceof UsageStatistic) {
		        String handle = ((UsageStatistic)object).getHandle();
//		        if (handle.equals(UsageSession.NUM_KEYSTROKES_JAVA_EDITOR)) {
//		            return MylarImages.getImage(MylarImages.USAGE_KEYSTROKES);
//		        } else if (handle.equals(UsageSession.NUM_SECONDS_ELAPSED)) {
//		            return MylarImages.getImage(MylarImages.TIME);
//		        } else if (handle.equals(UsageSession.NUM_SELECTIONS_PATHFINDER)) {
//		            return MylarImages.getImage(MylarImages.PATHFINDER);
//		        } else if (handle.equals(UsageSession.NUM_SELECTIONS_JAVA_EDITOR)) {
//		            return MylarImages.getImage(MylarImages.USAGE_SELECTIONS_EDITOR);
//		        } else if (handle.equals(UsageSession.NUM_SELECTIONS_JAVA_EDITOR_AUTOFOLD)) {
//		            return MylarImages.getImage(MylarImages.USAGE_SELECTIONS_EDITOR);
//		        } else if (handle.equals(UsageSession.NUM_SELECTIONS_JAVA_OUTLINE)) {
//		            return MylarImages.getImage(MylarImages.OUTLINE);
//		        } else if (handle.equals(UsageSession.NUM_SELECTIONS_JAVA_OUTLINE_MYLAR)) {
//		            return MylarImages.getImage(MylarImages.OUTLINE_MYLAR);
//		        } else if (handle.equals(UsageSession.NUM_SELECTIONS_OTHER)) {
//		            return MylarImages.getImage(MylarImages.USAGE_UNKNOWN);
//		        } else if (handle.equals(UsageSession.NUM_SELECTIONS_PKG_EXPLORER)) {
//		            return MylarImages.getImage(MylarImages.PACKAGE_EXPLORER);
//		        } else if (handle.equals(UsageSession.NUM_SELECTIONS_PKG_EXPLORER_MYLAR)) {
//		            return MylarImages.getImage(MylarImages.PACKAGE_EXPLORER_MYLAR);
//		        } else if (handle.equals(UsageSession.NUM_SELECTIONS_PROBLEMS)) {
//		            return MylarImages.getImage(MylarImages.PROBLEMS);
//		        } else if (handle.equals(UsageSession.NUM_SELECTIONS_PROBLEMS_MYLAR)) {
//		            return MylarImages.getImage(MylarImages.PROBLEMS_MYLAR);
//		        } else if (handle.equals(UsageSession.NUM_SELECTIONS_SEARCH)) {
//		            return MylarImages.getImage(MylarImages.SEARCH);
//		        } else if (handle.equals(UsageSession.NUM_SELECTIONS_SEARCH_MYLAR)) {
//		            return MylarImages.getImage(MylarImages.SEARCH_MYLAR);
//		        } else if (handle.equals(UsageSession.START_DATE)) {
//		            return MylarImages.getImage(MylarImages.TIME);
//		        } else if (handle.equals(UsageSession.START_TIME)) {
//		            return MylarImages.getImage(MylarImages.TIME);
//		        } 
		    }  
		    return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}
 
    protected void resetUnderlyingModel() {
        if(!MonitorPlugin.getStatisticsManager().clearUsageDataAndStore()) {
            ContextCorePlugin.fail(null, "Could not delete usage file", true);
        }
    }
    public static UsageStatisticsView getDefault() {
        return INSTANCE;
    }

    /**
     * @return
     */
    public TableViewer getViewer() {
        return viewer;
    }
}
