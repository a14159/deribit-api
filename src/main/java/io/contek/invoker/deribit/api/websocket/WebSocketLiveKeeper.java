package io.contek.invoker.deribit.api.websocket;

import io.contek.invoker.commons.websocket.AnyWebSocketMessage;
import io.contek.invoker.commons.websocket.IWebSocketLiveKeeper;
import io.contek.invoker.commons.websocket.WebSocketSession;
import io.contek.invoker.commons.websocket.WebSocketSessionInactiveException;
import io.contek.invoker.deribit.api.websocket.common.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicBoolean;


@ThreadSafe
public final class WebSocketLiveKeeper implements IWebSocketLiveKeeper {

    private static final Logger log = LogManager.getLogger(WebSocketLiveKeeper.class);

    public static final boolean DEBUG = false; // javac should remove the corresponding code

    private static final int HEARTBEAT_FREQ = 15; // seconds

    private final WebSocketRequestIdGenerator requestIdGenerator;
    private volatile int heartbeats = 0;
    private volatile int testResponses = 0;
    private volatile long lastHeartbeat = 0;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    WebSocketLiveKeeper(WebSocketRequestIdGenerator requestIdGenerator) {
        this.requestIdGenerator = requestIdGenerator;
    }

    @Override
    public void onHeartbeat(WebSocketSession session) throws WebSocketSessionInactiveException {
        synchronized (initialized) {
            if (!initialized.get()) {
                log.debug("Sending set heartbeat request");
                HeartbeatParams params = new HeartbeatParams();
                params.interval = HEARTBEAT_FREQ;
                WebSocketRequest<HeartbeatParams> request = new WebSocketRequest<>();
                request.id = requestIdGenerator.getNextRequestId(HeartbeatResponse.class);
                request.method = "public/set_heartbeat";
                request.params = params;
                session.send(request);
                initialized.set(true);
                lastHeartbeat = System.currentTimeMillis();
            } else {
                if (System.currentTimeMillis() - lastHeartbeat > 2 * HEARTBEAT_FREQ * 1000L) {
                    log.warn("No heartbeats for the last {} seconds, resetting connection", 5 * HEARTBEAT_FREQ / 2);
                    initialized.set(false);
                    throw new WebSocketSessionInactiveException("no messages or heartbeats from server");
                }
            }
        }
    }

    private final WebSocketTestRequest testRequest = new WebSocketTestRequest();

    @Override
    public void onMessage(AnyWebSocketMessage message, WebSocketSession session) {
        lastHeartbeat = System.currentTimeMillis();
        if (message instanceof WebSocketHeartbeat) { // server is requesting us to send a test request
            if (DEBUG) {
                heartbeats++;
                log.debug("Received heartbeat message #{} (and sending a test request)", heartbeats);
            }
            synchronized (testRequest) {
                testRequest.id = requestIdGenerator.getNextRequestId(WebSocketTestResponse.class);
                session.send(testRequest);
            }
        }
        if (DEBUG) {
            if (message instanceof WebSocketTestResponse) { // received a response to our last test request
                testResponses++;
                log.debug("Received test response message #{}", testResponses);
            }
            if (message instanceof HeartbeatResponse hb) { // confirmation for our set heartbeat
                log.debug("Receiving set heartbeat response: {}", hb.result);
            }
        }
    }

    @Override
    public void afterDisconnect() {
        heartbeats = 0;
        testResponses = 0;
        synchronized (initialized) {
            initialized.set(false);
        }
    }

    public static final class HeartbeatResponse extends WebSocketResponse<String> {}
}
