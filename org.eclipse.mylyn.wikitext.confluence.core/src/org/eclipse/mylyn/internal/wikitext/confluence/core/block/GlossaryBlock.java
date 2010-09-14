/*******************************************************************************
 * Copyright (c) 20010 Guillaume Nodet and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.confluence.core.block;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * quoted text block, matches blocks that start with <code>{quote}</code>. Creates an extended block type of
 * {@link ParagraphBlock paragraph}.
 *
 * @author Guillaume Nodet
 */
public class GlossaryBlock extends Block {

    static final Pattern startPattern = Pattern.compile("\\s*-\\s*+(.*?)\\s*+::\\s*+(.*?)\\s*+"); //$NON-NLS-1$

    private Matcher matcher;
    private int blockLineCount = 0;

    @Override
    public int processLineContent(String line, int offset) {
        if (blockLineCount == 0) {
            builder.beginBlock(DocumentBuilder.BlockType.DEFINITION_LIST, new Attributes());
        } else {
            matcher = startPattern.matcher(line);
            if (!matcher.matches()) {
                setClosed(true);
                return 0;
            }
        }
        ++blockLineCount;
        String key = matcher.group(1);
        String val = matcher.group(2);
        builder.beginBlock(DocumentBuilder.BlockType.DEFINITION_TERM, new Attributes());
        markupLanguage.emitMarkupLine(getParser(), state, key, 0);
        builder.endBlock();
        builder.beginBlock(DocumentBuilder.BlockType.DEFINITION_ITEM, new Attributes());
        markupLanguage.emitMarkupLine(getParser(), state, val, 0);
        builder.endBlock();
        return -1;
    }

    @Override
    public boolean canStart(String line, int lineOffset) {
        if (lineOffset == 0 && !markupLanguage.isFilterGenerativeContents()) {
            matcher = startPattern.matcher(line);
            return matcher.matches();
        } else {
            matcher = null;
            return false;
        }
    }

    @Override
    public void setClosed(boolean closed) {
        if (closed && !isClosed()) {
            builder.endBlock();
        }
        super.setClosed(closed);
    }
}
