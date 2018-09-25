package com.appcrossings.config;

import java.util.Map;
import java.util.Properties;
import javax.ws.rs.core.MediaType;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.processor.PropertiesProcessor;
import com.jsoniter.JsonIterator;
import com.jsoniter.spi.TypeLiteral;


public class GetJsonFromClasspathITCase extends AbstractTestSuiteITCase {

  private static final Logger logger = LoggerFactory.getLogger(GetJsonFromClasspathITCase.class);

  @BeforeClass
  public static void setup() throws Throwable {

    System.setProperty(ConfigSourceResolver.CONFIGRD_SYSTEM_PROPERTY,
        "classpath:classpath-repos.yaml");
    AbstractITCase.setup();

  }

  @AfterClass
  public static void teardown() throws Exception {
    AbstractITCase.teardown();
  }

  @Before
  @Override
  public void init() throws Exception {
    super.init();
    target = client.target("http://localhost:8891/configrd/v1/");
    content = MediaType.TEXT_PLAIN_TYPE;
    accept = MediaType.APPLICATION_JSON_TYPE;
  }

  @Override
  public Properties convert(String body) throws Exception {

    Map<String, Object> map = JsonIterator.deserialize(body, new TypeLiteral<Map<String, Object>>(){});
    return PropertiesProcessor.asProperties(map);
  }

}
