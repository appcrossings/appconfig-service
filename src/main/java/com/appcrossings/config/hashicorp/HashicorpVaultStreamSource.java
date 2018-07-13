package com.appcrossings.config.hashicorp;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.source.PropertyPacket;
import com.appcrossings.config.source.RepoDef;
import com.appcrossings.config.source.StreamSource;
import com.appcrossings.config.util.FNV;
import com.appcrossings.config.util.StringUtils;
import com.appcrossings.config.util.URIBuilder;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.AuthResponse;
import com.bettercloud.vault.response.LogicalResponse;
import com.google.common.base.Throwables;

public class HashicorpVaultStreamSource implements StreamSource {

  private final static Logger logger = LoggerFactory.getLogger(HashicorpVaultStreamSource.class);

  private Vault vault;
  private final HashicorpRepoDef repoDef;
  private VaultConfig config;
  private final URIBuilder builder;
  private AuthResponse authResp;

  public static final String HASHICORP_VAULT = "hashicorp_vault";

  public HashicorpVaultStreamSource(HashicorpRepoDef repoDef) {

    this.repoDef = repoDef;
    this.builder = URIBuilder.create(repoDef.getUri());

    try {

      if (StringUtils.hasText(repoDef.getToken())) {
        config = new VaultConfig().address(repoDef.getUri()).token(repoDef.getToken()).build();
      } else {
        config = new VaultConfig().address(repoDef.getUri());
      }

    } catch (VaultException e) {
      Throwables.propagateIfPossible(e);
    }
  }

  public boolean put(String path, String etag, Map<String, Object> vals) {

    boolean success = false;

    if (etag != null) {

      Optional<PropertyPacket> packet = stream(path);

      // Apply optimistic lock
      if (packet.isPresent() && etag.equals(packet.get().getETag())) {

        try {

          LogicalResponse resp = vault.withRetries(5, 1000).logical().write(path, vals);

          if (resp.getRestResponse().getStatus() == 200)
            success = true;

        } catch (VaultException e) {
          logger.error(e.getMessage(), e);
          // TODO: handle exception
        }

      }
    } else {

      try {

        LogicalResponse resp = vault.withRetries(5, 1000).logical().write(path, vals);

        if (resp.getRestResponse().getStatus() == 200)
          success = true;

      } catch (VaultException e) {
        logger.error(e.getMessage(), e);
        // TODO: handle exception
      }
    }

    return success;

  }

  @Override
  public Optional<PropertyPacket> stream(String path) {

    Optional<PropertyPacket> packet = Optional.empty();

    try {

      final LogicalResponse response = vault.withRetries(5, 1000).logical().read(path);

      if (!response.getData().isEmpty()) {
        Map<String, String> vals = response.getData();

        PropertyPacket p = new PropertyPacket(prototypeURI(path));
        StringBuilder builder = new StringBuilder();
        vals.values().stream().sorted().forEach(s -> {
          builder.append(s);
        });
        p.setETag(FNV.fnv1_64(builder.toString().getBytes()).toString());
        p.putAll(vals);
        packet = Optional.of(p);
      }

    } catch (Exception e) {
      logger.debug(e.getMessage(), e);
      // nothing else
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
    vault = new Vault(config);

    if (StringUtils.hasText(repoDef.getPassword())) {
      try {
        authResp = vault.auth().loginByUserPass(repoDef.getPassword(), repoDef.getUsername());
        repoDef.setToken(authResp.getAuthClientToken());
      } catch (VaultException e) {
        com.google.common.base.Throwables.propagateIfPossible(e);
      }
    }

  }

  @Override
  public void close() {
    // nothing
  }

}
