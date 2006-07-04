/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.tasklist.ui.editors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.mylar.internal.tasklist.ui.TaskUiUtil;

/**
 * @author Rob Elves
 */
public class TaskEditorUrlHyperlinkDetector implements IHyperlinkDetector {

	// Regexp from:
	// http://www.forta.com/blog/index.cfm?mode=entry&entry=A61B9FF5-3048-80A9-EF09C0A7F1E9FBD3
	private static final Pattern urlPattern = Pattern.compile(
			"https?://([-\\w\\.]+)+(:\\d+)?(/([\\w/_\\.]*(\\?\\S+)?)?)?", Pattern.CASE_INSENSITIVE);

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {

		if (region == null || textViewer == null)
			return null;

		IDocument document = textViewer.getDocument();

		int offset = region.getOffset();

		if (document == null)
			return null;

		IRegion lineInfo;
		String line;
		try {
			lineInfo = document.getLineInformationOfOffset(offset);
			line = document.get(lineInfo.getOffset(), lineInfo.getLength());
		} catch (BadLocationException ex) {
			return null;
		}

		Matcher m = urlPattern.matcher(line);

		if (m.find()) {
			IRegion urlRegion = new Region(lineInfo.getOffset() + m.start(), m.end() - m.start());
			return new IHyperlink[] { new TaskEditorUrlHyperlink(urlRegion, m.group()) };
		}

		return null;
	}

	class TaskEditorUrlHyperlink extends URLHyperlink {

		public TaskEditorUrlHyperlink(IRegion region, String urlString) {
			super(region, urlString);
		}

		public void open() {
			// TODO: if url is to a repository task, open task instead of url
			TaskUiUtil.openUrl(getURLString());
		}

	}

}
