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
 * Created on Jul 26, 2004
  */
package org.eclipse.mylyn.ui.views.support;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.viewsupport.JavaUILabelProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.*;

import org.eclipse.mylyn.model.*;
import org.eclipse.mylyn.ui.UiUtil;

/**
 * @author Mik Kersten
 * @deprecated
 */
public class DoiDecoratingTableLabelProvider extends JavaUILabelProvider 
	implements ITableLabelProvider, IFontProvider {
    
	public String getColumnText(Object object, int index) {
        if (object instanceof TaskscapeNode) {
                TaskscapeNode node = (TaskscapeNode)object;
                if (node.getKind().equals(ITaskscapeNode.Kind.Java)) {
                    IJavaElement element = JavaCore.create(node.getElementHandle());
                    if (element != null) {
        		        String label = element.getElementName();
        		        return label + " (" + node.getDegreeOfInterest().getDegreeOfInterest().getDoiValue() + ")";     
                    } else {
                        return "<missing element>";
                    }
                } else {
                    return node.getElementHandle();
                }
	    } else if (object instanceof TaskscapeEdge){
            TaskscapeEdge edge = (TaskscapeEdge)object;
            return edge.toString();
        } 
        return "? " + object;
	} 

	public Image getColumnImage(Object object, int index) {
        if (object instanceof TaskscapeNode) {
            TaskscapeNode node = (TaskscapeNode)object;
            if (node.getKind().equals(ITaskscapeNode.Kind.Java)) {
                IJavaElement javaElement = JavaCore.create(node.getElementHandle());
                return getImage(javaElement);
            }
        } 
        return null;
	}  
 
    public Color getBackground(Object element) {
//        if (element instanceof IJavaElement) {
//            return UiUtil.getBackgroundForElement((IJavaElement)element);
//        } else {
            return null;
//        }
    }

    public Color getForeground(Object element) {
//        if (element instanceof IJavaElement) {
//            return UiUtil.getForegroundForElement((IJavaElement)element);
//        } else {
            return null;
//        }
    }
    
    public Font getFont(Object object) {
        if (object instanceof IJavaElement) {
            return UiUtil.getFontForElement((IJavaElement)object);
        } else {
            return null;
        }
    }
}
