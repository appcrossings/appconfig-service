package com.appcrossings.config;

import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;

public class TestHealthCheck {

  private AppConfigService service;

  @Test
  public void testHealthCheck() {

    Response resp = service.getHealth();
    Assert.assertNotNull(resp);
    Assert.assertEquals(resp.getStatus(), 200);
    Assert.assertNotNull(resp.getEntity());

    String body = (String) resp.getEntity();
    Assert.assertTrue(body.contains("version"));
    Assert.assertTrue(body.contains("build"));

  }

}
