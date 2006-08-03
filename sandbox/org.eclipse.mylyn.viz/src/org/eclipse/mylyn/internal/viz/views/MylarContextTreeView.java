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
package org.eclipse.mylar.internal.viz.views;


import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.IMylarContext;
import org.eclipse.mylar.context.core.IMylarContextListener;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.context.core.MylarStatusHandler;
import org.eclipse.mylar.context.ui.InterestSorter;
import org.eclipse.mylar.internal.context.ui.ColorMap;
import org.eclipse.mylar.internal.context.ui.ContextUiImages;
import org.eclipse.mylar.internal.context.ui.actions.ToggleDecorateInterestLevelAction;
import org.eclipse.mylar.internal.context.ui.views.ContextContentProvider;
import org.eclipse.mylar.internal.context.ui.views.ContextNodeOpenListener;
import org.eclipse.mylar.internal.context.ui.views.DelegatingContextLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Mik Kersten
 */
public class MylarContextTreeView extends ViewPart {

	private ViewerSorter viewerSorter;

	protected TreeViewer viewer;

	private Action decorateInterestLevel;

	private Action linkRefresh;

	private boolean activeRefresh = true;// ContextCorePlugin.DEBUG_MODE;

	private final IMylarContextListener REFRESH_UPDATE_LISTENER = new IMylarContextListener() {

		public void interestChanged(List<IMylarElement> nodes) {
			for (IMylarElement node : nodes)
				refresh(node);
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

		public void presentationSettingsChanged(UpdateKind kind) {
			refresh();
		}

		public void landmarkAdded(IMylarElement element) {
			refresh();
		}

		public void landmarkRemoved(IMylarElement element) {
			refresh();
		}

		public void edgesChanged(IMylarElement node) {
			refresh();
		}

		private void refresh() {
			refresh(null);
		}

		private void refresh(final IMylarElement node) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						if (viewer != null && !viewer.getTree().isDisposed()) {
							if (node != null) {
								viewer.refresh(node);
							} else {
								viewer.refresh();
							}
						}
					} catch (Throwable t) {
						MylarStatusHandler.fail(t, "Could not update viewer", false);
					}
				}
			});
		}

		public void nodeDeleted(IMylarElement node) {
			refresh();
		}
	};

	public MylarContextTreeView() {
		ContextCorePlugin.getContextManager().addListener(REFRESH_UPDATE_LISTENER);
	}

	@Override
	public void createPartControl(Composite parent) {
		viewerSorter = new InterestSorter();
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ContextContentProvider(viewer.getTree(), this.getViewSite(), false));
		viewer.setSorter(viewerSorter);
		viewer.setInput(getViewSite());

		// viewer.setLabelProvider(new TaskscapeNodeLabelProvider());
		viewer.setLabelProvider(new DecoratingLabelProvider(new DelegatingContextLabelProvider(), PlatformUI
				.getWorkbench().getDecoratorManager().getLabelDecorator()));

		makeActions();
		hookContextMenu();
		contributeToActionBars();
		viewer.getTree().setBackground(ColorMap.BACKGROUND_COLOR);

		viewer.addOpenListener(new ContextNodeOpenListener(viewer));
	}

	protected Object[] refreshView(Object parent) {
		if (ContextCorePlugin.getContextManager() == null) {
			return new String[] { "No model" };
		} else {
			try {
				return ContextCorePlugin.getContextManager().getActiveContext().getAllElements().toArray();
			} catch (Throwable t) {
				MylarStatusHandler.fail(t, "failed to show model", false);
				return new String[] { "Absent or incompatible model data: " + t.getMessage(),
						"Consider resetting model file." };
			}
		}
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				MylarContextTreeView.this.fillContextMenu(manager);
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
		manager.add(decorateInterestLevel);
		manager.add(new Separator());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(decorateInterestLevel);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(linkRefresh);
		manager.add(decorateInterestLevel);
	}

	private void makeActions() {
		linkRefresh = new ActiveRefreshAction();
		linkRefresh.setToolTipText("Active Refresh");
		linkRefresh.setImageDescriptor(ContextUiImages.SYNCHED);
		linkRefresh.setChecked(activeRefresh);

		decorateInterestLevel = new ToggleDecorateInterestLevelAction();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	class ActiveRefreshAction extends Action {
		public ActiveRefreshAction() {
			super(null, IAction.AS_CHECK_BOX);
		}

		@Override
		public void run() {
			activeRefresh = !activeRefresh;
			setChecked(activeRefresh);
			if (activeRefresh) {
				ContextCorePlugin.getContextManager().addListener(REFRESH_UPDATE_LISTENER);
			} else {
				ContextCorePlugin.getContextManager().removeListener(REFRESH_UPDATE_LISTENER);
			}
		}
	}

}
