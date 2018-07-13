package com.appcrossings.config;

import java.util.HashMap;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import io.undertow.util.StatusCodes;


public class TestLookupConfigByHost {

  private AppConfigService service;

  @Test
  public void testLookupConfigByHostFile() {

    HashMap<String, Object> body = new HashMap<>();
    body.put(Environment.HOST_NAME, "kkarski-ibm");

    Response resp = service.discover("env", "http/hosts.properties", body);
    Assert.assertNotNull(resp);
    Assert.assertEquals(Integer.valueOf(StatusCodes.SEE_OTHER), Integer.valueOf(resp.getStatus()));

  }

}
