/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     Fintan Bolton - modified for use in Confdoc plugin
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.confluence.core.block;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.XmlTableAttributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * Table block, matches blocks that start with <code>table. </code> or those that start with a table row.
 * 
 * @author David Green
 */
public class TableBlock extends Block {
    private static final Logger log = Logger.getLogger(TableBlock.class);

	static final Pattern startPattern = Pattern.compile("(\\|(.*)?(\\|\\s*$))");

    static final Pattern CONT_CELL_PATTERN = Pattern.compile("^((?:(?:[^\\|\\[]*)(?:\\[[^\\]]*\\])?)*)");
	static final Pattern NEXT_CELL_PATTERN = Pattern.compile("\\|(\\|)?" + "((?:(?:[^\\|\\[]*)(?:\\[[^\\]]*\\])?)*)");
            // + "(\\|\\|?\\s*$)?");
    //static final Pattern END_ROW_PATTERN = Pattern.compile("(\\|\\|?\\s*$)");

    private int blockLineCount = 0;
	private Matcher matcher;
	private enum State { IN_TABLE, IN_CELL };
	private State tableState;
	private int cols;
	private RowContent rowContent = null;
	private CellContent currentCell = null;
	
	private static class CellContent {
	    private List<String> lines;
	    private List<Integer> lineOffsets;
	    
	    CellContent() {
	        lines = new java.util.ArrayList<String>();
	        lineOffsets = new java.util.ArrayList<Integer>();
	    }

        public List<String> getLines() {
            return lines;
        }

        public void setLines(List<String> lines) {
            this.lines = lines;
        }

        public List<Integer> getLineOffsets() {
            return lineOffsets;
        }

        public void setLineOffsets(List<Integer> lineOffsets) {
            this.lineOffsets = lineOffsets;
        }
	}
	
	private static class RowContent {
	    private List<CellContent> cells;
	    private boolean headerRow = false;
	    
	    RowContent() {
	        cells = new java.util.ArrayList<CellContent>();
	    }

        public List<CellContent> getCells() {
            return cells;
        }

        public void setCells(List<CellContent> cells) {
            this.cells = cells;
        }

        public boolean isHeaderRow() {
            return headerRow;
        }

        public void setHeaderRow(boolean headerRow) {
            this.headerRow = headerRow;
        }	    
	}

	public TableBlock() {
	    tableState = State.IN_TABLE;
	    cols = 0;
	}

