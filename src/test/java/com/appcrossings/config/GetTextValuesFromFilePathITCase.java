package com.appcrossings.config;

import java.io.File;
import java.io.StringReader;
import java.util.Properties;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetTextValuesFromFilePathITCase extends AbstractTestSuiteITCase {

  private static final Logger logger =
      LoggerFactory.getLogger(GetTextValuesFromFilePathITCase.class);

  
  @BeforeClass
  public static void setup() throws Throwable {

    System.setProperty(ConfigSourceResolver.CONFIGRD_CONFIG,
        "file:/tmp/junit/file-repos.yaml");

    System.out.println(
        "from: " + FileUtils.toFile(GetTextValuesFromFilePathITCase.class.getResource("/")));
    System.out.println("to: " + new File("/tmp/junit"));

    FileUtils.forceMkdir(new File("tmp/junit"));
    FileUtils.copyDirectory(
        FileUtils.toFile(GetTextValuesFromFilePathITCase.class.getResource("/")),
        new File("/tmp/junit"));

    TestConfigServer.serverStart();

    logger.info("Running " + GetTextValuesFromFilePathITCase.class.getName());

  }

  @AfterClass
  public static void teardown() throws Exception {
    TestConfigServer.serverStop();
    FileUtils.forceDelete(new File("/tmp/junit"));
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
