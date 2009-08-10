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

package org.eclipse.mylyn.internal.hypertext.ui.editors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.hypertext.HypertextStructureBridge;
import org.eclipse.mylyn.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylyn.provisional.core.IMylarContext;
import org.eclipse.mylyn.provisional.core.IMylarContextListener;
import org.eclipse.mylyn.provisional.core.IMylarElement;
import org.eclipse.mylyn.provisional.core.MylarPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;
import org.eclipse.ui.part.EditorPart;

public class WebElementsEditor extends EditorPart {

	private static final String LABEL = "Web Documents in Active Context";

	private Composite editorComposite;

	private ScrolledForm form;

	private Tree linksTree;

	private TreeViewer treeViewer;

	private List<String> links;

	private WebResourceContentProvider contentProvider;

	private Action add;

	private Action delete;

	private final IMylarContextListener REFRESH_UPDATE_LISTENER = new IMylarContextListener() {
		public void interestChanged(IMylarElement node) {
			// ignore
		}

		public void interestChanged(List<IMylarElement> nodes) {
			// ignore
		}

		public void contextActivated(IMylarContext taskscape) {
			update();
		}

		public void contextDeactivated(IMylarContext taskscape) {
			update();
		}

		public void presentationSettingsChanging(UpdateKind kind) {
			// ignore
		}

		public void landmarkAdded(IMylarElement node) {
			// ignore
		}

		public void landmarkRemoved(IMylarElement node) {
			// ignore
		}

		public void edgesChanged(IMylarElement node) {
			// ignore
		}

		public void nodeDeleted(IMylarElement node) {
			// ignore
		}

		public void presentationSettingsChanged(UpdateKind kind) {
			// ignore
		}
	};

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {

	}

