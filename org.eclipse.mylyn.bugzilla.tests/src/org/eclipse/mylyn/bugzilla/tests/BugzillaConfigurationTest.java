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

package org.eclipse.mylar.bugzilla.tests;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylar.internal.bugzilla.core.RepositoryConfigurationFactory;
import org.eclipse.mylar.internal.bugzilla.core.SaxConfigurationContentHandler;
import org.eclipse.mylar.internal.bugzilla.core.XmlCleaner;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class BugzillaConfigurationTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test222RDFProductConfig() throws Exception {
		RepositoryConfigurationFactory factory = new RepositoryConfigurationFactory();
		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_222_URL);
		RepositoryConfiguration config = factory.getConfiguration(repository.getUrl(), null, repository.getUserName(),
				repository.getPassword(), null);
		assertNotNull(config);
		assertEquals("2.22", config.getInstallVersion());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(8, config.getResolutions().size());
		assertEquals(4, config.getPlatforms().size());
		assertEquals(5, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertEquals(3, config.getProducts().size());
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(1, config.getComponents("TestProduct").size());
		assertEquals(1, config.getVersions("TestProduct").size());
		assertEquals(0, config.getTargetMilestones("TestProduct").size());
	}

	public void test2201RDFProductConfig() throws Exception {
		RepositoryConfigurationFactory factory = new RepositoryConfigurationFactory();
		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_2201_URL);
		RepositoryConfiguration config = factory.getConfiguration(repository.getUrl(), null, repository.getUserName(),
				repository.getPassword(), null);
		assertNotNull(config);
		assertEquals("2.20.1", config.getInstallVersion());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(8, config.getResolutions().size());
		assertEquals(4, config.getPlatforms().size());
		assertEquals(5, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertEquals(1, config.getProducts().size());
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(2, config.getComponents("TestProduct").size());
		assertEquals(1, config.getVersions("TestProduct").size());
		// assertEquals(1, config.getTargetMilestones("TestProduct").size());
	}

	public void test220RDFProductConfig() throws Exception {
		RepositoryConfigurationFactory factory = new RepositoryConfigurationFactory();
		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_220_URL);
		RepositoryConfiguration config = factory.getConfiguration(repository.getUrl(), null, repository.getUserName(),
				repository.getPassword(), null);
		assertNotNull(config);
		assertEquals("2.20", config.getInstallVersion());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(8, config.getResolutions().size());
		assertEquals(4, config.getPlatforms().size());
		assertEquals(5, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertEquals(2, config.getProducts().size());
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(2, config.getComponents("TestProduct").size());
		assertEquals(1, config.getVersions("TestProduct").size());
		// assertEquals(1, config.getTargetMilestones("TestProduct").size());
	}

	public void test218RDFProductConfig() throws Exception {
		RepositoryConfigurationFactory factory = new RepositoryConfigurationFactory();
		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND,
				IBugzillaConstants.TEST_BUGZILLA_218_URL);
		RepositoryConfiguration config = factory.getConfiguration(repository.getUrl(), null, repository.getUserName(),
				repository.getPassword(), null);
		assertNotNull(config);
		assertEquals("2.18.5", config.getInstallVersion());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(8, config.getResolutions().size());
		assertEquals(8, config.getPlatforms().size());
		assertEquals(36, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertEquals(1, config.getProducts().size());
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(1, config.getComponents("TestProduct").size());
		assertEquals(1, config.getVersions("TestProduct").size());
		// assertEquals(1, config.getTargetMilestones("TestProduct").size());
	}

	public void testEclipseRDFProductConfig() throws Exception {
		RepositoryConfigurationFactory factory = new RepositoryConfigurationFactory();
		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.REPOSITORY_KIND,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL);
		RepositoryConfiguration config = factory.getConfiguration(repository.getUrl(), null, repository.getUserName(),
				repository.getPassword(), null);
		assertNotNull(config);
		assertEquals("2.20.1", config.getInstallVersion());
		assertEquals(7, config.getStatusValues().size());
		assertEquals(8, config.getResolutions().size());
		assertEquals(6, config.getPlatforms().size());
		assertEquals(28, config.getOSs().size());
		assertEquals(5, config.getPriorities().size());
		assertEquals(7, config.getSeverities().size());
		assertTrue(config.getProducts().size() > 50);
		assertEquals(4, config.getOpenStatusValues().size());
		assertEquals(11, config.getComponents("Mylar").size());
		assertEquals(22, config.getKeywords().size());
		// assertEquals(10, config.getComponents("Hyades").size());
		// assertEquals(1, config.getTargetMilestones("TestProduct").size());
	}

	public void testRepositoryConfigurationCachePersistance() throws Exception {
		RepositoryConfiguration configuration1 = new RepositoryConfiguration();
		configuration1.setRepositoryUrl("url1");
		configuration1.addProduct("Test Product 1");
		assertEquals(1, configuration1.getProducts().size());

		RepositoryConfiguration configuration2 = new RepositoryConfiguration();
		configuration1.setRepositoryUrl("url2");
		configuration2.addProduct("Test Product 2");
		assertEquals(1, configuration2.getProducts().size());

		BugzillaCorePlugin.addRepositoryConfiguration(configuration1);
		BugzillaCorePlugin.addRepositoryConfiguration(configuration2);
		BugzillaCorePlugin.writeRepositoryConfigFile();
		BugzillaCorePlugin.getDefault().removeConfiguration(configuration1);
		BugzillaCorePlugin.getDefault().removeConfiguration(configuration2);
		assertNull(BugzillaCorePlugin.getRepositoryConfiguration(configuration1.getRepositoryUrl()));
		assertNull(BugzillaCorePlugin.getRepositoryConfiguration(configuration2.getRepositoryUrl()));
		BugzillaCorePlugin.readRepositoryConfigurationFile();
		assertNotNull(BugzillaCorePlugin.getRepositoryConfiguration(configuration1.getRepositoryUrl()));
		assertNotNull(BugzillaCorePlugin.getRepositoryConfiguration(configuration2.getRepositoryUrl()));
		RepositoryConfiguration testLoadedConfig = BugzillaCorePlugin.getRepositoryConfiguration(configuration1
				.getRepositoryUrl());
		assertEquals(1, testLoadedConfig.getProducts().size());
		assertEquals(configuration1.getProducts().get(0), testLoadedConfig.getProducts().get(0));
	}

	/**
	 * Can use this to test config data submitted by users. Be sure not to commit user's config file though.
	 * The file included (rdfconfig218.txt) is from mylar.eclipse.org/bugs218
	 */
	public void testRepositoryConfigurationFromFile() throws Exception {
		
		URL entryURL = BugzillaTestPlugin.getDefault().getBundle().getEntry("testdata/configuration/rdfconfig218.txt");
		assertNotNull(entryURL);
		URL fileURL = FileLocator.toFileURL(entryURL);
		assertNotNull(fileURL);		

		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileURL.getFile())));

		if (true) {
			StringBuffer result = XmlCleaner.clean(in);			
			StringReader strReader = new StringReader(result.toString());
			in = new BufferedReader(strReader);
		}

		SaxConfigurationContentHandler contentHandler = new SaxConfigurationContentHandler();
		final XMLReader reader = XMLReaderFactory.createXMLReader();
		reader.setContentHandler(contentHandler);
		reader.setErrorHandler(new ErrorHandler() {

			public void error(SAXParseException exception) throws SAXException {
				throw exception;
			}

			public void fatalError(SAXParseException exception) throws SAXException {
				throw exception;
			}

			public void warning(SAXParseException exception) throws SAXException {
				throw exception;
			}
		});
		reader.parse(new InputSource(in));
		
		RepositoryConfiguration config = contentHandler.getConfiguration();
		assertNotNull(config);
		
		// Add your additional checking for valid data here if necessary
	}

}
