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

package org.eclipse.mylyn.internal.sandbox.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractTaskRepositoryLinkProvider;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.ifeature.IFeatureModel;
import org.eclipse.pde.internal.core.natures.PDE;

/**
 * Task repository link provider for Eclipse.org PDE projects
 * 
 * @author Eugene Kuleshov
 */
// TODO could use extension point to declare mapping for 3rd party plugin providers
public class EclipseTaskRepositoryLinkProvider extends AbstractTaskRepositoryLinkProvider {

	@Override
	public TaskRepository getTaskRepository(IResource resource, IRepositoryManager repositoryManager) {
		IProject project = resource.getProject();
		if (PDE.hasPluginNature(project)) {
			IPluginModelBase pluginModel = PluginRegistry.findModel(project);
			if (pluginModel != null) {
				String providerName = pluginModel.getPluginBase().getProviderName();
				return getTaskRepository(providerName, pluginModel, repositoryManager);
			}
		} else if (PDE.hasFeatureNature(project)) {
			IFeatureModel featureModel = PDECore.getDefault().getFeatureModelManager().getFeatureModel(project);
			if (featureModel != null) {
				String providerName = featureModel.getFeature().getProviderName();
				return getTaskRepository(providerName, featureModel, repositoryManager);
			}
		} else if (PDE.hasUpdateSiteNature(project)) {
			// TODO could use referenced features to lookup task repository
		}
		return null;
	}

	private TaskRepository getTaskRepository(String providerName, IModel model, IRepositoryManager repositoryManager) {
		if (providerName.startsWith("%")) {
			providerName = model.getResourceString(providerName);
		}
		if ("Eclipse.org".equals(providerName)) {
			return repositoryManager.getRepository(BugzillaCorePlugin.CONNECTOR_KIND, "https://bugs.eclipse.org/bugs");
		}
		return null;
	}

}
