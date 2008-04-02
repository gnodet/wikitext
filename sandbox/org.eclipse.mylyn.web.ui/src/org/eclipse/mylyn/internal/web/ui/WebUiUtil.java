/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.web.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;

/**
 * @author Mik Kersten
 */
public class WebUiUtil {

	/**
	 * Activates instead of opening if browser with that URL is already open.
	 */
	public static void openUrl(WebResource webResource) {
		String url = webResource.getUrl();
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IEditorReference[] editorReferences = page.getEditorReferences();
			for (IEditorReference editorReference : editorReferences) {
				IEditorInput input = editorReference.getEditorInput();
				if (input instanceof WebBrowserEditorInput) {
					WebBrowserEditorInput webInput = (WebBrowserEditorInput) input;
					URL alreadyOpenUrl = webInput.getURL();
					if (alreadyOpenUrl != null && alreadyOpenUrl.toExternalForm().equals(url)) {
						page.activate(editorReference.getEditor(true));
						return;
					}
				}
			}
			TasksUiUtil.openTask(url);
		} catch (PartInitException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "URL not found", url
					+ " could not be opened");
		}
	}

	/**
	 * @param repositoryUrl
	 *            The URL of the web site including protocol. E.g. <code>http://foo.bar</code> or
	 *            <code>https://foo.bar/baz</code>
	 * @return a 16*16 favicon, or null if no favicon found
	 * @throws MalformedURLException
	 */
	public static ImageDescriptor getFaviconForUrl(String repositoryUrl) throws MalformedURLException {
		URL url = new URL(repositoryUrl);

		String host = url.getHost();
		String protocol = url.getProtocol();
		String favString = protocol + "://" + host + "/favicon.ico";

		URL favUrl = new URL(favString);
		try {
			ImageDescriptor desc = ImageDescriptor.createFromURL(favUrl);
			if (desc != null && desc.getImageData() != null) {
				if (desc.getImageData().width != 16 && desc.getImageData().height != 16) {
					ImageData data = desc.getImageData().scaledTo(16, 16);
					return ImageDescriptor.createFromImageData(data);
				}
			}
			return desc;
		} catch (SWTException e) {
			return null;
		}
	}

	public static String stripProtocol(String url) {
		if (url == null) {
			return null;
		}
		int indexStart = url.indexOf("//");
		if (indexStart != -1) {
			return url.substring(indexStart + 2);
		} else {
			return url;
		}
	}

	public static String getUrlFromClipboard() {
		Clipboard clipboard = new Clipboard(Display.getDefault());
		TextTransfer transfer = TextTransfer.getInstance();
		String contents = (String) clipboard.getContents(transfer);
		if (contents != null) {
			if ((contents.startsWith("http://") || contents.startsWith("https://") && contents.length() > 10)) {
				return contents;
			}
		}
		return null;
	}
}
