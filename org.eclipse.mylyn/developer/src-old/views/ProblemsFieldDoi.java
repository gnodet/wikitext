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
 * Created on Jul 6, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.mylyn.java.ui.views;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.markers.internal.*;

import org.eclipse.mylyn.core.MylarPlugin;
import org.eclipse.mylyn.core.model.ITaskscapeNode;
import org.eclipse.mylyn.java.ui.JavaUiUtil;

/**
 * @author beatmik
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ProblemsFieldDoi implements org.eclipse.ui.views.markers.internal.IField {
    
	private String description;
	
	public ProblemsFieldDoi() {
		description = Messages.getString("DOI"); //$NON-NLS-1$
	}

	public String getDescription() {
		return description;
	}

	public Image getDescriptionImage() {
		return null;
	}

	public String getColumnHeaderText() {
		return description;
	}

	public Image getColumnHeaderImage() {
		return null;
	}

	public String getValue(Object obj) {
		if (obj == null || !(obj instanceof ConcreteMarker)) {
			return ""; //$NON-NLS-1$
		}
		ConcreteMarker marker = (ConcreteMarker) obj;
		IJavaElement javaElement= JavaUiUtil.getJavaElement(marker);//SearchUtil.getJavaElement(marker);
		if (javaElement != null) {
            return "" + ContextCorePlugin.getTaskscapeManager().getActiveTaskscape().get(javaElement.getHandleIdentifier()).getDegreeOfInterest().getDegreeOfInterest().getValue();
//			return marker.getResourceName();
		} else {
		    return "<undefined>";
        }
	}

	public Image getImage(Object obj) {
		return null;
	}

    // TODO: slow?
	public int compare(Object obj1, Object obj2) {
		if (obj1 != null && obj2 != null && obj1 instanceof ProblemMarker && obj2 instanceof ProblemMarker) {
            ProblemMarker m1 = (ProblemMarker) obj1;
            ProblemMarker m2 = (ProblemMarker) obj2;
            IJavaElement j1 = JavaUiUtil.getJavaElement(m1);//SearchUtil.getJavaElement(marker);
            IJavaElement j2 = JavaUiUtil.getJavaElement(m2);//SearchUtil.getJavaElement(marker);
            if (j1 != null && j2 != null) {
                ITaskscapeNode n1 = ContextCorePlugin.getTaskscapeManager().getActiveTaskscape().get(j1.getHandleIdentifier());
                ITaskscapeNode n2 = ContextCorePlugin.getTaskscapeManager().getActiveTaskscape().get(j2.getHandleIdentifier());
                return  (int)(n1.getDegreeOfInterest().getDegreeOfInterest().getValue() - n2.getDegreeOfInterest().getDegreeOfInterest().getValue());
            }
        }
        return 0;
	}
}
