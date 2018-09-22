package com.appcrossings.config;

import java.net.BindException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.junit.Before;
import com.appcrossings.config.util.StringUtils;

public abstract class AbstractITCase {

  protected Client client;
  protected WebTarget target;
  protected MediaType content;
  protected MediaType accept;

  protected static ConfigServer server;
  public static final String SERVER_PORT = "8891";

  public static void setup() throws Throwable {

    System.setProperty("org.jboss.logging.provider", "slf4j");

    if (!StringUtils.hasText(System.getProperty(ConfigSourceResolver.CONFIGRD_SYSTEM_PROPERTY))) {
      System.setProperty(ConfigSourceResolver.CONFIGRD_SYSTEM_PROPERTY,
          "classpath:test-repos.yaml");
    }

    if (server != null)
      server.stop();

    server = new ConfigServer();

    try {
      server.start(SERVER_PORT);
    } catch (BindException e) {
      // ignore
    } catch (Throwable e) {
      throw e;
    }
  }

  @Before
  public void init() throws Exception {
    client = ClientBuilder.newClient();
  }

  public static void teardown() throws Exception {
    if (server != null)
      server.stop();
  }

}
