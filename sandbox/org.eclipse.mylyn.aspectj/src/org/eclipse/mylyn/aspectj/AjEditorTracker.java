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

import org.eclipse.ajdt.internal.ui.editor.AspectJEditor;
import org.eclipse.mylar.java.JavaEditorTracker;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 */
public class AjEditorTracker extends JavaEditorTracker {
 
  // -----------------------------------------------------------
  // ---------------------- Interface --------------------------
  // -----------------------------------------------------------

  public final void editorOpened(org.eclipse.ui.IEditorPart arg0) {
    super.editorOpened(arg0);
    if (debug) debug("editorOpened(" + arg0 + ")");
  }
  public final void editorClosed(org.eclipse.ui.IEditorPart arg0) {
    super.editorClosed(arg0);
    if (debug) debug("editorClosed(" + arg0 + ")");
  }
  
  public final void unregisterEditor(org.eclipse.jdt.internal.ui.javaeditor.JavaEditor e) {
  	//
  	// Only do this for an AspectJ editor
  	//
  	if (e instanceof AspectJEditor) {  	
  		super.unregisterEditor(e);
  		if (debug) debug("unregisterEditor(" + e + ")");
  	}
  }
  public final void registerEditor(org.eclipse.jdt.internal.ui.javaeditor.JavaEditor e) {
  	//
  	// Only do this for an AspectJ editor
  	//
  	if (e instanceof AspectJEditor) {
  		super.registerEditor(e);
  		if (debug) debug("registerEditor(" + e + ")");
  	}
  }
  
	public boolean shouldRegistor(IEditorPart part) {
		return part instanceof AspectJEditor;
	}  

  // -----------------------------------------------------------
  // ------------------ Debugging support ----------------------
  // -----------------------------------------------------------

  private boolean debug;
  void setDebug(boolean debug) {this.debug = debug;}
  AjEditorTracker() {this(false);}
  AjEditorTracker(boolean debug) {setDebug(debug);}

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
}

