package org.eclipse.mylyn.internal.wikitext.confluence.core.phrase;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;

/**
 * 
 * This class is 'strange' because it deals with the strange Confluence syntax for inline
 * formatting. For example, although the syntax for bolding text is meant to be *BoldText*,
 * Confluence might actually convert this into {*}BoldText{*}, *BoldText{*}, or {*}BoldText*,
 * depending on the context.
 *
 */
public class StrangePhraseModifier extends SimpleWrappedPhraseModifier {
	
	public StrangePhraseModifier(String delimiter, SpanType spanType, boolean nesting) {
		super(delimiter, delimiter, spanType, nesting);
	}

	@Override
	protected String getPattern(int groupOffset) {
		String quotedStartDelimiter = quoteLite(startDelimiter);
		String quotedDelimiter = quoteLite(endDelimiter);

		return "(?:\\{" + quotedStartDelimiter + "\\}|" + quotedStartDelimiter + ")"
		    + "(\\S+?|\\S.*?\\S)"
		    + "(?:\\{" + quotedDelimiter + "\\}|" + quotedDelimiter + ")" ;
	}

}
