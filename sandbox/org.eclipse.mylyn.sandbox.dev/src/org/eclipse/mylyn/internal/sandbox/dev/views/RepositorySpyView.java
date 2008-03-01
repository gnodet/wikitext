package org.eclipse.mylyn.internal.sandbox.dev.views;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The
 * sample creates a dummy model on the fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the
 * same model objects using different labels and icons, if needed. Alternatively, a single label provider can be shared
 * between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

public class RepositorySpyView extends ViewPart {
	class RefreshAction extends Action {
		RepositorySpyView view = null;

		public RefreshAction(RepositorySpyView repositorySpyView) {
			// TODO Auto-generated constructor stub
			view = repositorySpyView;
		}

		@Override
		public void run() {
			viewer.getContentProvider().inputChanged(viewer, null, null);
			view.refresh();
		}
	}

	private TreeViewer viewer;

	private DrillDownAdapter drillDownAdapter;

	private Action refreshViewAction;

	private Action clearConfigLastUpdateAction;

	private Action doubleClickAction;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	class KeyValuePair extends Object {
		Object key;

		Object value;

		Object parent;

		public KeyValuePair(Object key, Object value, Object parent) {
			super();
			this.key = key;
			this.value = value;
			this.parent = parent;
		}

		@Override
		public String toString() {
			return key.toString() + " : " + value.toString();
		}

		/**
		 * @return the key
		 */
		public Object getKey() {
			return key;
		}

		/**
		 * @return the value
		 */
		public Object getValue() {
			return value;
		}

		/**
		 * @return the parent
		 */
		public Object getParent() {
			return parent;
		}
	}

	private KeyValuePair[] MaptoArray(TaskRepository repository) {
		Map<String, String> map = repository.getProperties();
		KeyValuePair[] results = new KeyValuePair[map.size()];

		int i = 0;
		for (Object key : map.keySet()) {
			results[i++] = new KeyValuePair(key, map.get(key), repository);
		}
		return results;
	}

	public void refresh() {
		// TODO Auto-generated method stub
		viewer.getContentProvider().inputChanged(viewer, null, null);
		viewer.refresh();
	}

	class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {
		List<TaskRepository> repositories = null;

		@SuppressWarnings("unchecked")
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput instanceof List || newInput == null) {
				repositories = (List<TaskRepository>) newInput;
			}
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (repositories == null) {
					initialize();
				}
				return getChildren(repositories);
			}
			return getChildren(parent);
		}

		public Object getParent(Object child) {
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof List) {
				return repositories.toArray();
			}
			if (parent instanceof TaskRepository) {
				TaskRepository repository = (TaskRepository) parent;
				Object[] result = MaptoArray(repository);
				return result;
			}
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof List) {
				return repositories.size() > 0;
			}
			if (parent instanceof TaskRepository) {
				TaskRepository repository = (TaskRepository) parent;
				return repository.getProperties().size() > 0;
			}
			return false;
		}

		private void initialize() {

			if (repositories == null) {
				repositories = TasksUiPlugin.getRepositoryManager().getAllRepositories();
			}
		}
	}

	class ViewLabelProvider extends LabelProvider {

		@Override
		public String getText(Object obj) {
			if (obj instanceof List) {
				return "Repositories";
			}
			if (obj instanceof TaskRepository) {
				TaskRepository repository = (TaskRepository) obj;
				return repository.getRepositoryLabel();
			}
			return obj.toString();
		}

		@Override
		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof List) {
				return TasksUiImages.REPOSITORIES.createImage();
			}
			if (obj instanceof TaskRepository) {
				// TODO: decorate with proper overlay
				return TasksUiImages.REPOSITORY.createImage();
			}
			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
	}

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public RepositorySpyView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				RepositorySpyView.this.fillContextMenu(manager);
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
		manager.add(refreshViewAction);
		manager.add(new Separator());
		manager.add(clearConfigLastUpdateAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(refreshViewAction);
		manager.add(clearConfigLastUpdateAction);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshViewAction);
		manager.add(clearConfigLastUpdateAction);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		refreshViewAction = new RefreshAction(this);
		refreshViewAction.setText("Refresh");
		refreshViewAction.setToolTipText("Rebuild Repository List");
		// refereshViewAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
		// getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		refreshViewAction.setImageDescriptor(TasksUiImages.REFRESH);

		clearConfigLastUpdateAction = new Action() {
			@Override
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (null != obj && obj instanceof KeyValuePair) {
					if (askDeleteMessage((KeyValuePair) obj)) {
						KeyValuePair kvp = (KeyValuePair) obj;
						((TaskRepository) kvp.getParent()).removeProperty((String) kvp.getKey());
						TasksUiPlugin.getRepositoryManager().saveRepositories(
								TasksUiPlugin.getDefault().getRepositoriesFilePath());
						viewer.refresh();
					}
				} else if (null == obj) {
					List<TaskRepository> repositories = TasksUiPlugin.getRepositoryManager().getAllRepositories();
					for (TaskRepository repository : repositories) {
						repository.removeProperty("config.lastupdate");
						TasksUiPlugin.getRepositoryManager().saveRepositories(
								TasksUiPlugin.getDefault().getRepositoriesFilePath());
						viewer.refresh();
					}
					showMessage("Removed config.lastupdate property");
				}
			}
		};
		clearConfigLastUpdateAction.setText("Clear Config Update");
		clearConfigLastUpdateAction.setToolTipText("Forget last config update date");
		clearConfigLastUpdateAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_TOOL_DELETE));
		doubleClickAction = new Action() {
			@Override
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (obj instanceof KeyValuePair) {
					if (askDeleteMessage((KeyValuePair) obj)) {
						KeyValuePair kvp = (KeyValuePair) obj;
						((TaskRepository) kvp.getParent()).removeProperty((String) kvp.getKey());
						TasksUiPlugin.getRepositoryManager().saveRepositories(
								TasksUiPlugin.getDefault().getRepositoriesFilePath());
						viewer.refresh();
					}
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Repository Spy View", message);
	}

	private boolean askDeleteMessage(KeyValuePair obj) {
		return MessageDialog.openConfirm(viewer.getControl().getShell(), "Repository Spy View",
				"Do you want to clear property '" + obj.getKey().toString() + "' from '" + obj.getParent().toString()
						+ "'");
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