	@Override
	public int processLineContent(String line, int offset) {
        if (blockLineCount == 0) {
            // Put code here that executes at very start of table
            log.debug("Table started.");
            tableState = State.IN_TABLE;
            rowContent = null;
            cols = 0;
        } else if (markupLanguage.isEmptyLine(line)) {
            // [End of Table]
            log.debug("End of table");
            setClosed(true);
            return 0;
        }
        ++blockLineCount;

        if (offset == line.length()) {
            return -1;
        }
	    
        String textileLine = (offset == 0) ? line : line.substring(offset);
                
        Matcher contCellMatcher = CONT_CELL_PATTERN.matcher(textileLine);
        contCellMatcher.find();
        String cellText = contCellMatcher.group(1);
        if (log.isDebugEnabled()) {
            log.debug("Matching cellText = [" + cellText + "]");
            log.debug("offset = " + offset);
            log.debug("textileLine = " + textileLine);
        }
        if (cellText != null && !cellText.matches("\\s*")) {
            if (tableState==State.IN_CELL) {
                if (currentCell != null) {
                    // Add cell line
                    // Truncate text by removing any trailing '\\<whitespace>' (line continuation)
                    cellText = cellText.replaceFirst("\\\\\\\\\\s*$", "");
                    currentCell.getLines().add(cellText);
                    currentCell.getLineOffsets().add(new Integer(offset));
                    log.debug("Adding extra line to table cell:" + cellText);
                }
                else {
                    log.warn("Continued table cell - CellContent should not be null!");
                }
            }
            else {
                // [End of Table]
                // If we get here, it means the line started without a pipe, |, symbol.
                log.debug("End of table");
                setClosed(true);
                return 0;
            }
        }
        
        int lineOffset = offset + ((cellText!=null) ? contCellMatcher.start(1) : 0);
        textileLine = line.substring(lineOffset);
        Matcher nextCellMatcher = NEXT_CELL_PATTERN.matcher(textileLine);
        
        while (nextCellMatcher.find()) {
            if (tableState==State.IN_CELL && rowContent != null) {
                // Close current cell
                log.debug("End of current cell");
                rowContent.getCells().add(currentCell);
                currentCell = null;
                tableState = State.IN_TABLE;
            }
            else if (rowContent == null) {
                // [Start new row]
                log.debug("Start new row");
                String headerIndicator = nextCellMatcher.group(1);
                rowContent = new RowContent();
                rowContent.setHeaderRow(headerIndicator!=null && "|".equals(headerIndicator));
            }
            
            // Test for end of row
            // Note: The following test relies on the fact that Confluence always trims white space
            // off the end of a table row.
            boolean foundEndRowMarker =
                (nextCellMatcher.start()==(textileLine.length()-1)) ||
                (rowContent.isHeaderRow() && (nextCellMatcher.start()==(textileLine.length()-2)));
            boolean reachedColLimit = (cols != 0) ? (rowContent.getCells().size() >= cols) : false;
            log.debug("foundEndRowMarker = " + Boolean.toString(foundEndRowMarker));
            log.debug("reachedColLimit = " + Boolean.toString(reachedColLimit));
            
            cellText = nextCellMatcher.group(2);
            if (!foundEndRowMarker && !reachedColLimit) {
                // Start a new cell
                cellText = cellText.replaceFirst("\\\\\\\\\\s*$", "");
                int cellLineOffset = lineOffset + nextCellMatcher.start(2);
                log.debug("Start new cell: cellText = [" + cellText + "]" +
                          ", cellLineOffset = [" + cellLineOffset + "]"
                );
                currentCell = new CellContent();
                currentCell.getLines().add(
                        cellText
                );
                currentCell.getLineOffsets().add(
                        new Integer(cellLineOffset)
                );
                tableState = State.IN_CELL;
            }
            
            /*
            if (foundEndRowMarker) {
                if (rowContent != null) {
                    // Close current cell
                    log.debug("End of current cell");
                    rowContent.getCells().add(currentCell);
                    currentCell = null;
                }
                else {
                    log.warn("RowContent was null while closing cell at end of row");
                }
                tableState = State.IN_TABLE;
            }
            */
            if (foundEndRowMarker || reachedColLimit) {
                // [End of row]
                log.debug("End of row");
                if (cols == 0) {
                    // [End of *first* row]
                    log.debug("(first row)");
                    cols = rowContent.getCells().size();
                    XmlTableAttributes tableAttributes = new XmlTableAttributes();
                    tableAttributes.setCols(Integer.toString(cols));
                    builder.beginBlock(BlockType.TABLE, tableAttributes);
                }
                log.debug("Row length = " + rowContent.getCells().size());
                emitRow(rowContent);
                rowContent = null;
                break;
            }
        }
        
        return -1;
	}

	private void emitRow(RowContent row) {
	    builder.beginBlock(BlockType.TABLE_ROW, new Attributes());
        for (CellContent cell : row.getCells()) {
            emitCell(cell, row.isHeaderRow());
        }
        builder.endBlock(); // Table row
    }
	
	private void emitCell(CellContent cell, boolean isHeaderCell) {
        // Emit cell
        builder.beginBlock(
            isHeaderCell ? BlockType.TABLE_CELL_HEADER : BlockType.TABLE_CELL_NORMAL,
            new Attributes()
        );
        
        int lineCount = cell.getLines().size();
        if (lineCount == 1) {
            // Emit cell line
            markupLanguage.emitMarkupLine(
                getParser(),
                state,
                cell.getLineOffsets().get(0).intValue(),
                cell.getLines().get(0),
                0
            );
        }
        else if (lineCount > 1) {
            // Join lines into a single string
            StringBuffer cellTextBuf = new StringBuffer("");
            for (String line : cell.getLines()) {
                cellTextBuf.append(line);
                cellTextBuf.append('\n');
            }
            String cellText = cellTextBuf.toString();
            
            // Process all kinds of text, including blocks
            markupLanguage.processContent(getParser(), cellText, false);
        }
        
        builder.endBlock(); // Table cell
	}

    @Override
	public boolean canStart(String line, int lineOffset) {
		blockLineCount = 0;
		if (lineOffset == 0) {
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
