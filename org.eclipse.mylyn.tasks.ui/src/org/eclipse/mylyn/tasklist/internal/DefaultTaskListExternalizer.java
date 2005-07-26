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

package org.eclipse.mylar.tasklist.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.AbstractCategory;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskListExternalizer;
import org.eclipse.mylar.tasklist.Task;
import org.eclipse.mylar.tasklist.ui.actions.TaskActivateAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class DefaultTaskListExternalizer implements ITaskListExternalizer {

	public static final String LABEL = "Label";
	public static final String HANDLE = "Handle";
	public static final String TAG_CATEGORY = "Category";
	public static final String TAG_TASK = "Task";
	public static final String TAG_TASK_CATEGORY = "Task" + TAG_CATEGORY;
	
	public static final String LINK = "Link";
	public static final String ESTIMATED = "Estimated";
	public static final String ELAPSED = "Elapsed";
	public static final String NOTES = "Notes";
	public static final String BUGZILLA = "Bugzilla";
	public static final String ACTIVE = "Active";
	public static final String COMPLETE = "Complete";
	public static final String PRIORITY = "Priority";
	public static final String PATH = "Path";
	public static final String FALSE = "false";
	public static final String TRUE = "true";
	public static final String NAME = "Name";
	public static final String END_DATE = "EndDate";

	private List<ITaskListExternalizer> externalizers = new ArrayList<ITaskListExternalizer>();
	
	void setExternalizers(List<ITaskListExternalizer> externalizers) {
		this.externalizers = externalizers;
	}
	
	public boolean canCreateElementFor(AbstractCategory category) {
		return category instanceof TaskCategory;
	}
	
	public Element createCategoryElement(AbstractCategory category, Document doc, Element parent) {
		Element node = doc.createElement(getCategoryTagName());
		node.setAttribute(NAME, category.getDescription(false));
		
		for (ITask task : ((TaskCategory)category).getChildren()) {
			try {
				Element element = null;
				for (ITaskListExternalizer externalizer : externalizers) {
					if (externalizer.canCreateElementFor(task)) element = externalizer.createTaskElement(task, doc, node);
				}
				if (element == null) createTaskElement(task, doc, node);
			} catch (Exception e) {
				MylarPlugin.log(e, e.getMessage());
			}
			
		}
		parent.appendChild(node);
		return node;
	}

	public boolean canCreateElementFor(ITask task) {
		return true;
	}
	
	public Element createTaskElement(ITask task, Document doc, Element parent) {
		Element node = doc.createElement(getTaskTagName());
		node.setAttribute(PATH, task.getPath());
		node.setAttribute(LABEL, task.getLabel());
		node.setAttribute(HANDLE, task.getHandle());
		node.setAttribute(PRIORITY, task.getPriority());
		
		if (task.isCompleted()) {
			node.setAttribute(COMPLETE, TRUE);
		} else {
			node.setAttribute(COMPLETE, FALSE);
		}
		if (task.isActive()) {
			node.setAttribute(ACTIVE, TRUE);
		} else {
			node.setAttribute(ACTIVE, FALSE);
		}
		node.setAttribute(BUGZILLA, FALSE); // TODO: this is not great
		
		node.setAttribute(NOTES, task.getNotes());
		node.setAttribute(ELAPSED, task.getElapsedTime());
		node.setAttribute(ESTIMATED, task.getEstimatedTime());
		node.setAttribute(END_DATE, task.getEndDateString());		
		
		List<String> rl = task.getRelatedLinks().getLinks();
		int i = 0;
		for (String link : rl) {
			node.setAttribute(LINK+i, link);
			i++;
		}
		
		for (ITask t : task.getChildren()) {
			createTaskElement(t, doc, node);
		}
		parent.appendChild(node);
		return node;
	}

	public boolean canReadCategory(Node node) {
		return node.getNodeName().equals(getCategoryTagName());
	}
	
	public void readCategory(Node node, TaskList tlist)  throws MylarExternalizerException {
		boolean hasCaughtException = false;
		Element element = (Element) node;
		TaskCategory category = new TaskCategory(element.getAttribute("Name"));
		tlist.addCategory(category);
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			boolean read = false;
			try {
				for (ITaskListExternalizer externalizer : externalizers) {
					if (externalizer.canReadTask(child)) {
						category.addTask(externalizer.readTask(child, tlist,
								category, null));
						read = true;
					}
				}
				if (!read && canReadTask(child)) {
					category.addTask(readTask(child, tlist, category, null));
				}
			} catch (MylarExternalizerException e) {
				hasCaughtException = true;
			}
		}
		if (hasCaughtException) throw new MylarExternalizerException("Failed to load all tasks");
	}

	public boolean canReadTask(Node node) {
		return node.getNodeName().equals(getTaskTagName());
	}

	public ITask readTask(Node node, TaskList tlist, AbstractCategory category, ITask parent)  throws MylarExternalizerException {
		Element element = (Element) node;
		String handle;
		String label;
		if (element.hasAttribute(HANDLE)) {
			handle = element.getAttribute(HANDLE);
		} else {
			throw new MylarExternalizerException("Handle not stored for task");			
		}
		if (element.hasAttribute(LABEL)) {
			label = element.getAttribute(LABEL);
		} else {
			label = "Description was corrupted in stored tasklist";
		}
		Task task = new Task(handle, label);		
		readTaskInfo(task, tlist, element, category, parent);
		return task;
	}

	protected void readTaskInfo(ITask task, TaskList tlist, Element element, AbstractCategory category, ITask parent)  throws MylarExternalizerException{
		if (element.hasAttribute(PRIORITY)) {
			task.setPriority(element.getAttribute(PRIORITY));
		} else {
			task.setPriority("P3");
		}
		if (element.hasAttribute(PATH)) {
			task.setPath(element.getAttribute(PATH));
		} else {
			task.setPath(task.getHandle());
		}		
		
		if (element.getAttribute(ACTIVE).compareTo(TRUE) == 0) {
			task.setActive(true);
			tlist.setActive(task, true);
			new TaskActivateAction(task).run();
		} else {
			task.setActive(false);
		}			
		if (element.hasAttribute(NOTES)) {
			task.setNotes(element.getAttribute(NOTES));
		} else {
			task.setNotes("");
		}
		if (element.hasAttribute(ELAPSED)) {
			task.setElapsedTime(element.getAttribute(ELAPSED));			
		} else {
			task.setElapsedTime("");
		}
		if (element.hasAttribute(ESTIMATED)) {
			task.setEstimatedTime(element.getAttribute(ESTIMATED));			
		} else {
			task.setEstimatedTime("");
		}
		// NOTE: do not change the order of complete and end date!!
		if (element.getAttribute(COMPLETE).compareTo(TRUE) == 0) {
			task.setCompleted(true);
		} else {
			task.setCompleted(false);
		}
		if (element.hasAttribute(END_DATE)) {
			task.setEndDate(element.getAttribute(END_DATE));
		} else {
			task.setEndDate("");
		}		
		int i = 0;
		while (element.hasAttribute(LINK+i)) {
			task.getRelatedLinks().add(element.getAttribute(LINK+i));
			i++;
		}
		if (category != null) {
			task.setCategory((TaskCategory) category);
		} else {
			task.setCategory(null);
		}
		task.setParent(parent);
		NodeList list = element.getChildNodes();
		for (int j = 0; j < list.getLength(); j++) {
			Node child = list.item(j);
			task.addSubTask(readTask(child, tlist, null, task));
		}
	}

	public String getCategoryTagName() {
		return TAG_TASK_CATEGORY;
	}

	public String getTaskTagName() {
		return TAG_TASK;
	}

	public void createRegistry(Document doc, Node parent) {
		// nothing to do
	}
}
