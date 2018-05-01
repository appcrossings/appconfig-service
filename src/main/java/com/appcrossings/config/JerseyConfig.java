package com.appcrossings.config;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("/configrd")
public class JerseyConfig extends ResourceConfig {

  public JerseyConfig() {
    register(AppConfigServiceImpl.class);
  }

}
