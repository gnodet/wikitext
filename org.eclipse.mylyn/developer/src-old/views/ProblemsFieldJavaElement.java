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

import org.eclipse.jdt.core.*;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.markers.internal.ConcreteMarker;
import org.eclipse.ui.views.markers.internal.Messages;

import org.eclipse.mylyn.java.ui.JavaUiUtil;

/**
 * @author beatmik
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ProblemsFieldJavaElement implements org.eclipse.ui.views.markers.internal.IField {
    
	private String description;
	private Image image;
	private JavaElementImageProvider imageProvider = new JavaElementImageProvider();
	
	public ProblemsFieldJavaElement() {
		description = Messages.getString("Java Element"); //$NON-NLS-1$
	}

	public String getDescription() {
		return description;
	}

	public Image getDescriptionImage() {
		return image;
	}

	public String getColumnHeaderText() {
		return description;
	}

	public Image getColumnHeaderImage() {
		return image;
	}

	public String getValue(Object obj) {
		if (obj == null || !(obj instanceof ConcreteMarker)) {
			return ""; //$NON-NLS-1$
		}
		ConcreteMarker marker = (ConcreteMarker) obj;
		IJavaElement javaElement= JavaUiUtil.getJavaElement(marker);//SearchUtil.getJavaElement(marker);
		if (javaElement == null) {
			return marker.getResourceName();
		} else {
		    String name;
		    if (javaElement instanceof IMember 
		        && javaElement.getParent() != null 
		        && !(javaElement.getParent() instanceof ICompilationUnit)) {
		        name = javaElement.getParent().getElementName()
		        	+ "." + javaElement.getElementName();
		    } else { 
		        name = javaElement.getElementName();
		    }
			return name;
		}
	}

	public Image getImage(Object obj) {
		if (obj == null || !(obj instanceof ConcreteMarker)) {
			return null; //$NON-NLS-1$
		}
		ConcreteMarker marker = (ConcreteMarker) obj;
		IJavaElement javaElement= JavaUiUtil.getJavaElement(marker);//SearchUtil.getJavaElement(marker);
		if (javaElement == null) {
			return null;
		} else {
            Image image =  imageProvider.getImageLabel(javaElement, JavaElementImageProvider.SMALL_ICONS);
		    return image; 
		}
	}

	public int compare(Object obj1, Object obj2) {
		if (obj1 == null || obj2 == null || !(obj1 instanceof ConcreteMarker) || !(obj2 instanceof ConcreteMarker)) {
			return 0;
		}
		
		ConcreteMarker marker1 = (ConcreteMarker) obj1;
		ConcreteMarker marker2 = (ConcreteMarker) obj2;
		
		try {
		    return marker1.getResourceNameKey().compareTo(marker2.getResourceNameKey());
		} catch (NoSuchMethodError e) {
		    return 0; // TODO: will not sorting these cause any problems?
		} 
	}
}
