package com.appcrossings.config;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public class JerseyConfig extends ResourceConfig {

  public JerseyConfig() {
    register(AppConfigServiceImpl.class);
    register(ExceptionMapper.class);
    register(JacksonJsonProvider.class);
  }

}
