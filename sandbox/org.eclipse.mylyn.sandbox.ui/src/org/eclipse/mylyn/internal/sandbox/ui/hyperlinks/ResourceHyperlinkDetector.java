/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.hyperlinks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * Resource hyperlink detector, detecting format like < prefix > < qualified name >, examples are:
 * 
 * See java class foo.bar.Baz, it could be related; See cpp class Foo, it could be related; See file foo.txt, I left
 * some notes there; See task 123, I put a comment there
 * 
 * @author Jingwen Ou
 */
public class ResourceHyperlinkDetector extends AbstractHyperlinkDetector {

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		try {
			IDocument document = textViewer.getDocument();
			if (document == null) {
				return null;
			}
			int offset = region.getOffset();

			IRegion lineInfo = document.getLineInformationOfOffset(offset);
			String line = document.get(lineInfo.getOffset(), lineInfo.getLength());
			if (line.length() == 0) {
				return null;
			}

			List<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
			detectHyperlinks(line, region.getOffset() - lineInfo.getOffset(), lineInfo.getOffset(), hyperlinks);

			if (hyperlinks.isEmpty()) {
				return null;
			}
			return hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);

		} catch (BadLocationException e) {
			return null;
		}
	}

	private void detectHyperlinks(String line, int lineOffset, int regionOffset, List<IHyperlink> hyperlinks) {
		for (IResourceHyperlinkExtension resourceHyperlinkExtension : ResourceHyperlinkExtensions.getResourceHyperlinkExtensions()) {
			IHyperlink[] links = resourceHyperlinkExtension.findHyperlink(line, lineOffset, regionOffset);
			if (links == null) {
				continue;
			}
			hyperlinks.addAll(Arrays.asList(links));
		}
	}
}
