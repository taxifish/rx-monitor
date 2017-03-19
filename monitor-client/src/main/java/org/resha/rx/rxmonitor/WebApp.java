package org.resha.rx.rxmonitor;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.net.URL;

public class WebApp {

    private static final int DEFAULT_PORT = 8080;
    private static final String PORT_PROPERTY_NAME = "port";

    public static void main(String[] args) {
        int port;

        try {
            port = Integer.parseInt(System.getProperty(PORT_PROPERTY_NAME), DEFAULT_PORT);
        } catch (Exception e) {
            port = DEFAULT_PORT;
        }

        WebApp app = new WebApp();

        try {
            app.start(port);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void start(int port) throws Exception {

        Server server = new Server();

        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(port);
        server.addConnector(connector);

        // Create static resources handler
        String baseStr = "/static";
        URL baseUrl = WebApp.class.getResource(baseStr);
        String basePath = baseUrl.toExternalForm();

        ResourceHandler rh = new ResourceHandler();
        rh.setDirectoriesListed(true);
        rh.setWelcomeFiles(new String[]{"index.html"});
        rh.setResourceBase(basePath);

        //Create servlet handler
        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ctx.setContextPath("/app");
        ctx.addServlet(new ServletHolder(new ConfigServlet()), "/config.js");

        //Register handlers
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {rh, ctx, new DefaultHandler()});

        server.setHandler(handlers);

        server.start();
        server.join();
    }

}
