package io.contek.invoker.deribit.api;

import io.contek.invoker.commons.ApiContext;
import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.actor.IActorFactory;
import io.contek.invoker.commons.actor.SimpleActorFactory;
import io.contek.invoker.commons.actor.http.SimpleHttpClientFactory;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.websocket.WebSocketContext;
import io.contek.invoker.deribit.api.rest.market.MarketRestApi;
import io.contek.invoker.deribit.api.rest.user.UserRestApi;
import io.contek.invoker.deribit.api.websocket.market.MarketWebSocketApi;
import io.contek.invoker.deribit.api.websocket.user.UserWebSocketApi;
import io.contek.invoker.security.ApiKey;
import io.contek.invoker.security.SimpleCredentialFactory;
import is.fm.util.BaseEncoding;

import javax.annotation.concurrent.ThreadSafe;
import java.time.Duration;

import static io.contek.invoker.security.SecretKeyAlgorithm.HMAC_SHA256;

@ThreadSafe
public final class ApiFactory {

  public static final ApiContext MAIN_NET_CONTEXT =
      ApiContext.newBuilder()
          .setRestContext(RestContext.newBuilder().setBaseUrl("https://www.deribit.com")
              .setConnectionTimeout(Duration.ofMillis(2000))
              .setReadTimeout(Duration.ofMillis(1000))
              .setWriteTimeout(Duration.ofMillis(1000)))
          .setWebSocketContext(WebSocketContext.forBaseUrl("wss://www.deribit.com", Duration.ofMillis(0)))
          .build();

  public static final ApiContext GATEWAY_CONTEXT =
      ApiContext.newBuilder()
          .setRestContext(RestContext.newBuilder().setBaseUrl("https://gateway.deribit.com")
              .setConnectionTimeout(Duration.ofMillis(1000))
              .setReadTimeout(Duration.ofMillis(500))
              .setWriteTimeout(Duration.ofMillis(500))
          )
          .setWebSocketContext(WebSocketContext.forBaseUrl("wss://gateway.deribit.com", Duration.ofMillis(0)))
          .build();

  public static final ApiContext CROSS_CONTEXT =
      ApiContext.newBuilder()
          .setRestContext(RestContext.newBuilder().setBaseUrl("http://193.58.254.1:8021")
              .setConnectionTimeout(Duration.ofMillis(1000))
              .setReadTimeout(Duration.ofMillis(500))
              .setWriteTimeout(Duration.ofMillis(500))
          )
          .setWebSocketContext(WebSocketContext.forBaseUrl("ws://193.58.254.1:8022", Duration.ofMillis(0)))
          .build();

  public static final ApiContext TEST_NET_CONTEXT =
      ApiContext.newBuilder()
          .setRestContext(RestContext.forBaseUrl("https://test.deribit.com"))
          .setWebSocketContext(WebSocketContext.forBaseUrl("wss://test.deribit.com"))
          .build();

  private final ApiContext context;
  private final IActorFactory actorFactory;

  private ApiFactory(ApiContext context, IActorFactory actorFactory) {
    this.context = context;
    this.actorFactory = actorFactory;
  }

  public static ApiFactory getMainNet() {
    return fromContext(MAIN_NET_CONTEXT);
  }

  public static ApiFactory getGatewayNet() {
    return fromContext(GATEWAY_CONTEXT);
  }

  public static ApiFactory getDirectNet() {
    return fromContext(CROSS_CONTEXT);
  }

  public static ApiFactory getTestNet() {
    return fromContext(TEST_NET_CONTEXT);
  }

  public static ApiFactory fromContext(ApiContext context) {
    return new ApiFactory(context, createActorFactory());
  }

  public SelectingRestApi rest() {
    return new SelectingRestApi();
  }

  public SelectingWebSocketApi ws() {
    return new SelectingWebSocketApi();
  }

  private static SimpleActorFactory createActorFactory() {
    return SimpleActorFactory.newBuilder()
        .setCredentialFactory(createCredentialFactory())
        .setHttpClientFactory(SimpleHttpClientFactory.getInstance())
        .build();
  }

  private static SimpleCredentialFactory createCredentialFactory() {
    return SimpleCredentialFactory.newBuilder()
        .setAlgorithm(HMAC_SHA256)
        .setEncoding(BaseEncoding.base16().lowerCase())
        .build();
  }

  @ThreadSafe
  public final class SelectingRestApi {

    private SelectingRestApi() {}

    public MarketRestApi market() {
      RestContext restContext = context.getRestContext();
      IActor actor = actorFactory.create(null, restContext);
      return new MarketRestApi(actor, restContext);
    }

    public UserRestApi user(ApiKey apiKey) {
      RestContext restContext = context.getRestContext();
      IActor actor = actorFactory.create(apiKey, restContext);
      return new UserRestApi(actor, restContext);
    }
  }

  @ThreadSafe
  public final class SelectingWebSocketApi {

    private SelectingWebSocketApi() {}

    public MarketWebSocketApi market() {
      WebSocketContext wsContext = context.getWebSocketContext();
      IActor actor = actorFactory.create(null, wsContext);
      return new MarketWebSocketApi(actor, wsContext);
    }

    public UserWebSocketApi user(ApiKey apiKey) {
      WebSocketContext wsContext = context.getWebSocketContext();
      IActor actor = actorFactory.create(apiKey, wsContext);
      return new UserWebSocketApi(actor, wsContext);
    }
  }
}
