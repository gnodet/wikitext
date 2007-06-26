/*******************************************************************************
 * Copyright (c) 2005, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package upload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author - Meghan Allen
 */
public class GetUserIDServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String USER_ID_PARAM = "MylarUserID";

	private static final String USER_ID_DIRECTORY = "//home//study//userIDS//";

	private static final String NEXT_USER_ID_FILENAME = "MylarNextUserID.txt";

	private static final String MYLAR_USER_IDS_FILENAME = "MylarUserIDs.txt";

	private static final String LOGGING_DIRECTORY = "//home//study//logging//";

	private static final String ERROR_LOGGING_FILE = "MylarUsageGetUIDErrorLog.txt";

	private static final int SIZE_OF_INT = 8;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String userIDRequest = req.getParameter(USER_ID_PARAM);

		if (userIDRequest == null) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		res.setContentType("text/plain");
		PrintWriter out = res.getWriter();
		try {
			out.print("" + this.getNewUserID());
			res.setStatus(HttpServletResponse.SC_OK);
		} catch (IOException ioe) {
			logError("GetUserIDServlet:doPost out.print() failed " + ioe.getMessage());
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private synchronized int getNewUserID() throws IOException {
		File nextUserIDFile = new File(USER_ID_DIRECTORY, NEXT_USER_ID_FILENAME);
		if (!nextUserIDFile.exists()) {
			throw new IOException(USER_ID_DIRECTORY + NEXT_USER_ID_FILENAME + " does not exist");
		}

		FileInputStream fileInputStream = new FileInputStream(nextUserIDFile);
		byte[] buffer = new byte[SIZE_OF_INT];
		int numBytesRead = fileInputStream.read(buffer);
		int uID = new Integer(new String(buffer, 0, numBytesRead)).intValue();
		fileInputStream.close();

		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(nextUserIDFile));
		int nextUId = uID + 17;
		bufferedWriter.write(new Integer(nextUId).toString());
		bufferedWriter.close();

		File allUserIDFile = new File(USER_ID_DIRECTORY, MYLAR_USER_IDS_FILENAME);
		if (!allUserIDFile.exists()) {
			throw new IOException(USER_ID_DIRECTORY + MYLAR_USER_IDS_FILENAME + " does not exist");
		}

		PrintStream printStreamAllIds = new PrintStream(new FileOutputStream(allUserIDFile, true));

		printStreamAllIds.println(new Integer(uID).toString());
		printStreamAllIds.flush();

		printStreamAllIds.close();

		return uID;
	}

	private void logError(String errorMessage) {
		File errorLogFile = new File(LOGGING_DIRECTORY, ERROR_LOGGING_FILE);
		try {
			if (!errorLogFile.exists()) {
				errorLogFile.createNewFile();
			}

			PrintStream errorLogStream = new PrintStream(new FileOutputStream(errorLogFile, true));

			errorLogStream.println(errorMessage);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
