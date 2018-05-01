package com.appcrossings.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import com.appcrossings.config.util.Environment;
import com.appcrossings.config.util.StringUtils;

@SpringBootApplication(scanBasePackages = {"com.appcrossings.config"})
@PropertySource("classpath:application.properties")
public class AppConfigServiceBoot {

  @Value("${repo.config:#{null}}")
  private String repoConfig;

  public static void main(String[] args) {
    SpringApplication.run(AppConfigServiceBoot.class, args);
  }

  @Bean
  public ConfigSourceResolver buildConfigClient() {
    
    if (StringUtils.hasText(repoConfig)) {
      return new ConfigSourceResolver(repoConfig, new Environment());
    } else {
      return new ConfigSourceResolver(new Environment());
    }
  }
}
