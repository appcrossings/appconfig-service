package com.appcrossings.config.hashicorp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import com.appcrossings.config.source.DefaultRepoDef;
import com.appcrossings.config.source.SecuredRepo;
import com.appcrossings.config.util.URIBuilder;
import com.appcrossings.config.util.UriUtil;
import com.google.common.base.Throwables;

@SuppressWarnings("serial")
public class HashicorpRepoDef extends DefaultRepoDef implements SecuredRepo {

  public static final String AUTH_METHOD_FIELD ="authMethod";
  
  public enum AuthMethod {

    IAM, UserPass;

  }
  private String authMethod;
  private String keystoreFile;
  private String password;
  private String passwordFile;
  private String token;
  private String username;

  public HashicorpRepoDef(String name, Map<String, Object> values) {

    super(name);

    try {
      if (values != null && !values.isEmpty())
        BeanUtils.populate(this, values);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  public String getAuthMethod() {
    return authMethod;
  }

  public String getKeystoreFile() {
    return keystoreFile;
  }

  public String getPassword() {
    return password;
  }

  public String getPasswordFile() {
    return passwordFile;
  }

  public String getToken() {
    return token;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public void setAuthMethod(String authMethod) {
    this.authMethod = authMethod;
  }

  public void setKeystoreFile(String keystoreFile) {
    this.keystoreFile = keystoreFile;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setPasswordFile(String passwordFile) {
    this.passwordFile = passwordFile;

    File passfile = new File(passwordFile);

    try {

      FileReader reader = new FileReader(passfile);
      BufferedReader buff = new BufferedReader(reader);
      setPassword(buff.readLine());

    } catch (IOException e) {
      Throwables.propagateIfPossible(e);
    }

  }

  public void setToken(String token) {
    this.token = token;
  }


  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public URI toURI() {
    URIBuilder builder = URIBuilder.create(URI.create(getUri()));
    return builder.build();
  }

  @Override
  public String[] valid() {
    String[] err = new String[] {};

    URI uri = toURI();

    if (UriUtil.validate(uri).isAbsolute().invalid()) {
      err = new String[] {"Uri must be absolute"};
    }

    return err;
  }

}
