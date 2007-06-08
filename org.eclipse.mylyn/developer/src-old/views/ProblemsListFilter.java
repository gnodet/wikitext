/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Apr 18, 2005
  */
package org.eclipse.mylyn.java.ui.views;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.markers.internal.ProblemMarker;

import org.eclipse.mylyn.java.ui.JavaUiUtil;
import org.eclipse.mylyn.ui.InterestFilter;

public class ProblemsListFilter extends InterestFilter{

    public boolean select(Viewer viewer, Object parent, Object element) {
        if (element instanceof ProblemMarker) {
            ProblemMarker problemMarker = (ProblemMarker)element;
            if (problemMarker.getSeverity() == IMarker.SEVERITY_ERROR) {
                return true;
            } else {
                IJavaElement javaElement = JavaUiUtil.getJavaElement(problemMarker);
                return super.select(viewer, null, javaElement);
            }
        }
        return false;
    }
    
}
