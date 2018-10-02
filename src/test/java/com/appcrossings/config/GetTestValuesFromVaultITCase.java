package com.appcrossings.config;

import java.io.StringReader;
import java.util.Properties;
import javax.ws.rs.core.MediaType;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetTestValuesFromVaultITCase extends AbstractTestSuiteITCase {

  private static final Logger logger = LoggerFactory.getLogger(GetTestValuesFromVaultITCase.class);
  
  
  
  @BeforeClass
  public static void setup() throws Throwable {

    System.setProperty(ConfigSourceResolver.CONFIGRD_CONFIG, "classpath:vault-repos.yaml");
    TestConfigServer.serverStart();
    logger.info("Running " + GetTestValuesFromVaultITCase.class.getName());
  }

  @AfterClass
  public static void teardown() throws Exception {
    TestConfigServer.serverStop();
  }

  @Before
  @Override
  public void init() throws Exception {
    super.init();

    do {
      Thread.sleep(100);
    } while (System.getProperty(
        ConfigSourceResolver.CONFIGRD_CONFIG) != "classpath:vault-repos.yaml");

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
