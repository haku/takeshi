package com.vaguehope.takeshi;

import java.net.URL;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.Mongo;
import com.vaguehope.takeshi.reporter.JvmReporter;
import com.vaguehope.takeshi.reporter.Reporter;
import com.vaguehope.takeshi.reporter.SessionReporter;
import com.vaguehope.takeshi.servlets.DataServlet;
import com.vaguehope.takeshi.servlets.HelloWorldServlet;

public class Main {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private final Server server;

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public Main () throws Exception { // NOSONAR Exception is throw by Server.start().
		// Reporting.
		SessionReporter sessionReporter = new SessionReporter();
		Reporter reporter = new Reporter(new JvmReporter(), sessionReporter);
		reporter.start();

		// Servlet container.
		ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletHandler.setContextPath("/");

		// Session management.
		SessionManager sessionManager = servletHandler.getSessionHandler().getSessionManager();
		sessionManager.setMaxInactiveInterval(Config.SERVER_SESSION_INACTIVE_TIMEOUT_SECONDS);
		sessionManager.setMaxCookieAge(Config.SERVER_SESSION_INACTIVE_TIMEOUT_SECONDS);
		sessionManager.setSessionIdPathParameterName(null);
		sessionManager.addEventListener(sessionReporter);

		// Services.
		Mongo mongo = new Mongo();

		// Servlets.
		servletHandler.addServlet(new ServletHolder(new HelloWorldServlet()), HelloWorldServlet.CONTEXT);
		servletHandler.addServlet(new ServletHolder(new DataServlet(mongo)), DataServlet.CONTEXT);

		// Static files on classpath.
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });
		URL webroot = Main.class.getResource("/webroot");
		resourceHandler.setResourceBase(webroot.toExternalForm());

		// Prepare final handler.
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resourceHandler, servletHandler });

		// Listening connector.
		String portString = System.getenv("PORT"); // Heroko pattern.
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setMaxIdleTime(Config.SERVER_MAX_IDLE_TIME_MS);
		connector.setAcceptors(Config.SERVER_ACCEPTORS);
		connector.setStatsOn(false);
		connector.setLowResourcesConnections(Config.SERVER_LOW_RESOURCES_CONNECTIONS);
		connector.setLowResourcesMaxIdleTime(Config.SERVER_LOW_RESOURCES_MAX_IDLE_TIME_MS);
		connector.setPort(Integer.parseInt(portString));

		// Start server.
		this.server = new Server();
		this.server.setHandler(handlers);
		this.server.addConnector(connector);
		this.server.start();
		LOG.info("Server ready on port " + portString + ".");
	}

	public void join () throws InterruptedException {
		this.server.join();
	}

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public static void main (String[] args) throws Exception { // NOSONAR throw by Server.start()
		new Main().join();
	}
}
