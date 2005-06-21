/*******************************************************************************
 * Copyright (c) 2003 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.bugzilla.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.bugzilla.BugzillaPlugin;
import org.eclipse.mylar.bugzilla.BugzillaPreferences;
import org.eclipse.mylar.bugzilla.IBugzillaConstants;
import org.eclipse.mylar.bugzilla.TrustAll;
import org.eclipse.mylar.bugzilla.core.BugzillaException;
import org.eclipse.search.ui.NewSearchUI;


import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;

/**
 * Queries the Bugzilla server for the list of bugs matching search criteria.
 */
public class BugzillaSearchEngine {

	protected static final String QUERYING_SERVER = "Querying Bugzilla Server...";

	/** regular expression matching Bugzilla query results format used in Eclipse.org Bugzilla */
	protected static final RegularExpression re = new RegularExpression("<a href=\"show_bug.cgi\\?id=(\\d+)\">", "i");
	
	/** regular expression matching values of query matches' attributes in Eclipse.org Bugzilla */
	protected static final RegularExpression reValue = new RegularExpression("<td><nobr>([^<]*)</nobr>");
	
	/** regular expression matching Bugzilla query results format used in v2.12 */
	protected static final RegularExpression reOld = new RegularExpression("<a href=\"show_bug.cgi\\?id=(\\d+)\">\\d+</a>\\s*<td class=severity><nobr>([^>]+)</nobr><td class=priority><nobr>([^>]+)</nobr><td class=platform><nobr>([^>]*)</nobr><td class=owner><nobr>([^>]*)</nobr><td class=status><nobr>([^>]*)</nobr><td class=resolution><nobr>([^>]*)</nobr><td class=summary>(.*)$", "i");
	
	private String urlString;
	
