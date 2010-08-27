/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Eike Stepper - fixes for bug 323568
 *******************************************************************************/

package org.eclipse.mylyn.builds.core.spi;

import java.io.Reader;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlanData;
import org.eclipse.mylyn.builds.core.IBuildPlanWorkingCopy;
import org.eclipse.mylyn.builds.core.IBuildWorkingCopy;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.mylyn.internal.builds.core.BuildFactory;

/**
 * @author Steffen Pingel
 */
public abstract class BuildServerBehaviour {

	public BuildServerBehaviour() {
	}

	protected IBuildPlanWorkingCopy createBuildPlan() {
		return BuildFactory.eINSTANCE.createBuildPlan();
	}

	protected IBuildWorkingCopy createBuild() {
		return BuildFactory.eINSTANCE.createBuild();
	}

	public abstract List<IBuild> getBuilds(BuildRequest request, IOperationMonitor monitor) throws CoreException;

	public abstract BuildServerConfiguration getConfiguration();

	public abstract Reader getConsole(IBuild build, IOperationMonitor monitor) throws CoreException;

	public abstract List<IBuildPlanData> getPlans(BuildPlanRequest request, IOperationMonitor monitor)
			throws CoreException;

	public abstract BuildServerConfiguration refreshConfiguration(IOperationMonitor monitor) throws CoreException;

	public abstract void runBuild(RunBuildRequest request, IOperationMonitor monitor) throws CoreException;

	public abstract IStatus validate(IOperationMonitor monitor) throws CoreException;

}
