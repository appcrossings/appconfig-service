package com.appcrossings.config;

import javax.ws.rs.core.Response;
import com.google.common.base.Throwables;

public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Throwable> {

  @Override
  public Response toResponse(Throwable arg0) {
    
    Throwable t = Throwables.getRootCause(arg0);
        
    return Response.ok().build();
  }

}