	@SuppressWarnings("deprecation")
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(LABEL);
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		ContextCorePlugin.getContextManager().removeListener(REFRESH_UPDATE_LISTENER);
	}

	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.getBody().setLayout(new TableWrapLayout());
		editorComposite = form.getBody();

		TableWrapLayout layout = new TableWrapLayout();
		layout.bottomMargin = 10;
		layout.topMargin = 10;
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		editorComposite.setLayout(layout);
		// editorComposite.setLayoutData(new
		// TableWrapData(TableWrapData.FILL_GRAB));

		// Put the info onto the editor
		createContent(editorComposite, toolkit);
		form.setFocus();
		ContextCorePlugin.getContextManager().addListener(REFRESH_UPDATE_LISTENER);
	}

	private void createContent(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		section.setText(LABEL);
		section.setLayout(new TableWrapLayout());
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				form.reflow(true);
			}

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});

		Composite container = toolkit.createComposite(section);
		section.setClient(container);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 3;
		container.setLayout(layout);
		container.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		createTable(container, toolkit);
		createTableViewer(container, toolkit);
		toolkit.paintBordersFor(container);
		// createAddDeleteButtons(container, toolkit);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private void createTableViewer(Composite parent, FormToolkit toolkit) {
		// String[] columnNames = {"Links"};
		treeViewer = new TreeViewer(linksTree);
		// treeViewer.setColumnProperties(columnNames);

		// CellEditor[] editors = new CellEditor[columnNames.length];
		// TextCellEditor textEditor = new TextCellEditor(linksTree);
		// ((Text) textEditor.getControl()).setTextLimit(50);
		// ((Text) textEditor.getControl()).setOrientation(SWT.LEFT_TO_RIGHT);
		// editors[0] = textEditor;
		// tableViewer.setCellEditors(editors);
		// tableViewer.setCellModifier(new RelatedLinksCellModifier());

		contentProvider = new WebResourceContentProvider();
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(new WebResourceLabelProvider());
		treeViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				String url = (String) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();
				if (url != null)
					openURLinBrowser(url);
			}
		});

		update();
		defineActions();
		hookContextMenu();
	}

	public void update() {
		if (treeViewer.getContentProvider() != null) {
			treeViewer.setInput(getWebDocs());
			treeViewer.expandAll();
		}
	}

	private List<String> getWebDocs() {
		links = new ArrayList<String>();
		List<IMylarElement> elements = ContextCorePlugin.getContextManager().getInterestingDocuments();
		for (IMylarElement element : elements) {
			if (element.getContentType().equals(HypertextStructureBridge.CONTENT_TYPE)) {
				links.add(element.getHandleIdentifier());
			}
		}
		return links;
	}

	private void createTable(Composite parent, FormToolkit toolkit) {
		linksTree = toolkit.createTree(parent, SWT.NONE);
		// TreeColumn col1 = new TreeColumn(linksTree, SWT.NULL);
		// TableLayout tlayout = new TableLayout();
		// tlayout.addColumnData(new ColumnWeightData(0,0,false));
		// linksTree.setLayout(tlayout);
		TableWrapData wd = new TableWrapData(TableWrapData.FILL_GRAB);
		wd.heightHint = 350;
		wd.grabVertical = true;
		linksTree.setLayoutData(wd);
		linksTree.setHeaderVisible(false);
		// col1.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// treeViewer.setSorter(new RelatedLinksTableSorter(
		// RelatedLinksTableSorter.LABEL));
		// }
		// });
		linksTree.addMouseTrackListener(new MouseTrackListener() {
			public void mouseEnter(MouseEvent e) {
				if (!((WebResourceContentProvider) treeViewer.getContentProvider()).isEmpty()) {
					Cursor hyperlinkCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
					Display.getCurrent().getCursorControl().setCursor(hyperlinkCursor);
				}
			}

			public void mouseExit(MouseEvent e) {
				Cursor pointer = new Cursor(Display.getCurrent(), SWT.CURSOR_ARROW);
				if (Display.getCurrent() != null && Display.getCurrent().getCursorControl() != null) {
					Display.getCurrent().getCursorControl().setCursor(pointer);
				}
			}

			public void mouseHover(MouseEvent e) {
				if (!((WebResourceContentProvider) treeViewer.getContentProvider()).isEmpty()) {
					Cursor hyperlinkCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
					Display.getCurrent().getCursorControl().setCursor(hyperlinkCursor);
				}
			}
		});
	}

	// private void createAddDeleteButtons(Composite parent, FormToolkit
	// toolkit) {
	// Composite container = toolkit.createComposite(parent);
	// container.setLayout(new GridLayout(2, true));
	// Button addButton = toolkit.createButton(container, " Add Hyperlink ",
	// SWT.PUSH | SWT.CENTER);
	// addButton.addSelectionListener(new SelectionAdapter() {
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// addLinkToTable();
	// }
	// });
	//
	// Button deleteButton = toolkit.createButton(container, "Delete Hyperlink
	// ", SWT.PUSH | SWT.CENTER);
	// deleteButton.addSelectionListener(new SelectionAdapter() {
	//			
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// removeLinkFromTable();
	// }
	// });
	// }

	private void addLinkToTable() {
		// InputDialog dialog = new InputDialog(Display.getDefault()
		// .getActiveShell(), "New related link",
		// "Enter new related link for this task", "", null);
		// dialog.open();
		// String url = null;
		// String link = dialog.getValue();
		// if (link != null) {
		// if (!(link.startsWith("http://") || link.startsWith("https://"))) {
		// url = "http://" + link;
		// } else {
		// url = link;
		// }
		// tableViewer.add(url);
		// }
	}

	private void removeLinkFromTable() {
		String url = (String) ((IStructuredSelection) treeViewer.getSelection()).getFirstElement();
		if (url != null) {
			ContextCorePlugin.getContextManager().delete(ContextCorePlugin.getContextManager().getElement(url));
		}
		treeViewer.setInput(getWebDocs());
	}

	private void defineActions() {
		delete = new Action() {
			@Override
			public void run() {
				removeLinkFromTable();
			}
		};
		delete.setText("Mark as Uninteresting");
		delete.setToolTipText("Mark as Uninteresting");
		delete.setImageDescriptor(TaskListImages.REMOVE);

		add = new Action() {
			@Override
			public void run() {
				addLinkToTable();
			}
		};
		add.setText("Add");
		add.setToolTipText("Add");
		// add.setImageDescriptor(MylarImages.REMOVE);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				// manager.add(add);
				manager.add(delete);
			}
		});
		Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
		treeViewer.getControl().setMenu(menu);
		// getSite().registerContextMenu(menuMgr, tableViewer);
	}

	private void openURLinBrowser(String url) {
		try {
			IWebBrowser browser = null;
			int flags = 0;
			if (WorkbenchBrowserSupport.getInstance().isInternalWebBrowserAvailable()) {
				flags = WorkbenchBrowserSupport.AS_EDITOR | WorkbenchBrowserSupport.LOCATION_BAR
						| WorkbenchBrowserSupport.NAVIGATION_BAR;

			} else {
				flags = WorkbenchBrowserSupport.AS_EXTERNAL | WorkbenchBrowserSupport.LOCATION_BAR
						| WorkbenchBrowserSupport.NAVIGATION_BAR;
			}
			browser = WorkbenchBrowserSupport.getInstance().createBrowser(flags, "org.eclipse.mylyn.tasklist",
					"Mylar Context Browser", "tasktooltip");
			browser.openURL(new URL(url));
		} catch (PartInitException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "URL not found", url
					+ " could not be opened");
		} catch (MalformedURLException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "URL not found", url
					+ " could not be opened");
		}
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	// private class RelatedLinksCellModifier implements ICellModifier,
	// IColorProvider {
	// RelatedLinksCellModifier() {
	// super();
	//
	// }
	// public boolean canModify(Object element, String property) {
	// return true;
	// }
	// public Object getValue(Object element, String property) {
	// Object res = null;
	// if (element instanceof String) {
	// String url = (String) element;
	// openURLinBrowser(url);
	// res = (String) element;
	// }
	// return res;
	// }
	// public void modify(Object element, String property, Object value) {
	// return;
	// }
	//		
	// public Color getForeground(Object element) {
	// return HYPERLINK;
	// }
	//		
	// public Color getBackground(Object element) {
	// return null;
	// }
	// }

	// private class RelatedLinksTableSorter extends ViewerSorter {
	//
	// public final static int LABEL = 1;
	//
	// private int criteria;
	//
	// public RelatedLinksTableSorter(int criteria) {
	// super();
	// this.criteria = criteria;
	// }
	//
	// @Override
	// public int compare(Viewer viewer, Object o1, Object o2) {
	// String s1 = (String) o1;
	// String s2 = (String) o2;
	// switch (criteria) {
	// case LABEL:
	// return compareLabel(s1, s2);
	// default:
	// return 0;
	// }
	// }
	//
	// protected int compareLabel(String s1, String s2) {
	// return s1.compareTo(s2);
	// }
	//
	// public int getCriteria() {
	// return criteria;
	// }
	// }
}
