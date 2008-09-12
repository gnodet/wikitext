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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.mylyn.internal.tests.report.TestCaseResult.TestCaseResultType;

/**
 * @author Steffen Pingel
 */
public class JUnitReportParser {

	private static final String TAG_TEST_CASE = "testcase";

	private static final String TAG_TEST_CASE_FAILURE = "failure";

	private static final String TAG_TEST_CASE_ERROR = "error";

	private static final String ATTRIBUTE_CLASS_NAME = "classname";

	private static final String ATTRIBUTE_TEST_NAME = "name";

	private static final String ATTRIBUTE_MESSAGE = "message";

	private static final String ATTRIBUTE_TYPE = "type";

	private final TestCaseVisitor reportVisitor;

	public JUnitReportParser(TestCaseVisitor reportVisitor) {
		this.reportVisitor = reportVisitor;
	}

	private String getAttributeValue(StartElement element, String name) throws XMLStreamException {
		Attribute attribute = element.getAttributeByName(new QName(name));
		if (attribute == null) {
			throw new XMLStreamException("Missing mandatory attribute: " + name, element.getLocation());
		}
		return attribute.getValue();
	}

	private String getOptionalAttributeValue(StartElement element, String name) {
		Attribute attribute = element.getAttributeByName(new QName(name));
		if (attribute == null) {
			return null;
		}
		return attribute.getValue();
	}

	public void parse(XMLEventReader reader) throws XMLStreamException {
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				if (TAG_TEST_CASE.equals(element.getName().getLocalPart())) {
					TestCase testCase = parseTestCase(reader, element);
					if (testCase != null) {
						reportVisitor.visit(testCase);
					}
				}
			}
		}
	}

	private TestCase parseTestCase(XMLEventReader reader, StartElement testCaseElement) throws XMLStreamException {
		String className = getAttributeValue(testCaseElement, ATTRIBUTE_CLASS_NAME);
		String testName = getAttributeValue(testCaseElement, ATTRIBUTE_TEST_NAME);
		TestCaseResult result = null;
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				if (TAG_TEST_CASE_FAILURE.equals(element.getName().getLocalPart())) {
					result = parseTestCaseResult(reader, element, TestCaseResultType.FAILURE);
				} else if (TAG_TEST_CASE_ERROR.equals(element.getName().getLocalPart())) {
					result = parseTestCaseResult(reader, element, TestCaseResultType.FAILURE);
				}
			} else if (event.isEndElement()) {
				break;
			}
		}

		return new TestCase(className, testName, result);
	}

	private TestCaseResult parseTestCaseResult(XMLEventReader reader, StartElement resultElement,
			TestCaseResultType resultType) throws XMLStreamException {
		String type = getAttributeValue(resultElement, ATTRIBUTE_TYPE);
		String message = getOptionalAttributeValue(resultElement, ATTRIBUTE_MESSAGE);
		return new TestCaseResult(resultType, type, message, reader.getElementText());
	}
}
