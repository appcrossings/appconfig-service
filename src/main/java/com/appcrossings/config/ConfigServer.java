package com.appcrossings.config;

import javax.servlet.ServletException;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.util.Throwables;
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

  public static void main(String[] args) throws Throwable {

    start(args[0]);
  }

  protected static void start(String port) throws Throwable {
    
    long start = System.currentTimeMillis();
    
    System.setProperty("org.jboss.logging.provider", "slf4j");

    if (server == null) {

      PathHandler path = Handlers.path();

      server = Undertow.builder().addHttpListener(Integer.valueOf(port), "localhost")
          .setHandler(path).build();

      try {
        server.start();
      } catch (Exception e) {
        Throwable ex = Throwables.getRootCause(e);
        logger.error(ex.getMessage(), ex);
        throw ex;
      }

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
        Throwable ex = Throwables.getRootCause(e);
        logger.error(ex.getMessage(), ex);
        throw ex;
      }

      logger.info("Application deployed");
    }
    
    logger.info("Server started in " + (System.currentTimeMillis() - start) / 60 + "s");

  }

  public static void stop() {

    if (server == null) {
      throw new IllegalStateException("Server has not been started yet");
    }

    logger.info("Stopping server");

    deploymentManager.undeploy();
    server.stop();
    server = null;
    
    logger.info("Server stopped");
  }


}
