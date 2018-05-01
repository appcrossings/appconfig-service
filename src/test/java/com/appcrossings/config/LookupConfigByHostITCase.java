package com.appcrossings.config;


import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections.map.HashedMap;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import com.appcrossings.config.util.Environment;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AppConfigServiceBoot.class},
    webEnvironment = WebEnvironment.DEFINED_PORT)
public class LookupConfigByHostITCase {

  private Client client;
  private WebTarget target;

  @Before
  public void init() {

    client = ClientBuilder.newClient();
    target = client.target("http://localhost:8891/configrd/v1");

  }

  @Test
  public void testResolveHttpLocationViaHost() throws Exception {

    Map<String, Object> post = new HashedMap();
    post.put(Environment.HOST_NAME, "http-location");
    post.put(Environment.APP_NAME, "test");
    post.put(Environment.ENV_NAME, "test");

    target.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE);
    
    Response resp = target.path("/q/env/http/hosts.properties").request(MediaType.APPLICATION_JSON)
        .accept(MediaType.WILDCARD).post(Entity.entity(post, MediaType.APPLICATION_JSON));
    
    Assert.assertEquals(303, resp.getStatus());
    Assert.assertEquals("http://localhost:8891/configrd/v1/env/http/default.properties", resp.getLocation().toString());

  }
  
  @Test
  public void testResolveFileLocationViaHost() throws Exception {

    Map<String, Object> post = new HashedMap();
    post.put(Environment.HOST_NAME, "file-location");
    post.put(Environment.APP_NAME, "test");
    post.put(Environment.ENV_NAME, "test");

    target.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE);
    
    Response resp = target.path("/q/env/http/hosts.properties").request(MediaType.APPLICATION_JSON)
        .accept(MediaType.WILDCARD).post(Entity.entity(post, MediaType.APPLICATION_JSON));
    
    Assert.assertEquals(303, resp.getStatus());
    Assert.assertEquals("file:src/main/resources/env/http/default.properties", resp.getLocation().toString());

  }
  
  @Test
  public void testResolveClasspathLocationViaHost() throws Exception {

    Map<String, Object> post = new HashedMap();
    post.put(Environment.HOST_NAME, "classpath-location");
    post.put(Environment.APP_NAME, "test");
    post.put(Environment.ENV_NAME, "test");

    target.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE);
    
    Response resp = target.path("/q/env/http/hosts.properties").request(MediaType.APPLICATION_JSON)
        .accept(MediaType.WILDCARD).post(Entity.entity(post, MediaType.APPLICATION_JSON));
    
    Assert.assertEquals(303, resp.getStatus());
    Assert.assertEquals("classpath:env/http/default.properties", resp.getLocation().toString());

  }
  
  @Test
  public void testResolveDefaultLocationViaHost() throws Exception {

    Map<String, Object> post = new HashedMap();
    post.put(Environment.HOST_NAME, "unknown-location");
    post.put(Environment.APP_NAME, "test");
    post.put(Environment.ENV_NAME, "test");

    target.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE);
    
    Response resp = target.path("/q/env/http/hosts.properties").request(MediaType.APPLICATION_JSON)
        .accept(MediaType.WILDCARD).post(Entity.entity(post, MediaType.APPLICATION_JSON));
    
    Assert.assertEquals(303, resp.getStatus());
    Assert.assertEquals("http://localhost:8891/configrd/v1/env/http/default.properties", resp.getLocation().toString());

  }

}
