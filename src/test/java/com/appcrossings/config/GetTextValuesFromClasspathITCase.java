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


public class GetTextValuesFromClasspathITCase extends AbstractITCase {

  private static final Logger logger =
      LoggerFactory.getLogger(GetTextValuesFromClasspathITCase.class);

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
  public void testGetValuesFromDefaultRepo() throws Exception {

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
  public void testGetValuesFromNamedRepo() throws Exception {

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
  public void testGetValuesFromNamedRepoWithPath() throws Exception {

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

  @Test
  public void testGetValuesFromDefaultRepoWithNamedProfile() throws Exception {

    Response resp = target.path("/").queryParam("p", "simple").request(MediaType.TEXT_PLAIN).get();
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

  @Test
  public void testGetValuesFromNamedRepoWithNamedProfile() throws Exception {

    Response resp = target.path("/").queryParam("repo", "default").queryParam("p", "simple")
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

  @Test
  public void testAttemptNonExistingRepo() throws Exception {

    Response resp =
        target.path("/").queryParam("repo", "dontexist").request(MediaType.TEXT_PLAIN).get();
    Assert.assertEquals(404, resp.getStatus());

    String body = resp.readEntity(String.class);

    Assert.assertTrue(body.contains("Not Found"));

  }

  @Test
  public void testReturnBasePropertiesWhenExist() throws Exception {

    Response resp = target.path("/doesnt/exist").request(MediaType.TEXT_PLAIN).get();

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
  public void testGetBasePropertiesWithoutTraverse() throws Exception {

    Response resp = target.path("/").queryParam("t", "false").request(MediaType.TEXT_PLAIN).get();

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
  public void testGetPropertiesAtPathWithoutTraverse() throws Exception {

    Response resp =
        target.path("/env/dev/simple").queryParam("t", "false").request(MediaType.TEXT_PLAIN).get();

    Assert.assertEquals(200, resp.getStatus());

    String body = resp.readEntity(String.class);
    Properties props = new Properties();
    props.load(new StringReader(body));

    Assert.assertEquals(4, props.size());

    Assert.assertEquals("simple", props.getProperty("property.1.name"));
    Assert.assertEquals("simple", props.getProperty("property.3.name"));
    Assert.assertEquals("simple-simple", props.getProperty("property.4.name"));
    Assert.assertEquals("bonus2", props.getProperty("bonus.1.property"));

  }

  /**
   * Not supported at this time, will ignore p parameter
   * @throws Exception
   */
  @Test
  public void testGetPropertiesWithoutTraverseAndNamedProfileNotSupported() throws Exception {

    Response resp = target.path("/").queryParam("repo", "default")
        .queryParam("p", "simple").queryParam("t", "false").request(MediaType.TEXT_PLAIN).get();

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
  public void testTraversePropertiesToARepoRootOtherThanBasePath() throws Exception {

    Response resp = target.path("/dev/simple").queryParam("repo", "classpath-env").request(MediaType.TEXT_PLAIN).get();

    Assert.assertEquals(200, resp.getStatus());

    String body = resp.readEntity(String.class);
    Properties props = new Properties();
    props.load(new StringReader(body));

    Assert.assertEquals(5, props.size());

    Assert.assertEquals("simple", props.getProperty("property.1.name"));
    Assert.assertEquals("simple", props.getProperty("property.3.name"));
    Assert.assertEquals("simple-simple", props.getProperty("property.4.name"));
    Assert.assertEquals("bonus2", props.getProperty("bonus.1.property"));

    Assert.assertEquals("value2", props.getProperty("property.2.name"));

  }
  
  @Test
  public void testTraversePropertiesToARepoRootOtherThanBasePathWithNamedProfile() throws Exception {

    Response resp = target.path("/").queryParam("repo", "classpath-env")
        .queryParam("p", "simple").request(MediaType.TEXT_PLAIN).get();

    Assert.assertEquals(200, resp.getStatus());

    String body = resp.readEntity(String.class);
    Properties props = new Properties();
    props.load(new StringReader(body));

    Assert.assertEquals(5, props.size());

    Assert.assertEquals("simple", props.getProperty("property.1.name"));
    Assert.assertEquals("simple", props.getProperty("property.3.name"));
    Assert.assertEquals("simple-simple", props.getProperty("property.4.name"));
    Assert.assertEquals("bonus2", props.getProperty("bonus.1.property"));

    Assert.assertEquals("value2", props.getProperty("property.2.name"));

  }

  @Test
  public void testGetPropertiesAtRepoRootOtherThanBasePath() throws Exception {

    //classpath:env doen't have any properties defined
    Response resp = target.path("/").queryParam("repo", "classpath-env").request(MediaType.TEXT_PLAIN).get();

    Assert.assertEquals(404, resp.getStatus());

  }
  
  @Test
  public void testGetPropertiesFromJsonFile() throws Exception {

    Response resp = target.path("/env/dev/json").queryParam("repo", "appx-j").request(MediaType.TEXT_PLAIN).get();

    Assert.assertEquals(200, resp.getStatus());

    String body = resp.readEntity(String.class);
    Properties props = new Properties();
    props.load(new StringReader(body));

    Assert.assertEquals(14, props.size());

    Assert.assertEquals("classpath", props.getProperty("property.5.name"));
    Assert.assertEquals("DEBUG", props.getProperty("log.root.level"));
    Assert.assertEquals("ENC(NvuRfrVnqL8yDunzmutaCa6imIzh6QFL)",
        props.getProperty("property.6.name"));

    Assert.assertEquals("simple", props.getProperty("property.1.name"));
    Assert.assertNull(props.getProperty("property.3.name"));
    Assert.assertEquals("simple", props.getProperty("property.3"));
    Assert.assertEquals("simple-${property.3.name}", props.getProperty("property.4.name"));
    Assert.assertEquals("bonus2", props.getProperty("bonus.1.property"));

    Assert.assertEquals("value2", props.getProperty("property.2.name"));
    
    Assert.assertEquals("value1", props.getProperty("array.named[0]"));
    Assert.assertEquals("value2", props.getProperty("array.named[1]"));
    Assert.assertEquals("value3", props.getProperty("array.named[2]"));
    Assert.assertEquals("true", props.getProperty("array.named2.value4.sub"));
    Assert.assertEquals("5", props.getProperty("array.named2.value5.sub"));
    Assert.assertEquals("value", props.getProperty("array.named2.value6.sub"));

  }
  
  @Test
  public void testGetPropertiesFromYamlFile() throws Exception {

    Response resp = target.path("/env/dev/yaml").queryParam("repo", "appx-y").request(MediaType.TEXT_PLAIN).get();

    Assert.assertEquals(200, resp.getStatus());

    String body = resp.readEntity(String.class);
    Properties props = new Properties();
    props.load(new StringReader(body));

    Assert.assertEquals(14, props.size());

    Assert.assertEquals("classpath", props.getProperty("property.5.name"));
    Assert.assertEquals("DEBUG", props.getProperty("log.root.level"));
    Assert.assertEquals("ENC(NvuRfrVnqL8yDunzmutaCa6imIzh6QFL)",
        props.getProperty("property.6.name"));

    Assert.assertEquals("simple", props.getProperty("property.1.name"));
    Assert.assertNull(props.getProperty("property.3.name"));
    Assert.assertEquals("simple", props.getProperty("property.3"));
    Assert.assertEquals("simple-${property.3.name}", props.getProperty("property.4.name"));
    Assert.assertEquals("bonus2", props.getProperty("bonus.1.property"));

    Assert.assertEquals("value2", props.getProperty("property.2.name"));
    
    Assert.assertEquals("value1", props.getProperty("array.named[0]"));
    Assert.assertEquals("value2", props.getProperty("array.named[1]"));
    Assert.assertEquals("value3", props.getProperty("array.named[2]"));
    Assert.assertEquals("true", props.getProperty("array.named2.value4.sub"));
    Assert.assertEquals("5", props.getProperty("array.named2.value5.sub"));
    Assert.assertEquals("value", props.getProperty("array.named2.value6.sub"));

  }

}
