package com.appcrossings.config;


import java.util.HashMap;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/v1")
public interface AppConfigService {

  /**
   * Create a new configuration resource at the path if one doean't already exist. Fails if already
   * exists
   * 
   * @param repo Optional repo name to specify for search otherwise default repo is used
   * @param path Path at which to create the resource
   * @return
   */
  @POST
  @Path("/{path:.+}")
  @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, "application/x-yam"})
  @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, "application/x-yam"})
  Response createNew(@QueryParam("repo") String repo, @NotNull @PathParam("path") String path);

  @POST
  @Path("/q/{path:.+}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces("application/cfgrd+v1")
  Response discover(@NotNull @QueryParam("repo") String repo,
      @NotNull @PathParam("path") String path, @NotNull HashMap<String, Object> envProps);

  /**
   * Update a value in an existing config resource. Follows optimistic locking semantics using eTag
   * as a comparison value
   * 
   * @param repo Optional repo name to specify for search otherwise default repo is used
   * @param path Path at which to create the resource
   * @return
   */
  /*
   * @PATCH
   * 
   * @Path("/{path:.+}")
   * 
   * @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, "application/x-yam"})
   * 
   * @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, "application/x-yam"}) Response
   * patch(@QueryParam("repo") String repo, @NotNull @PathParam("path") String path,
   * 
   * @NotNull @HeaderParam(HttpHeaders.ETAG) String eTag);
   */

  @OPTIONS
  @Path("/q/{path:.+}")
  @Consumes(MediaType.WILDCARD)
  @Produces("application/cfgrd+v1")
  Response discoveryOptions(@NotNull @QueryParam("repo") String repo,
      @NotNull @PathParam("path") String path, @NotNull HashMap<String, Object> envProps);

  @GET
  @Path("/health")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.WILDCARD)
  Response getHealth();

  /**
   * Traverse the path to the root of the repo and return merged properties.
   * 
   * @param repo Optional repo name to specify for search otherwise default repo is used
   * 
   * @param path The path to start traversal
   * 
   * @param traverse Flag whether or not to just fetch properties at the indicated path or to start
   *        traversal at that path. Default = traverse
   * @return
   */
  @GET
  @Path("/{path:.*}")
  @Consumes({MediaType.WILDCARD})
  @Produces({MediaType.APPLICATION_JSON})
  Response getJsonProperties(@QueryParam("repo") String repo, @PathParam("path") String path,
      @DefaultValue("true") @QueryParam("t") Boolean traverse, @Context UriInfo info);


  /**
   * Traverse the path to the root of the repo and return merged properties.
   * 
   * @param repo Optional repo name to specify for search otherwise default repo is used
   * 
   * @param path The path to start traversal
   * 
   * @param traverse Flag whether or not to just fetch properties at the indicated path or to start
   *        traversal at that path. Default = traverse
   * @return
   */
  @GET
  @Path("/{path:.*}")
  @Consumes({MediaType.WILDCARD})
  @Produces({MediaType.TEXT_PLAIN})
  Response getTextProperties(@QueryParam("repo") String repo, @PathParam("path") String path,
      @DefaultValue("true") @QueryParam("t") Boolean traverse, @Context UriInfo info);

  /**
   * Traverse the path to the root of the repo and return merged properties.
   * 
   * @param repo Optional repo name to specify for search otherwise default repo is used
   * 
   * @param path The path to start traversal
   * 
   * @param traverse Flag whether or not to just fetch properties at the indicated path or to start
   *        traversal at that path. Default = traverse
   * @return
   */
  @GET
  @Path("/{path:.*}")
  @Consumes({MediaType.WILDCARD})
  @Produces({"application/x-yam"})
  Response getYamlProperties(@QueryParam("repo") String repo, @PathParam("path") String path,
      @DefaultValue("true") @QueryParam("t") Boolean traverse, @Context UriInfo info);

  /**
   * Updates an existing resource in it's entierty. Follows optimistic locking semantics using eTag
   * as a comparison value
   * 
   * @param repo Optional repo name to specify for search otherwise default repo is used
   * @param path Path at which to create the resource
   * @return
   */
  @PUT
  @Path("/{path:.+}")
  @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, "application/x-yam"})
  @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, "application/x-yam"})
  Response update(@QueryParam("repo") String repo, @NotNull @PathParam("path") String path,
      @NotNull @HeaderParam(HttpHeaders.ETAG) String eTag);

}
