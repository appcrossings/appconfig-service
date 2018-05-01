package com.appcrossings.config;


import java.util.HashMap;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
      @NotNull @PathParam("path") String path,
      @DefaultValue("true") @QueryParam("t") Boolean traverse);

  @POST
  @Path("/{repo}/{path:.+}")
  @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, "application/x-yam"})
  @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, "application/x-yam"})
  Response writeProperty(@NotNull @PathParam("repo") String repo,
      @NotNull @PathParam("path") String path);

  @POST
  @Path("/q/{repo}/{path:.+}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.WILDCARD)
  Response resoveHost(@NotNull @PathParam("repo") String repo,
      @NotNull @PathParam("path") String path, @NotNull HashMap<String, Object> envProps);

  @GET
  @Path("/health")
  Response getHealth();

}
