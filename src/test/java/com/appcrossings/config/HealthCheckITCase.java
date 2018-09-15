package com.appcrossings.config;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;


public class HealthCheckITCase extends AbstractITCase {

  @Test
  public void testHealthEndpoint() throws Exception {

    WebTarget target = client.target("http://localhost:8891/configrd/v1/health");

    Response resp = target.request().accept(MediaType.WILDCARD).get();
    Assert.assertEquals(200, resp.getStatus());
    String body = resp.readEntity(String.class);

    Assert.assertTrue(body.contains("version"));
    Assert.assertTrue(body.contains("build"));
  }



}
