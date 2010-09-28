package org.eclipse.mylyn.internal.wikitext.confluence.core.phrase;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.markup.PatternBasedElementProcessor;

public class HyperlinkPhraseModifier extends SimpleWrappedPhraseModifier {

	public HyperlinkPhraseModifier() {
		// The SPAN span type is just a dummy value to satisfy the base constructor.
		// The emitter actually uses beginLink()/endLink() to generate the output.
		super("\\[", "\\]", SpanType.SPAN, true);
	}

	
	@Override
	protected PatternBasedElementProcessor newProcessor() {
		return new HyperlinkPhraseModifierProcessor();
	}


	private static class HyperlinkPhraseModifierProcessor extends PatternBasedElementProcessor {
		@Override
		public void emit() {
			String linkComposite = getContent(this);
			String[] parts = linkComposite.split("\\s*\\|\\s*"); //$NON-NLS-1$
			String text = parts.length > 1 ? parts[0] : null;
			if (text != null) {
				text = text.trim();
			}
			String href = parts.length > 1 ? parts[1] : parts[0];
			if (href != null) {
				href = href.trim();
			}
			String tip = parts.length > 2 ? parts[2] : null;
			if (tip != null) {
				tip = tip.trim();
			}
			if (text == null || text.length() == 0) {
				text = href;
			}
                        if (href.charAt(0) == '#') {
                            href = "#" + state.getIdGenerator().getGenerationStrategy().generateId(href.substring(1));
			}
                        LinkAttributes attributes = new LinkAttributes();
                        attributes.setTitle(tip);
			attributes.setHref(href);
			getBuilder().beginLink(attributes, href);
			getMarkupLanguage().emitMarkupLine(parser, state, getStart(this), text, 0);
			getBuilder().endLink();
		}
	}
}
