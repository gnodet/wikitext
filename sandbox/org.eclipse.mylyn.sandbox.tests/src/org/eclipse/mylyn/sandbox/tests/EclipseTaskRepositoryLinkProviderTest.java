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

package org.eclipse.mylyn.sandbox.tests;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.sandbox.tests.util.PdeProject;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Eugene Kuleshov
 */
public class EclipseTaskRepositoryLinkProviderTest extends TestCase {

	public void testEclipsePluginProject() throws Exception {
		TaskRepository repo1 = TasksUiPlugin.getRepositoryManager().getRepository("https://bugs.eclipse.org/bugs");
		assertNotNull("Eclipse.org repository is not found", repo1);

		String mf = "Manifest-Version: 1.0\n" + //
				"Bundle-ManifestVersion: 2\n" + //
				"Bundle-Name: Mylyn PDE Tests 1\n" + // 
				"Bundle-SymbolicName: org.eclipse.mylyn.pde.tests1\n" + // 
				"Bundle-Version: 1.0.0\n" + //
				"Bundle-Vendor: Eclipse.org\n" + // 
				"Bundle-RequiredExecutionEnvironment: J2SE-1.3\n";

		PdeProject pdeProject = new PdeProject("eclipsePluginProject");
		pdeProject.createPlugin(mf);

		IProject project = pdeProject.getProject();

		TasksUiPlugin tasksUiPlugin = TasksUiPlugin.getDefault();

		TaskRepository repo2 = tasksUiPlugin.getRepositoryForResource(project);
		assertEquals(repo1, repo2);

		assertFalse(tasksUiPlugin.canSetRepositoryForResource(project));

		pdeProject.delete();
	}

	public void testAcmePluginProject() throws Exception {
		TaskRepository repo1 = TasksUiPlugin.getRepositoryManager().getRepository("https://bugs.eclipse.org/bugs");
		assertNotNull("Eclipse.org repository is not found", repo1);

		String mf = "Manifest-Version: 1.0\n" + //
				"Bundle-ManifestVersion: 2\n" + //
				"Bundle-Name: Mylyn PDE Tests 2\n" + //
				"Bundle-SymbolicName: org.eclpse.mylyn.pde.tests2\n" + // 
				"Bundle-Version: 1.0.0\n" + //
				"Bundle-Vendor: Acme.org\n" + //
				"Bundle-RequiredExecutionEnvironment: J2SE-1.3\n";

		PdeProject pdeProject = new PdeProject("acmePluginProject");
		pdeProject.createPlugin(mf);

		TaskRepository repo2 = TasksUiPlugin.getDefault().getRepositoryForResource(pdeProject.getProject());

		pdeProject.delete();

		assertNull("Not expected to find repository " + repo2, repo2);
	}

	public void testEclipseFeatureProject() throws Exception {
		TaskRepository repo1 = TasksUiPlugin.getRepositoryManager().getRepository("https://bugs.eclipse.org/bugs");
		assertNotNull("Eclipse.org repository is not found", repo1);

		String feature = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + // 
				"<feature\n" + //
				"      id=\"org.eclipse.mylyn.pde.tests2_feature\"\n" + // 
				"      label=\"Mylyn PDE Test Feature 2\"\n" + //
				"      version=\"2.3.0.qualifier\"\n" + //
				"      provider-name=\"Eclipse.org\">" + //
				"</feature>";

		PdeProject pdeProject = new PdeProject("eclipseFeatureProject");
		pdeProject.createFeature(feature);

		IProject project = pdeProject.getProject();

		TasksUiPlugin tasksUiPlugin = TasksUiPlugin.getDefault();

		TaskRepository repo2 = tasksUiPlugin.getRepositoryForResource(project);
		assertEquals(repo1, repo2);

		assertFalse(tasksUiPlugin.canSetRepositoryForResource(project));

		pdeProject.delete();
	}

	public void testAcmeFeatureProject() throws Exception {
		TaskRepository repo1 = TasksUiPlugin.getRepositoryManager().getRepository("https://bugs.eclipse.org/bugs");
		assertNotNull("Eclipse.org repository is not found", repo1);

		String feature = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + // 
				"<feature\n" + //
				"      id=\"org.eclipse.mylyn.pde.tests2_feature\"\n" + // 
				"      label=\"Mylyn PDE Test Feature 2\"\n" + //
				"      version=\"2.3.0.qualifier\"\n" + //
				"      provider-name=\"Acme.org\">" + //
				"</feature>";

		PdeProject pdeProject = new PdeProject("acmeFeatureProject");
		pdeProject.createFeature(feature);

		TaskRepository repo2 = TasksUiPlugin.getDefault().getRepositoryForResource(pdeProject.getProject());

		pdeProject.delete();

		assertNull("Not expected to find repository " + repo2, repo2);
	}

}