package org.eclipse.mylyn.internal.browser.views;
///*******************************************************************************
// * Copyright (c) 2004 - 2006 University Of British Columbia and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *     University Of British Columbia - initial API and implementation
// *******************************************************************************/
//
//package org.eclipse.mylyn.internal.browser.views;
//
//import org.eclipse.jface.viewers.TreeViewer;
//import org.eclipse.mylyn.internal.tasks.ui.TaskListPatternFilter;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.ui.dialogs.FilteredTree;
//import org.eclipse.ui.views.navigator.ResourceNavigator;
//
///**
// * @author Mik Kersten
// */
//public class ExplorerView extends ResourceNavigator {
//
//	public static final String ID = "org.eclipse.mylyn.explorer.ui.explorer";
//	
//	protected TreeViewer createViewer(Composite parent) {
//		FilteredTree filteredTree = new ExplorerFilteredTree(ID, parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL,
//				new TaskListPatternFilter());
//
//		TreeViewer viewer = filteredTree.getViewer();
//
//		// NOTE: below from super
//		viewer.setUseHashlookup(true);
//		initContentProvider(viewer);
//		initLabelProvider(viewer);
//		initFilters(viewer);
//		initListeners(viewer);
//
//		return viewer;
//	}
//}
