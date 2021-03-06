package com.vaguehope.takeshi;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.WriteConcern;
import com.vaguehope.takeshi.config.Config;
import com.vaguehope.takeshi.config.Modes;
import com.vaguehope.takeshi.reporter.JvmReporter;
import com.vaguehope.takeshi.reporter.Reporter;
import com.vaguehope.takeshi.servlets.DataServlet;
import com.vaguehope.takeshi.servlets.StatusServlet;

public class Main {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	private final Server server;

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public Main () throws Exception { // NOSONAR Exception is throw by Server.start().
		// Reporting.
		Reporter reporter = new Reporter(new JvmReporter());
		reporter.start();

		// Services.
		// http://stackoverflow.com/questions/6520439/how-to-configure-mongodb-java-driver-mongooptions-for-production-use
		MongoOptions mongoOptions = new MongoOptions();
		mongoOptions.setAutoConnectRetry(true);
		mongoOptions.setSocketTimeout(60000);
		mongoOptions.setW(WriteConcern.SAFE.getW());
		mongoOptions.setSocketTimeout(15000);
		mongoOptions.setConnectTimeout(60000);
		Mongo mongo = new Mongo("localhost", mongoOptions);

		// Servlet container.
		ServletContextHandler servletHandler = new ServletContextHandler();
		servletHandler.setContextPath("/");

		// Servlets.
		servletHandler.addServlet(new ServletHolder(new DataServlet(mongo)), DataServlet.CONTEXT);
		servletHandler.addServlet(new ServletHolder(new StatusServlet()), StatusServlet.CONTEXT);

		// Static files on classpath.
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(false);
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });
		resourceHandler.setResourceBase(
				Modes.isDebug() ?
						"./src/main/resources/webroot" :
						Main.class.getResource("/webroot").toExternalForm()
				);

		// Prepare final handler.
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resourceHandler, servletHandler });

		// Listening connector.
		int port = Integer.parseInt(System.getenv("PORT")); // Heroko pattern.
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setMaxIdleTime(Config.SERVER_MAX_IDLE_TIME_MS);
		connector.setAcceptors(Config.SERVER_ACCEPTORS);
		connector.setStatsOn(false);
		connector.setLowResourcesConnections(Config.SERVER_LOW_RESOURCES_CONNECTIONS);
		connector.setLowResourcesMaxIdleTime(Config.SERVER_LOW_RESOURCES_MAX_IDLE_TIME_MS);
		connector.setPort(port);

		// Start server.
		this.server = new Server();
		this.server.setHandler(handlers);
		this.server.addConnector(connector);
		this.server.start();
		LOG.info("Server ready on port " + System.getenv("PORT") + ".");
	}

	public void join () throws InterruptedException {
		this.server.join();
	}

//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public static void main (String[] args) throws Exception { // NOSONAR throw by Server.start()
		new Main().join();
	}
}
