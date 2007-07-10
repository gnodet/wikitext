/*******************************************************************************
 * Copyright (c) 2007 - 2007 CodeGear and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.xplanner.ui.wizard;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.xplanner.ui.XPlannerImages;
import org.eclipse.swt.graphics.Image;
import org.xplanner.soap.*;


/**
 * @author Ravi Kumar
 * @author Helen Bershadskaya
 */
public class ProjectsViewerLabelProvider extends LabelProvider {

	public String getText(Object element) {
		String text = null;
	
		if (element instanceof ProjectData) {
			text = ((ProjectData)element).getName();
		}
		else if (element instanceof IterationData) {
			text = ((IterationData)element).getName();
		}
		else if (element instanceof UserStoryData) {
			text = ((UserStoryData)element).getName();
		}
		
		if (text == null) {
			text = Messages.ProjectsViewerLabelProvider_NO_NAME;
		}
		
		return text;
	}

  public Image getImage(Object element) {
		Image image = null;
		
		if (element instanceof ProjectData) {
			image = XPlannerImages.getImage(XPlannerImages.TREEITEM_PROJECT);
		}
		else if (element instanceof IterationData) {
			image = XPlannerImages.getImage(XPlannerImages.TREEITEM_ITERATION);
		}
		else if (element instanceof UserStoryData) {
			image = XPlannerImages.getImage(XPlannerImages.TREEITEM_USER_STORY);
		}
		
		return image;
  }

}
