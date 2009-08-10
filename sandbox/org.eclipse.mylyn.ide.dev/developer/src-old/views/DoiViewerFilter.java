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
 * Created on Jul 27, 2004
  */
package org.eclipse.mylyn.java.ui;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.ui.packageview.ClassPathContainer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.views.markers.internal.ProblemMarker;

import org.eclipse.mylyn.core.MylarPlugin;
import org.eclipse.mylyn.core.model.ITaskscapeNode;
import org.eclipse.mylyn.java.JavaUiUtil;

/**
 * @author Mik Kersten
 */
public class DoiViewerFilter extends ViewerFilter {

    private boolean filterUninterestingEnabled = false;
    private boolean filterDeclarationsEnabled = false;
    
    public boolean select(Viewer viewer, Object parentElement, Object object) {
        if (!filterUninterestingEnabled) return true;
        
        if (object instanceof ProblemMarker) {
            ProblemMarker problemMarker = (ProblemMarker)object;
            if (problemMarker.getSeverity() == IMarker.SEVERITY_ERROR) {
                return true;
            } else {
                object = JavaUiUtil.getJavaElement(problemMarker); // TODO: don't reassing
            }
        }
        
        if (object instanceof IJavaElement) {
            IJavaElement element = (IJavaElement)object;
//            if (element instanceof IJavaProject) {
//                return true;
//            } else if (element instanceof PackageFragmentRoot && !(element instanceof JarPackageFragmentRoot)) {
//                return true;
//          } else if (element instanceof PackageFragment && !acceptPackageFragment((PackageFragment)element)) {
//                return false;
//          } else if (element instanceof IPackageDeclaration ||
//                     element instanceof IImportContainer) {
//              return false;
//            } else 
            if (ContextCorePlugin.getTaskscapeManager().isTempRaised(element.getParent().getHandleIdentifier())) {
                return true;
            } else {  
                if (isDeclaration(element) && filterDeclarationsEnabled) {
                    return false;
                } else if (!filterUninterestingEnabled) {
                    return true;
                } else {
                    ITaskscapeNode info = ContextCorePlugin.getTaskscapeManager().getDoi(element.getHandleIdentifier());
                    return info != null && info.getDegreeOfInterest().getDegreeOfInterest().isInteresting();
                }
            }
        } else if (object instanceof File) {
            ITaskscapeNode info = ContextCorePlugin.getTaskscapeManager().getDoi(((File)object).getFullPath().toPortableString());
            boolean interesting = info != null && info.getDegreeOfInterest().getDegreeOfInterest().isInteresting();
            if (!filterUninterestingEnabled) {
                return true;
            } else {
                return !(((File)object).getName().charAt(0) == '.') && interesting;
            }
        } else if (object instanceof ClassPathContainer) {
            ClassPathContainer container = (ClassPathContainer)object;
            return true;
        } else {
            return false;
        }
    }

    private boolean acceptPackageFragment(PackageFragment fragment) {
        try {
            if (fragment.getChildren() == null) return false;
            if (fragment.getChildren().length == 0) return false;
        } catch (JavaModelException e) {
            ContextCorePlugin.fail(e, "Could not determine if package fragment had children", false);
        }
        return true;
    }

    private boolean isDeclaration(IJavaElement element) {
        return 
            element instanceof IMember 
            || element instanceof IPackageDeclaration 
            || element instanceof IImportContainer
            || element instanceof IImportDeclaration;
    }

    public boolean isFilterUninterestingEnabled() {
        return filterUninterestingEnabled;
    }
    
    public void setFilterUninterestingEnabled(boolean enabled) {
        filterUninterestingEnabled = enabled;
    }
    
    public boolean isFilterDeclarationsEnabled() {
        return filterDeclarationsEnabled;
    }
    
    public void setFilterDeclarationsEnabled(
            boolean enabled) {
        filterDeclarationsEnabled = enabled;
    }
}
