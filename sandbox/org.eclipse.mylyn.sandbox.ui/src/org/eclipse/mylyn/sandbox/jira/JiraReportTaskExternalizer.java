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

package org.eclipse.mylar.sandbox.jira;

import org.eclipse.mylar.tasklist.ITaskListCategory;

import org.eclipse.mylar.tasklist.IQuery;
import org.eclipse.mylar.tasklist.IQueryHit;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.ITaskListExternalizer;
import org.eclipse.mylar.tasklist.internal.MylarExternalizerException;
import org.eclipse.mylar.tasklist.internal.TaskList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JiraReportTaskExternalizer implements ITaskListExternalizer {

	public void createRegistry(Document doc, Node parent) {
		// TODO Auto-generated method stub
	}

	public String getCategoryTagName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTaskTagName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getQueryTagNameForElement(IQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getQueryHitTagName() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canCreateElementFor(ITaskListCategory category) {
		// TODO Auto-generated method stub
		return false;
	}

	public Element createCategoryElement(ITaskListCategory category, Document doc,
			Element parent) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canCreateElementFor(ITask task) {
		// TODO Auto-generated method stub
		return false;
	}

	public Element createTaskElement(ITask task, Document doc, Element parent) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canReadCategory(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

	public void readCategory(Node node, TaskList tlist)
			throws MylarExternalizerException {
		// TODO Auto-generated method stub

	}

	public boolean canReadTask(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

	public ITask readTask(Node node, TaskList tlist, ITaskListCategory category,
			ITask parent) throws MylarExternalizerException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canCreateElementFor(IQuery category) {
		// TODO Auto-generated method stub
		return false;
	}

	public Element createQueryElement(IQuery query, Document doc, Element parent) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canReadQuery(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

	public void readQuery(Node node, TaskList tlist)
			throws MylarExternalizerException {
		// TODO Auto-generated method stub

	}

	public boolean canCreateElementFor(IQueryHit queryHit) {
		// TODO Auto-generated method stub
		return false;
	}

	public Element createQueryHitElement(IQueryHit queryHit, Document doc,
			Element parent) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canReadQueryHit(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

	public void readQueryHit(Node node, TaskList tlist, IQuery query)
			throws MylarExternalizerException {
		// TODO Auto-generated method stub

	}

}
