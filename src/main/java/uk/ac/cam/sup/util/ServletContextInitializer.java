package uk.ac.cam.sup.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletContextInitializer implements ServletContextListener {
	private static Logger log = LoggerFactory.getLogger(ServletContextInitializer.class);
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("Initializing apiKey and dashboardUrl fields in ServContext...");
		ServletContext context = sce.getServletContext();
		ServContext.setApiKey(context.getInitParameter("apiKey"));
		ServContext.setDashboardUrl(context.getInitParameter("dashboardUrl"));
		ServContext.setUploadsDir(context.getInitParameter("uploadsDir"));
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		log.info("Destroying ServletContext - setting apiKey and dashboardUrl in ServContext to null.");
		ServContext.setApiKey(null);
		ServContext.setDashboardUrl(null);
		ServContext.setUploadsDir(null);
	}

}
