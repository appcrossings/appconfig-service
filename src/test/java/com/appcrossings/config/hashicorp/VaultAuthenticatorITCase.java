package com.appcrossings.config.hashicorp;

import java.net.URI;
import java.util.HashMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import com.appcrossings.config.exception.AuthenticationException;

public class VaultAuthenticatorITCase {

  VaultAuthenticator auth = new VaultAuthenticator();

  private final URI uri = URI.create("http://localhost:8200/v1/secret");
  private String token;
  
  @After
  public void cleanup() {
    this.token = null;
  }

  @Test
  public void testGetTokenUserPass() {

    HashicorpRepoDef def = new HashicorpRepoDef("VaultAuthenticatorITCase", new HashMap<>());
    def.setUsername("test");
    def.setPassword("password");

    token = auth.authenticate(uri, def, "userpass");
    Assert.assertNotNull(token);

    AuthResponse resp = auth.renewToken(uri, token);
    token = resp.auth.client_token;
    
    Assert.assertNotNull(token);
    
  }
  
  @Test(expected = AuthenticationException.class)
  public void testFailRenewUnknownToken() {

    AuthResponse resp = auth.renewToken(uri, "SKJODJFDKSNLKf");
    
  }



}
