package com.appcrossings.config;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AppConfigServiceBoot.class}, webEnvironment = WebEnvironment.DEFINED_PORT)
public class HealthCheckITCase {

  Client client = ClientBuilder.newClient();

  @Test
  public void testHealthEndpoint() throws Exception {

    WebTarget target = client.target("http://localhost:8891/configrd/v1/health");

    Response resp = target.request().accept(MediaType.WILDCARD).get();
    Assert.assertEquals(200, resp.getStatus());
    Object body = resp.getEntity();

    // String body = (String) IOUtils.toString(resp.getEntity());
    // Assert.assertTrue(body.contains("version"));
    // Assert.assertTrue(body.contains("build"));


  }

}
