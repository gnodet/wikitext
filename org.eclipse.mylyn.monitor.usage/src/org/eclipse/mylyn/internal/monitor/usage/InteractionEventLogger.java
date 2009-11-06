/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Ken Sueda - XML serialization
 *******************************************************************************/

package org.eclipse.mylyn.internal.monitor.usage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.net.HtmlStreamTokenizer;
import org.eclipse.mylyn.commons.net.HtmlStreamTokenizer.Token;
import org.eclipse.mylyn.internal.context.core.InteractionContextExternalizer;
import org.eclipse.mylyn.monitor.core.AbstractMonitorLog;
import org.eclipse.mylyn.monitor.core.IInteractionEventListener;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.mylyn.monitor.core.InteractionEvent.Kind;

/**
 * @author Mik Kersten TODO: use buffered output stream for better performance?
 */
public class InteractionEventLogger extends AbstractMonitorLog implements IInteractionEventListener {

	private int eventAccumulartor = 0;

	private final List<InteractionEvent> queue = new CopyOnWriteArrayList<InteractionEvent>();

	private final InteractionEventObfuscator handleObfuscator = new InteractionEventObfuscator();

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S z", Locale.ENGLISH); //$NON-NLS-1$

	public InteractionEventLogger(File outputFile) {
		this.outputFile = outputFile;
	}

