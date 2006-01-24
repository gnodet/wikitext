/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.viz.seesoft;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.mylar.core.util.MylarStatusHandler;

/**
 * Contains some methods used by the Provider classes
 * 
 * @author Wesley Coelho
 */
public class Util {

	/**
	 * Returns the compilation unit for a JDTMember representing a java type by
	 * searching its parent elements. Returns null if a parent compilation unit
	 * was not found.
	 */
	protected static ICompilationUnit getCompilationUnit(IJavaElement javaType) {
		IJavaElement currElt = javaType;
		while (currElt != null) {
			if (currElt instanceof ICompilationUnit) {
				return (ICompilationUnit) currElt;
			}
			currElt = currElt.getParent();
		}

		return null;
	}

	/**
	 * Given a compilation unit, work out the number of lines in its source.
	 * 
	 * @param element
	 *            Compilation unit to investigate
	 * @return number of lines in the compilation unit
	 * 
	 * Note: Adapted from org.eclipse.contribution.visualiser.JDTContentProvider
	 */
	protected static int getLength(ISourceReference sourceRef) {
		try {
			String srccode;
			int lines = 0;

			srccode = sourceRef.getSource();

			while (srccode.indexOf("\n") != -1) {
				lines++;
				srccode = srccode.substring(srccode.indexOf("\n") + 1);
			}
			return lines;
		} catch (JavaModelException e) {
			MylarStatusHandler.fail(e, "Could not get the number of lines of a compilation unit", false);
			return 1;
		}
	}
}
