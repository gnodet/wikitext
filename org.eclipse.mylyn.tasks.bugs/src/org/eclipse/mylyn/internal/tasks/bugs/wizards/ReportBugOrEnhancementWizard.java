/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.IProduct;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.IProvider;
import org.eclipse.mylyn.internal.tasks.bugs.AbstractSupportElement;
import org.eclipse.mylyn.internal.tasks.bugs.AttributeTaskMapper;
import org.eclipse.mylyn.internal.tasks.bugs.SupportCategory;
import org.eclipse.mylyn.internal.tasks.bugs.SupportProduct;
import org.eclipse.mylyn.internal.tasks.bugs.SupportProvider;
import org.eclipse.mylyn.internal.tasks.bugs.SupportProviderManager;
import org.eclipse.mylyn.internal.tasks.bugs.SupportRequest;
import org.eclipse.mylyn.internal.tasks.bugs.TaskErrorReporter;
import org.eclipse.mylyn.internal.tasks.bugs.TasksBugsPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;

/**
 * @author Steffen Pingel
 */
public class ReportBugOrEnhancementWizard extends Wizard {

	private class SupportContentProvider implements IStructuredContentProvider {

		private SupportProviderManager providerManager;

		private Object input;

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof SupportProvider) {
				List<SupportProduct> providerProducts = getProducts((SupportProvider) inputElement);
				return providerProducts.toArray();
			} else if (input == inputElement) {
				List<AbstractSupportElement> elements = new ArrayList<AbstractSupportElement>();
				Collection<SupportCategory> categories = providerManager.getCategories();
				for (SupportCategory category : categories) {
					List<IProvider> providers = category.getProviders();
					// filter valid providers
					List<IProvider> validProviders = new ArrayList<IProvider>();
					for (IProvider provider : providers) {
						if (isValid((SupportProvider) provider)) {
							validProviders.add(provider);
						}
					}
					if (!validProviders.isEmpty()) {
						elements.add(category);
						for (IProvider provider : validProviders) {
							elements.add((AbstractSupportElement) provider);
						}
					}
				}
				return elements.toArray();
			} else {
				return new Object[0];
			}
		}

		private boolean isValid(SupportProvider provider) {
			Collection<SupportProduct> products = providerManager.getProducts();
			for (SupportProduct product : products) {
				if (provider.equals(product.getProvider()) && product.isInstalled()) {
					return true;
				}
			}
			return false;
		}

		private List<SupportProduct> getProducts(SupportProvider provider) {
			Collection<SupportProduct> products = providerManager.getProducts();
			List<SupportProduct> providerProducts = new ArrayList<SupportProduct>();
			for (SupportProduct product : products) {
				if (provider.equals(product.getProvider()) && product.isInstalled()) {
					providerProducts.add(product);
				}
			}
			return providerProducts;
		}

		public void dispose() {
			// ignore
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.input = newInput;
			this.providerManager = TasksBugsPlugin.getTaskErrorReporter().getProviderManager();
		}

	}

	private SupportContentProvider contentProvider;

	public ReportBugOrEnhancementWizard() {
		setForcePreviousAndNextButtons(true);
		setNeedsProgressMonitor(true);
		setWindowTitle(Messages.ReportBugOrEnhancementWizard_Report_Bug_or_Enhancement);
		setDefaultPageImageDescriptor(TasksUiImages.BANNER_REPORT_BUG);
	}

	@Override
	public void addPages() {
		contentProvider = new SupportContentProvider();
		SelectSupportElementPage page = new SelectSupportElementPage("selectProvider", contentProvider); //$NON-NLS-1$
		page.setInput(new Object());
		addPage(page);
	}

	@Override
	public boolean canFinish() {
		return getSelectedElement() instanceof SupportProduct;
	}

	public AbstractSupportElement getSelectedElement() {
		IWizardPage page = getContainer().getCurrentPage();
		if (page != null) {
			AbstractSupportElement element = ((SelectSupportElementPage) page).getSelectedElement();
			if (!(element instanceof SupportProduct)) {
				Object[] elements = contentProvider.getElements(element);
				if (elements.length == 1) {
					return (AbstractSupportElement) elements[0];
				}
			}
			return element;
		}
		return null;
	}

	@Override
	public boolean performFinish() {
		final AbstractSupportElement product = getSelectedElement();
		if (!(product instanceof SupportProduct)) {
			return false;
		}

		TaskErrorReporter reporter = TasksBugsPlugin.getTaskErrorReporter();
		IStatus status = new ProductStatus((IProduct) product);
		SupportRequest request = reporter.preProcess(status, ((ProductStatus) status).getProduct());
		if (!((AttributeTaskMapper) request.getDefaultContribution()).isMappingComplete()) {
			TasksUiInternal.displayStatus(Messages.ReportBugOrEnhancementWizard_Report_Bug_or_Enhancement, new Status(
					IStatus.ERROR, TasksBugsPlugin.ID_PLUGIN,
					Messages.ReportBugOrEnhancementWizard_Support_request_faild_Information_incomplete_Error));
			return false;
		}
		return reporter.process(request.getDefaultContribution(), getContainer());
	}

}