	public BugzillaSearchEngine(String url) {
		this.urlString = url;

		// use the username and password if we have it to log into bugzilla
		if(BugzillaPreferences.getUserName() != null && !BugzillaPreferences.getUserName().equals("") && BugzillaPreferences.getPassword() != null && !BugzillaPreferences.getPassword().equals(""))
		{
			try {
				url += "&GoAheadAndLogIn=1&Bugzilla_login=" + URLEncoder.encode(BugzillaPreferences.getUserName(), "UTF-8") + "&Bugzilla_password=" + URLEncoder.encode(BugzillaPreferences.getPassword(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				/*
				 * Do nothing. Every implementation of the Java platform is required
				 * to support the standard charset "UTF-8"
				 */
			}
		}
	}
	
	/**
	 * Wrapper for search
	 * @param collector - The collector for the results to go into
 	 */
	public IStatus search(IBugzillaSearchResultCollector collector) throws LoginException 
	{
		return this.search(collector, 0);
	}
	
	/**
	 * Executes the query, parses the response, and adds hits to the search result collector.
	 * 
	 * <p>
	 * The output for a single match looks like this:
	 * <pre>
	 *  <tr class="bz_enhancement bz_P5 ">
	 *
	 *    <td>
	 *      <a href="show_bug.cgi?id=6747">6747</a>
	 *    </td>
	 *
	 *    <td><nobr>enh</nobr>
	 *    </td>
	 *    <td><nobr>P5</nobr>
	 *    </td>
	 *    <td><nobr>All</nobr>
	 *    </td>
	 *    <td><nobr>Olivier_Thomann@oti.com</nobr>
	 *    </td>
	 *    <td><nobr>ASSI</nobr>
	 *    </td>
	 *    <td><nobr></nobr>
	 *    </td>
	 *    <td>Code Formatter exchange several blank lines  w/ one
	 *    </td>
	 *
	 *  </tr>
	 * <pre>
	 * 
	 * <p>Or in the older format:
	 * <pre>
	 * <A HREF="show_bug.cgi?id=8">8</A> <td class=severity><nobr>blo</nobr><td class=priority><nobr>P1</nobr><td class=platform><nobr>PC</nobr><td class=owner><nobr>cubranic@cs.ubc.ca</nobr><td class=status><nobr>CLOS</nobr><td class=resolution><nobr>DUPL</nobr><td class=summary>"Document root" missing when querying on files and revisions
	 * </pre>
	 * @param collector - The collector for the search results
	 * @param startMatches - The number of matches to start with for the progress monitor
	 */
	public IStatus search(IBugzillaSearchResultCollector collector, int startMatches) throws LoginException {
		IProgressMonitor monitor = collector.getProgressMonitor();

		IStatus status = null; 
		
		boolean possibleBadLogin = false;
		int numCollected = 0;
		
		BufferedReader in = null;
		
		try {
			monitor.beginTask(QUERYING_SERVER, IProgressMonitor.UNKNOWN);
			collector.aboutToStart(startMatches);

			SSLContext ctx = SSLContext.getInstance("TLS");
			
			javax.net.ssl.TrustManager[] tm = new javax.net.ssl.TrustManager[]{new TrustAll()};
			ctx.init(null, tm, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());	
		
			URL url = new URL(this.urlString);

			HttpURLConnection connect =  (HttpURLConnection) url.openConnection();
			connect.connect();

			int responseCode = connect.getResponseCode();
									
			if(responseCode != HttpURLConnection.HTTP_OK)
			{
				String msg;
				if(responseCode == -1 || responseCode == HttpURLConnection.HTTP_FORBIDDEN)
					msg = BugzillaPlugin.getDefault().getServerName() + " does not seem to be a valid Bugzilla server.  Check Bugzilla preferences.";
				else
					msg = "HTTP Error " + responseCode + " (" + connect.getResponseMessage() + ") while querying Bugzilla Server.  Check Bugzilla preferences.";

				throw new BugzillaException(msg);
			}
			
			in = new BufferedReader(new InputStreamReader(url.openStream()));

			Match match = new Match();
			String line;
			while ((line = in.readLine()) != null) {
				// create regular expressions that can be mathced to check if we have 
				// bad login information
				RegularExpression loginRe = new RegularExpression("<title>.*login.*</title>.*");
				RegularExpression invalidRe = new RegularExpression(".*<title>.*invalid.*password.*</title>.*");
				RegularExpression passwordRe = new RegularExpression(".*<title>.*password.*invalid.*</title>.*");
				RegularExpression emailRe = new RegularExpression(".*<title>.*check e-mail.*</title>.*");
				RegularExpression errorRe = new RegularExpression(".*<title>.*error.*</title>.*");
				
				String lowerLine = line.toLowerCase();
				
				// check if we have anything that suggests bad login info
				if(loginRe.matches(lowerLine) || invalidRe.matches(lowerLine) || passwordRe.matches(lowerLine) || emailRe.matches(lowerLine) || errorRe.matches(lowerLine))
					possibleBadLogin = true;
				
				if (reOld.matches(line, match)) {
					int id = Integer.parseInt(match.getCapturedText(1));
					String severity = match.getCapturedText(2);
					String priority = match.getCapturedText(3);
					String platform = match.getCapturedText(4);
					String owner = match.getCapturedText(5);
					String state = match.getCapturedText(6);
					String result = match.getCapturedText(7);
					String description = match.getCapturedText(8);
					String query = BugzillaPlugin.getMostRecentQuery();
					if (query == null)
						query = "";
					
					String server = BugzillaPlugin.getDefault().getServerName();
					
					BugzillaSearchHit hit = new BugzillaSearchHit(id, description, severity, priority, platform, state, result, owner, query, server);
					collector.accept(hit);
					numCollected++;
				}
				else if (re.matches(line, match)) {
					int id = Integer.parseInt(match.getCapturedText(1));
					String severity = null;
					String priority = null;
					String platform = null;
					String owner = null;
					String state = null;
					String result = null;
					for (int i = 0; i < 6; i++) {
						do {
							line = in.readLine().trim();
							if (line == null) break;
							line = line.trim();
						} while (!reValue.matches(line, match));
						switch (i) {
							case 0: 
								severity = match.getCapturedText(1);
								break;
							case 1:
								priority = match.getCapturedText(1);
								break;
							case 2:
								platform = match.getCapturedText(1);
								break;
							case 3:
								owner = match.getCapturedText(1);
								break;
							case 4:
								state = match.getCapturedText(1);
								break;
							case 5:
								result = match.getCapturedText(1);
								break;
						}
					}
					
					// two more
					line = in.readLine();
					line = in.readLine();
					
					String description = line.substring(8);
					String query = BugzillaPlugin.getMostRecentQuery();
					if (query == null)
						query = "";
					
					String server = BugzillaPlugin.getDefault().getServerName();
					
					BugzillaSearchHit hit = new BugzillaSearchHit(id, description, severity, priority, platform, state, result, owner, query, server);
					collector.accept(hit);
					numCollected++;
				}
				if (monitor.isCanceled()) {
					throw new OperationCanceledException("Search cancelled");
				}
			}
		}catch (CoreException e) {
			status = new MultiStatus( IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, "Core Exception occurred while querying Bugzilla Server " + BugzillaPlugin.getDefault().getServerName() + ".\n"
									  + "\nClick Details for more information.", e);	
			((MultiStatus)status).add(e.getStatus());
			
			// write error to log
			BugzillaPlugin.log(status);
		} 
		catch (OperationCanceledException e) {
		    status = new Status(IStatus.CANCEL, IBugzillaConstants.PLUGIN_ID, 
		            IStatus.CANCEL, "", null);
		}catch (Exception e) {
			status = new MultiStatus( IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getClass().toString() + " occurred while querying Bugzilla Server " + BugzillaPlugin.getDefault().getServerName() + ".\n"
									  + "\nClick Details or see log for more information.", e);

			IStatus s = new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getClass().toString() + ":  ", e);
			((MultiStatus)status).add(s);
			s = new Status (IStatus.ERROR, IBugzillaConstants.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e);
			((MultiStatus)status).add(s);
									
			// write error to log 
			BugzillaPlugin.log(status);

		} finally {
			monitor.done();
			collector.done();
			try{
				if(in != null)
					in.close();
			}catch(IOException e)
			{
				BugzillaPlugin.log(new Status(IStatus.ERROR, IBugzillaConstants.PLUGIN_ID,IStatus.ERROR,"Problem closing the stream", e));
			}
		}
		
		// if we haven't collected any serach results and we suspect a bad login, we assume it was a bad login
		if(numCollected == 0 && possibleBadLogin)
			throw new LoginException("Bugzilla login information incorrect");

		if(status == null)
			return new Status(IStatus.OK, NewSearchUI.PLUGIN_ID, IStatus.OK, "", null);
		else
			return status;
	}
}
