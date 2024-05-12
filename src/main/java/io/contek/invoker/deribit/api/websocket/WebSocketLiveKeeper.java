package io.contek.invoker.deribit.api.websocket;

import io.contek.invoker.commons.websocket.AnyWebSocketMessage;
import io.contek.invoker.commons.websocket.IWebSocketLiveKeeper;
import io.contek.invoker.commons.websocket.WebSocketSession;
import io.contek.invoker.commons.websocket.WebSocketSessionInactiveException;
import io.contek.invoker.deribit.api.websocket.common.WebSocketHeartbeat;
import io.contek.invoker.deribit.api.websocket.common.WebSocketTestRequest;
import io.contek.invoker.deribit.api.websocket.common.WebSocketTestResponse;
import org.slf4j.Logger;

import javax.annotation.concurrent.ThreadSafe;
import java.time.Clock;

import static org.slf4j.LoggerFactory.getLogger;

@ThreadSafe
public final class WebSocketLiveKeeper implements IWebSocketLiveKeeper {

    private static final Logger log = getLogger(WebSocketLiveKeeper.class);

    private final String name;
    private final WebSocketRequestIdGenerator requestIdGenerator;
    private volatile int heartbeats = 0;
    private volatile int testResponses = 0;

    WebSocketLiveKeeper(String name, WebSocketRequestIdGenerator requestIdGenerator) {
        this.name = name;
        this.requestIdGenerator = requestIdGenerator;
    }

    @Override
    public void onHeartbeat(WebSocketSession session) throws WebSocketSessionInactiveException {
    }

    @Override
    public void onMessage(AnyWebSocketMessage message, WebSocketSession session) {
        if (message instanceof WebSocketHeartbeat) {
            heartbeats++;
            log.debug("Received heartbeat message {} on WebSocket connection \"{}\".", heartbeats, name);
            WebSocketTestRequest request = new WebSocketTestRequest();
            request.id = requestIdGenerator.getNextRequestId(WebSocketTestResponse.class);
            session.send(request);
        }
        if (message instanceof WebSocketTestResponse) {
            testResponses++;
            log.debug("Received test response message {} on WebSocket connection \"{}\".", testResponses, name);
        }
    }

    @Override
    public void afterDisconnect() {
        heartbeats = 0;
        testResponses = 0;
    }
}
