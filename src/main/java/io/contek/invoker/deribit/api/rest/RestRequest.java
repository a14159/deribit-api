package io.contek.invoker.deribit.api.rest;

import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.actor.http.AnyHttpException;
import io.contek.invoker.commons.rest.*;
import io.contek.invoker.security.ICredential;
import is.fm.util.BaseEncoding;
import is.fm.util.Encoder;
import is.fm.util.Escaper;
import is.fm.util.Escapers;

import javax.annotation.concurrent.NotThreadSafe;
import java.time.Clock;
import java.util.Map;
import java.util.Random;

import static io.contek.invoker.commons.rest.RestMediaType.JSON;

@NotThreadSafe
public abstract class RestRequest<R> extends BaseRestRequest<R> {

  private static final Encoder ENCODING = BaseEncoding.base32();
  private static final Escaper urlPathSegmentEscaper = Escapers.urlPathSegmentEscaper();
  private final RestContext context;
  private final Clock clock;

  protected RestRequest(IActor actor, RestContext context) {
    super(actor);
    this.context = context;
    clock = actor.getClock();
  }

  protected abstract RestMethod getMethod();

  protected abstract String getEndpointPath();

  protected abstract RestParams getParams();

  @Override
  protected final RestCall createCall(ICredential credential) {
    RestMethod method = getMethod();
    switch (method) {
      case GET, DELETE -> {
        String paramsString = buildParamsString();
        return RestCall.newBuilder()
            .setUrl(buildUrlString(paramsString))
            .setMethod(method)
            .setHeaders(generateHeaders(method, paramsString, "", credential))
            .build();
      }
      case POST, PUT -> {
        RestMediaBody body = JSON.createBody(getParams());
        return RestCall.newBuilder()
            .setUrl(buildUrlString(""))
            .setMethod(method)
            .setHeaders(generateHeaders(method, "", body.getStringValue(), credential))
            .setBody(body)
            .build();
      }
      default -> throw new IllegalStateException(getMethod().name());
    }
  }

  @Override
  protected final void checkResult(R result, RestResponse response) throws AnyHttpException {}

  private Map<String, String> generateHeaders(
      RestMethod method, String paramsString, String bodyString, ICredential credential) {

    if (credential.isAnonymous()) {
      return Map.of();
    }
    String clientId = credential.getApiKeyId();
    String timestamp = Long.toString(clock.millis());
    String nonce = generateNounce();
    String uri = getEndpointPath() + paramsString;
    String payload =
        timestamp + "\n" + nonce + "\n" + method + "\n" + uri + "\n" + bodyString + "\n";
    String signature = credential.sign(payload);
    String authorizationValue =
        String.format("deri-hmac-sha256 id=%s,ts=%s,sig=%s,nonce=%s", clientId, timestamp, signature, nonce);
    return Map.of("Authorization", authorizationValue);
  }

  private static final Random rnd = new Random();
  private static final byte[] randomBytes = new byte[8];

  private static String generateNounce() {
    rnd.nextBytes(randomBytes);
    return ENCODING.encode(randomBytes);
  }

  private String buildParamsString() {
    RestParams params = getParams();
    if (params.isEmpty()) {
      return "";
    }
    return "?" + params.getQueryString(urlPathSegmentEscaper);
  }

  private String buildUrlString(String paramsString) {
    return context.getBaseUrl() + getEndpointPath() + paramsString;
  }
}
