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

package org.eclipse.mylar.internal.bugzilla.core.internal;

import org.eclipse.mylar.bugzilla.core.AbstractRepositoryReportAttribute;
import org.eclipse.mylar.bugzilla.core.BugzillaReport;
import org.eclipse.mylar.bugzilla.core.BugzillaReportAttribute;
import org.eclipse.mylar.bugzilla.core.Comment;
import org.eclipse.mylar.bugzilla.core.ReportAttachment;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parser for xml bugzilla reports.
 * 
 * @author Rob Elves
 */
public class SaxBugReportContentHandler extends DefaultHandler {

	private StringBuffer characters;

	private Comment comment;

	private int commentNum = 0;

	private ReportAttachment attachment;

	private BugzillaReport report;

	private String errorMessage = null;

	public SaxBugReportContentHandler(BugzillaReport rpt) {
		this.report = rpt;
	}

	public boolean errorOccurred() {
		return errorMessage != null;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public BugzillaReport getReport() {
		return report;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		characters.append(ch, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		characters = new StringBuffer();
		BugzillaReportElement tag = BugzillaReportElement.UNKNOWN;
		try {
			tag = BugzillaReportElement.valueOf(localName.trim().toUpperCase());
		} catch (RuntimeException e) {
			if (e instanceof IllegalArgumentException) {
				// ignore unrecognized tags
				return;
			}
			throw e;
		}
		switch (tag) {
		case BUGZILLA:
			// Note: here we can get the bugzilla version if necessary
			break;
		case BUG:
			if (attributes != null && (attributes.getValue("error") != null)) {
				errorMessage = attributes.getValue("error");
			}
			break;
		case LONG_DESC:
			comment = new Comment(report, commentNum++);
			break;
		case ATTACHMENT:
			attachment = new ReportAttachment();
			if (attributes != null && (attributes.getValue(BugzillaReportElement.IS_OBSOLETE.getKeyString()) != null)) {
				attachment.addAttribute(BugzillaReportElement.IS_OBSOLETE, new BugzillaReportAttribute(
						BugzillaReportElement.IS_OBSOLETE));
			}
			break;
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		BugzillaReportElement tag = BugzillaReportElement.UNKNOWN;
		try {
			tag = BugzillaReportElement.valueOf(localName.trim().toUpperCase());
		} catch (RuntimeException e) {
			if (e instanceof IllegalArgumentException) {
				// ignore unrecognized tags
				return;
			}
			throw e;
		}
		switch (tag) {
		case BUG_ID: {
			try {
				if (report.getId() != Integer.parseInt(characters.toString())) {
					errorMessage = "Requested report number does not match returned report number.";
				}
			} catch (NumberFormatException e) {
				errorMessage = "Bug id from server did not match requested id.";
			}

			AbstractRepositoryReportAttribute attr = report.getAttribute(tag);
			if (attr == null) {
				attr = new BugzillaReportAttribute(tag);
				report.addAttribute(tag, attr);
			}
			attr.setValue(characters.toString());
			break;
		}

			// Comment attributes
		case WHO:
		case BUG_WHEN:
		case THETEXT:
			if (comment != null) {
				BugzillaReportAttribute attr = new BugzillaReportAttribute(tag);
				attr.setValue(characters.toString());
				// System.err.println(">>> "+comment.getNumber()+"
				// "+characters.toString());
				comment.addAttribute(tag, attr);
			}
			break;
		case LONG_DESC:
			if (comment != null) {
				report.addComment(comment);
			}
			break;

		// Attachment attributes
		case ATTACHID:
		case DATE:
		case DESC:
		case FILENAME:
		case CTYPE:
		case TYPE:
			if (attachment != null) {
				BugzillaReportAttribute attr = new BugzillaReportAttribute(tag);
				attr.setValue(characters.toString());
				attachment.addAttribute(tag, attr);
			}
			break;
		case DATA:
			// TODO: Need to figure out under what circumstanceswhen attachments
			// are inline and
			// what to do with them.
			break;
		case ATTACHMENT:
			if (attachment != null) {
				report.addAttachment(attachment);
			}
			break;

		// IGNORED ELEMENTS
		case REPORTER_ACCESSIBLE:
		case CLASSIFICATION_ID:
		case CLASSIFICATION:
		case CCLIST_ACCESSIBLE:
		case EVERCONFIRMED:
		case BUGZILLA:
			break;
		case BUG:
			// Reached end of bug. Need to set LONGDESCLENGTH to number of
			// comments
			AbstractRepositoryReportAttribute numCommentsAttribute = report
					.getAttribute(BugzillaReportElement.LONGDESCLENGTH);
			if (numCommentsAttribute == null) {
				numCommentsAttribute = new BugzillaReportAttribute(BugzillaReportElement.LONGDESCLENGTH);
				numCommentsAttribute.setValue("" + report.getComments().size());
				report.addAttribute(BugzillaReportElement.LONGDESCLENGTH, numCommentsAttribute);
			} else {
				numCommentsAttribute.setValue("" + report.getComments().size());
			}
			break;

		// All others added as report attribute
		default:
			AbstractRepositoryReportAttribute attribute = report.getAttribute(tag);
			if (attribute == null) {
				// System.err.println(">>> Undeclared attribute added: " +
				// tag.toString()+" value: "+characters.toString());
				attribute = new BugzillaReportAttribute(tag);
				attribute.setValue(characters.toString());
				report.addAttribute(tag, attribute);
			} else {
				// System.err.println("Attr: " + attribute.getName() + " = " +
				// characters.toString());
				attribute.addValue(characters.toString());
			}
			break;
		}

	}

}
