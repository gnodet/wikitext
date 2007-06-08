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

package org.eclipse.mylyn.internal.web.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.mylyn.internal.web.FocusedWebPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class WebImages {

	private static ImageRegistry imageRegistry;

	public static final String T_OBJ = "obj16";

	public static final String T_TOOL = "etool16";

	public static final String T_VIEW = "eview16";

	private static final URL baseURL = FocusedWebPlugin.getDefault().getBundle().getEntry("/icons/");
	
	public static final ImageDescriptor WEB_PAGE = create(T_OBJ, "web-page.png");

	public static final ImageDescriptor WEB_SITE = create(T_OBJ, "web-site.gif");
	
	public static final ImageDescriptor WEB_ROOT = create(T_OBJ, "web-root.png");
	
	public static ImageDescriptor create(String prefix, String name) {
		return create(prefix, name, baseURL);
	}

	public static ImageDescriptor create(String prefix, String name, URL baseURL) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name, baseURL));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static URL makeIconFileURL(String prefix, String name, URL baseURL) throws MalformedURLException {
		if (baseURL == null)
			throw new MalformedURLException();

		StringBuffer buffer = new StringBuffer(prefix);
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
		if (imageDescriptor == null) {
			return null;
		} 
		
		ImageRegistry imageRegistry = getImageRegistry();
		Image image = imageRegistry.get("" + imageDescriptor.hashCode());
		if (image == null) {
			image = imageDescriptor.createImage();
			imageRegistry.put("" + imageDescriptor.hashCode(), image);
		}
		return image;
	}
}
