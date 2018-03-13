package com.appcrossings.config;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.appcrossings.config.strategy.DefaultMergeStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AppConfigServiceImpl implements AppConfigService {

  private static final Logger logger = LoggerFactory.getLogger(AppConfigService.class);

  @Value("${filesystem.root:classpath:/configs}")
  private String filesystemRoot;

  private final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  private ConfigSourceResolver resolver;

  private final DefaultResourceLoader loader = new DefaultResourceLoader();

  @Override
  public Response resolveProperties(String repo, String path, Boolean traverse) {

    logger.debug("Requested path" + path);

    ConfigSource source = resolver.resolveSource(path);
    Properties props = new Properties();
    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {

      if (traverse == null || !traverse) {

        props = source.fetchConfig(path, Config.DEFAULT_PROPERTIES_FILE_NAME);
        props.store(output, "");

      } else {

        // TODO: Make default properties file configurable
        props = source.traverseConfigs(path, Config.DEFAULT_PROPERTIES_FILE_NAME,
            new DefaultMergeStrategy());

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

      Resource r = loader.getResource(filesystemRoot + "/health.properties");
      Properties props = new Properties();
      props.load(r.getInputStream());

      Map<Object, Object> health = new HashMap<Object, Object>();
      health.putAll(props);

      return Response.ok(mapper.writeValueAsString(health)).cacheControl(control).build();

    } catch (Exception e) {

      return Response.status(Status.INTERNAL_SERVER_ERROR).cacheControl(control).build();

    }
  }
}
