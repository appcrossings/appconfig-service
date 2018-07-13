package com.appcrossings.config.hashicorp;

import java.net.URI;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;

public class TestVaultAuthenticator {

  VaultAuthenticator auth = new VaultAuthenticator();

  @Test
  public void testExtractMount() {

    HashicorpRepoDef value = new HashicorpRepoDef("name", new HashMap<>());
    value.setPassword("testUser");
    value.setUsername("passWord");

    URI uri = URI.create("http://localhost:8200/v1/mount");
    Assert.assertEquals("mount", auth.extractMount(uri));

    uri = URI.create("http://localhost:8200/something/v1/mount/other");
    Assert.assertEquals("mount", auth.extractMount(uri));
  }

  @Test
  public void testBuildURL() {

    URI uri = URI.create("http://localhost:8200/v1/mount");
    Assert.assertEquals("http://localhost:8200/v1", auth.extractBaseURL(uri));

    uri = URI.create("http://localhost/something/v1/mount/other");
    Assert.assertEquals("http://localhost/something/v1", auth.extractBaseURL(uri));

  }

}
