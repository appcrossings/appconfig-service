package com.appcrossings.config.hashicorp;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.appcrossings.config.source.DefaultConfigSource;
import com.appcrossings.config.source.PropertyPacket;
import com.appcrossings.config.source.StreamSource;
import com.appcrossings.config.source.WritableConfigSource;

public class HashicorpVaultConfigSource extends DefaultConfigSource
    implements WritableConfigSource {

  public HashicorpVaultConfigSource(HashicorpVaultStreamSource source, Map<String, Object> values) {
    super(source, values);
  }

  @Override
  public Map<String, Object> getRaw(String path) {

    Optional<PropertyPacket> packet = getStreamSource().stream(path);

    if (!packet.isPresent())
      return new HashMap<>();

    return packet.get();
  }

  @Override
  public boolean isCompatible(StreamSource source) {
    return source instanceof HashicorpVaultStreamSource;
  }

  @Override
  public boolean put(String path, Map<String, Object> props) {

    HashicorpVaultStreamSource source = (HashicorpVaultStreamSource) getStreamSource();
    boolean success = source.put(path, null, props);
    return success;
  }

  @Override
  public boolean patch(String path, String etag, String key, Object value) {

    Map<String, Object> existing = new HashMap<>();

    Optional<PropertyPacket> packet = getStreamSource().stream(path);

    if (packet.isPresent())
      existing = packet.get();

    existing.put(key, value);

    HashicorpVaultStreamSource source = (HashicorpVaultStreamSource) getStreamSource();
    boolean success = source.put(path, packet.get().getETag(), existing);
    return success;

  }

}
