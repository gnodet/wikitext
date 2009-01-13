/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.sandbox.tests;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;
import org.eclipse.mylyn.internal.sandbox.ui.InterestInducingProblemListener;
import org.eclipse.mylyn.java.tests.AbstractJavaContextTest;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class PredictedErrorInterestTest extends AbstractJavaContextTest {

	public void testErrorInterest() throws CoreException, InterruptedException, InvocationTargetException {

		JavaPlugin.getDefault().getProblemMarkerManager().addListener(new InterestInducingProblemListener());

		IViewPart problemsPart = JavaPlugin.getActivePage().showView("org.eclipse.ui.views.ProblemView");
		assertNotNull(problemsPart);

		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		IMethod m1 = type1.createMethod("public void m1() { }", null, true, null);
		IPackageFragment p2 = project.createPackage("p2");

		IType type2 = project.createType(p2, "Type2.java", "public class Type2 { }");
		IMethod m2 = type2.createMethod("void m2() { new p1.Type1().m1(); }", null, true, null);

		assertTrue(m1.exists());
		assertEquals(1, type1.getMethods().length);

		monitor.selectionChanged(part, new StructuredSelection(m1));
		IInteractionElement m1Node = ContextCore.getContextManager().getElement(m1.getHandleIdentifier());
		assertTrue(m1Node.getInterest().isInteresting());

		// delete method to cause error
		m1.delete(true, null);
		assertEquals(0, type1.getMethods().length);
		project.build();

		IMarker[] markers = type2.getResource().findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false,
				IResource.DEPTH_INFINITE);
		assertEquals(1, markers.length);

		String resourceHandle = new JavaStructureBridge().getHandleIdentifier(m2.getCompilationUnit());
		assertTrue(ContextCore.getContextManager().getElement(resourceHandle).getInterest().isInteresting());

		// put it back
		type1.createMethod("public void m1() { }", null, true, null);

		// XXX: put this back, but it needs to wait on the resource marker
		// update somehow
		// project.build();
		// project.build(); // HACK
		// project.build(); // HACK
		// assertFalse(ContextCorePlugin.getContextManager().getElement(resourceHandle).getInterest().isInteresting());
	}
}
