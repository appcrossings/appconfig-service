package com.appcrossings.config;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.appcrossings.config.source.ConfigSource;
import com.appcrossings.config.strategy.DefaultConfigLookupStrategy;
import com.appcrossings.config.util.PropertiesProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.util.StatusCodes;

@Service
public class AppConfigServiceImpl implements AppConfigService {

  private static final Logger logger = LoggerFactory.getLogger(AppConfigService.class);

  private final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  private ConfigSourceResolver resolver;

  private ConfigLookupStrategy lookupStrategy = new DefaultConfigLookupStrategy();

  private final DefaultResourceLoader loader = new DefaultResourceLoader();

  @Override
  public Response resolveProperties(String repo, String path, Boolean traverse) {

    logger.debug("Requested path" + path);

    Optional<ConfigSource> source = resolver.resolveByUri(path);
    Properties props = new Properties();
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {

      if (traverse == null || !traverse) {

        props = source.get().fetchConfig(path);
        props.store(output, "");

      } else {

        // TODO: Make default properties file configurable
        props = source.get().traverseConfigs(path);

      }

      props.store(output, "");
      return Response.status(Status.OK).entity(output.toByteArray()).build();

    } catch (FileNotFoundException not) {

      logger.info(not.getMessage());
      return Response.status(Status.NOT_FOUND).build();

    } catch (IOException io) {

      logger.error(io.getMessage());
      return Response.status(Status.INTERNAL_SERVER_ERROR).build();

    }

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
  public Response writeProperty(String repo, String path) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response resoveHost(String repo, String path, HashMap<String, Object> envProps) {

    logger.info("Loading hosts file at " + path + " in repo: " + repo);

    Optional<ConfigSource> configSource = resolver.resolveByRepoName(repo);

    if (!configSource.isPresent()) {
      logger.warn("No hosts file found at " + path + " with repo: " + repo);

      logger.info("...attempting from file system");
      configSource = resolver.resolveByUri("file:" + repo + "/" + path);
    }

    if (configSource.isPresent()) {
      Properties hosts = configSource.get().fetchConfig("file:" + repo + "/" + path);

      if (hosts == null || hosts.isEmpty()) {

        logger.warn("No hosts entries found at file:" + repo + "/" + path);

        logger.info("...attempting from classpath");
        configSource = resolver.resolveByUri("classpath:" + repo + "/" + path);

        if (configSource.isPresent()) {
          hosts = configSource.get().fetchConfig("classpath:" + repo + "/" + path);
        }

        if (hosts == null || hosts.isEmpty()) {
          return Response.status(Status.NOT_FOUND).build();
        }
      }

      Optional<String> startPath =
          lookupStrategy.lookupConfigPath(hosts, PropertiesProcessor.asProperties(envProps));

      if (startPath.isPresent()) {
        return Response.seeOther(URI.create(startPath.get())).build();
      }
    }

    return Response.status(StatusCodes.NOT_FOUND).build();

  }


}
