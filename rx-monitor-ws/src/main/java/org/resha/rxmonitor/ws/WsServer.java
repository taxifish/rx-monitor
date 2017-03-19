package org.resha.rxmonitor.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;

import java.util.concurrent.TimeUnit;

public class WsServer {

    private static final String PORT_PROPERTY = "port";
    private static final String DEFAULT_PORT = "8787";

    private static final String MAP_NAME_PROPERTY = "map";
    private static final String DEFAULT_MAP_NAME = "monitor";

    private static final String TICK_PROPERTY = "tick";
    private static final String DEFAULT_TICK = "100";

    private IMap<String, String> map;

    private Gson gson;

    public static void main(String[] args) {

        try {
            int port = Integer.parseInt(System.getProperty(PORT_PROPERTY, DEFAULT_PORT));
            int tick = Integer.parseInt(System.getProperty(TICK_PROPERTY, DEFAULT_TICK));
            String mapName = System.getProperty(MAP_NAME_PROPERTY, DEFAULT_MAP_NAME);

            WsServer server = new WsServer(mapName);
            server.start(port, tick);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    private WsServer(String mapName) {
        ClientConfig cfg = new ClientConfig();
        HazelcastInstance client = HazelcastClient.newHazelcastClient(cfg);
        this.map = client.getMap(mapName);
        this.gson = new GsonBuilder().create();
    }

    private void start(int port, int interval) {
        HttpServer<ByteBuf, ByteBuf> server;

        server = HttpServer.newServer(port)
                .enableWireLogging(LogLevel.DEBUG)
                .start((req, resp) -> {
                    if (req.isWebSocketUpgradeRequested()) {
                        return resp.acceptWebSocketUpgrade(wsConn -> {
                            Observable<WebSocketFrame> out =
                                    Observable.interval(interval, TimeUnit.MILLISECONDS)
                                            .map(tick -> new TextWebSocketFrame(gson.toJson(map)));
                            return wsConn.writeAndFlushOnEach(out);
                        });
                    } else {
                        /* send NOT FOUND response wor non-ws requests */
                        return resp.setStatus(HttpResponseStatus.NOT_FOUND);
                    }
                });

        server.awaitShutdown();

    }
}
