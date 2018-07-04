package com.appcrossings.config;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Component
@ApplicationPath("/configrd")
public class JerseyConfig extends ResourceConfig {

  public JerseyConfig() {
    register(AppConfigServiceImpl.class);
    register(ExceptionMapper.class);
    register(JacksonJsonProvider.class);
  }

}
