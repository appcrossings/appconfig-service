package com.appcrossings.config.hashicorp;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import com.appcrossings.config.source.PropertyPacket;
import com.appcrossings.config.source.RepoDef;

public class TestVaultStreamSource {

  private final HashicorpVaultConfigSourceFactory factory = new HashicorpVaultConfigSourceFactory();
  private HashicorpVaultStreamSource stream;

  @Before
  public void init() {

    Map<String, Object> vals = new HashMap<>();
    Map<String, Object> defaults = new HashMap<>();

    vals.put(RepoDef.URI_FIELD, "http://localhost:8200/secret");

    stream = factory.newStreamSource("TestVaultStreamSource", vals, defaults);
  }

  @Test
  public void testGetSingleKeyValue() {

    Optional<PropertyPacket> packet = stream.stream("env/dev/redis");

  }

}
