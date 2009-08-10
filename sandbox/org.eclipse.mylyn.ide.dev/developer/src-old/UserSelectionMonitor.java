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
 * Created on Feb 8, 2005
  */
package org.eclipse.mylyn.monitor;

import org.eclipse.core.internal.resources.File;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaOutlinePage;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.text.*;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search2.internal.ui.SearchView;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.markers.internal.ProblemView;

import org.eclipse.mylyn.core.AbstractSelectionMonitor;
import org.eclipse.mylyn.monitor.stats.UsageSession;
import org.eclipse.mylyn.monitor.views.UsageStatisticsView;

/**
 * @author Mik Kersten
 */
public class UserSelectionMonitor extends AbstractSelectionMonitor {

    private final ITextListener TEXT_LISTENER = new ITextListener() {
        public void textChanged(TextEvent event) {
            MonitorPlugin.getStatisticsManager().getCurrentSession().getCardinalStatistic(
                    UsageSession.NUM_KEYSTROKES_JAVA_EDITOR).increment();
        }
    };
    
    public UserSelectionMonitor() {
        super();
    }

    public void handleElementSelection(IJavaElement selected) {
    }

    protected void handleUnknownSelection(Object selectedObject) {
    }

    public void handleReferenceNavigation(IJavaElement from, IJavaElement to) {
    }
    
    protected void handleSelection(File file) {
    } 

    protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection) {
        UsageSession session = MonitorPlugin.getStatisticsManager().getCurrentSession();
        if (part instanceof PackageExplorerPart) {
            session.getCardinalStatistic(UsageSession.NUM_SELECTIONS_PKG_EXPLORER).increment();
        } else if (part instanceof ContentOutline) {
            ContentOutline outline = (ContentOutline)part;
            if (outline.getCurrentPage() instanceof JavaOutlinePage) { 
                session.getCardinalStatistic(UsageSession.NUM_SELECTIONS_JAVA_OUTLINE).increment();
            }
        } else if (part instanceof SearchView) {
            session.getCardinalStatistic(UsageSession.NUM_SELECTIONS_SEARCH).increment();
        } else if (part instanceof ProblemView) {
            session.getCardinalStatistic(UsageSession.NUM_SELECTIONS_PROBLEMS).increment();
        } else if (part instanceof AbstractTextEditor) {
            session.getCardinalStatistic(UsageSession.NUM_SELECTIONS_JAVA_EDITOR).increment();
        } else {
            session.getCardinalStatistic(UsageSession.NUM_SELECTIONS_OTHER).increment();
//            MonitorPlugin.log(this, "unknow selection from: " + part.getClass());
        }
        if (UsageStatisticsView.getDefault() != null) {     
            UsageStatisticsView.getDefault().getViewer().refresh();
        }
        if (part instanceof AbstractTextEditor) {
            if (selection instanceof TextSelection && part instanceof JavaEditor) {
                JavaEditor currentEditor = (JavaEditor)part;
                currentEditor.getViewer().removeTextListener(TEXT_LISTENER); // in case already added
                currentEditor.getViewer().addTextListener(TEXT_LISTENER);
            }
        }
    }

    public void handleImplementorNavigation(IJavaElement from, IJavaElement to) {
    }
}
