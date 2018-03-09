package com.appcrossings.config;


import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public interface AppConfigService {

  @GET
  @Path("/config/{path:.+}")
  @Consumes({MediaType.WILDCARD, MediaType.TEXT_PLAIN})
  @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, "application/x-yam"})
  Response getRawProperties(@NotNull @PathParam("path") String path);
  
  @GET
  @Path("/raw/{path:.+}")
  @Consumes({MediaType.WILDCARD, MediaType.TEXT_PLAIN})
  @Produces(MediaType.TEXT_PLAIN)
  Response getResolvedProperties(@NotNull @PathParam("path") String path);
  
  @GET
  @Path("/health")
  Response getHealth();

}
