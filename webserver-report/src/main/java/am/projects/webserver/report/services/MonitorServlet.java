package am.projects.webserver.report.services;

import am.projects.webserver.report.Constants;
import am.projects.webserver.report.policy.MonitorPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@WebServlet(value = "/monitor", loadOnStartup = 1)
public class MonitorServlet extends HttpServlet {

    private static Logger logger = LoggerFactory.getLogger(MonitorServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {

            String action = request.getParameter("action");
            String as = request.getParameter("as");
            String server = request.getParameter("server");
            String applicationName = request.getParameter("applicationName");
            String strStartDate = request.getParameter("startDate");
            String strEndDate = request.getParameter("endDate");
            Date startDate = null;
            Date endDate = null;
            try {
                if (strStartDate != null)
                    startDate = new Date(new Long(strStartDate));
                if (strEndDate != null)
                    endDate = new Date(new Long(strEndDate));
            } catch (Exception e) {
                logger.error("Warning, interval date not valid", e);
            }

            response.setContentType("application/json");
            if ("available-memory".equals(action)) {
                MonitorPolicy.requestMemory(Constants.MEM_AVAILABLE, applicationName, server, as, startDate, endDate, response.getOutputStream());
            } else if ("total-memory".equals(action)) {
                MonitorPolicy.requestMemory(Constants.MEM_TOTAL, applicationName, server, as, startDate, endDate, response.getOutputStream());
            } else if ("free-memory".equals(action)) {
                MonitorPolicy.requestMemory(Constants.MEM_FREE, applicationName, server, as, startDate, endDate, response.getOutputStream());
            } else if ("max-memory".equals(action)) {
                MonitorPolicy.requestMemory(Constants.MEM_MAX, applicationName, server, as, startDate, endDate, response.getOutputStream());
            } else if ("used-connections".equals(action)) {
                String ds = request.getParameter("idObject");
                MonitorPolicy.requestUsedConnection(ds, applicationName, server, as, startDate, endDate, response.getOutputStream());
            } else if ("threads".equals(action)) {
                MonitorPolicy.requestTotalThreads(applicationName, server, as, startDate, endDate, response.getOutputStream());
            }
            response.flushBuffer();
        } catch (Exception e) {
          logger.error("Error in the request to the Monitor Servlet", e);
        }
    }

}