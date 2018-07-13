package com.appcrossings.config;

import javax.servlet.ServletException;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

public class ConfigServer {

  private static final Logger logger = LoggerFactory.getLogger(ConfigServer.class);
  private static Undertow server;
  private static DeploymentManager deploymentManager;

  public static void main(String[] args) {

    start(args[0]);
  }

  protected static void start(String port) {
    System.setProperty("org.jboss.logging.provider", "slf4j");

    PathHandler path = Handlers.path();

    server = Undertow.builder().addHttpListener(Integer.valueOf(port), "localhost").setHandler(path)
        .build();

    server.start();

    logger.info("Server started on port " + port);

    DeploymentInfo servletBuilder = Servlets.deployment()
        .setClassLoader(ConfigServer.class.getClassLoader()).setContextPath("/")
        .setResourceManager(new ClassPathResourceManager(ConfigServer.class.getClassLoader()))
        .addServlets(Servlets.servlet("jerseyServlet", ServletContainer.class).setLoadOnStartup(1)
            .addInitParam("javax.ws.rs.Application", JerseyConfig.class.getName())
            .addMapping("/configrd/*"))
        .setDeploymentName("Application.war");

    logger.info("Starting application deployment");

    deploymentManager = Servlets.defaultContainer().addDeployment(servletBuilder);
    deploymentManager.deploy();

    try {
      path.addPrefixPath("/", deploymentManager.start());
    } catch (ServletException e) {
      throw new RuntimeException(e);
    }

    logger.info("Application deployed");

  }

  public static void stop() {

    if (server == null) {
      throw new IllegalStateException("Server has not been started yet");
    }

    logger.info("Stopping server");

    deploymentManager.undeploy();
    server.stop();

    logger.info("Server stopped");
  }


}
