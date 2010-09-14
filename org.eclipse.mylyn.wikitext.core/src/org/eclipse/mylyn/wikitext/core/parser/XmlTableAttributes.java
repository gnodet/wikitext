/*******************************************************************************
 * Copyright (c) 2009 Progress Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fintan Bolton - initial implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser;

/**
 * 
 * 
 * @author Fintan Bolton
 * @since 1.0
 */
public class XmlTableAttributes extends TableAttributes {

	private String pgwide;
	private String rowsep;
	private String colsep;
	private String cols;

	public String getPgwide() {
		return pgwide;
	}

	public void setPgwide(String pgwide) {
		this.pgwide = pgwide;
	}

	public String getRowsep() {
		return rowsep;
	}

	public void setRowsep(String rowsep) {
		this.rowsep = rowsep;
	}

	public String getColsep() {
		return colsep;
	}

	public void setColsep(String colsep) {
		this.colsep = colsep;
	}

    public String getCols() {
        return cols;
    }

    public void setCols(String cols) {
        this.cols = cols;
    }

}