	public synchronized void interactionObserved(InteractionEvent event) {
//		System.err.println("> " + event); //$NON-NLS-1$
		if (UiUsageMonitorPlugin.getDefault() == null) {
			StatusHandler.log(new Status(IStatus.WARNING, UiUsageMonitorPlugin.ID_PLUGIN,
					"Attempted to log event before usage monitor start")); //$NON-NLS-1$
		}
		if (UiUsageMonitorPlugin.getDefault().isObfuscationEnabled()) {
			String obfuscatedHandle = handleObfuscator.obfuscateHandle(event.getStructureKind(),
					event.getStructureHandle());
			event = new InteractionEvent(event.getKind(), event.getStructureKind(), obfuscatedHandle,
					event.getOriginId(), event.getNavigation(), event.getDelta(), event.getInterestContribution());
		}
		try {
			if (started) {
				String xml = getXmlForEvent(event);
				outputStream.write(xml.getBytes());
			} else if (event != null) {
				queue.add(event);
			}
			eventAccumulartor++;
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.WARNING, UiUsageMonitorPlugin.ID_PLUGIN,
					"Could not log interaction event", t)); //$NON-NLS-1$
		}
	}

	@Override
	public void startMonitoring() {
		super.startMonitoring();
		for (InteractionEvent queuedEvent : queue) {
			interactionObserved(queuedEvent);
		}
		queue.clear();
	}

	@Override
	public void stopMonitoring() {
		super.stopMonitoring();
		if (UiUsageMonitorPlugin.getDefault() != null) {
			UiUsageMonitorPlugin.getDefault().incrementObservedEvents(eventAccumulartor);
		}
		eventAccumulartor = 0;
	}

	private String getXmlForEvent(InteractionEvent event) {
		return writeLegacyEvent(event);
	}

	/**
	 * @return true if successfully cleared
	 */
	public synchronized void clearInteractionHistory() throws IOException {
		this.clearInteractionHistory(true);
	}

	public synchronized void clearInteractionHistory(boolean startMonitoring) throws IOException {
		stopMonitoring();
		outputStream = new FileOutputStream(outputFile, false);
		outputStream.flush();
		outputStream.close();
		outputFile.delete();
		outputFile.createNewFile();
		if (startMonitoring) {
			startMonitoring();
		}
	}

	public List<InteractionEvent> getHistoryFromFile(File file) {
		return getHistoryFromFile(file, new NullProgressMonitor());
	}

	public List<InteractionEvent> getHistoryFromFile(File file, IProgressMonitor monitor) {

		List<InteractionEvent> events = new ArrayList<InteractionEvent>();
		InputStream inputStream = null;
		long fileLength = 0;

		ZipFile zip = null;
		try {
			// The file may be a zip file...
			if (file.getName().endsWith(".zip")) { //$NON-NLS-1$
				zip = new ZipFile(file);
				if (zip.entries().hasMoreElements()) {
					ZipEntry entry = zip.entries().nextElement();
					inputStream = zip.getInputStream(entry);
					fileLength = entry.getSize();
				}
			} else {
				inputStream = new FileInputStream(file);
				fileLength = file.length();
			}

			//450: the approximate size of an event in XML 
			int numberOfEventsEstimate = (int) (fileLength / 450);

			monitor.beginTask(Messages.InteractionEventLogger_Reading_History_From_File, numberOfEventsEstimate);

			getHistoryFromStream(inputStream, events, monitor);

		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
					"Could not read interaction history", e)); //$NON-NLS-1$
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN,
							"unable to close input stream", e)); //$NON-NLS-1$
				}
			}
			if (zip != null) {
				try {
					zip.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

		monitor.done();

		return events;
	}

	/**
	 * @param events
	 * @param monitor
	 * @param tag
	 * @param endl
	 * @param buf
	 */
	private void getHistoryFromStream(InputStream reader, List<InteractionEvent> events, IProgressMonitor monitor)
			throws IOException {
		String xml;
		int index;
		String buf = ""; //$NON-NLS-1$
		String tag = "</" + InteractionContextExternalizer.ELMNT_INTERACTION_HISTORY_OLD + ">"; //$NON-NLS-1$ //$NON-NLS-2$
		String endl = "\r\n"; //$NON-NLS-1$
		byte[] buffer = new byte[1000];
		int bytesRead = 0;
		while ((bytesRead = reader.read(buffer)) != -1) {
			buf = buf + new String(buffer, 0, bytesRead);
			while ((index = buf.indexOf(tag)) != -1) {
				index += tag.length();
				xml = buf.substring(0, index);
				InteractionEvent event = readLegacyEvent(xml);
				if (event != null) {
					events.add(event);
				}

				if (index + endl.length() > buf.length()) {
					buf = ""; //$NON-NLS-1$
				} else {
					buf = buf.substring(index + endl.length(), buf.length());
				}

				monitor.worked(1);
			}
			buffer = new byte[1000];
		}
	}

	private static final String OPEN = "<"; //$NON-NLS-1$

	private static final String CLOSE = ">"; //$NON-NLS-1$

	private static final String SLASH = "/"; //$NON-NLS-1$

	private static final String ENDL = "\n"; //$NON-NLS-1$

	private static final String TAB = "\t"; //$NON-NLS-1$

	@Deprecated
	public String writeLegacyEvent(InteractionEvent e) {
		try {
			StringBuffer res = new StringBuffer();
			String tag = "interactionEvent"; //$NON-NLS-1$
			res.append(OPEN);
			res.append(tag);
			res.append(CLOSE);
			res.append(ENDL);

			openElement(res, "kind"); //$NON-NLS-1$
			formatContent(res, e.getKind());
			closeElement(res, "kind"); //$NON-NLS-1$

			openElement(res, "date"); //$NON-NLS-1$
			formatContent(res, e.getDate());
			closeElement(res, "date"); //$NON-NLS-1$

			openElement(res, "endDate"); //$NON-NLS-1$
			formatContent(res, e.getEndDate());
			closeElement(res, "endDate"); //$NON-NLS-1$

			openElement(res, "originId"); //$NON-NLS-1$
			formatContent(res, e.getOriginId());
			closeElement(res, "originId"); //$NON-NLS-1$

			openElement(res, "structureKind"); //$NON-NLS-1$
			formatContent(res, e.getStructureKind());
			closeElement(res, "structureKind"); //$NON-NLS-1$

			openElement(res, "structureHandle"); //$NON-NLS-1$
			formatContent(res, e.getStructureHandle());
			closeElement(res, "structureHandle"); //$NON-NLS-1$

			openElement(res, "navigation"); //$NON-NLS-1$
			formatContent(res, e.getNavigation());
			closeElement(res, "navigation"); //$NON-NLS-1$

			openElement(res, "delta"); //$NON-NLS-1$
			formatContent(res, e.getDelta());
			closeElement(res, "delta"); //$NON-NLS-1$

			openElement(res, "interestContribution"); //$NON-NLS-1$
			formatContent(res, e.getInterestContribution());
			closeElement(res, "interestContribution"); //$NON-NLS-1$

			res.append(OPEN);
			res.append(SLASH);
			res.append(tag);
			res.append(CLOSE);
			res.append(ENDL);
			return res.toString();
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, UiUsageMonitorPlugin.ID_PLUGIN, "Could not write event", t)); //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
	}

	private void formatContent(StringBuffer buffer, float interestContribution) {
		buffer.append(interestContribution);
	}

	@SuppressWarnings("deprecation")
	private void formatContent(StringBuffer buffer, String content) {
		if (content != null && content.length() > 0) {
			String xmlContent;
			xmlContent = org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertToXmlString(content);
			xmlContent = xmlContent.replace("\n", "\n\t\t"); //$NON-NLS-1$ //$NON-NLS-2$
			buffer.append(xmlContent);
		}
	}

	private void formatContent(StringBuffer buffer, Kind kind) {
		buffer.append(kind.toString());
	}

	private void formatContent(StringBuffer buffer, Date date) {
		buffer.append(dateFormat.format(date));
	}

	private void openElement(StringBuffer buffer, String tag) {
		buffer.append(TAB);
		buffer.append(OPEN);
		buffer.append(tag);
		buffer.append(CLOSE);
	}

	private void closeElement(StringBuffer buffer, String tag) {
		buffer.append(OPEN);
		buffer.append(SLASH);
		buffer.append(tag);
		buffer.append(CLOSE);
		buffer.append(ENDL);
	}

	public InteractionEvent readLegacyEvent(String xml) {
		Reader reader = new StringReader(xml);
		HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(reader, null);
		String kind = ""; //$NON-NLS-1$
		String startDate = ""; //$NON-NLS-1$
		String endDate = ""; //$NON-NLS-1$
		String originId = ""; //$NON-NLS-1$
		String structureKind = ""; //$NON-NLS-1$
		String structureHandle = ""; //$NON-NLS-1$
		String navigation = ""; //$NON-NLS-1$
		String delta = ""; //$NON-NLS-1$
		String interest = ""; //$NON-NLS-1$
		try {
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {
				if (token.getValue().toString().equals("<kind>")) { //$NON-NLS-1$
					kind = readStringContent(tokenizer, "</kind>"); //$NON-NLS-1$
					kind = kind.toLowerCase(Locale.ENGLISH);
				} else if (token.getValue().toString().equals("<date>")) { //$NON-NLS-1$
					startDate = readStringContent(tokenizer, "</date>"); //$NON-NLS-1$
				} else if (token.getValue().toString().equals("<endDate>")) { //$NON-NLS-1$
					endDate = readStringContent(tokenizer, "</endDate>"); //$NON-NLS-1$
				} else if (token.getValue().toString().equals("<originId>")) { //$NON-NLS-1$
					originId = readStringContent(tokenizer, "</originId>"); //$NON-NLS-1$
				} else if (token.getValue().toString().equals("<structureKind>")) { //$NON-NLS-1$
					structureKind = readStringContent(tokenizer, "</structureKind>"); //$NON-NLS-1$
				} else if (token.getValue().toString().equals("<structureHandle>")) { //$NON-NLS-1$
					structureHandle = readStringContent(tokenizer, "</structureHandle>"); //$NON-NLS-1$
				} else if (token.getValue().toString().equals("<navigation>")) { //$NON-NLS-1$
					navigation = readStringContent(tokenizer, "</navigation>"); //$NON-NLS-1$
				} else if (token.getValue().toString().equals("<delta>")) { //$NON-NLS-1$
					delta = readStringContent(tokenizer, "</delta>"); //$NON-NLS-1$
				} else if (token.getValue().toString().equals("<interestContribution>")) { //$NON-NLS-1$
					interest = readStringContent(tokenizer, "</interestContribution>"); //$NON-NLS-1$
				}
			}
			float interestFloatVal = 0;
			try {
				interestFloatVal = Float.parseFloat(interest);
			} catch (NumberFormatException nfe) {
				// ignore for empty interest values
			}
			InteractionEvent event = new InteractionEvent(Kind.fromString(kind), structureKind, structureHandle,
					originId, navigation, delta, interestFloatVal, dateFormat.parse(startDate),
					dateFormat.parse(endDate));
			return event;

		} catch (ParseException e) {
			System.err.println("readevent: " + xml); //$NON-NLS-1$
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("readevent: " + xml); //$NON-NLS-1$
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("readevent: " + xml); //$NON-NLS-1$
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("deprecation")
	private String readStringContent(HtmlStreamTokenizer tokenizer, String endTag) throws IOException, ParseException {
		StringBuffer content = new StringBuffer();
		Token token = tokenizer.nextToken();
		while (!token.getValue().toString().equals(endTag)) {
			if (content.length() > 0) {
				content.append(' ');
			}
			content.append(token.getValue().toString());
			token = tokenizer.nextToken();
		}
		return org.eclipse.mylyn.internal.commons.core.XmlStringConverter.convertXmlToString(content.toString()).trim();
	}

	public static DateFormat dateFormat() {
		return (DateFormat) dateFormat.clone();
	}
}
