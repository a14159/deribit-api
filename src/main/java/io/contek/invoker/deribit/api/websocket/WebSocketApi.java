package io.contek.invoker.deribit.api.websocket;

import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.actor.ratelimit.TypedPermitRequest;
import io.contek.invoker.commons.websocket.AnyWebSocketMessage;
import io.contek.invoker.commons.websocket.BaseWebSocketApi;
import io.contek.invoker.commons.websocket.WebSocketCall;
import io.contek.invoker.commons.websocket.WebSocketContext;
import io.contek.invoker.security.ICredential;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

import static io.contek.invoker.deribit.api.ApiFactory.RateLimits.ONE_WEB_SOCKET_CONNECTION;

@ThreadSafe
public abstract class WebSocketApi extends BaseWebSocketApi {

  private final WebSocketContext context;
  private final WebSocketRequestIdGenerator requestIdGenerator;

  protected WebSocketApi(IActor actor, WebSocketContext context) {
    this(actor, context, new WebSocketMessageParser());
  }

  private WebSocketApi(IActor actor, WebSocketContext context, WebSocketMessageParser parser) {
    this(actor, context, parser, new WebSocketRequestIdGenerator(parser));
  }

  private WebSocketApi(
      IActor actor,
      WebSocketContext context,
      WebSocketMessageParser parser,
      WebSocketRequestIdGenerator requestIdGenerator) {
    super(
        actor,
        parser,
        new WebSocketAuthenticator(actor.getCredential(), requestIdGenerator, actor.getClock()),
        new WebSocketLiveKeeper(requestIdGenerator));
    this.context = context;
    this.requestIdGenerator = requestIdGenerator;
  }

  public WebSocketRequestIdGenerator getRequestIdGenerator() {
    return requestIdGenerator;
  }

  @Override
  protected List<TypedPermitRequest> getRequiredQuotas() {
    return ONE_WEB_SOCKET_CONNECTION;
  }

  @Override
  protected WebSocketCall createCall(ICredential credential) {
    return WebSocketCall.fromUrl(context.getBaseUrl() + "/ws/api/v2");
  }

  @Override
  protected final void checkErrorMessage(AnyWebSocketMessage message) {}
}
