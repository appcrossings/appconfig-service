package com.appcrossings.config;

import java.io.StringReader;
import java.util.Properties;
import javax.ws.rs.core.MediaType;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetTestValuesFromHttpITCase extends AbstractTestSuiteITCase {

  private static final Logger logger =
      LoggerFactory.getLogger(GetTestValuesFromHttpITCase.class);

  @BeforeClass
  public static void setup() throws Throwable {

    System.setProperty(ConfigSourceResolver.CONFIGRD_SYSTEM_PROPERTY,
        "http://config.appcrossings.net/http-repos.yaml");
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
    accept = MediaType.TEXT_PLAIN_TYPE;
  }

  @Override
  public Properties convert(String body) throws Exception {
    Properties props = new Properties();
    props.load(new StringReader(body));
    return props;
  }

}
