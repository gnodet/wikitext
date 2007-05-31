/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylar.xplanner.ui.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.xplanner.ui.XPlannerMylarUIPlugin;
import org.eclipse.mylar.xplanner.ui.XPlannerCustomQuery;


/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class XPlannerQueryWizardUtils {
	private static final boolean SHOW_SELECT_QUERY_PAGE = false;
	
	private XPlannerQueryWizardUtils() {
		
	}
	
  public static boolean isShowSelectQueryPage() {
  	return SHOW_SELECT_QUERY_PAGE;
  }
  
	public static AbstractXPlannerQueryWizardPage addQueryWizardFirstPage(Wizard wizard, TaskRepository repository,
		XPlannerCustomQuery existingQuery) {
		
		AbstractXPlannerQueryWizardPage queryPage = null;
		
		try {
			if (isShowSelectQueryPage()) {
				queryPage = new XPlannerQuerySelectionWizardPage(repository);
			}
			else {
				queryPage = new XPlannerCustomQueryPage(repository, existingQuery);
			}
			queryPage.setWizard(wizard);
			wizard.addPage(queryPage);
		}
		catch (RuntimeException e) {
			if (e.getCause() instanceof CoreException) { 
				XPlannerMylarUIPlugin.log(e.getCause(), Messages.XPlannerQueryWizardUtils_COULD_NOT_CREATE_QUERY_PAGE_MESSAGE, true);
			}
			else {
				throw e;
			}
		}		
		
		return queryPage;
	}

  
}
