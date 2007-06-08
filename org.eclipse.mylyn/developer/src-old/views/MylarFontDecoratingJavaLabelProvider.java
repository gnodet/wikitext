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
 * Created on Aug 6, 2004
  */
package org.eclipse.mylyn.java.ui;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.viewsupport.DecoratingJavaLabelProvider;
import org.eclipse.jdt.internal.ui.viewsupport.JavaUILabelProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.swt.graphics.Font;



/**
 * @author Mik Kersten
 */
public class MylarFontDecoratingJavaLabelProvider extends DecoratingJavaLabelProvider implements IFontProvider {
    
	public MylarFontDecoratingJavaLabelProvider(JavaUILabelProvider labelProvider, boolean errorTick) {
	    super(labelProvider, errorTick);
	}
	
    public String getText(Object element) {
        if (element instanceof IJavaElement) {
            return ((IJavaElement)element).getElementName();
        }
        return super.getText(element);
    }
    
    public Font getFont(Object element) {
	    if (element instanceof IJavaElement) {
	        return JavaUiUtil.getFontForElement((IJavaElement)element);
	    } else {
	        return null;
	    }
    }
} 
