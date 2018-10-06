package com.appcrossings.config.hashicorp;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.hashicorp.util.RequestBuilder;
import com.appcrossings.config.hashicorp.util.RequestBuilder.PutKV_v2;
import com.appcrossings.config.hashicorp.util.VaultUtil;
import com.appcrossings.config.source.PropertyPacket;
import com.appcrossings.config.source.RepoDef;
import com.appcrossings.config.source.StreamSource;
import com.appcrossings.config.util.URIBuilder;
import com.google.common.base.Throwables;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HashicorpVaultStreamSource implements StreamSource {

  private final static Logger logger = LoggerFactory.getLogger(HashicorpVaultStreamSource.class);
  private final VaultAuthenticator authenticator = new VaultAuthenticator();

  private final HashicorpRepoDef repoDef;
  private final URIBuilder builder;

  private OkHttpClient client;

  public static final String HASHICORP_VAULT = "hashicorp_vault";

  public HashicorpVaultStreamSource(HashicorpRepoDef repoDef) {

    this.repoDef = repoDef;
    this.builder = URIBuilder.create(repoDef.getUri());

  }

  public boolean put(String path, PropertyPacket packet) {

    boolean success = false;

    Request put = null;

    switch (VaultUtil.detectVersion(repoDef.toURI())) {
      case 1:
        put = RequestBuilder.v1_postKv(repoDef, path, packet);
      case 2:
        put = RequestBuilder.v2_postKv(repoDef, path, packet.getETag(), packet);
    }

    logger.debug("Post to " + put.toString());

    try (Response response = client.newCall(put).execute()) {

      success = response.isSuccessful();

    } catch (IOException e) {
      logger.error(e.getMessage(), e);
      Throwables.propagateIfPossible(e);
    }

    return success;

  }

  @Override
  public Optional<PropertyPacket> stream(String path) {

    Optional<PropertyPacket> packet = Optional.empty();

    Request get = RequestBuilder.v2_getKv(repoDef, path);
    logger.debug("Get from " + get.toString());

    try (Response response = client.newCall(get).execute()) {

      if (response.isSuccessful()) {

        Any vals = JsonIterator.deserialize(response.body().bytes());

        PropertyPacket p = new PropertyPacket(prototypeURI(path));

        if (vals.get("data") != null) {
          PutKV_v2 data = vals.get("data").as(RequestBuilder.PutKV_v2.class);

          if (data.getMetadata().containsKey("version")) {
            p.setETag(String.valueOf(data.getMetadata().get("version")));
          }

          p.putAll(data.getData());
        }

        packet = Optional.of(p);
      }

    } catch (IOException e) {

      logger.error(e.getMessage(), e);
      Throwables.propagateIfPossible(e);

    }

    return packet;

  }

  @Override
  public String getSourceName() {
    return HASHICORP_VAULT;
  }

  @Override
  public RepoDef getSourceConfig() {
    return repoDef;
  }

  @Override
  public URI prototypeURI(String path) {
    return builder.build(path);
  }

  @Override
  public void init() {

    URI uri = repoDef.toURI();

    if (repoDef.getAuthMethod() != null) {
      String token = authenticator.authenticate(uri, repoDef);
      repoDef.setToken(token);
    }

    client = new OkHttpClient.Builder().build();

  }

  @Override
  public void close() {
    // nothing
  }

}
