/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.aspectj;

// /*******************************************************************************
// * Copyright (c) 2004 - 2005 University Of British Columbia and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// * University Of British Columbia - initial API and implementation
// *******************************************************************************/
// /*
// * Created on Apr 7, 2005
// */
// package org.eclipse.mylyn.internal.aspectj;
//
// import org.eclipse.ajdt.core.AspectJCore;
// import org.eclipse.ajdt.core.javaelements.IAspectJElement;
// import org.eclipse.jdt.core.IJavaElement;
//
// public class AjStructureBridge extends
// org.eclipse.mylyn.internal.java.JavaStructureBridge {
//
// // -----------------------------------------------------------
// // ---------------------- Interface --------------------------
// // -----------------------------------------------------------
//	
// public final String getContentType() {return "aj";}
//
// public final String getContentType(String arg0) {
// String val = super.getContentType(arg0);
// return val;
// }
// public final String getParentHandle(String arg0) {
// //
// // TODO: abstract this here
// //
// IJavaElement javaElement = AspectJCore.create(arg0);
// if (javaElement != null && javaElement.getParent() != null) {
// return getHandleIdentifier(javaElement.getParent());
// } else {
// return null;
// }
// /*
// IJavaElement javaElement = JavaCore.create(handle);
// if (javaElement != null && javaElement.getParent() != null) {
// return getHandleIdentifier(javaElement.getParent());
// } else {
// return null;
// }
// */
//    
// }
// public final String getHandleIdentifier(Object arg0) {
// String val = super.getHandleIdentifier(arg0);
// return val;
// }
// public final Object getObjectForHandle(String arg0) {
// Object val = super.getObjectForHandle(arg0);
// return val;
// }
// public final boolean canBeLandmark(String arg0) {
// boolean val = super.canBeLandmark(arg0);
// return val;
// }
// public final boolean acceptsObject(Object arg0) {
// boolean val = super.acceptsObject(arg0);
// if (val) return true;
// if (arg0 instanceof IAspectJElement) return true;
// return false;
// }
// public final boolean canFilter(Object arg0) {
// boolean val = super.canFilter(arg0);
// return val;
// }
// public final boolean isDocument(String arg0) {
// boolean val = super.isDocument(arg0);
// return val;
// }
//
// public final org.eclipse.core.resources.IProject getProjectForObject(Object
// arg0) {
// org.eclipse.core.resources.IProject val = super.getProjectForObject(arg0);
// return val;
// }
// public final String getName(Object arg0) {
// String val = super.getName(arg0);
// return val;
// }
//
//
// }
//
