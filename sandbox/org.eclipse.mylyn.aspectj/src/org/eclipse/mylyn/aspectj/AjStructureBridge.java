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
 * Created on Apr 7, 2005
  */
package org.eclipse.mylar.aspectj;

import org.eclipse.ajdt.core.AspectJCore;
import org.eclipse.ajdt.core.javaelements.IAspectJElement;
import org.eclipse.jdt.core.IJavaElement;

public class AjStructureBridge extends org.eclipse.mylar.java.JavaStructureBridge {

  // -----------------------------------------------------------
  // ---------------------- Interface --------------------------
  // -----------------------------------------------------------
	
  public final String getResourceExtension() {return "aj";}

  public final String getResourceExtension(String arg0) {
    String val = super.getResourceExtension(arg0);
    if (debug) debug("getResourceExtension(" + arg0 + ")" + " = " + val);
    return val;
  }
  public final String getParentHandle(String arg0) {
  	//
  	// TODO: abstract this here
  	//
  	IJavaElement javaElement = AspectJCore.create(arg0);
    if (javaElement != null && javaElement.getParent() != null) {            
      return getHandleIdentifier(javaElement.getParent());
    } else {
      return null;
    }  
    /* 	
    IJavaElement javaElement = JavaCore.create(handle);
    if (javaElement != null && javaElement.getParent() != null) {            
        return getHandleIdentifier(javaElement.getParent());
    } else {
        return null;
    } 
    */
    
  }
  public final String getHandleIdentifier(Object arg0) {
    String val = super.getHandleIdentifier(arg0);
    if (debug) debug("getHandleIdentifier(" + arg0 + ")" + " = " + val);
    return val;
  }
  public final Object getObjectForHandle(String arg0) {
    Object val = super.getObjectForHandle(arg0);
    if (debug) debug("getObjectForHandle(" + arg0 + ")" + " = " + val);
    return val;
  }
  public final boolean canBeLandmark(Object arg0) {
    boolean val = super.canBeLandmark(arg0);
    if (debug) debug("canBeLandmark(" + arg0 + ")" + " = " + val);
    return val;
  }
  public final boolean acceptsObject(Object arg0) {
    boolean val = super.acceptsObject(arg0);
    if (debug) debug("acceptsObject(" + format(arg0) + ")" + " = " + val);
  	if (val) return true;
  	//
  	// Now check AspectJ stuff
  	//
  	if (arg0 instanceof IAspectJElement) return true;
  	return false;
  }
  public final boolean canFilter(Object arg0) {
    boolean val = super.canFilter(arg0);
    if (debug) debug("canFilter(" + arg0 + ")" + " = " + val);
    return val;
  }
  public final boolean isDocument(String arg0) {
    boolean val = super.isDocument(arg0);
    if (debug) debug("isDocument(" + arg0 + ")" + " = " + val);
    return val;
  }
  public final String getHandleForMarker(org.eclipse.ui.views.markers.internal.ProblemMarker arg0) {
    String val = super.getHandleForMarker(arg0);
    if (debug) debug("getHandleForMarker(" + arg0 + ")" + " = " + val);
    return val;
  }
  public final org.eclipse.core.resources.IProject getProjectForObject(Object arg0) {
    org.eclipse.core.resources.IProject val = super.getProjectForObject(arg0);
    if (debug) debug("getProjectForObject(" + arg0 + ")" + " = " + val);
    return val;
  }
  public final String getName(Object arg0) {
    String val = super.getName(arg0);
    if (debug) debug("getName(" + arg0 + ")" + " = " + val);
    return val;
  }

  // -----------------------------------------------------------
  // ------------------ Debugging support ----------------------
  // -----------------------------------------------------------
  private boolean debug;
  void setDebug(boolean debug) {this.debug = debug;}
  AjStructureBridge() {this(false);}
  AjStructureBridge(boolean debug) {setDebug(debug);}

  private void debug(String s) {
    System.err.println(prefix() + s);
  }
  private String prefix;
  private String prefix() {
    if (prefix == null) {
      String s = getClass().getName();
      int ilast = s.lastIndexOf('.');
      prefix = "[" + (ilast==-1 ? s : s.substring(ilast+1)) + "] ";
    }
    return prefix;
  }
  
  private String format(Object o) {
  	if (o == null) return null;
  	String s = String.valueOf(o);
  	return s.length() > 100 ? o.getClass().getName() : s;
  }
}

