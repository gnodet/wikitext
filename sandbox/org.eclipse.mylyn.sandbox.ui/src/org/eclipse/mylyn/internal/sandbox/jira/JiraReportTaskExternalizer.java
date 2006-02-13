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

package org.eclipse.mylar.internal.sandbox.jira;

import org.eclipse.mylar.internal.tasklist.AbstractRepositoryClient;
import org.eclipse.mylar.internal.tasklist.AbstractQueryHit;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.internal.tasklist.ITask;
import org.eclipse.mylar.internal.tasklist.ITaskContainer;
import org.eclipse.mylar.internal.tasklist.ITaskListExternalizer;
import org.eclipse.mylar.internal.tasklist.TaskList;
import org.eclipse.mylar.internal.tasklist.TaskExternalizationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Mik Kersten
 */
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

	public String getQueryTagNameForElement(AbstractRepositoryQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getQueryHitTagName() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canCreateElementFor(ITaskContainer category) {
		// TODO Auto-generated method stub
		return false;
	}

	public Element createCategoryElement(ITaskContainer category, Document doc, Element parent) {
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

	public void readCategory(Node node, TaskList tlist) throws TaskExternalizationException {
		// TODO Auto-generated method stub

	}

	public boolean canReadTask(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

	public ITask readTask(Node node, TaskList tlist, ITaskContainer category, ITask parent)
			throws TaskExternalizationException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canCreateElementFor(AbstractRepositoryQuery category) {
		// TODO Auto-generated method stub
		return false;
	}

	public Element createQueryElement(AbstractRepositoryQuery query, Document doc, Element parent) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canReadQuery(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

	public void readQuery(Node node, TaskList tlist) throws TaskExternalizationException {
		// TODO Auto-generated method stub

	}

	public boolean canCreateElementFor(AbstractQueryHit queryHit) {
		// TODO Auto-generated method stub
		return false;
	}

	public Element createQueryHitElement(AbstractQueryHit queryHit, Document doc, Element parent) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canReadQueryHit(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

	public void readQueryHit(Node node, TaskList tlist, AbstractRepositoryQuery query) throws TaskExternalizationException {
		// TODO Auto-generated method stub

	}

	public AbstractRepositoryClient getRepositoryClient() {
		// ignore
		return null;
	}

}
