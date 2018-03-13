package com.appcrossings.config;


import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v1")
public interface AppConfigService {

  @GET
  @Path("/{repo}/{path:.+}")
  @Consumes({MediaType.WILDCARD, MediaType.TEXT_PLAIN})
  @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, "application/x-yam"})
  Response resolveProperties(@NotNull @PathParam("repo") String repo,
      @NotNull @PathParam("path") String path, @QueryParam("t") Boolean traverse);

  @GET
  @Path("/health")
  Response getHealth();

}
