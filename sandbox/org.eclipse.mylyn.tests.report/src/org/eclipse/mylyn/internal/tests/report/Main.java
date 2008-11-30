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

package org.eclipse.mylyn.internal.tests.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient.Version;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class Main {

	public static void main(String[] args) {
		List<String> filenames = new ArrayList<String>();
		Build build = null;
		String propertiesFilename = null;
		String tag = null;
		boolean clear = false;
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				if ("-build".equals(args[i])) {
					build = new Build(readArg(args, ++i));
				} else if ("-config".equals(args[i])) {
					propertiesFilename = readArg(args, ++i);
				} else if ("-tag".equals(args[i])) {
					tag = readArg(args, ++i);
				} else if ("-clear".equals(args[i])) {
					clear = "YES".equals(readArg(args, ++i));
				}

			} else {
				StringTokenizer t = new StringTokenizer(args[i], File.pathSeparator);
				while (t.hasMoreTokens()) {
					filenames.add(t.nextToken());
				}
			}
		}

		if (build == null || filenames.isEmpty() || propertiesFilename == null) {
			printUsage();
			System.exit(1);
		}

		try {
			TaskRepository repository = readConfig(new File(propertiesFilename));
			TaskReporter reporter = new TaskReporter(build, repository, tag);
			if (clear) {
				reporter.clearAll();
			}
			reporter.initialize();
			for (String filename : filenames) {
				process(repository, new File(filename), reporter);
			}
			reporter.done();
			System.out.println(reporter.getStatistics());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printUsage() {
		System.err.println("Main -config [config file] -build [id] [junit report file]...");

	}

	private static void process(TaskRepository repository, File file, TaskReporter reporter) throws Exception {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream in = new FileInputStream(file);
		try {
			XMLEventReader reader = inputFactory.createXMLEventReader(in);
			JUnitReportParser parser = new JUnitReportParser(reporter);
			parser.parse(reader);
		} finally {
			in.close();
		}
	}

	private static String readArg(String[] args, int i) {
		if (i > args.length) {
			printUsage();
			System.exit(1);
		}
		return args[i];
	}

	private static TaskRepository readConfig(File file) throws Exception {
		Properties properties = new Properties();
		InputStream in = new FileInputStream(file);
		try {
			properties.load(in);
			String url = properties.getProperty("url", null);

			if (url == null) {
				throw new Exception("propertiy 'url' not defined in config file");
			}

			String userName = properties.getProperty("username", "");
			String password = properties.getProperty("password", "");
			AuthenticationCredentials credentials = new AuthenticationCredentials(userName, password);

			TaskRepository repository = new TaskRepository(TracCorePlugin.CONNECTOR_KIND, url);
			repository.setCredentials(AuthenticationType.REPOSITORY, credentials, false);
			repository.setVersion(Version.XML_RPC.name());
			return repository;
		} finally {
			in.close();
		}
	}

}
