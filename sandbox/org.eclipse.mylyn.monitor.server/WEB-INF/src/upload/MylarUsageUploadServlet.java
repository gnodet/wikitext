/*******************************************************************************
 * Copyright (c) 2005, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * @author - Meghan Allen
 */
public class MylarUsageUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String UPLOAD_DIRECTORY = "//home//study//uploads//";

	private static final String LOGGING_DIRECTORY = "//home//study//logging//";

	private static final String ERROR_LOGGING_FILE = "MylarUsageUploadErrorLog.txt";

	// 4 digit year, 2 digit month, 2 digit day, 2 digit hour, 2 digit minute
	// 2 digit second, 3 digit millisecond
	private static final String DATE_FORMAT_STRING = "yyyy.MM.dd.HH.mm.ss.SSS";

	// Supress warnings because
	// org.apache.commons.fileupload.servlet.ServletFileUpload
	// parseRequest doesn't use generics (suppress unchecked), and
	// ServletFileUpload.isMultipartContent is deprecated (suppress deprecation)
	@SuppressWarnings( { "unchecked", "deprecation" })
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		try {

			if (isMultipart) {

				// Create a factory for disk-based file items
				FileItemFactory factory = new DiskFileItemFactory();

				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload(factory);

				// Parse the request
				List<FileItem> items = upload.parseRequest(request);

				for (FileItem fi : items) {

					String oldFilename = fi.getName();

					int indexFirstDot = oldFilename.indexOf(".");

					String uid = oldFilename.substring(0, indexFirstDot);
					String extension = oldFilename.substring(oldFilename.length() - 4, oldFilename.length());

					String name = "USAGE-1.0-usage-" + uid + "-"
							+ new SimpleDateFormat(DATE_FORMAT_STRING).format(new Date()) + extension;

					File destFile = new File(UPLOAD_DIRECTORY, name);
					destFile.createNewFile();

					try {
						fi.write(destFile);
					} catch (Exception e) {
						logError("MylarUsageUploadServlet:doPost fi.write() failed " + e.getMessage());

					}

				}

			}
		} catch (FileUploadException fue) {
			logError("MylarUsageUploadServlet:doPost upload.parseRequest(request) failed " + fue.getMessage());

		} catch (IOException ioe) {
			logError("MylarUsageUploadServlet:doPost destFile.createNewFile() failed " + ioe.getMessage());
		}
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
