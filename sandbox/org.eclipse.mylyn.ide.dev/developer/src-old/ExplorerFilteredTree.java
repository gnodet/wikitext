/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.browser.views;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.mylyn.internal.tasks.ui.views.AbstractMylarFilteredTree;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Mik Kersten
 */
public class ExplorerFilteredTree extends AbstractMylarFilteredTree {

	private InterestFilter suppressedFilter = null;
    
    public ExplorerFilteredTree(String viewId, Composite parent, int treeStyle, PatternFilter filter) {
		super(parent, treeStyle, filter);
//		this.viewId = viewId;
	}
	
    /**
     * Create the tree.  Subclasses may override.
     * 
     * @param parent parent <code>Composite</code>
     * @param style SWT style bits used to create the tree
     * @return the tree
     */
    protected Control createTreeControl(Composite parent, int style) {
//        treeViewer = new ResourceTreeViewer(viewId, parent, style);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        treeViewer.getControl().setLayoutData(data);
        treeViewer.getControl().addDisposeListener(new DisposeListener(){
        	/* (non-Javadoc)
        	 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
        	 */
        	public void widgetDisposed(DisposeEvent e) {
        		Job refreshJob = getRefreshJob();
        		if (refreshJob != null) {
        			getRefreshJob().cancel();
        		}
        	}
        });

        treeViewer.addFilter(getPatternFilter());  
        return treeViewer.getControl();
    }

	@Override
	protected Composite createStatusComposite(Composite container) {
		return null;
	}
}