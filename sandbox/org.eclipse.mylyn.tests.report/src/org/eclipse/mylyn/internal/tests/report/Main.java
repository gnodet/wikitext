package org.eclipse.mylyn.internal.tests.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.web.core.AuthenticationCredentials;
import org.eclipse.mylyn.web.core.AuthenticationType;

public class Main {

	public static void main(String[] args) {
		Build build = null;
		String filename = null;
		String propertiesFilename = null;

		for (int i = 0; i < args.length; i++) {
			if ("-build".equals(args[i])) {
				build = new Build(readArg(args, ++i));
			} else if ("-in".equals(args[i])) {
				filename = readArg(args, ++i);
			}
			if ("-config".equals(args[i])) {
				propertiesFilename = readArg(args, ++i);
			}
		}

		if (build == null || filename == null || propertiesFilename == null) {
			printUsage();
			System.exit(1);
		}

		try {
			TaskRepository repository = readConfig(new File(propertiesFilename));
			process(repository, new File(filename), build);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printUsage() {
		System.err.println("Main -in [junit report file] -config [config file] -build [id]");

	}

	private static void process(TaskRepository repository, File file, Build build) throws Exception {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		InputStream in = new FileInputStream(file);
		try {
			XMLEventReader reader = inputFactory.createXMLEventReader(in);
			TaskReporter reporter = new TaskReporter(build, repository);
			JUnitReportParser parser = new JUnitReportParser(reporter);
			parser.parse(reader);
			System.out.println(reporter.getStatistics());
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

			TaskRepository repository = new TaskRepository(TracCorePlugin.REPOSITORY_KIND, url);
			repository.setCredentials(AuthenticationType.REPOSITORY, credentials, false);
			repository.setVersion(Version.XML_RPC.name());
			return repository;
		} finally {
			in.close();
		}
	}

}
