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

package org.eclipse.mylar.aspectj;

import org.eclipse.mylar.java.JavaEditingMonitor;

/**
 * @author Mik Kersten
 */
public class AjEditingMonitor extends JavaEditingMonitor {
    
  // -----------------------------------------------------------
  // ---------------------- Interface --------------------------
  // -----------------------------------------------------------

  public final void handleWorkbenchPartSelection(org.eclipse.ui.IWorkbenchPart arg0,org.eclipse.jface.viewers.ISelection arg1) {
    super.handleWorkbenchPartSelection(arg0,arg1);
    if (debug) debug("handleWorkbenchPartSelection(" + (arg0!=null ? arg0.getClass().getName() : "null") +"," 
    	+ (arg1!=null ? arg1.getClass().getName() : "null") + ")");
  }

  // -----------------------------------------------------------
  // ------------------ Debugging support ----------------------
  // -----------------------------------------------------------

  private boolean debug;
  void setDebug(boolean debug) {this.debug = debug;}
  AjEditingMonitor() {this(false);}
  AjEditingMonitor(boolean debug) {setDebug(debug);}

  private void debug(String s) {
    System.out.println(prefix() + s);
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
}	
