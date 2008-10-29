/*******************************************************************************
 * Copyright (c) 2004, 2008 Eugene Kuleshov and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.sandbox.tests.util;

import java.io.ByteArrayInputStream;

import org.eclipse.core.internal.events.BuildCommand;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.pde.internal.core.natures.PDE;

/**
 * @author Eugene Kuleshov
 */
public class PdeProject {

	private final IProject project;

	public PdeProject(String name) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject(name);
		project.create(null);
		project.open(null);
	}

	public IProject getProject() {
		return project;
	}

	public void createPlugin(String mf) throws CoreException {
		setPluginNature();

		IProject project = getProject();

		IFolder folder = project.getFolder("META-INF");
		if (!folder.exists()) {
			folder.create(true, false, null);
		}

		IFile file = folder.getFile("MANIFEST.MF");
		if (!file.exists()) {
			file.create(new ByteArrayInputStream(mf.getBytes()), true, null);
		}

		project.build(IncrementalProjectBuilder.FULL_BUILD, null);
	}

	public void createFeature(String feature) throws CoreException {
		setFeatureNature();

		IProject project = getProject();

		IFile file = project.getFile("feature.xml");
		if (!file.exists()) {
			file.create(new ByteArrayInputStream(feature.getBytes()), true, null);
		}

		project.build(IncrementalProjectBuilder.FULL_BUILD, null);
	}

	public void setPluginNature() throws CoreException {
		IProjectDescription description = project.getDescription();

		description.setNatureIds(new String[] { PDE.PLUGIN_NATURE, JavaCore.NATURE_ID });

		BuildCommand javaBuildCommand = new BuildCommand();
		javaBuildCommand.setBuilderName(JavaCore.BUILDER_ID);

		BuildCommand manifestBuildCommand = new BuildCommand();
		manifestBuildCommand.setName(PDE.MANIFEST_BUILDER_ID);

		BuildCommand schemaBuildCommand = new BuildCommand();
		schemaBuildCommand.setName(PDE.SCHEMA_BUILDER_ID);

		description.setBuildSpec(new ICommand[] { javaBuildCommand, manifestBuildCommand, schemaBuildCommand });

		project.setDescription(description, null);
	}

	public void setFeatureNature() throws CoreException {
		IProjectDescription description = project.getDescription();

		description.setNatureIds(new String[] { PDE.FEATURE_NATURE });

		BuildCommand featureBuildCommand = new BuildCommand();
		featureBuildCommand.setName(PDE.FEATURE_BUILDER_ID);

		description.setBuildSpec(new ICommand[] { featureBuildCommand });

		project.setDescription(description, null);
	}

	public void delete() throws CoreException {
		getProject().delete(true, true, null);
	}

}
