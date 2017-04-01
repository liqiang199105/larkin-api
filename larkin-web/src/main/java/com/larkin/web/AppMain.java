package com.larkin.web;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.WebAppContext;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AppMain {
	private static Logger logger = Logger.getLogger(AppMain.class);

	private static final String DEFAULT_CENTRAL_PORT = "8081";
	private static final String WEB_XML = "webapp/WEB-INF/web.xml";
	private static final String CLASS_ONLY_AVAILABLE_IN_IDE = "com.sjl.IDE";
	private static final String PROJECT_RELATIVE_PATH_TO_WEBAPP = "src/main/webapp";
	private static final String BASIC_PATH = "/api/v1/";
	private static final String WEB_APP_NAME = "painter";

	private Server server;
	private int port;

	public AppMain(int port) {
		this.port = port;
	}

	public void start() throws Exception {
		server = new Server(port);
		server.setHandler(createHandlers());
		server.setStopAtShutdown(true);
		server.start();
	}

	public void join() throws InterruptedException {
		server.join();
	}

	public void stop() throws Exception {
		server.stop();
	}

	private HandlerCollection createHandlers() {
		WebAppContext ctx = new WebAppContext(WEB_APP_NAME, BASIC_PATH);
		if (isRunningInShadedJar()) {
			logger.info("run jar.");
			ctx.setWar(getShadedWarUrl());
		} else {
			logger.info("run project.");
			ctx.setWar(PROJECT_RELATIVE_PATH_TO_WEBAPP);
		}
		List<Handler> handlers = new ArrayList<Handler>();
		handlers.add(ctx);

		HandlerList contexts = new HandlerList();
		contexts.setHandlers(handlers.toArray(new Handler[0]));

		HandlerCollection result = new HandlerCollection();
		result.setHandlers(new Handler[] { contexts});

		return result;
	}

	private boolean isRunningInShadedJar() {
		try {
			Class.forName(CLASS_ONLY_AVAILABLE_IN_IDE);
			return false;
		} catch (ClassNotFoundException anExc) {
			return true;
		}
	}

	private URL getResource(String aResource) {
		return Thread.currentThread().getContextClassLoader().getResource(aResource);
	}

	private String getShadedWarUrl() {
		String url = getResource(WEB_XML).toString();
		return url.substring(0, url.length() - 15);
	}

	public static void main(String[] args) throws Exception {
		CommandLineParser parser = new BasicParser();
		Options options = new Options();
		options.addOption("p", "port", true, "host port for connection");

		CommandLine cmd = parser.parse(options, args);
		int port = Integer.parseInt(cmd.getOptionValue("port", DEFAULT_CENTRAL_PORT));
		new AppMain(port).start();
	}
}
