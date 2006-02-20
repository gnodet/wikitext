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

package org.eclipse.mylar.internal.sandbox.viz;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.mylar.internal.ui.MylarImages;
import org.eclipse.mylar.internal.ui.views.DelegatingContextLabelProvider;
import org.eclipse.mylar.provisional.core.IMylarContext;
import org.eclipse.mylar.provisional.core.IMylarContextListener;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.zest.core.ZestStyles;
import org.eclipse.mylar.zest.core.viewers.StaticGraphViewer;
import org.eclipse.mylar.zest.layouts.LayoutStyles;
import org.eclipse.mylar.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Mik Kersten
 */
public class TaskscapeGraphView extends ViewPart {

	private StaticGraphViewer viewer;

	private Action refreshAction;

	private final IMylarContextListener REFRESH_UPDATE_LISTENER = new IMylarContextListener() {
		public void interestChanged(final IMylarElement node) {
			refresh();
		}

		public void interestChanged(final List<IMylarElement> nodes) {
			refresh();
		}

		public void contextActivated(IMylarContext taskscape) {
			refresh();
		}

		public void contextDeactivated(IMylarContext taskscape) {
			refresh();
		}

		public void presentationSettingsChanging(UpdateKind kind) {
			refresh();
		}

		public void landmarkAdded(final IMylarElement element) {
			// viewer.refresh(element, true);
			refresh();
		}

		public void landmarkRemoved(final IMylarElement element) {
			// viewer.refresh(element, true);
			refresh();
		}

		public void edgesChanged(IMylarElement node) {
			refresh();
		}

		private void refresh() {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						if (viewer.getControl() != null && !viewer.getControl().isDisposed()) {
							viewer.setInput(getViewSite()); // HACK
							// viewer.updateModel();
						}
					} catch (Throwable t) {
						t.printStackTrace(); // TODO: handle
					}
				}
			});
		}

		public void presentationSettingsChanged(UpdateKind kind) {
			if (kind == IMylarContextListener.UpdateKind.HIGHLIGHTER)
				viewer.refresh();
		}

		public void nodeDeleted(IMylarElement node) {
			refresh();
		}
	};

	public TaskscapeGraphView() {
		MylarPlugin.getContextManager().addListener(REFRESH_UPDATE_LISTENER);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		viewer = new StaticGraphViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| ZestStyles.NO_OVERLAPPING_NODES | ZestStyles.PANNING);
		viewer.setContentProvider(new TaskscapeGraphContentProvider());
		viewer.setLabelProvider(new DecoratingLabelProvider(new DelegatingContextLabelProvider(), PlatformUI
				.getWorkbench().getDecoratorManager().getLabelDecorator()));
		// viewer.setLabelProvider(new TaskscapeNodeLabelProvider());
		// viewer.setLabelProvider(new SampleGraphLabelProvider());
		// viewer.setSorter(new NameSorter());

		SpringLayoutAlgorithm springLayout = new SpringLayoutAlgorithm(LayoutStyles.NONE);
		springLayout.setRandom(false);
		springLayout.setIterations(20);
		viewer.setLayoutAlgorithm(springLayout, false);

		viewer.setInput(getViewSite());

		makeActions();
		contributeToActionBars();
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(refreshAction);
		manager.add(new Separator());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAction);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class type) {
		// the CommandStack is needed for the delete action
		// if (type == CommandStack.class) {
		// return viewer.getCommandStack();
		// }
		return super.getAdapter(type);
	}

	/**
	 * Creates the "Add Node" action which creates a new node and adds it to the
	 * model.
	 */
	private void makeActions() {
		refreshAction = new Action() {

			/** Create a new node and add a connection from it to a random node. */
			@Override
			public void run() {
				viewer.refresh();
			}
		};

		refreshAction.setText("Run Layout");
		refreshAction.setToolTipText("Runs the layout algorithm again");
		refreshAction.setImageDescriptor(MylarImages.EDGE_REFERENCE);

	}

}
