package com.appcrossings.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = {"com.appcrossings.config"})
@PropertySource("classpath:application.properties")
public class ApplicationContext {

  @Value("${repo.def.path:classpath:repo-defaults.yml}")
  private String repoConfig;

  public static void main(String[] args) {
    SpringApplication.run(ApplicationContext.class, args);
  }

  @Bean
  public ConfigSourceResolver buildConfigClient() {
    return new ConfigSourceResolver(repoConfig);
  }
}
