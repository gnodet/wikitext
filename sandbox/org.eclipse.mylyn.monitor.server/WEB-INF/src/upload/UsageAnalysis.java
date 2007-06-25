package upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UsageAnalysis {

	Map<Integer, Map<String, Integer>> userToViewMap = new HashMap<Integer, Map<String, Integer>>();

	Map<Integer, Integer> usersNumSelections = new HashMap<Integer, Integer>();

	Map<String, Integer> totalNumSelections = new HashMap<String, Integer>();

	Map<String, Integer> totalNumCommands = new HashMap<String, Integer>();

	final static String USAGE_DIRECTORY = "//home//study//uploads//";

	final static String OUTPUT_DIRECTORY = "//home//study//tomcat//apache-tomcat-5.5.23//webapps//upload//";

	final static String LOGGING_DIRECTORY = "//home//study//logging//";

	final static String ERROR_LOGGING_FILE = "MylarUsageAnalysisErrorLog.txt";

	final static String USAGE_SUMMARY_FILE = "usageSummary.html";

	int MAX_NUM_VIEWS_TO_REPORT = 10;

	int MAX_NUM_COMMANDS_TO_REPORT = 25;

	int totalSelections = 0;

	int totalCommands = 0;

	public static void main(String[] args) {
		UsageAnalysis ua = new UsageAnalysis();
		ua.analyzeLogs();
	}

	public void analyzeLogs() {

		try {
			String[] files = new File(USAGE_DIRECTORY).list();
			int userID = 0;

			for (String filename : files) {
				File currFile = new File(USAGE_DIRECTORY, filename);

				userID = this.getUserId(currFile);

				int numSelections = 0;
				int numCommands = 0;
				if (usersNumSelections.containsKey(userID)) {
					numSelections = usersNumSelections.get(userID);
				}

				Map<String, Integer> viewToNumberMap = userToViewMap.get(userID);
				if (viewToNumberMap == null) {
					viewToNumberMap = new HashMap<String, Integer>();
					userToViewMap.put(userID, viewToNumberMap);
				}

				int index;
				int endIndex;
				String currOriginId;
				String buf = "";
				byte[] buffer = new byte[1000];
				int bytesRead = 0;
				String beginningTag = "<originId>";
				String endTag = "</originId>";

				String kindTag = "<kind>";
				String selectionKind = "selection";
				String commandKind = "command";

				// they should all be zip files, ignore anything that's not
				if (currFile.getName().endsWith(".zip")) {
					ZipFile zip = new ZipFile(currFile);

					if (zip.entries().hasMoreElements()) {
						ZipEntry entry = zip.entries().nextElement();
						InputStream stream = zip.getInputStream(entry);

						while ((bytesRead = stream.read(buffer)) != -1) {
							buf = buf + new String(buffer, 0, bytesRead);

							while ((endIndex = buf.indexOf(endTag)) != -1) {
								index = buf.indexOf(beginningTag);
								index += beginningTag.length();

								int kindIndex = buf.indexOf(kindTag);
								kindIndex += kindTag.length();

								String currKind = buf.substring(kindIndex, kindIndex + selectionKind.length());

								if (currKind.contains(selectionKind)) {

									numSelections++;
									totalSelections++;

									currOriginId = buf.substring(index, endIndex);

									if (!viewToNumberMap.containsKey(currOriginId)) {
										viewToNumberMap.put(currOriginId, 0);
									}
									int numViews = viewToNumberMap.get(currOriginId) + 1;
									viewToNumberMap.put(currOriginId, numViews);

									if (!totalNumSelections.containsKey(currOriginId)) {
										totalNumSelections.put(currOriginId, 0);
									}
									int totalNumViews = totalNumSelections.get(currOriginId) + 1;
									totalNumSelections.put(currOriginId, totalNumViews);

								} else if (currKind.contains(commandKind)) {
									numCommands++;
									totalCommands++;
									currOriginId = buf.substring(index, endIndex);

									if (!totalNumCommands.containsKey(currOriginId)) {
										totalNumCommands.put(currOriginId, 0);
									}

									int currNumCommands = totalNumCommands.get(currOriginId) + 1;
									totalNumCommands.put(currOriginId, currNumCommands);
								}

								buf = buf.substring(endIndex + endTag.length(), buf.length());

							}

							buffer = new byte[1000];
						}
					}
				}
				usersNumSelections.put(userID, numSelections);

			}
			// print to a file.
			printSummaryToFile();
		} catch (IOException ioe) {
			logError(ioe.getMessage());
		}

	}

	private static void logError(String error) {
		File errorLogFile = new File(LOGGING_DIRECTORY, ERROR_LOGGING_FILE);
		try {
			if (!errorLogFile.exists()) {
				errorLogFile.createNewFile();
			}

			PrintStream errorLogStream = new PrintStream(new FileOutputStream(errorLogFile, true));

			errorLogStream.println(error);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Assuming the file naming convention of <phase>-<version>-usage-<userID>-<date and time>.zip
	 * 
	 * copied from: org.eclipse.mylyn.monitor.usage.core.ReportGenerator author: Mik Kersten
	 */
	private int getUserId(File source) {
		String userIDText = source.getName();
		int userId = -1;
		String prefix = "-usage-";

		if (source.getName().indexOf(prefix) >= 0) {
			try {
				userIDText = userIDText.substring(userIDText.indexOf(prefix) + prefix.length(), userIDText.length());
				userIDText = userIDText.substring(0, userIDText.indexOf("-"));
				userId = Integer.valueOf(userIDText);
			} catch (Throwable t) {
				logError(t.getMessage());
			}
		}

		return userId;
	}

	private String formatAsPercentage(float viewUse) {
		String formattedViewUse = ("" + viewUse * 100);

		// sometimes the floats are so small that formattedViewUsage ends up
		// being
		// something like 7.68334E-4, which would get formatted to 7.68% without
		// this check
		if (formattedViewUse.contains("E")) {
			return "0.00%";
		}
		int indexOf2ndDecimal = formattedViewUse.indexOf('.') + 3;
		if (indexOf2ndDecimal <= formattedViewUse.length()) {
			formattedViewUse = formattedViewUse.substring(0, indexOf2ndDecimal);
		}
		return formattedViewUse + "%";
	}

	static public String getSummaryFilePath() {
		File summaryFile = new File(USAGE_DIRECTORY, USAGE_SUMMARY_FILE);
		if (!summaryFile.exists()) {
			try {
				summaryFile.createNewFile();
			} catch (IOException e) {
				logError(e.getMessage());
			}
		}

		return summaryFile.getAbsolutePath();
	}

	public void printSummaryToFile() {

		File summaryFile = new File(OUTPUT_DIRECTORY, USAGE_SUMMARY_FILE);
		try {
			if (!summaryFile.exists()) {
				summaryFile.createNewFile();
			} else {
				summaryFile.delete();
				summaryFile.createNewFile();
			}

			PrintStream summaryLogStream = new PrintStream(new FileOutputStream(summaryFile, true));

			summaryLogStream.println("<html><body>");
			summaryLogStream.println("<h2>Mylyn Community Usage Statistics</h2>");
			summaryLogStream.println("These statistics are updated once per day.  They were last updated at "
					+ DateFormat.getTimeInstance(DateFormat.DEFAULT).format(Calendar.getInstance().getTime()) + " "
					+ new SimpleDateFormat("z").format(Calendar.getInstance().getTime()) + " on "
					+ DateFormat.getDateInstance().format(Calendar.getInstance().getTime()) + ".");
			summaryLogStream.println("<br><br>");

			summaryLogStream.println("<b>Total events: " + (totalSelections + totalCommands) + "</b><br>");
			summaryLogStream.println("<b>Number of unique users: " + userToViewMap.entrySet().size() + "</b><br><br>");
			summaryLogStream.println("");

			summaryLogStream.println("<b>" + " " + MAX_NUM_VIEWS_TO_REPORT + " most used views: </b>");

			summaryLogStream.println("<table border=1 rules=rows|columns cellpadding=4>");

			List<String> viewUsage = new ArrayList<String>();
			for (String view : totalNumSelections.keySet()) {
				float numSelections = (totalNumSelections.get(view));
				float viewUse = numSelections / totalSelections;
				String formattedViewUse = formatAsPercentage(viewUse);
				viewUsage.add(formattedViewUse + "," + view + "," + totalNumSelections.get(view));
			}
			Collections.sort(viewUsage, new PercentUsageComparator());
			int numViewsToReport = 0;
			Iterator<String> listIterator = viewUsage.iterator();
			while (listIterator.hasNext()
					&& (MAX_NUM_VIEWS_TO_REPORT == -1 || numViewsToReport < MAX_NUM_VIEWS_TO_REPORT)) {

				String[] nextRow = listIterator.next().split(",");

				summaryLogStream.println("<tr>");

				summaryLogStream.println("<td>");
				summaryLogStream.println(nextRow[0]);
				summaryLogStream.println("</td>");

				summaryLogStream.println("<td>");
				summaryLogStream.println(nextRow[1]);
				summaryLogStream.println("</td>");

				summaryLogStream.println("<td>");
				summaryLogStream.println(nextRow[2]);
				summaryLogStream.println("</td>");

				summaryLogStream.println("</tr>");
				numViewsToReport++;
			}

			summaryLogStream.println("</table>");
			summaryLogStream.println("<br><br>");

			summaryLogStream.println("<b>" + " " + MAX_NUM_COMMANDS_TO_REPORT + " most used commands: </b>");

			// Commands
			summaryLogStream.println("<table border=1 rules=rows|columns cellpadding=4>");

			List<String> commandUsage = new ArrayList<String>();
			for (String cmd : totalNumCommands.keySet()) {
				float numCommands = (totalNumCommands.get(cmd));
				float commandUse = numCommands / totalCommands;
				String formattedCmdUse = formatAsPercentage(commandUse);
				commandUsage.add(formattedCmdUse + "," + cmd + "," + totalNumCommands.get(cmd));
			}
			Collections.sort(commandUsage, new PercentUsageComparator());
			int numCommandsToReport = 0;

			Iterator<String> cmdListIterator = commandUsage.iterator();
			while (cmdListIterator.hasNext()
					&& (MAX_NUM_COMMANDS_TO_REPORT == -1 || numCommandsToReport < MAX_NUM_COMMANDS_TO_REPORT)) {

				String[] nextRow = cmdListIterator.next().split(",");

				summaryLogStream.println("<tr>");

				summaryLogStream.println("<td>");
				summaryLogStream.println(nextRow[0]);
				summaryLogStream.println("</td>");

				summaryLogStream.println("<td>");
				summaryLogStream.println(nextRow[1]);
				summaryLogStream.println("</td>");

				summaryLogStream.println("<td>");
				summaryLogStream.println(nextRow[2]);
				summaryLogStream.println("</td>");

				summaryLogStream.println("</tr>");
				numCommandsToReport++;
			}

			summaryLogStream.println("</table>");
			summaryLogStream.println("</html></body>");
			summaryLogStream.close();
		} catch (IOException e) {
			logError(e.getMessage());
		}

	}

	public void printSummary(int userId, PrintWriter out) {
		Map<String, Integer> normalViewSelections = userToViewMap.get(userId);

		float numSelections = usersNumSelections.get(userId);

		List<String> viewUsage = new ArrayList<String>();
		for (String view : normalViewSelections.keySet()) {
			float viewUse = ((float) (normalViewSelections.get(view))) / numSelections;
			String formattedViewUse = formatAsPercentage(viewUse);
			viewUsage.add(formattedViewUse + ": " + view + " (" + normalViewSelections.get(view) + ")" + "<br>");
		}
		Collections.sort(viewUsage, new PercentUsageComparator());
		int numViewsToReport = 0;
		for (String viewUsageSummary : viewUsage) {
			if (MAX_NUM_VIEWS_TO_REPORT == -1 || numViewsToReport < MAX_NUM_VIEWS_TO_REPORT) {
				out.println(viewUsageSummary);
				numViewsToReport++;
			}
		}
	}

	public void printReport(PrintWriter out) {
		for (int userId : userToViewMap.keySet()) {
			printSummary(userId, out);
		}
	}
}
