/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.textile.core;

import java.util.List;

import org.eclipse.mylyn.internal.wikitext.textile.core.block.BugzillaQuoteBlock;
import org.eclipse.mylyn.internal.wikitext.textile.core.block.ParagraphBlock;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * A dialect of the Textile language that is suited for displaying Bugzilla content.
 * 
 * Extensions to the Textile language include:
 * <ul>
 * 	<li>Email-style quoted regions, starting with a '&gt;' character</li>
 *  <li>All paragraphs are wrapped in &lt;p&gt; tags even if they start with a space character<li>
 * </ul>
 * 
 * @author David Green
 *
 */
public class BugzillaTextileLanguage extends TextileLanguage {
	
	public BugzillaTextileLanguage() {
		setExtendsLanguage(getName());
		setName("Textile (Bugzilla Dialect)");
	}
	
	@Override
	protected void addBlockExtensions(List<Block> blocks, List<Block> paragraphBreakingBlocks) {
		super.addBlockExtensions(blocks,paragraphBreakingBlocks);
		
		BugzillaQuoteBlock quoteBlock = new BugzillaQuoteBlock();
		blocks.add(quoteBlock);
		paragraphBreakingBlocks.add(quoteBlock);
	}
	
	@Override
	protected void initializeBlocks() {
		super.initializeBlocks();
		ParagraphBlock block = (ParagraphBlock) getBlocks().get(getBlocks().size()-1);
		block.setEnableUnwrapped(false);
	}
}