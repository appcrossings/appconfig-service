package com.appcrossings.config;

import java.util.Map;
import java.util.Properties;
import javax.ws.rs.core.MediaType;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.processor.PropertiesProcessor;
import com.jsoniter.JsonIterator;
import com.jsoniter.spi.TypeLiteral;

public class GetJsonFromClasspathITCase extends AbstractTestSuiteITCase {

  private static final Logger logger = LoggerFactory.getLogger(GetJsonFromClasspathITCase.class);

  static {
    System.setProperty(ConfigSourceResolver.CONFIGRD_CONFIG, "classpath:classpath-repos.yaml");
  }

  @BeforeClass
  public static void setup() throws Throwable {

    TestConfigServer.serverStart();
    logger.info("Running " + GetJsonFromClasspathITCase.class.getName());
  }

  @AfterClass
  public static void teardown() throws Exception {
    TestConfigServer.serverStop();
  }

  @Before
  @Override
  public void init() throws Exception {
    super.init();
    target = client.target("http://localhost:8891/configrd/v1/");
    content = MediaType.TEXT_PLAIN_TYPE;
    accept = MediaType.APPLICATION_JSON_TYPE;
  }

  @Test
  @Override
  public void testGetPropertiesFromJsonFile() throws Exception {
    super.testGetPropertiesFromJsonFile();
  }

  @Test
  @Override
  public void testGetPropertiesFromYamlFile() throws Exception {
    super.testGetPropertiesFromYamlFile();
  }

  @Override
  public Properties convert(String body) throws Exception {

    Map<String, Object> map =
        JsonIterator.deserialize(body, new TypeLiteral<Map<String, Object>>() {});
    return PropertiesProcessor.asProperties(map);
  }

}
