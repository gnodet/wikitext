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

package org.eclipse.mylyn.internal.sandbox.bridge.bugs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTask;
import org.eclipse.mylyn.internal.tasks.ui.editors.ContentOutlineTools;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskOutlineNode;
import org.eclipse.mylyn.tasks.ui.editors.RepositoryTaskSelection;
import org.eclipse.ui.views.markers.internal.ProblemMarker;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class BugzillaStructureBridge extends AbstractContextStructureBridge {

	public final static String CONTENT_TYPE = "bugzilla";

	public List<AbstractRelationProvider> providers;

	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}

	public BugzillaStructureBridge() {
		super();
		providers = new ArrayList<AbstractRelationProvider>();
		// providers.add(MylarBugsPlugin.getReferenceProvider());
	}

	/**
	 * Handle format: <server-name:port>;<bug-taskId>;<comment#>
	 * 
	 * Use: OutlineTools ???
	 */
	@Override
	public String getHandleIdentifier(Object object) {
		if (object instanceof RepositoryTaskOutlineNode) {
			RepositoryTaskOutlineNode node = (RepositoryTaskOutlineNode) object;
			return ContentOutlineTools.getHandle(node);
		} else if (object instanceof RepositoryTaskSelection) {
			RepositoryTaskSelection n = (RepositoryTaskSelection) object;
			return ContentOutlineTools.getHandle(n);
		}
		return null;
	}

//	private BugzillaReport result;

	/**
	 * TODO: this will not return a non-cached handle
	 */
	@Override
	public Object getObjectForHandle(final String handle) {
//		result = null;
//
//		// HACK: determine appropriate repository
//		final TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepositoryForActiveTask(
//				BugzillaPlugin.REPOSITORY_KIND);
//
//		String[] parts = handle.split(";");
//		if (parts.length >= 2) {
//			String server = parts[0];
//			final int taskId = Integer.parseInt(parts[1]);
//
//			final String bugHandle = server + ";" + taskId;
//
//			int commentNumber = -1;
//			if (parts.length == 3) {
//				commentNumber = Integer.parseInt(parts[2]);
//			}
//
//			// get the bugzillaOutlineNode for the element
//			IEditorPart editorPart = null;
//			try {
//				editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
//			} catch (NullPointerException e) {
//				// do nothing, this just means that there is no active page
//			}
//			if (editorPart != null && editorPart instanceof AbstractTaskEditor) {
//				AbstractTaskEditor abe = ((AbstractTaskEditor) editorPart);
//				RepositoryTaskOutlineNode node = abe.getOutlineModel();
//				return findNode(node, commentNumber);
//			}
//
//			BugzillaReportElement reportNode = MylarBugsPlugin.getReferenceProvider().getCached(handle);
//
//			// try to get from the cache, if it doesn't exist, startup an
//			// operation to get it
//			result = MylarBugsPlugin.getDefault().getCache().getFromCache(bugHandle);
//			if (result == null && reportNode != null) {
//				return reportNode;
//			} else if (result == null && reportNode == null) {
//				IRunnableWithProgress op = new IRunnableWithProgress() {
//					public void run(IProgressMonitor monitor) {
//						monitor.beginTask("Downloading Bug# " + taskId, IProgressMonitor.UNKNOWN);
//						try {
//							Proxy proxySettings = MylarTaskListPlugin.getDefault().getProxySettings();
//							// XXX: move this
//							result = BugzillaRepositoryUtil.getBug(repository.getUrl(), repository.getUserName(), repository.getPassword(), proxySettings, repository.getCharacterEncoding(), taskId);
//							if (result != null) {
//								MylarBugsPlugin.getDefault().getCache().cache(bugHandle, result);
//							}
//						} catch (Exception e) {
//							result = null;
//						}
//					}
//				};
//
//				IProgressService service = PlatformUI.getWorkbench().getProgressService();
//				try {
//					service.run(false, false, op);
//				} catch (InvocationTargetException e) {
//					// RepositoryOperation was canceled
//				} catch (InterruptedException e) {
//					// Handle the wrapped exception
//				}
//				return null;
//			}
//		}
		return null;
	}

//	private RepositoryTaskOutlineNode findNode(RepositoryTaskOutlineNode startNode, int commentNumber) {
//
//		if (commentNumber == -1) {
//			return startNode;
//		} else if (startNode.getComment() != null && startNode.getComment().getNumber() == commentNumber - 1) {
//			return startNode;
//		} else if (startNode.isCommentHeader() && commentNumber == 1) {
//			return startNode;
//		} else if (startNode.isDescription() && commentNumber == 0) {
//			return startNode;
//		}
//
//		RepositoryTaskOutlineNode[] children = startNode.getChildren();
//		for (int i = 0; i < children.length; i++) {
//			RepositoryTaskOutlineNode n = findNode(children[i], commentNumber);
//			if (n != null)
//				return n;
//		}
//		return null;
//	}

	@Override
	public String getParentHandle(String handle) {

		// check so that we don't need to try to get the parent if we are
		// already at the bug report
		if (!handle.matches(".*;.*;.*"))
			return null;

		RepositoryTaskOutlineNode bon = (RepositoryTaskOutlineNode) getObjectForHandle(handle);
		if (bon != null && bon.getParent() != null)
			return ContentOutlineTools.getHandle(bon.getParent());
		else
			return null;
		// String [] parts = handle.split(";");
		// if (parts.length == 1){
		// return null;
		// }else if (parts.length > 2) {
		// String newHandle = "";
		// for(int i = 0; i < parts.length - 1; i++)
		// newHandle += parts[i] + ";";
		// return newHandle.substring(0, newHandle.length() - 1);
		// // return handle.substring(0, handle.lastIndexOf(";"));
		// }
		// return null;
	}

	@Override
	public String getName(Object object) {
		if (object instanceof RepositoryTaskOutlineNode) {
			RepositoryTaskOutlineNode b = (RepositoryTaskOutlineNode) object;
			return ContentOutlineTools.getName(b);
		} else if (object instanceof BugzillaReportInfo) {
			BugzillaTask hit = ((BugzillaReportInfo) object).getHit();
			return hit.getRepositoryUrl() + ": Bug#: " + hit.getTaskId() + ": " + hit.getSummary();
		}
		return "";
	}

	@Override
	public boolean canBeLandmark(String handle) {
		return false;
	}

	@Override
	public boolean acceptsObject(Object object) {
		return object instanceof RepositoryTaskOutlineNode || object instanceof RepositoryTaskSelection;
	}

	@Override
	public boolean canFilter(Object element) {
		return true;
	}

	@Override
	public boolean isDocument(String handle) {
		return (handle.indexOf(';') == handle.lastIndexOf(';') && handle.indexOf(";") != -1);
	}

	public String getHandleForMarker(ProblemMarker marker) {
		return null;
	}

	@Override
	public String getContentType(String elementHandle) {
		return getContentType();
	}

	@Override
	public String getHandleForOffsetInObject(Object resource, int offset) {
		return null;
	}

	@Override
	public List<String> getChildHandles(String handle) {
		return Collections.emptyList();
	}
}
