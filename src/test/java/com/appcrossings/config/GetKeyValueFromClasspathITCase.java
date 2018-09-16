package com.appcrossings.config;

import java.io.StringReader;
import java.util.Properties;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;;


public class GetKeyValueFromClasspathITCase extends AbstractITCase {

  private static final Logger logger =
      LoggerFactory.getLogger(GetKeyValueFromClasspathITCase.class);

  @BeforeClass
  public static void setup() throws Throwable {

    System.setProperty(ConfigSourceResolver.CONFIGRD_SYSTEM_PROPERTY, "classpath:test-repos.yaml");
    AbstractITCase.setup();

  }

  @Before
  @Override
  public void init() throws Exception {
    super.init();
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

    Assert.assertEquals("classpath", props.getProperty("property.5.name"));
    Assert.assertEquals("DEBUG", props.getProperty("log.root.level"));
    Assert.assertEquals("ENC(NvuRfrVnqL8yDunzmutaCa6imIzh6QFL)",
        props.getProperty("property.6.name"));
  }

  @Test
  public void testGetPropertiesFromNamedRepo() throws Exception {

    Response resp =
        target.path("/").queryParam("repo", "classpath").request(MediaType.TEXT_PLAIN).get();
    Assert.assertEquals(200, resp.getStatus());

    String body = resp.readEntity(String.class);
    Properties props = new Properties();
    props.load(new StringReader(body));

    Assert.assertEquals(3, props.size());

    Assert.assertEquals("classpath", props.getProperty("property.5.name"));
    Assert.assertEquals("DEBUG", props.getProperty("log.root.level"));
    Assert.assertEquals("ENC(NvuRfrVnqL8yDunzmutaCa6imIzh6QFL)",
        props.getProperty("property.6.name"));
  }

  @Test
  public void testGetPropertiesFromNamedRepoWithPath() throws Exception {

    Response resp = target.path("/env/dev/simple").queryParam("repo", "classpath")
        .request(MediaType.TEXT_PLAIN).get();
    Assert.assertEquals(200, resp.getStatus());

    String body = resp.readEntity(String.class);
    Properties props = new Properties();
    props.load(new StringReader(body));

    Assert.assertEquals(8, props.size());

    Assert.assertEquals("classpath", props.getProperty("property.5.name"));
    Assert.assertEquals("DEBUG", props.getProperty("log.root.level"));
    Assert.assertEquals("ENC(NvuRfrVnqL8yDunzmutaCa6imIzh6QFL)",
        props.getProperty("property.6.name"));

    Assert.assertEquals("simple", props.getProperty("property.1.name"));
    Assert.assertEquals("simple", props.getProperty("property.3.name"));
    Assert.assertEquals("simple-simple", props.getProperty("property.4.name"));
    Assert.assertEquals("bonus2", props.getProperty("bonus.1.property"));
    
    Assert.assertEquals("value2", props.getProperty("property.2.name"));
  }

}
