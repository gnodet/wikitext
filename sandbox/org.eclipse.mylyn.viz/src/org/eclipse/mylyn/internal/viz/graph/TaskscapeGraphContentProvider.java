/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.viz.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.context.core.IMylarRelation;
import org.eclipse.mylar.zest.core.viewers.IGraphContentProvider;

/**
 * @author Mik Kersten
 */
public class TaskscapeGraphContentProvider implements IGraphContentProvider {

	public Object getSource(Object rel) {
		if (rel instanceof IMylarRelation) {
			return ((IMylarRelation) rel).getSource();
		} else {
			return null;
		}
	}

	public Object getDestination(Object rel) {
		if (rel instanceof IMylarRelation) {
			return ((IMylarRelation) rel).getTarget();
		} else {
			return null;
		}
	}

	public Object[] getRelationships() {
		List<IMylarElement> nodes = ContextCorePlugin.getContextManager().getActiveContext().getAllElements();
		Set<IMylarRelation> edges = new HashSet<IMylarRelation>();
		for (IMylarElement node : nodes)
			edges.addAll(node.getRelations());
		return edges.toArray();
	}

	public Object[] getElements(Object o) {
		return null;
	}

	public double getWeight(Object rel) {
		if (rel instanceof IMylarRelation) {
			return ((IMylarRelation) rel).getInterest().getValue();
		} else {
			return 0;
		}
	}

	public void dispose() {
		// don't care when we are disposed
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// don't care when the input changes
	}
}
