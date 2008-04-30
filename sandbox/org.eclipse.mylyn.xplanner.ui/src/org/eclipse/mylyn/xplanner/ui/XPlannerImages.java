/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.mylyn.internal.commons.ui.TaskListImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
@SuppressWarnings("restriction")
public class XPlannerImages {

	private static ImageRegistry imageRegistry;

	private static final String T_VIEW = "eview16"; //$NON-NLS-1$

	private static final URL baseURL = XPlannerMylynUIPlugin.getDefault() == null ? null
			: XPlannerMylynUIPlugin.getDefault().getBundle().getEntry("/icons/"); //$NON-NLS-1$ 

	public static final ImageDescriptor OVERLAY_XPLANNER = create(T_VIEW, "overlay-xplanner.gif"); //$NON-NLS-1$

	public static final ImageDescriptor OVERLAY_ITERATION = create(T_VIEW, "overlay-iteration.gif"); //$NON-NLS-1$

	public static final ImageDescriptor OVERLAY_USER_STORY = create(T_VIEW, "overlay-userstory.gif"); //$NON-NLS-1$

	public static final ImageDescriptor OVERLAY_TASK = create(T_VIEW, "overlay-task.gif"); //$NON-NLS-1$

	public static final ImageDescriptor TREEITEM_PROJECT = create(T_VIEW, "treeitem-project.gif"); //$NON-NLS-1$

	public static final ImageDescriptor TREEITEM_ITERATION = create(T_VIEW, "treeitem-iteration.gif"); //$NON-NLS-1$

	public static final ImageDescriptor TREEITEM_USER_STORY = create(T_VIEW, "treeitem-userstory.gif"); //$NON-NLS-1$

	public static final ImageDescriptor TREEITEM_PROJECT_OVERLAY = create(TREEITEM_PROJECT, OVERLAY_XPLANNER);

	public static final ImageDescriptor TREEITEM_ITERATION_OVERLAY = create(TREEITEM_ITERATION, OVERLAY_XPLANNER);

	public static final ImageDescriptor TREEITEM_USER_STORY_OVERLAY = create(TREEITEM_USER_STORY, OVERLAY_XPLANNER);

	private XPlannerImages() {

	}

	private static ImageDescriptor create(String prefix, String name) {
		ImageDescriptor id;
		try {
			id = ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
		} catch (MalformedURLException e) {
			id = ImageDescriptor.getMissingImageDescriptor();
		}

		return id;
	}

	private static ImageDescriptor create(ImageDescriptor imageDescriptor, ImageDescriptor overlayDescriptor) {
		return new TaskListImageDescriptor(imageDescriptor, overlayDescriptor, false, false); // bottom right
	}

	private static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
		if (baseURL == null) {
			throw new MalformedURLException();
		}

		StringBuilder buffer = new StringBuilder(prefix);
		buffer.append('/');
		buffer.append(name);
		return new URL(baseURL, buffer.toString());
	}

	private static ImageRegistry getImageRegistry() {
		if (imageRegistry == null) {
			imageRegistry = new ImageRegistry();
		}

		return imageRegistry;
	}

	/**
	 * Lazily initializes image map.
	 */
	public static Image getImage(ImageDescriptor imageDescriptor) {
		ImageRegistry imageRegistry = getImageRegistry();

		Image image = imageRegistry.get("" + imageDescriptor.hashCode()); //$NON-NLS-1$
		if (image == null) {
			image = imageDescriptor.createImage();
			imageRegistry.put("" + imageDescriptor.hashCode(), image); //$NON-NLS-1$
		}

		return image;
	}

}
