/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlExtension;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiImages;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.DoiOrderSorter;
import org.eclipse.mylyn.internal.context.ui.views.ContextContentProvider;
import org.eclipse.mylyn.internal.context.ui.views.QuickOutlinePatternAndInterestFilter;
import org.eclipse.mylyn.internal.sandbox.ui.DelegatingContextLabelProvider;
import org.eclipse.mylyn.internal.sandbox.ui.SandboxUiPlugin;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.misc.StringMatcher;

/**
 * Derived from {@link QuickOutlinePopupDialog}
 * 
 * @author Tracy Mew
 */
public class RelatedElementsPopupDialog extends PopupDialog implements IInformationControl,
		IInformationControlExtension, IInformationControlExtension2, DisposeListener {

	public static final String ID = "org.eclipse.mylyn.context.ui.navigator.context.related";

	private TreeViewer viewer;

	private Text fFilterText;

	private StringMatcher fStringMatcher;

	private QuickOutlinePatternAndInterestFilter namePatternFilter;

	private final DelegatingContextLabelProvider labelProvider = new DelegatingContextLabelProvider();

	private final DegreeZeroAction zero = new DegreeZeroAction();

	private final DegreeOneAction one = new DegreeOneAction();

	private final DegreeTwoAction two = new DegreeTwoAction();

	private final DegreeThreeAction three = new DegreeThreeAction();

	private final DegreeFourAction four = new DegreeFourAction();

	private final DegreeFiveAction five = new DegreeFiveAction();

	private int degree = 2;

	// TODO e3.4 move to new api
	@SuppressWarnings("deprecation")
	public RelatedElementsPopupDialog(Shell parent, int shellStyle) {
		super(parent, shellStyle, true, true, true, true, null, "Context Search");
		ContextCore.getContextManager().addListener(REFRESH_UPDATE_LISTENER);
		for (AbstractRelationProvider provider : ContextCorePlugin.getDefault().getRelationProviders()) {
			provider.setEnabled(true);
		}
		create();
	}

	/**
	 * For testing.
	 */
	private boolean syncExecForTesting = true;

	private final IInteractionContextListener REFRESH_UPDATE_LISTENER = new IInteractionContextListener() {

		public void interestChanged(List<IInteractionElement> nodes) {
			refresh(nodes.get(nodes.size() - 1), false);
		}

		public void contextActivated(IInteractionContext taskscape) {
			refreshRelatedElements();
			refresh(null, true);
		}

		public void contextDeactivated(IInteractionContext taskscape) {
			refresh(null, true);
		}

		public void contextCleared(IInteractionContext context) {
			refresh(null, true);
		}

		public void landmarkAdded(IInteractionElement node) {
			refresh(null, true);
		}

		public void landmarkRemoved(IInteractionElement node) {
			refresh(null, true);
		}

		public void relationsChanged(IInteractionElement node) {
			refresh(node, true);
		}

		public void elementDeleted(IInteractionElement node) {
			refresh(null, true);
		}
	};

	@Override
	protected Control createDialogArea(Composite parent) {
		createViewer(parent);
		createUIListenersTreeViewer();
		addDisposeListener(this);

		return viewer.getControl();
	}

	private void createViewer(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		viewer.setUseHashlookup(true);

		viewer.setContentProvider(new ContextContentProvider(viewer.getTree(), parent.getShell(), true));

		viewer.setLabelProvider(new DecoratingLabelProvider(labelProvider, PlatformUI.getWorkbench()
				.getDecoratorManager()
				.getLabelDecorator()));

		viewer.setSorter(new DoiOrderSorter());

		try {
			viewer.getControl().setRedraw(false);
			viewer.setInput(getShell());
		} finally {
			refreshRelatedElements();
			viewer.getControl().setRedraw(true);
		}
	}

	private void createUIListenersTreeViewer() {
		final Tree tree = viewer.getTree();
		tree.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.character == 0x1B) {
					// Dispose on ESC key press
					dispose();
				}
			}

			public void keyReleased(KeyEvent e) {
				// ignore
			}
		});

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				handleTreeViewerMouseUp(tree, e);
			}
		});

		tree.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				// ignore
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				gotoSelectedElement();
			}
		});
	}

	private void handleTreeViewerMouseUp(final Tree tree, MouseEvent e) {
		if ((tree.getSelectionCount() < 1) || (e.button != 1) || (tree.equals(e.getSource()) == false)) {
			return;
		}
		// Selection is made in the selection changed listener
		Object object = tree.getItem(new Point(e.x, e.y));
		TreeItem selection = tree.getSelection()[0];
		if (selection.equals(object)) {
			gotoSelectedElement();
		}
	}

	private void gotoSelectedElement() {
		Object selectedElement = getSelectedElement();
		if (selectedElement == null) {
			return;
		}
		IInteractionElement node = null;
		if (selectedElement instanceof IInteractionElement) {
			node = (IInteractionElement) selectedElement;
		} else if (!(selectedElement instanceof IInteractionRelation)) {
			AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(selectedElement);
			String handle = bridge.getHandleIdentifier(selectedElement);
			node = ContextCore.getContextManager().getElement(handle);
		}
		if (node != null) {
			ContextUiPlugin.getDefault().getUiBridge(node.getContentType()).open(node);
		}
		dispose();
	}

	private Object getSelectedElement() {
		if (viewer == null) {
			return null;
		}
		return ((IStructuredSelection) viewer.getSelection()).getFirstElement();
	}

	public void addDisposeListener(DisposeListener listener) {
		getShell().addDisposeListener(listener);
	}

	public void addFocusListener(FocusListener listener) {
		getShell().addFocusListener(listener);
	}

	public Point computeSizeHint() {
		// Note that it already has the persisted size if persisting is enabled.
		return getShell().getSize();
	}

	public boolean isFocusControl() {
		if (viewer.getControl().isFocusControl() || fFilterText.isFocusControl()) {
			return true;
		}
		return false;
	}

	public void removeDisposeListener(DisposeListener listener) {
		getShell().removeDisposeListener(listener);
	}

	public void removeFocusListener(FocusListener listener) {
		getShell().removeFocusListener(listener);
	}

	public void setBackgroundColor(Color background) {
		applyBackgroundColor(background, getContents());
	}

	public void setFocus() {
		viewer.refresh();
		viewer.getControl().setFocus();
		fFilterText.setFocus();
		getShell().forceFocus();
	}

	public void setForegroundColor(Color foreground) {
		applyForegroundColor(foreground, getContents());
	}

	public void setInformation(String information) {
		// See IInformationControlExtension2
	}

	// TODO e3.4 move to new api
	@SuppressWarnings("deprecation")
	public void setLocation(Point location) {
		/*
		 * If the location is persisted, it gets managed by PopupDialog - fine. Otherwise, the location is
		 * computed in Window#getInitialLocation, which will center it in the parent shell / main
		 * monitor, which is wrong for two reasons:
		 * - we want to center over the editor / subject control, not the parent shell
		 * - the center is computed via the initalSize, which may be also wrong since the size may 
		 *   have been updated since via min/max sizing of AbstractInformationControlManager.
		 * In that case, override the location with the one computed by the manager. Note that
		 * the call to constrainShellSize in PopupDialog.open will still ensure that the shell is
		 * entirely visible.
		 */
		if ((getPersistBounds() == false) || (getDialogSettings() == null)) {
			getShell().setLocation(location);
		}
	}

	public void setSize(int width, int height) {
		getShell().setSize(width, height);
	}

	public void setSizeConstraints(int maxWidth, int maxHeight) {
		// Ignore
	}

	public void setVisible(boolean visible) {
		if (visible) {
			open();
		} else {
			saveDialogBounds(getShell());
			getShell().setVisible(false);
		}
	}

	public boolean hasContents() {
		if ((viewer == null) || (viewer.getInput() == null)) {
			return false;
		}
		return true;
	}

	public void setInput(Object input) {
		// Input comes from PDESourceInfoProvider.getInformation2()
		// The input should be a model object of some sort
		// Turn it into a structured selection and set the selection in the tree
		if (input != null) {
			viewer.setSelection(new StructuredSelection(input));
		}
	}

	public void widgetDisposed(DisposeEvent e) {
		// Note: We do not reuse the dialog
		viewer = null;
		fFilterText = null;
	}

	@Override
	protected Control createTitleControl(Composite parent) {
		// Applies only to dialog title - not body.  See createDialogArea
		// Create the text widget
		createUIWidgetFilterText(parent);
		// Add listeners to the text widget
		createUIListenersFilterText();
		// Return the text widget
		return fFilterText;
	}

	private void createUIWidgetFilterText(Composite parent) {
		// Create the widget
		fFilterText = new Text(parent, SWT.NONE);
		// Set the font 
		GC gc = new GC(parent);
		gc.setFont(parent.getFont());
		FontMetrics fontMetrics = gc.getFontMetrics();
		gc.dispose();
		// Create the layout
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = Dialog.convertHeightInCharsToPixels(fontMetrics, 1);
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.CENTER;
		fFilterText.setLayoutData(data);
	}

	private void createUIListenersFilterText() {
		fFilterText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 0x0D) {
					// Return key was pressed
					gotoSelectedElement();
				} else if (e.keyCode == SWT.ARROW_DOWN) {
					// Down key was pressed
					viewer.getTree().setFocus();
				} else if (e.keyCode == SWT.ARROW_UP) {
					// Up key was pressed
					viewer.getTree().setFocus();
				} else if (e.character == 0x1B) {
					// Escape key was pressed
					dispose();
				}
			}

			public void keyReleased(KeyEvent e) {
				// NO-OP
			}
		});
		// Handle text modify events
		fFilterText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String text = ((Text) e.widget).getText();
				int length = text.length();
				if (length > 0) {
					// Append a '*' pattern to the end of the text value if it
					// does not have one already
					if (text.charAt(length - 1) != '*') {
						text = text + '*';
					}
					// Prepend a '*' pattern to the beginning of the text value
					// if it does not have one already
					if (text.charAt(0) != '*') {
						text = '*' + text;
					}
				}
				// Set and update the pattern
				setMatcherString(text, true);
			}
		});
	}

	/**
	 * Sets the patterns to filter out for the receiver.
	 * <p>
	 * The following characters have special meaning: ? => any character * => any string
	 * </p>
	 * 
	 * @param pattern
	 *            the pattern
	 * @param update
	 *            <code>true</code> if the viewer should be updated
	 */
	private void setMatcherString(String pattern, boolean update) {
		if (pattern.length() == 0) {
			fStringMatcher = null;
		} else {
			fStringMatcher = new StringMatcher(pattern, true, false);
		}
		// Update the name pattern filter on the tree viewer
		namePatternFilter.setStringMatcher(fStringMatcher);
		// Update the tree viewer according to the pattern
		if (update) {
			stringMatcherUpdated();
		}
	}

	/**
	 * The string matcher has been modified. The default implementation refreshes the view and selects the first matched
	 * element
	 */
	private void stringMatcherUpdated() {
		// Refresh the tree viewer to re-filter
		viewer.getControl().setRedraw(false);
		viewer.refresh();
		viewer.expandAll();
		selectFirstMatch();
		viewer.getControl().setRedraw(true);
	}

	/**
	 * Selects the first element in the tree which matches the current filter pattern.
	 */
	private void selectFirstMatch() {
		Tree tree = viewer.getTree();
		Object element = findFirstMatchToPattern(tree.getItems());
		if (element != null) {
			viewer.setSelection(new StructuredSelection(element), true);
		} else {
			viewer.setSelection(StructuredSelection.EMPTY);
		}
	}

	/**
	 * @param items
	 * @return
	 */
	private Object findFirstMatchToPattern(TreeItem[] items) {
		// Match the string pattern against labels
		ILabelProvider labelProvider = (ILabelProvider) viewer.getLabelProvider();
		// Process each item in the tree
		for (TreeItem item : items) {
			Object element = item.getData();
			// Return the first element if no pattern is set
			if (fStringMatcher == null) {
				return element;
			}
			// Return the element if it matches the pattern
			if (element != null) {
				String label = labelProvider.getText(element);
				if (fStringMatcher.match(label)) {
					return element;
				}
			}
			// Recursively check the elements children for a match
			element = findFirstMatchToPattern(item.getItems());
			// Return the child element match if found
			if (element != null) {
				return element;
			}
		}
		// No match found
		return null;
	}

	/**
	 * fix for bug 109235
	 * 
	 * @param node
	 * @param updateLabels
	 */
	void refresh(final IInteractionElement node, final boolean updateLabels) {
		if (!syncExecForTesting) { // for testing
			// if (viewer != null && !viewer.getTree().isDisposed()) {
			// internalRefresh(node, updateLabels);
			// }
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {
					try {
						internalRefresh(node, updateLabels);
					} catch (Throwable t) {
						StatusHandler.log(new Status(IStatus.WARNING, SandboxUiPlugin.ID_PLUGIN,
								"Context search refresh failed", t));
					}
				}
			});
		} else {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						internalRefresh(node, updateLabels);
					} catch (Throwable t) {
						StatusHandler.log(new Status(IStatus.WARNING, SandboxUiPlugin.ID_PLUGIN,
								"Context search refresh failed", t));
					}
				}
			});
		}

	}

	private void internalRefresh(final IInteractionElement node, boolean updateLabels) {
		Object toRefresh = null;
		if (node != null) {
			AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(
					node.getContentType());
			toRefresh = bridge.getObjectForHandle(node.getHandleIdentifier());
		}
		if (viewer != null && !viewer.getTree().isDisposed()) {
			viewer.getControl().setRedraw(false);
			if (toRefresh != null && containsNode(viewer.getTree(), toRefresh)) {
				viewer.refresh(toRefresh, updateLabels);
			} else if (node == null) {
				viewer.refresh();
			}
			viewer.expandAll();
			viewer.getControl().setRedraw(true);
		}
	}

	private boolean containsNode(Tree tree, Object object) {
		boolean contains = false;
		for (int i = 0; i < tree.getItems().length; i++) {
			TreeItem item = tree.getItems()[i];
			if (object.equals(item.getData())) {
				contains = true;
			}
		}
		return contains;
	}

	public void refreshRelatedElements() {
		try {
			for (AbstractRelationProvider provider : ContextCorePlugin.getDefault().getRelationProviders()) {
				List<AbstractRelationProvider> providerList = new ArrayList<AbstractRelationProvider>();
				providerList.add(provider);
				updateDegreesOfSeparation(providerList, provider.getCurrentDegreeOfSeparation());
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.WARNING, SandboxUiPlugin.ID_PLUGIN,
					"Could not refresh related elements", t));
		}
	}

	public void updateDegreesOfSeparation(Collection<AbstractRelationProvider> providers, int degreeOfSeparation) {
		for (AbstractRelationProvider provider : providers) {
			updateDegreeOfSeparation(provider, degreeOfSeparation);
		}
	}

	public void updateDegreeOfSeparation(AbstractRelationProvider provider, int degreeOfSeparation) {
		ContextCorePlugin.getContextManager().resetLandmarkRelationshipsOfKind(provider.getId());
		ContextUiPlugin.getDefault().getPreferenceStore().setValue(provider.getGenericId(), degreeOfSeparation);
		provider.setDegreeOfSeparation(degreeOfSeparation);
		for (IInteractionElement element : ContextCore.getContextManager().getActiveContext().getInteresting()) {
			if (element.getInterest().isLandmark()) {
				provider.landmarkAdded(element);
			}
		}
	}

	@Override
	protected void fillDialogMenu(IMenuManager dialogMenu) {
		MenuManager degMenu = new MenuManager("Degree of Separation");
		degMenu.add(zero);
		degMenu.add(one);
		degMenu.add(two);
		degMenu.add(three);
		degMenu.add(four);
		degMenu.add(five);
		check(getDegree());
		dialogMenu.add(degMenu);
		dialogMenu.add(new Separator());

		IAction qualifyElements = new ShowQualifiedNamesAction(this);
		dialogMenu.add(qualifyElements);
		dialogMenu.add(new Separator());

		super.fillDialogMenu(dialogMenu);
	}

	public void setQualifiedNameMode(boolean qualifiedNameMode) {
		DelegatingContextLabelProvider.setQualifyNamesMode(qualifiedNameMode);
		refresh(null, true);
	}

	private class DegreeZeroAction extends Action {
		DegreeZeroAction() {
			super(JFaceResources.getString("0: Disabled"), //$NON-NLS-1$
					IAction.AS_CHECK_BOX);
		}

		@Override
		public void run() {
			check(0);
			refreshAction(0);

		}
	}

	private class DegreeOneAction extends Action {
		DegreeOneAction() {
			super(JFaceResources.getString("1: Landmark Resources"), //$NON-NLS-1$
					IAction.AS_CHECK_BOX);
		}

		@Override
		public void run() {
			check(1);
			refreshAction(1);
		}
	}

	private class DegreeTwoAction extends Action {
		DegreeTwoAction() {
			super(JFaceResources.getString("2: Interesting Resources"), //$NON-NLS-1$
					IAction.AS_CHECK_BOX);
		}

		@Override
		public void run() {
			check(2);
			refreshAction(2);
		}
	}

	private class DegreeThreeAction extends Action {
		DegreeThreeAction() {
			super(JFaceResources.getString("3: Interesting Projects"), //$NON-NLS-1$
					IAction.AS_CHECK_BOX);
		}

		@Override
		public void run() {
			check(3);
			refreshAction(3);
		}
	}

	private class DegreeFourAction extends Action {
		DegreeFourAction() {
			super(JFaceResources.getString("4: Project Dependencies"), //$NON-NLS-1$
					IAction.AS_CHECK_BOX);
		}

		@Override
		public void run() {
			check(4);
			refreshAction(4);
		}
	}

	private class DegreeFiveAction extends Action {
		DegreeFiveAction() {
			super(JFaceResources.getString("5: Entire Workspace (slow)"), //$NON-NLS-1$
					IAction.AS_CHECK_BOX);
		}

		@Override
		public void run() {
			check(5);
			refreshAction(5);
		}
	}

	private void refreshAction(int degOfSep) {
		try {
			for (AbstractRelationProvider provider : ContextCorePlugin.getDefault().getRelationProviders()) {
				List<AbstractRelationProvider> providerList = new ArrayList<AbstractRelationProvider>();
				providerList.add(provider);

				if (provider.getCurrentDegreeOfSeparation() != degOfSep) {
					updateDegreesOfSeparation(providerList, degOfSep);
					degree = provider.getCurrentDegreeOfSeparation();
				}
			}

		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.WARNING, SandboxUiPlugin.ID_PLUGIN,
					"Could not refresh related elements", t));
		}
	}

	private int getDegree() {
		for (AbstractRelationProvider provider : ContextCorePlugin.getDefault().getRelationProviders()) {
			degree = provider.getCurrentDegreeOfSeparation();
			break;
		}
		return degree;
	}

	private void check(int degree) {
		zero.setChecked(false);
		one.setChecked(false);
		two.setChecked(false);
		three.setChecked(false);
		four.setChecked(false);
		five.setChecked(false);
		switch (degree) {
		case 0:
			zero.setChecked(true);
			break;
		case 1:
			one.setChecked(true);
			break;
		case 2:
			two.setChecked(true);
			break;
		case 3:
			three.setChecked(true);
			break;
		case 4:
			four.setChecked(true);
			break;
		default:
			five.setChecked(true);
		}
	}

	private class ShowQualifiedNamesAction extends Action {

		public static final String LABEL = "Qualify Member Names";

		public static final String ID = "org.eclipse.mylyn.ui.views.elements.qualify";

		private final RelatedElementsPopupDialog dialog;

		public ShowQualifiedNamesAction(RelatedElementsPopupDialog dialog) {
			super(LABEL, IAction.AS_CHECK_BOX);
			this.dialog = dialog;
			setId(ID);
			setText(LABEL);
			setToolTipText(LABEL);
			setImageDescriptor(ContextUiImages.QUALIFY_NAMES);
			update(ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(ID));
		}

		public void update(boolean on) {
			dialog.setQualifiedNameMode(on);
			setChecked(on);
			ContextUiPlugin.getDefault().getPreferenceStore().setValue(ID, on);
		}

		@Override
		public void run() {
			update(!ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(ID));
		}
	}

	/**
	 * Set to false for testing
	 */
	public void setSyncExecForTesting(boolean asyncRefreshMode) {
		this.syncExecForTesting = asyncRefreshMode;
	}

	public void dispose() {
		ContextCore.getContextManager().removeListener(REFRESH_UPDATE_LISTENER);
		super.close();
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 500);
	}

}
