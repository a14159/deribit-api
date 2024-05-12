package io.contek.invoker.deribit.api.websocket;

import io.contek.invoker.commons.websocket.AnyWebSocketMessage;
import io.contek.invoker.commons.websocket.IWebSocketLiveKeeper;
import io.contek.invoker.commons.websocket.WebSocketSession;
import io.contek.invoker.commons.websocket.WebSocketSessionInactiveException;
import io.contek.invoker.deribit.api.websocket.common.*;
import org.slf4j.Logger;

import javax.annotation.concurrent.ThreadSafe;

import static org.slf4j.LoggerFactory.getLogger;

@ThreadSafe
public final class WebSocketLiveKeeper implements IWebSocketLiveKeeper {

    private static final Logger log = getLogger(WebSocketLiveKeeper.class);

    private final WebSocketRequestIdGenerator requestIdGenerator;
    private volatile int heartbeats = 0;
    private volatile int testResponses = 0;
    private volatile boolean initialized = false;

    WebSocketLiveKeeper(WebSocketRequestIdGenerator requestIdGenerator) {
        this.requestIdGenerator = requestIdGenerator;
    }

    @Override
    public void onHeartbeat(WebSocketSession session) throws WebSocketSessionInactiveException {
        if (!initialized) {
            log.debug("Sending set heartbeat request");
            WebSocketSetHeartbeatRequest request = new WebSocketSetHeartbeatRequest();
            request.interval = 30;
            request.id = requestIdGenerator.getNextRequestId(HeartbeatResponse.class);
            session.send(request);
            initialized = true;
        }
    }

    @Override
    public void onMessage(AnyWebSocketMessage message, WebSocketSession session) {
        if (message instanceof WebSocketHeartbeat) {
            heartbeats++;
            log.debug("Received heartbeat message #{}", heartbeats);
            WebSocketTestRequest request = new WebSocketTestRequest();
            request.id = requestIdGenerator.getNextRequestId(WebSocketTestResponse.class);
            session.send(request);
        }
        if (message instanceof WebSocketTestResponse) {
            testResponses++;
            log.debug("Received test response message #{}", testResponses);
        }
        if (message instanceof HeartbeatResponse hb) {
            log.debug("Receiving set heartbeat response: {}", hb.result);
        }
    }

    @Override
    public void afterDisconnect() {
        heartbeats = 0;
        testResponses = 0;
    }

    public static final class HeartbeatResponse extends WebSocketResponse<String> {}
}
