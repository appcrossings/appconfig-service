package com.appcrossings.config;

import java.util.HashMap;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.appcrossings.config.util.Environment;
import io.undertow.util.StatusCodes;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AppConfigServiceBoot.class})
public class TestLookupConfigByHost {

  @Autowired
  private AppConfigService service;

  @Test
  public void testLookupConfigByHostFile() {

    HashMap<String, Object> body = new HashMap<>();
    body.put(Environment.HOST_NAME, "kkarski-ibm");

    Response resp = service.resoveHost("env", "http/hosts.properties", body);
    Assert.assertNotNull(resp);
    Assert.assertEquals(Integer.valueOf(StatusCodes.SEE_OTHER),
        Integer.valueOf(resp.getStatus()));

  }

}
