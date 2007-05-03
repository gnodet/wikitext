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

package org.eclipse.mylar.internal.web.ui;

import java.net.URL;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.mylar.internal.web.WebResource;
import org.eclipse.mylar.tasks.ui.TasksUiUtil;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
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
			TasksUiUtil.openUrl(url, true);
		} catch (PartInitException e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "URL not found", url
					+ " could not be opened");
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
