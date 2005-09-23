/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.sandbox.viz;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylar.core.IMylarContextEdge;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.zest.core.viewers.IGraphContentProvider;

/**
 * @author Mik Kersten
 */
public class TaskscapeGraphContentProvider implements IGraphContentProvider {

    public Object getSource(Object rel) {
        if (rel instanceof IMylarContextEdge) {
            return ((IMylarContextEdge)rel).getSource();
        }  else {
            return null;
        }
    }
    
    public Object getDestination(Object rel) {
        if (rel instanceof IMylarContextEdge) {
            return ((IMylarContextEdge)rel).getTarget();
        } else {
            return null;
        }
	}
    
	public Object[] getRelationships() {
        List<IMylarContextNode> nodes = MylarPlugin.getContextManager().getActiveContext().getAllElements();
        Set<IMylarContextEdge> edges = new HashSet<IMylarContextEdge>();
        for (IMylarContextNode node : nodes) edges.addAll(node.getEdges());
        return edges.toArray();
	}

    public Object[] getElements( Object o ) {
        return null;
    }
    

    public double getWeight(Object rel) {
        if (rel instanceof IMylarContextEdge) {
            return ((IMylarContextEdge)rel).getDegreeOfInterest().getValue();
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
