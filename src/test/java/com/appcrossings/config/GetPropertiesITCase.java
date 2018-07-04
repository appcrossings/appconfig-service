package com.appcrossings.config;

import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.Properties;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationContext.class}, webEnvironment = WebEnvironment.DEFINED_PORT)
public class GetPropertiesITCase {
  
  private static final Logger logger = LoggerFactory.getLogger(GetPropertiesITCase.class);

  protected Client client;
  protected WebTarget target;

  @Before
  public void init() throws Exception {

    client = ClientBuilder.newClient();
    target = client.target("http://localhost:8891/configrd/v1/");

  }

  @Test
  public void testGetPropertiesFromDefaultRepo() throws Exception {

    Response resp = target.path("/").request(MediaType.TEXT_PLAIN).get();
    Assert.assertEquals(200, resp.getStatus());
    
    String body = resp.readEntity(String.class);
    Properties props = new Properties();
    props.load(new StringReader(body));
    
    Assert.assertEquals(3, props.size());
  }
  
  @Test
  public void testGetPropertiesFromNamedRepo() throws Exception {

    Response resp = target.path("/").queryParam("repo", "classpath").request(MediaType.TEXT_PLAIN).get();
    Assert.assertEquals(200, resp.getStatus());
    
    String body = resp.readEntity(String.class);
    Properties props = new Properties();
    props.load(new StringReader(body));
    
    Assert.assertEquals(3, props.size());
  }

}
