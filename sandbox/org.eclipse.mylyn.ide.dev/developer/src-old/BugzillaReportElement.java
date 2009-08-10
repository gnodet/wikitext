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

package org.eclipse.mylyn.internal.tasklist;

/**
 * Bugzilla XML element enum. Each enum has the attribute name
 * and associated xml element tag name.
 * 
 * @author Rob Elves
 */
public class BugzillaReportElement {
	// Format: ENUM ( "pretty name", "xml key", <hidden: true/false>, <readonly: true/false>)
	// Hidden elements are not automatically displayed in ui	
	public static final BugzillaReportElement ASSIGNED_TO = new BugzillaReportElement("Assigned to:", "assigned_to", false, true);
	public static final BugzillaReportElement ATTACHID = new BugzillaReportElement("attachid", "attachid");
	public static final BugzillaReportElement ATTACHMENT = new BugzillaReportElement("attachment", "attachment");
	public static final BugzillaReportElement BLOCKED = new BugzillaReportElement("Bug blocks:", "blocked");
	public static final BugzillaReportElement BUG = new BugzillaReportElement("bug","bug", true);
	public static final BugzillaReportElement BUG_FILE_LOC = new BugzillaReportElement("URL:", "bug_file_loc", true);
	public static final BugzillaReportElement BUG_ID = new BugzillaReportElement("Bug:", "bug_id", true);
	public static final BugzillaReportElement BUG_SEVERITY = new BugzillaReportElement("Severity:", "bug_severity", false);
	public static final BugzillaReportElement BUG_STATUS = new BugzillaReportElement("Status:", "bug_status", false, true);
	public static final BugzillaReportElement BUG_WHEN = new BugzillaReportElement("bug_when", "bug_when", true, true);
	public static final BugzillaReportElement BUGZILLA = new BugzillaReportElement("bugzilla", "bugzilla", true);
	public static final BugzillaReportElement CC = new BugzillaReportElement("CC:", "cc", true, true);
	public static final BugzillaReportElement CCLIST_ACCESSIBLE = new BugzillaReportElement("cclist_accessible", "cclist_accessible", true);
	public static final BugzillaReportElement CLASSIFICATION = new BugzillaReportElement("Classification:", "classification", true);
	public static final BugzillaReportElement CLASSIFICATION_ID = new BugzillaReportElement("Classification ID:", "classification_id", true);
	public static final BugzillaReportElement COMPONENT = new BugzillaReportElement("Component:", "component", false);
	public static final BugzillaReportElement CREATION_TS = new BugzillaReportElement("Creation date:", "creation_ts", true);
	public static final BugzillaReportElement CTYPE = new BugzillaReportElement("Content Type", "ctype");
	public static final BugzillaReportElement DATA = new BugzillaReportElement("data", "data"); 
	public static final BugzillaReportElement DATE = new BugzillaReportElement("Date", "date"); 
	public static final BugzillaReportElement DELTA_TS = new BugzillaReportElement("Last Modification", "delta_ts", true); 
	public static final BugzillaReportElement DEPENDSON = new BugzillaReportElement("Bug depends on:", "dependson"); 
	public static final BugzillaReportElement DESC = new BugzillaReportElement("desc", "desc"); 
	public static final BugzillaReportElement EVERCONFIRMED = new BugzillaReportElement("everconfirmed", "everconfirmed", true);
	public static final BugzillaReportElement FILENAME = new BugzillaReportElement("filename", "filename");
	public static final BugzillaReportElement IS_OBSOLETE = new BugzillaReportElement("Obsolete", "isobsolete", true); 
	public static final BugzillaReportElement KEYWORDS = new BugzillaReportElement("Keywords:", "keywords", true);
	public static final BugzillaReportElement LONG_DESC = new BugzillaReportElement("Description:", "long_desc"); 
	public static final BugzillaReportElement LONGDESCLENGTH = new BugzillaReportElement("Number of comments", "longdesclength", true); 
	public static final BugzillaReportElement NEWCC = new BugzillaReportElement("Add CC:", "newcc", true); 
	public static final BugzillaReportElement OP_SYS = new BugzillaReportElement("OS:", "op_sys", false); 
	public static final BugzillaReportElement PRIORITY = new BugzillaReportElement("Priority:", "priority", false); 
	public static final BugzillaReportElement PRODUCT = new BugzillaReportElement("Product:", "product", false); 
	public static final BugzillaReportElement REP_PLATFORM = new BugzillaReportElement("Platform:", "rep_platform", false);
	public static final BugzillaReportElement REPORTER = new BugzillaReportElement("Reporter:", "reporter", false, true);
	public static final BugzillaReportElement REPORTER_ACCESSIBLE = new BugzillaReportElement("reporter_accessible", "reporter_accessible", true);
	public static final BugzillaReportElement RESOLUTION = new BugzillaReportElement("Resolution:", "resolution", false, true); // Exiting bug field, new cc
	public static final BugzillaReportElement SHORT_DESC = new BugzillaReportElement("Summary:", "short_desc", true);
	public static final BugzillaReportElement TARGET_MILESTONE = new BugzillaReportElement("Target milestone:", "target_milestone", false);
	public static final BugzillaReportElement THETEXT = new BugzillaReportElement("thetext", "thetext");
	public static final BugzillaReportElement TYPE = new BugzillaReportElement("type", "type");
	public static final BugzillaReportElement UNKNOWN = new BugzillaReportElement("UNKNOWN", "UNKNOWN");
	public static final BugzillaReportElement VERSION = new BugzillaReportElement("Version:", "version", false);
	public static final BugzillaReportElement VOTES = new BugzillaReportElement("Votes:", "votes", false, true);
	public static final BugzillaReportElement WHO = new BugzillaReportElement("who", "who");
	public static final BugzillaReportElement QA_CONTACT= new BugzillaReportElement("QA Contact", "qa_contact", false, false);
	public static final BugzillaReportElement ADDSELFCC = new BugzillaReportElement("Add self to CC", "addselfcc", true, false);
	// Used by search engine
	public static final BugzillaReportElement LI = new BugzillaReportElement("used by search engine", "li", true);
	public static final BugzillaReportElement ID = new BugzillaReportElement("used by search engine", "id", true);
	public static final BugzillaReportElement SHORT_SHORT_DESC = new BugzillaReportElement("used by search engine", "short_short_desc", false);
	public static final BugzillaReportElement SEQ = new BugzillaReportElement("used by search engine", "seq", false);	
	public static final BugzillaReportElement RESULT = new BugzillaReportElement("used by search engine", "result", false);
	public static final BugzillaReportElement RDF = new BugzillaReportElement("used by search engine", "rdf", false);
	public static final BugzillaReportElement INSTALLATION = new BugzillaReportElement("used by search engine", "installation", false);
	public static final BugzillaReportElement BUGS = new BugzillaReportElement("used by search engine", "bugs", false);
	
	private final boolean isHidden;
	
	private final boolean isReadOnly;
	
	private final String keyString;
	
	private final String prettyName;

	BugzillaReportElement(String prettyName, String fieldName) {		
		this(prettyName, fieldName, false, false);
	}
	
	BugzillaReportElement(String prettyName, String fieldName, boolean hidden) {		
		this(prettyName, fieldName, hidden, false);
	}
	
	BugzillaReportElement(String prettyName, String fieldName, boolean hidden, boolean readonly) {		
		this.prettyName = prettyName;
		this.keyString = fieldName;
		this.isHidden = hidden;
		this.isReadOnly = readonly;
	}

	public String getKeyString() {
		return keyString;
	}

	public boolean isHidden() {
		return isHidden;
	}	
	
	public boolean isReadOnly() {
		return isReadOnly;
	}
	
	public String toString() {
		return prettyName;
	}
}
