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

package org.eclipse.mylar.internal.bugzilla.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.GeneralSecurityException;

import javax.security.auth.login.LoginException;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Rob Elves
 */
public class AbstractReportFactory {

//	private static final String CONTENT_TYPE_TEXT_HTML = "text/html";
//
//	private static final String CONTENT_TYPE_APP_RDF_XML = "application/rdf+xml";
//
//	private static final String CONTENT_TYPE_APP_XML = "application/xml";
//
//	private static final String CONTENT_TYPE_APP_XCGI = "application/x-cgi";
//
//	private static final String CONTENT_TYPE_TEXT_XML = "text/xml";
//
//	private static final String[] VALID_CONFIG_CONTENT_TYPES = { CONTENT_TYPE_APP_RDF_XML, CONTENT_TYPE_APP_XML,
//			CONTENT_TYPE_TEXT_XML };
//
//	private static final List<String> VALID_TYPES = Arrays.asList(VALID_CONFIG_CONTENT_TYPES);

	public static final int RETURN_ALL_HITS = -1;

	private InputStream inStream;

	private String characterEncoding;

	public AbstractReportFactory(InputStream inStream, String encoding) {
		this.inStream = inStream;
		this.characterEncoding = encoding;
	}

	/**
	 * expects rdf returned from repository (ctype=rdf in url)
	 * 
	 * @throws GeneralSecurityException
	 */
	protected void collectResults(DefaultHandler contentHandler, boolean clean) throws IOException, BugzillaException,
			GeneralSecurityException {

		// HttpURLConnection connection = null;
		// try {
		// connection = WebClientUtil.openUrlConnection(url, proxySettings,
		// false, null, null);
		//			
		// int responseCode = connection.getResponseCode();
		//			
		// if (responseCode != HttpURLConnection.HTTP_OK) {
		// String msg;
		// if (responseCode == -1 || responseCode ==
		// HttpURLConnection.HTTP_FORBIDDEN)
		// msg = "Repository does not seem to be a valid Bugzilla server: " +
		// url.toExternalForm();
		// else
		// msg = "HTTP Error " + responseCode + " (" +
		// connection.getResponseMessage()
		// + ") while querying Bugzilla server: " + url.toExternalForm();
		//
		// throw new IOException(msg);
		// }
		//
		// BufferedReader in = null;
		//
		// String contentEncoding = connection.getContentEncoding();
		// boolean gzipped = contentEncoding != null &&
		// WebClientUtil.ENCODING_GZIP.equals(contentEncoding);
		// if (characterEncoding != null) {
		// if (gzipped) {
		// in = new BufferedReader(new InputStreamReader(new
		// GZIPInputStream(connection.getInputStream()),
		// characterEncoding));
		// } else {
		// in = new BufferedReader(new
		// InputStreamReader(connection.getInputStream(), characterEncoding));
		// }
		// } else {
		// if (gzipped) {
		// in = new BufferedReader(new InputStreamReader(new
		// GZIPInputStream(connection.getInputStream())));
		// } else {
		// in = new BufferedReader(new
		// InputStreamReader(connection.getInputStream()));
		// }
		// }

		BufferedReader in;
		if (characterEncoding != null) {
			in = new BufferedReader(new InputStreamReader(inStream, characterEncoding));
		} else {
			in = new BufferedReader(new InputStreamReader(inStream));
		}
		// String line = in.readLine();
		// while(line != null) {
		// System.err.println(line);
		// line = in.readLine();
		// }
		if (in != null && clean) {
			StringBuffer result = XmlCleaner.clean(in);
			StringReader strReader = new StringReader(result.toString());
			in = new BufferedReader(strReader);
		}

		// if (VALID_TYPES.contains(connection.getContentType().toLowerCase()))
		// {

		try {
			final XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(contentHandler);
			
			EntityResolver resolver = new EntityResolver() {

				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					// The default resolver will try to resolve the dtd via URLConnection. We would need to implement
					// via httpclient to handle authorization properly. Since we don't have need of entity resolving
					// currently, we just supply a dummy (empty) resource for each request...					
					InputSource source = new InputSource();
					source.setCharacterStream(new StringReader(""));
					return source;
				}};
			
			reader.setEntityResolver(resolver);
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
		} catch (SAXException e) {
			if (e.getMessage().equals(IBugzillaConstants.ERROR_INVALID_USERNAME_OR_PASSWORD)) {
				throw new LoginException(e.getMessage());
			} else {
				throw new IOException(e.getMessage());
			}
		}
		// } else if
		// (connection.getContentType().contains(CONTENT_TYPE_TEXT_HTML)) {
		// BugzillaClient.parseHtmlError(in);
		// } else if
		// (connection.getContentType().toLowerCase().contains(CONTENT_TYPE_APP_XCGI))
		// {
		// // ignore
		// } else {
		// throw new IOException("Unrecognized content type: " +
		// connection.getContentType());
		// }
		// } finally {
		// if (connection != null) {
		// connection.disconnect();
		// }
		// }
	}
}
