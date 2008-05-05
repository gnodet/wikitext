/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.sandbox.dev.properties;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskElement;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Abstract class to display various properties in the Eclipse Properties View.<br /> See <a
 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=210639">Bug 210639</a> and <a
 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=208275">Bug 208275</a><br />
 * 
 * @author Maarten Meijer
 */
public abstract class AbstractTaskContainerPropertySource implements IPropertySource {

	protected static final String CHILDREN = "children";

	protected static final String DESCENDANDS = "descendents";

	protected static final String IS_CYCLIC = "iscyclic";

	protected static final String SUMMARY = "summary";

	private boolean cyclic;

	protected AbstractTaskContainer container;

	protected String description;

	public AbstractTaskContainerPropertySource(AbstractTaskContainer adaptableObject) {
		container = adaptableObject;
		description = container.getClass().getName();
	}

	/**
	 * @return an expanded set of all descendants, excluding itself.
	 */
	public Set<ITask> getDescendants(ITaskElement parent) {
		Set<ITask> childrenWithoutCycles = new HashSet<ITask>();
		this.getDescendantsHelper(parent, childrenWithoutCycles, parent);
		return Collections.unmodifiableSet(childrenWithoutCycles);
	}

	protected void getDescendantsHelper(ITaskElement parent, Set<ITask> visited, ITaskElement root) {
		for (ITask child : parent.getChildrenInternal()) {
			if (child == root) {
				cyclic = true;
			}
			if (!visited.contains(child) && child != root) {
				visited.add(child);
				getDescendantsHelper(child, visited, root);
			}
		}
	}

	/**
	 * @return true if the parent also occurs in its descendants.
	 */
	public boolean containsCyclic(AbstractTaskContainer parent) {
		Set<ITask> childrenWithoutCycles = new HashSet<ITask>();
		Set<ITaskElement> parentStack = new HashSet<ITaskElement>();
		cyclic = false;
		this.containsCyclicHelper(parent, childrenWithoutCycles, parentStack);
		return cyclic;
	}

	protected void containsCyclicHelper(ITaskElement parent, Set<ITask> visited, Set<ITaskElement> parentStack) {
		// fast exit
		if (cyclic) {
			return;
		}

		parentStack.add(parent);
		for (ITask child : parent.getChildrenInternal()) {
			if (parentStack.contains(child)) {
				cyclic = true;
				return;
			} else {
				containsCyclicHelper(child, visited, parentStack);
			}
		}
		parentStack.remove(parent);
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		TextPropertyDescriptor children = new TextPropertyDescriptor(CHILDREN, "Total Children (internal)");
		children.setCategory(description);
		TextPropertyDescriptor descendants = new TextPropertyDescriptor(DESCENDANDS, "Total Descendants");
		descendants.setCategory(description);
		TextPropertyDescriptor cyclic = new TextPropertyDescriptor(IS_CYCLIC, "Cycle in descendants graph?");
		cyclic.setCategory(description);
		return new IPropertyDescriptor[] { children, descendants, cyclic };
	}

	public Object getPropertyValue(Object id) {
		if (CHILDREN.equals(id)) {
			return container.getChildrenInternal().size();
		} else if (DESCENDANDS.equals(id)) {
			return getDescendants(container).size();
		} else if (IS_CYCLIC.equals(id)) {
			return containsCyclic(container) ? "Cyclic" : "Not Cyclic";
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		// ignore
		return false;
	}

	public void resetPropertyValue(Object id) {
		// ignore
	}

	public void setPropertyValue(Object id, Object value) {
		// ignore
	}

	public Object getEditableValue() {
		// ignore
		return null;
	}

	public IPropertyDescriptor[] appendSpecifics(IPropertyDescriptor[] specific, IPropertyDescriptor[] these) {
		IPropertyDescriptor[] all = new IPropertyDescriptor[specific.length + these.length];
		System.arraycopy(these, 0, all, 0, these.length);
		System.arraycopy(specific, 0, all, these.length, specific.length);
		return all;
	}

}