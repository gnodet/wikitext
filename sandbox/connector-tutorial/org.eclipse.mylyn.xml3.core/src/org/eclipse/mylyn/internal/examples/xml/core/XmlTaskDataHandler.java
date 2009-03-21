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
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.RepositoryResponse.ResponseKind;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.osgi.util.NLS;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlTaskDataHandler extends AbstractTaskDataHandler {

	private final XmlConnector connector;

	public XmlTaskDataHandler(XmlConnector connector) {
		this.connector = connector;
	}

	@Override
	public TaskAttributeMapper getAttributeMapper(TaskRepository repository) {
		return new TaskAttributeMapper(repository);
	}

	@Override
	public boolean initializeTaskData(TaskRepository repository, TaskData taskData, ITaskMapping initializationData,
			IProgressMonitor monitor) throws CoreException {
		XmlClient client = connector.getClient(repository);
		XmlConfiguration configuration = client.getConfiguration(monitor);

		TaskAttribute attribute = taskData.getRoot().createAttribute(TaskAttribute.SUMMARY);
		attribute.getMetaData().setReadOnly(false).setType(TaskAttribute.TYPE_SHORT_RICH_TEXT).setLabel("Summary:");

		attribute = taskData.getRoot().createAttribute(TaskAttribute.DESCRIPTION);
		attribute.getMetaData().setReadOnly(false).setType(TaskAttribute.TYPE_LONG_RICH_TEXT).setLabel("Description:");

		attribute = taskData.getRoot().createAttribute(TaskAttribute.DATE_MODIFICATION);
		attribute.getMetaData().setReadOnly(false).setType(TaskAttribute.TYPE_DATETIME).setLabel("Modified:");

		if (!taskData.isNew()) {
			attribute = taskData.getRoot().createAttribute(TaskAttribute.TASK_URL);
			attribute.getMetaData().setReadOnly(true).setKind(TaskAttribute.KIND_DEFAULT).setType(
					TaskAttribute.TYPE_URL).setLabel("Location:");
			File file = client.getTask(taskData.getTaskId(), monitor);
			try {
				attribute.setValue(file.toURI().toURL().toString());
			} catch (MalformedURLException e) {
			}
		}

		attribute = taskData.getRoot().createAttribute(TaskAttribute.PRODUCT);
		attribute.getMetaData().setReadOnly(false).setKind(TaskAttribute.KIND_DEFAULT).setType(
				TaskAttribute.TYPE_SINGLE_SELECT).setLabel("Project");
		for (String project : configuration.getProjects()) {
			attribute.putOption(project, project);
		}

		return true;
	}

	@Override
	public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
			Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException {
		XmlClient client = connector.getClient(repository);
		String taskId = getTaskId(taskData, client);
		File file = client.getTask(taskId, monitor);

		writeTaskData(repository, file, taskData, monitor);

		if (taskData.isNew()) {
			return new RepositoryResponse(ResponseKind.TASK_CREATED, taskId);
		} else {
			return new RepositoryResponse(ResponseKind.TASK_UPDATED, taskId);
		}
	}

	private String getTaskId(TaskData taskData, XmlClient client) throws CoreException {
		if (taskData.isNew()) {
			try {
				return getTaskId(File.createTempFile("task", ".xml", client.getLocation()));
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, XmlCorePlugin.ID_PLUGIN, NLS.bind(
						"Failed to create task at ''{0}''", client.getLocation().getAbsolutePath()), e));
			}
		} else {
			return taskData.getTaskId();
		}
	}

	public void writeTaskData(TaskRepository repository, File file, TaskData taskData, IProgressMonitor monitor)
			throws CoreException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder loader = factory.newDocumentBuilder();
			Document document;
			if (taskData.isNew()) {
				document = loader.newDocument();
				document.appendChild(document.createElement("task"));
			} else {
				document = loader.parse(file);
			}

			updateDocument(repository, file, document, document.getDocumentElement(), taskData, monitor);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.transform(new DOMSource(document), new StreamResult(file));
		} catch (Exception e) {
			//e.printStackTrace();
			throw new CoreException(new Status(IStatus.ERROR, XmlCorePlugin.ID_PLUGIN, NLS.bind(
					"Error parsing task ''{0}''", file.getAbsolutePath()), e));
		}
	}

	public TaskData readTaskData(TaskRepository repository, File file, IProgressMonitor monitor) throws CoreException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder loader = factory.newDocumentBuilder();
			Document document = loader.parse(file);
			TaskData taskData = parseDocument(repository, file, document.getDocumentElement(), monitor);
			return taskData;
		} catch (Exception e) {
			//e.printStackTrace();
			throw new CoreException(new Status(IStatus.ERROR, XmlCorePlugin.ID_PLUGIN, NLS.bind(
					"Error parsing task {0}", file.getAbsolutePath()), e));
		}
	}

	private TaskData parseDocument(TaskRepository repository, File file, Element input, IProgressMonitor monitor)
			throws CoreException {
		String taskId = getTaskId(file);
		TaskData taskData = new TaskData(getAttributeMapper(repository), repository.getConnectorKind(),
				repository.getRepositoryUrl(), taskId);
		initializeTaskData(repository, taskData, null, monitor);

		TaskAttribute attribute = taskData.getRoot().getAttribute(TaskAttribute.SUMMARY);
		attribute.setValue(getValue(input, "summary"));

		attribute = taskData.getRoot().getAttribute(TaskAttribute.DESCRIPTION);
		attribute.setValue(getValue(input, "description"));

		attribute = taskData.getRoot().getAttribute(TaskAttribute.DATE_MODIFICATION);
		taskData.getAttributeMapper().setDateValue(attribute, new Date(file.lastModified()));

		attribute = taskData.getRoot().getAttribute(TaskAttribute.PRODUCT);
		attribute.setValue(getValue(input, "project"));

		return taskData;
	}

	private void updateDocument(TaskRepository repository, File file, Document document, Element input,
			TaskData taskData, IProgressMonitor monitor) throws CoreException {
		TaskAttribute attribute = taskData.getRoot().getAttribute(TaskAttribute.SUMMARY);
		setValue(document, input, "summary", attribute.getValue());

		attribute = taskData.getRoot().getAttribute(TaskAttribute.DESCRIPTION);
		setValue(document, input, "description", attribute.getValue());

		attribute = taskData.getRoot().getAttribute(TaskAttribute.PRODUCT);
		setValue(document, input, "project", attribute.getValue());
	}

	private String getValue(Element input, String elementName) {
		NodeList nodes = input.getElementsByTagName(elementName);
		if (nodes.getLength() > 0) {
			return nodes.item(0).getTextContent();
		}
		return "";
	}

	private void setValue(Document document, Element input, String elementName, String value) {
		NodeList nodes = input.getElementsByTagName(elementName);
		if (nodes.getLength() > 0) {
			nodes.item(0).setTextContent(value);
		} else {
			Element element = document.createElement(elementName);
			element.setTextContent(value);
			input.appendChild(element);
		}
	}

	private String getTaskId(File file) {
		Matcher matcher = XmlClient.ID_PATTERN.matcher(file.getName());
		if (matcher.find()) {
			return matcher.group(1);
		}
		return file.getName();
	}

}
