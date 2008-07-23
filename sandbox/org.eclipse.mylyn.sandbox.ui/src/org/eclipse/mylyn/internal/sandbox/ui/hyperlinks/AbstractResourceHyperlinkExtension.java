/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.sandbox.ui.hyperlinks;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * An IResourceHyperlinkExtension implementation with various helper methods. Subclasses may choose to extend it instead
 * of IResourceHyperlinkExtension.
 * 
 * It matches text that is in the form of &lt;prefix&gt; &lt;qualified name&gt;.
 * 
 * @author Jingwen Ou
 */
public abstract class AbstractResourceHyperlinkExtension implements IResourceHyperlinkExtension {

	/**
	 * A regular expression matching class name, e.g. org.eclipse.mylyn.ITask or foo. Subclasses may use it as a default
	 * qualified name.
	 */
	protected static final String DEFAULT_QUALIFIED_NAME = "((\\w(\\w|\\.)*\\w)|\\w)";

	/**
	 * Extracts a IHyperlink from current region offset.
	 */
	private IHyperlink extractHyperlink(int regionOffset, Matcher m) {
		int start = m.start();

		int end = m.end();

		if (end == -1) {
			end = m.group().length();
		}

		try {
			start += regionOffset;
			end += regionOffset;
			String resourceName = extractResourceName(m.group());

			IRegion region = new Region(start, end - start);
			return createHyperlinkInstance(region, resourceName);

		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Extracts the resource name from the matched text. Subclasses may choose to override it for customized name
	 * extracting mechanism. The default implementation is to extract the last block of the matched text, e.g. the
	 * "foo.bar" from "java class foo.bar".
	 * 
	 * @param matchedText
	 *            the matched text using the regular expression defined in getResourceExpressions
	 */
	protected String extractResourceName(String matchedText) {
		return matchedText.substring(matchedText.lastIndexOf(" ") + 1);
	}

	public IHyperlink[] findHyperlink(String text, int lineOffset, int regionOffset) {
		ArrayList<IHyperlink> hyperlinksFound = new ArrayList<IHyperlink>();

		Matcher m = getMatcherFor(text);
		while (m.find()) {
			if (lineOffset >= m.start() && lineOffset < m.end()) {
				// ignore when resource does not exist
				if (!isResourceExists(extractResourceName(m.group()))) {
					continue;
				}

				IHyperlink link = extractHyperlink(regionOffset, m);
				if (link != null) {
					hyperlinksFound.add(link);
				}
			}
		}

		if (hyperlinksFound.size() > 0) {
			return hyperlinksFound.toArray(new IHyperlink[1]);
		}
		return null;
	}

	/**
	 * Creates correspondent IHyperlink instance for this resource.
	 * 
	 * @param region
	 *            the region of the hyperlink
	 * @param resourceName
	 *            the found resource name
	 * 
	 * @return the correspondent IHyperlink instance for this resouce
	 */
	protected abstract IHyperlink createHyperlinkInstance(IRegion region, String resourceName);

	/**
	 * Gets the Matcher for the to-be-matched text. Default flag is Pattern.CASE_INSENSITIVE.
	 */
	private Matcher getMatcherFor(String text) {
		return Pattern.compile(getResourceExpressions(), Pattern.CASE_INSENSITIVE).matcher(text);
	}

	/**
	 * Gets the resource's regular expressions.
	 * 
	 * @return the regular expression of matching current resource. For example, matching "java class org.foo.bar".
	 */
	protected abstract String getResourceExpressions();

	/**
	 * Indicate whether the current resource exists.
	 * 
	 * @param resourceName
	 *            the name of the resource to be tested
	 * @return if the resource exists
	 */
	protected abstract boolean isResourceExists(String resourceName);
}
