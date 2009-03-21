/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.examples.xml.core;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.osgi.util.NLS;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlTaskDataHandler extends AbstractTaskDataHandler {

	public XmlTaskDataHandler() {
	}

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository repository) {
		return new TaskAttributeMapper(repository);
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData data, ITaskMapping initializationData,
			IProgressMonitor monitor) throws CoreException {
		// ignore
		return false;
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
			Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException {
		// ignore
		return null;
	}

	public TaskData readTaskData(TaskRepository repository, File file) throws CoreException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder loader = factory.newDocumentBuilder();
			Document document = loader.parse(file);
			TaskData taskData = parseDocument(repository, file, document.getDocumentElement());
			return taskData;
		} catch (Exception e) {
			//e.printStackTrace();
			throw new CoreException(new Status(IStatus.ERROR, XmlCorePlugin.ID_PLUGIN, NLS.bind(
					"Error parsing task {0}", file.getAbsolutePath()), e));
		}
	}

	private TaskData parseDocument(TaskRepository repository, File file, Element input) {
		String taskId = getTaskId(repository, file);
		TaskData taskData = new TaskData(getAttributeMapper(repository), repository.getConnectorKind(),
				repository.getRepositoryUrl(), taskId);

		TaskAttribute attribute = taskData.getRoot().createAttribute(TaskAttribute.SUMMARY);
		attribute.setValue(getValue(input, "summary"));

		attribute = taskData.getRoot().createAttribute(TaskAttribute.DESCRIPTION);
		attribute.setValue(getValue(input, "description"));

		attribute = taskData.getRoot().createAttribute(TaskAttribute.DATE_MODIFICATION);
		taskData.getAttributeMapper().setDateValue(attribute, new Date(file.lastModified()));

		attribute = taskData.getRoot().createAttribute(TaskAttribute.TASK_URL);
		try {
			attribute.setValue(file.toURI().toURL().toString());
		} catch (MalformedURLException e) {
		}

		return taskData;
	}

	private String getValue(Element input, String elementName) {
		NodeList nodes = input.getElementsByTagName(elementName);
		if (nodes.getLength() > 0) {
			return nodes.item(0).getTextContent();
		}
		return "";
	}

	private String getTaskId(TaskRepository taskRepository, File file) {
		Matcher matcher = XmlClient.ID_PATTERN.matcher(file.getName());
		if (matcher.find()) {
			return matcher.group(1);
		}
		return file.getName();
	}

}
