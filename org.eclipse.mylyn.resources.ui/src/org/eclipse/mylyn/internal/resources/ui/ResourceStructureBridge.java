/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.resources.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.internal.resources.Marker;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.ui.views.markers.internal.ConcreteMarker;

/**
 * @author Mik Kersten
 */
public class ResourceStructureBridge extends AbstractContextStructureBridge {

	public final static String CONTENT_TYPE = ContextCore.CONTENT_TYPE_RESOURCE;

	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}

	@Override
	public String getParentHandle(String handle) {

		IResource resource = (IResource) getObjectForHandle(handle);
		if (resource != null) {
			IContainer parent = resource.getParent();
			// try to adapt to the corresponding content type's parent
			if (resource instanceof IFile) {
				for (String contentType : ContextCore.getChildContentTypes(CONTENT_TYPE)) {
					AbstractContextStructureBridge parentBridge = ContextCore.getStructureBridge(contentType);
					Object adaptedParent = parentBridge.getAdaptedParent(resource);
					// HACK: only returns first
					if (adaptedParent != null) {
						return parentBridge.getHandleIdentifier(adaptedParent);
					}
				}
			}
			return getHandleIdentifier(parent);
		} else {
			return null;
		}
	}

	@Override
	public List<String> getChildHandles(String handle) {
		Object object = getObjectForHandle(handle);
		if (object instanceof IResource) {
			IResource resource = (IResource) object;
			if (resource instanceof IContainer) {
				IContainer container = (IContainer) resource;
				IResource[] children;
				try {
					// make sure that we dont try to get the children of a closed project as this can cause an exception
					if (container.isAccessible()) {
						children = container.members();
						List<String> childHandles = new ArrayList<String>();
						for (IResource element : children) {
							String childHandle = getHandleIdentifier(element);
							if (childHandle != null) {
								childHandles.add(childHandle);
							}
						}
						return childHandles;
					}
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, ResourcesUiBridgePlugin.ID_PLUGIN, "" //$NON-NLS-1$
							+ "Could not get child", e)); //$NON-NLS-1$
				}
			} else if (resource instanceof IFile) {
				// delegate to child bridges
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Uses java-style path for projects.
	 */
	@Override
	public String getHandleIdentifier(Object object) {
		if (object instanceof IProject) {
			String path = ((IResource) object).getFullPath().toPortableString();
			String javaCoreStylePath = "=" + path.substring(1); //$NON-NLS-1$
			return javaCoreStylePath;
		}
		if (object instanceof IResource) {
			return ((IResource) object).getFullPath().toPortableString();

		} else if (object instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) object;
			Object adapter = adaptable.getAdapter(IResource.class);
			if (adapter instanceof IResource) {
				return ((IResource) adapter).getFullPath().toPortableString();
			}

		}
		return null;
	}

	@Override
	public Object getObjectForHandle(String handle) {
		if (handle == null) {
			return null;
		}
		IPath path = new Path(handle);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (path.segmentCount() == 1) {
			String projectName = handle.substring(1);
			try {
				return workspace.getRoot().getProject(projectName);
			} catch (IllegalArgumentException e) {
				return null;
			}
		} else if (path.segmentCount() > 1) {
			return workspace.getRoot().findMember(path);
		} else {
			return null;
		}
	}

	@Override
	public String getLabel(Object object) {
		if (object instanceof IResource) {
			return ((IResource) object).getName();
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	@Override
	public boolean canBeLandmark(String handle) {
		Object element = getObjectForHandle(handle);
		return element instanceof IFile;
	}

	@Override
	public boolean acceptsObject(Object object) {
		if (object instanceof IResource) {
			return true;
		}
		if (object instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) object;
			Object adapter = adaptable.getAdapter(IResource.class);
			if (adapter instanceof IResource) {
				return true;
			}

		}
		return false;
	}

	@Override
	public boolean canFilter(Object element) {
		return true;
	}

	@Override
	public boolean isDocument(String handle) {
		return getObjectForHandle(handle) instanceof IFile;
	}

	@Override
	public String getHandleForOffsetInObject(Object object, int offset) {
		IResource markerResource = null;
		try {
			if (object instanceof ConcreteMarker) {
				markerResource = ((ConcreteMarker) object).getMarker().getResource();
			} else if (object instanceof Marker) {
				markerResource = ((Marker) object).getResource();
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}

		// we can only get a handle for a marker with the resource plugin.xml
		try {
			if (markerResource instanceof IFile) {
				IFile file = (IFile) markerResource;
				return getHandleIdentifier(file);
			}
			return null;
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ResourcesUiBridgePlugin.ID_PLUGIN,
					"Could not find element for: \"" + object + "\"", t)); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
	}

	@Override
	public String getContentType(String elementHandle) {
		return getContentType();
	}
}
