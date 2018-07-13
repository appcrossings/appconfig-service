package com.appcrossings.config.hashicorp;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.exception.AuthenticationException;
import com.appcrossings.config.util.StringUtils;
import com.jsoniter.JsonIterator;
import com.jsoniter.output.JsonStream;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VaultAuthenticator {

  private static final Logger logger = LoggerFactory.getLogger(VaultAuthenticator.class);

  private final OkHttpClient client;
  private final MediaType mediaType = MediaType.parse(javax.ws.rs.core.MediaType.APPLICATION_JSON);
  private final String PASSWORD = "password";
  private final String INCREMENT = "increment";
  private final String TOKEN_HEADER = "X-Vault-Token";

  private String client_token;
  private boolean token_renewable = false;

  public VaultAuthenticator() {
    client =
        new OkHttpClient.Builder().retryOnConnectionFailure(true).followRedirects(true).build();
  }

  public String authenticate(URI uri, HashicorpRepoDef values, String method) {

    AuthResponse auth = null;

    if (StringUtils.hasText(client_token))
      return client_token;

    switch (method.toLowerCase()) {
      case "userpass":
        auth = loginByUserPass(uri, values.getUsername(), values.getPassword());

        break;

      default:
        break;
    }

    if (auth == null || auth.auth == null || !StringUtils.hasText(auth.auth.client_token)) {

      throw new RuntimeException("Unable to authenticate via method: " + method);

    } else {

      this.client_token = auth.auth.client_token;
      this.token_renewable = auth.auth.renewable;

    }

    return client_token;
  }

  protected AuthResponse loginByUserPass(URI uri, String username, String password) {

    AuthResponse auth = null;

    Map<String, Object> params = new HashMap<>();
    params.put(PASSWORD, password);

    String json = JsonStream.serialize(params);

    String url = extractBaseURL(uri) + "/auth/userpass/login/" + username;

    Request req = new Request.Builder().post(RequestBody.create(mediaType, json)).url(url).build();

    try {

      Response resp = client.newCall(req).execute();

      if (resp.isSuccessful() && resp.body().contentLength() > 0) {

        String jsonResp = new String(resp.body().bytes());
        logger.debug(jsonResp);

        auth = JsonIterator.deserialize(jsonResp, AuthResponse.class);

      } else if (resp.body().contentLength() > 0) {

        String jsonResp = new String(resp.body().bytes());
        logger.debug(jsonResp);

        auth = JsonIterator.deserialize(jsonResp, AuthResponse.class);
        throw new AuthenticationException(
            "Unable to authenticate with username/password. Response: " + auth.errors[0]);

      } else {

        throw new AuthenticationException(
            "Unable to authenticate with username/password. Response: " + resp.message());

      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return auth;

  }

  protected AuthResponse renewToken(URI uri, String token) {

    AuthResponse auth = null;

    String url = extractBaseURL(uri) + "/auth/token/renew-self";

    Request req = new Request.Builder().post(RequestBody.create(mediaType, "{}")).url(url)
        .addHeader(TOKEN_HEADER, token).build();

    try {

      Response resp = client.newCall(req).execute();

      if (resp.isSuccessful() && resp.body().contentLength() > 0) {

        String jsonResp = new String(resp.body().bytes());
        logger.debug(jsonResp);

        auth = JsonIterator.deserialize(jsonResp, AuthResponse.class);

      } else if (resp.body().contentLength() > 0) {

        String jsonResp = new String(resp.body().bytes());
        logger.debug(jsonResp);

        auth = JsonIterator.deserialize(jsonResp, AuthResponse.class);
        throw new AuthenticationException("Unable to renew token. Response: " + auth.errors[0]);

      } else {

        throw new AuthenticationException("Unable to renew token. Response: " + resp.message());

      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return auth;

  }

  public String getClient_token() {
    return client_token;
  }

  public boolean isToken_renewable() {
    return token_renewable;
  }

  protected String extractBaseURL(URI uri) {

    String path = uri.getPath();
    String[] segments = path.split("/");

    StringJoiner joiner = new StringJoiner("/");

    for (int i = 0; i < segments.length; i++) {

      if (StringUtils.hasText(segments[i])
          && (segments[i].equalsIgnoreCase("v1") || segments[i].equalsIgnoreCase("v2"))) {
        joiner.add(segments[i]);
        break;
      }

      joiner.add(segments[i]);

    }

    String baseURL = uri.getScheme() + "://" + uri.getHost();

    if (uri.getPort() > 0)
      baseURL += ":" + uri.getPort();

    baseURL += joiner.toString();

    return baseURL;

  }

  protected String extractMount(URI uri) {

    String path = uri.getPath();
    String[] segments = path.split("/");
    String mount = null;

    for (int i = 0; i < segments.length; i++) {
      if (StringUtils.hasText(segments[i])
          && (segments[i].equalsIgnoreCase("v1") || segments[i].equalsIgnoreCase("v2"))
          && segments.length > i) {
        mount = segments[i + 1];
        break;
      }
    }

    if (!StringUtils.hasText(mount))
      throw new IllegalArgumentException("Unable to find mount name in uri " + uri.toString());

    return mount;

  }

  protected int extractVersion(URI uri) {

    String path = uri.getPath();
    if (path.contains("/v1/"))
      return 1;
    else if (path.contains("/v2/"))
      return 2;

    return 1;

  }


}
