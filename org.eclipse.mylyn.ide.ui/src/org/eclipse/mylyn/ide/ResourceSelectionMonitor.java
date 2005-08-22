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
 * Created on Apr 20, 2005
  */
package org.eclipse.mylar.ide;

import org.eclipse.core.internal.resources.File;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.core.AbstractSelectionMonitor;
import org.eclipse.ui.IWorkbenchPart;


/**
 * @author Mik Kersten
 */
public class ResourceSelectionMonitor extends AbstractSelectionMonitor {

	@Override
    protected void handleWorkbenchPartSelection(IWorkbenchPart part, ISelection selection) {
        if (selection instanceof StructuredSelection) {
            StructuredSelection structuredSelection = (StructuredSelection)selection;
          
            Object selectedObject = structuredSelection.getFirstElement();
            if (selectedObject instanceof File) { 
                File file = (File)selectedObject;
                super.handleElementSelection(part, file);
            }       
        }
    }
}
