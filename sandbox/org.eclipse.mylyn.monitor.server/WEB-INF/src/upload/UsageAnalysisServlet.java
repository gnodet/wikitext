package upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UsageAnalysisServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PrintWriter out = res.getWriter();

// run the analysis as a cron job
//		UsageAnalysis usageAnalysis = new UsageAnalysis();
//		usageAnalysis.analyzeLogs();

		String filePath = UsageAnalysis.getSummaryFilePath();

		File summaryFile = new File(filePath);

		FileInputStream inputStream = new FileInputStream(summaryFile);

		int bytesRead = 0;
		byte[] buffer = new byte[1000];

		while ((bytesRead = inputStream.read(buffer)) != -1) {
			out.print(new String(buffer, 0, bytesRead));

		}

	}

}
