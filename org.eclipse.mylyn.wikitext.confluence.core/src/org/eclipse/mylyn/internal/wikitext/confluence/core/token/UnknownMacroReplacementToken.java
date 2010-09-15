/*******************************************************************************
 * Copyright (c) 2009 Progress Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Fintan Bolton - implementation based on ImagePhraseModifier class
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.confluence.core.token;

import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElement;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

/**
 * 
 * 
 * @author Fintan Bolton
 */
public class UnknownMacroReplacementToken extends PatternBasedElement {

	protected static final int MACRO_NAME_GROUP = 1;
	protected static final int ATTRIBUTES_GROUP = 2;
	protected static final String UNKNOWN_MACRO_PREFIX = "wiki-unknown-";

	@Override
	protected String getPattern(int groupOffset) {
		// Make sure that you don't match when the first opening brace is escaped.
		// E.g. \{macro} is not a match.
		// Assume that a macro identifier consists of: alphanumeric, -, _
		// which is represented by the regex, [\w\-]+
		return "(?:^|[^\\{\\\\])\\{([\\w\\-]+)(?::([^\\}]*))?\\}"; //$NON-NLS-1$
	}

	@Override
	protected int getPatternGroupCount() {
		return 2;
	}

	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new UnknownMacroPhraseModifierProcessor();
	}

	private static class UnknownMacroPhraseModifierProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String macroName = group(MACRO_NAME_GROUP);
			String attributes = group(ATTRIBUTES_GROUP);

			builder.annotation(UNKNOWN_MACRO_PREFIX + macroName, attributes);
		}
	}

}
