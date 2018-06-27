package com.appcrossings.config;

import java.io.File;
import java.io.FileWriter;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import com.appcrossings.config.util.StringUtils;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RepoLookupConfigByHostITCase.LocalAppContext.class},
    webEnvironment = WebEnvironment.DEFINED_PORT)
public class RepoLookupConfigByHostITCase extends LookupConfigByHostITCase {

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

  public String repoConfigPath;
  static File temp;

  @Override
  public void init() throws Exception {
    super.init();
    folder.create();

    temp = folder.newFile();
    FileWriter writer = new FileWriter(temp);

    FileUtils.copyDirectory(FileUtils.toFile(this.getClass().getResource("/")), folder.getRoot());

    RepoConfigBuilder builder =
        new RepoConfigBuilder().fileRepo("file-repo", "file:" + temp.getPath());
    String yaml = builder.build();
    writer.write(yaml);
    writer.flush();
    writer.close();
    target = client.target("http://localhost:8891/configrd/v1/q/file-repo/");
  }

  @After
  public void cleanup() throws Exception {
    FileUtils.forceDelete(folder.getRoot());
  }

  @SpringBootApplication
  public static class LocalAppContext {

    public static void main(String[] args) {
      SpringApplication.run(ApplicationContext.class, args);
    }

    @Bean
    public ConfigSourceResolver buildConfigClient() {

      if (StringUtils.hasText(temp.getAbsolutePath())) {
        return new ConfigSourceResolver(temp.getAbsolutePath());
      } else {
        return new ConfigSourceResolver("classpath:repo-defaults.yml");
      }
    }
  }

}
