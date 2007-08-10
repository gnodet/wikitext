/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public interface ITaskListPresentation {

	public abstract AbstractTaskListContentProvider getContentProvider();

	public abstract String getPresentationName();

	public abstract ImageDescriptor getImageDescriptor();

	public abstract String getId();

}