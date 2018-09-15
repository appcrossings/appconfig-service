package com.appcrossings.config;

import java.net.BindException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractITCase {

  protected Client client;
  protected WebTarget target;

  @BeforeClass
  public static void setup() throws Throwable {
    
    System.setProperty("repo", "classpath:repos.yaml");
    
    try {
      ConfigServer.start("8891");
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

  @AfterClass
  public static void teardown() throws Exception {
    ConfigServer.stop();
  }

}
