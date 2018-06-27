package com.appcrossings.config;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.appcrossings.config.source.ConfigSource;
import com.appcrossings.config.util.UriUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AppConfigServiceImpl implements AppConfigService {

  private static final Logger logger = LoggerFactory.getLogger(AppConfigService.class);

  private final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  private ConfigSourceResolver resolver;

  private final DefaultResourceLoader loader = new DefaultResourceLoader();

  @Override
  public Response getTextProperties(
      @DefaultValue(ConfigSourceResolver.DEFAULT_REPO_NAME) String repo, String path,
      @DefaultValue("true") Boolean traverse, UriInfo info) {

    logger.debug("Requested path" + path);

    Response resp = Response.status(Status.NOT_FOUND).build();
    final String[] named = UriUtil.getFragments(info.getRequestUri());

    Properties props = getProperties(repo, path, traverse, named);

    if (props.size() > 0) {

      try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {

        props.store(output, "");
        resp = Response.status(Status.OK).entity(output.toByteArray()).encoding("UTF-8").build();

      } catch (FileNotFoundException not) {

        logger.info(not.getMessage());

      } catch (IOException io) {

        logger.error(io.getMessage());
        resp = Response.status(Status.INTERNAL_SERVER_ERROR).build();

      }
    }

    return resp;

  }

  protected Properties getProperties(String repo, String path, boolean traverse, String[] named) {

    Optional<ConfigSource> source = resolver.findByRepoName(repo);
    Properties props = new Properties();

    if (source.isPresent()) {

      if (!traverse) {

        props = source.get().getRaw(path);

      } else {

        props = source.get().get(path, named);

      }
    }

    return props;
  }

  @Override
  public Response getHealth() {

    CacheControl control = new CacheControl();
    control.setNoCache(true);
    control.setMustRevalidate(true);

    try {

      Resource r = loader.getResource("health.properties");
      Properties props = new Properties();
      props.load(r.getInputStream());

      Map<Object, Object> health = new HashMap<Object, Object>();
      health.putAll(props);

      return Response.ok(mapper.writeValueAsString(health)).cacheControl(control).build();

    } catch (Exception e) {

      return Response.status(Status.INTERNAL_SERVER_ERROR).cacheControl(control).build();

    }
  }

  @Override
  public Response createNew(String repo, String path) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response discover(@DefaultValue(ConfigSourceResolver.DEFAULT_REPO_NAME) final String repo,
      final String path, HashMap<String, Object> envProps) {

    Response resp = Response.status(Status.NOT_FOUND).build();

    // Optional<ConfigSource> configSource = resolver.findByRepoName(repo);
    //
    // if (configSource.isPresent()) {
    //
    // Properties hosts = configSource.get().getRaw(path);
    //
    // if (hosts != null && !hosts.isEmpty()) {
    // Optional<URI> startPath =
    // lookupStrategy.discover(hosts, PropertiesProcessor.asProperties(envProps));
    //
    // if (startPath.isPresent()) {
    // logger.debug("Found config " + startPath.get());
    // resp = Response.seeOther(URI.create(startPath.get())).build();
    // }
    // }
    //
    // } else {
    // logger.error("Repo configuration missing configuration.");
    // }

    return resp;

  }

  @Override
  public Response update(String repo, String path, String eTag) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
  @Override
  public Response patch(String repo, String path, String eTag) {
    // TODO Auto-generated method stub
    return null;
  }
  */

  @Override
  public Response discoveryOptions(String repo, String path, HashMap<String, Object> envProps) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response getJsonProperties(String repo, String path, Boolean traverse, UriInfo info) {

    logger.debug("Requested path" + path);

    Response resp = Response.status(Status.NOT_FOUND).build();
    final String[] named = UriUtil.getFragments(info.getRequestUri());

    Properties props = getProperties(repo, path, traverse, named);

    if (props.size() > 0) {

      resp = Response.status(Status.OK).entity(props).encoding("UTF-8").build();

    }

    return resp;

  }

  @Override
  public Response getYamlProperties(String repo, String path, Boolean traverse, UriInfo info) {

    logger.debug("Requested path" + path);

    Response resp = Response.status(Status.NOT_FOUND).build();
    final String[] named = UriUtil.getFragments(info.getRequestUri());

    Properties props = getProperties(repo, path, traverse, named);

    if (props.size() > 0) {

      resp = Response.status(Status.OK).entity(props).encoding("UTF-8").build();

    }

    return resp;
  }
}
