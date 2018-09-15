package com.appcrossings.config.hashicorp;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.exception.AuthenticationException;
import com.appcrossings.config.hashicorp.util.VaultUtil;
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

  public String authenticate(URI uri, HashicorpRepoDef values) {

    AuthResponse auth = null;

    if (values.getAuthMethod() == null)
      throw new AuthenticationException("No authentication method specified");

    if (StringUtils.hasText(client_token))
      return client_token;

    switch (values.getAuthMethod().toLowerCase()) {
      case "userpass":
        auth = loginByUserPass(uri, values.getUsername(), values.getPassword());

        break;

      default:
        break;
    }

    if (auth == null || auth.auth == null || !StringUtils.hasText(auth.auth.client_token)) {

      throw new AuthenticationException("Unable to authenticate via method: "
          + values.getAuthMethod() + "No client token returned");

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

    String url = VaultUtil.extractBaseURL(uri) + "/v1/auth/userpass/login/" + username;

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

    String url = VaultUtil.extractBaseURL(uri) + "/v1/auth/token/renew-self";

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



}
