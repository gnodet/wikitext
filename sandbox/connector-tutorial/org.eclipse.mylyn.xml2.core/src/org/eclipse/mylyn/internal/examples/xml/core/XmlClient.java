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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Steffen Pingel
 */
public class XmlClient {

	public static final Pattern ID_PATTERN = Pattern.compile("task(.*)\\.xml");

	private final File location;

	private volatile XmlConfiguration configuration = new XmlConfiguration();

	public XmlClient(File location) {
		this.location = location;
	}

	public final File getLocation() {
		return location;
	}

	public final XmlConfiguration getConfiguration() {
		return configuration;
	}

	public final boolean hasConfiguration() {
		return configuration.updated != -1;
	}

	public final File[] listTasks(IProgressMonitor monitor) {
		return location.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return ID_PATTERN.matcher(file.getName()).matches();
			}
		});
	}

	public XmlConfiguration updateConfiguration(IProgressMonitor monitor) throws CoreException {
		File file = new File(location, "configuration.xml");
		try {
			// open as dom 
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder loader = factory.newDocumentBuilder();
			Document document = loader.parse(file);
			Element input = document.getDocumentElement();

			XmlConfiguration configuration = new XmlConfiguration();
			configuration.updated = file.lastModified();

			// read configuration from dom
			NodeList nodes = input.getElementsByTagName("project");
			List<String> projects = new ArrayList<String>(nodes.getLength());
			for (int i = 0; i < nodes.getLength(); i++) {
				projects.add(nodes.item(i).getTextContent());
			}
			configuration.setProjects(projects);

			// commit configuration
			this.configuration = configuration;

			return configuration;
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, XmlCorePlugin.ID_PLUGIN, NLS.bind(
					"Error reading configuration {0}", file.getAbsolutePath()), e));
		}
	}

	public File getTask(String taskId, IProgressMonitor monitor) {
		return new File(getLocation(), "task" + taskId + ".xml");
	}

}
