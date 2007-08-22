/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.wiki;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.mylyn.internal.trac.core.AbstractWikiHandler;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPage;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPageInfo;
import org.eclipse.mylyn.internal.trac.wiki.editor.TracWikiPageEditorInput;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.team.ui.history.HistoryPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Xiaoyang Guan
 */
public class TracWikiPageHistoryPage extends HistoryPage {

	private TableViewer viewer;

	class DownloadPageHistoryJob extends Job {

		private TaskRepository repository;

		private TracWikiPage page;

		public DownloadPageHistoryJob(TaskRepository repository, TracWikiPage page) {
			super("Download Page History");
			this.repository = repository;
			this.page = page;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			AbstractWikiHandler wikiHandler = TracCorePlugin.getDefault().getConnector().getWikiHandler();
			try {
				monitor.beginTask("Retrieving wiki page history", IProgressMonitor.UNKNOWN);
				final TracWikiPageInfo[] pageVersions = wikiHandler.getPageHistory(repository, page.getPageInfo()
						.getPageName(), monitor);
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					public void run() {
						viewer.setInput(pageVersions);
					}

				});
			} catch (CoreException e) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					public void run() {
						viewer.setInput(null);
					}

				});
			} finally {
				monitor.done();
			}
			return Status.OK_STATUS;
		}

	}

	@Override
	public boolean inputSet() {
		if (getInput() instanceof TracWikiPageEditorInput) {
			refresh();
			return true;
		}
		return false;
	}

	class PageHistoryContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			return (TracWikiPageInfo[]) inputElement;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	@Override
	public void createControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(new PageHistoryContentProvider());

		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(80);
		column.getColumn().setText("Version");
		column.getColumn().setMoveable(true);
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return ((TracWikiPageInfo) element).getVersion() + "";
			}
		});

		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(250);
		column.getColumn().setText("Date");
		column.getColumn().setMoveable(true);
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return ((TracWikiPageInfo) element).getLastModified().toString();
			}

		});

		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Author");
		column.getColumn().setMoveable(true);
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return ((TracWikiPageInfo) element).getAuthor();
			}

		});

		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Comment");
		column.getColumn().setMoveable(true);
		column.setLabelProvider(new ColumnLabelProvider() {

			public String getText(Object element) {
				return ((TracWikiPageInfo) element).getComment();
			}

		});

		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.setComparator(new ViewerComparator() {

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof TracWikiPageInfo && e2 instanceof TracWikiPageInfo) {
					// sort from the latest version to the earliest version
					return ((TracWikiPageInfo) e2).getVersion() - ((TracWikiPageInfo) e1).getVersion();
				}
				return super.compare(viewer, e1, e2);
			}

		});
	}

	@Override
	public Control getControl() {
		return viewer.getControl();
	}

	@Override
	public void setFocus() {
		getControl().setFocus();
	}

	public String getDescription() {
		return "Change History for " + getName();
	}

	public String getName() {
		TracWikiPageEditorInput input = (TracWikiPageEditorInput) getInput();
		return input.getPage().getPageInfo().getPageName() + " [" + input.getRepository().getRepositoryLabel() + "]";
	}

	public boolean isValidInput(Object object) {
		if (object instanceof TracWikiPageEditorInput) {
			TracWikiPageEditorInput input = (TracWikiPageEditorInput) object;
			return input.getRepository() != null && input.getPage() != null;
		}
		return false;
	}

	public void refresh() {
		TracWikiPageEditorInput input = (TracWikiPageEditorInput) getInput();
		(new DownloadPageHistoryJob(input.getRepository(), input.getPage())).schedule();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

}
