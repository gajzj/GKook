package com.gaj.GKook.framework.Handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaj.GKook.Main;
import com.gaj.GKook.config.BotConfig;

import javax.websocket.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class WebSocketHandler {

    private Session session;
    private WebSocketContainer container;
    private String sessionId;
    private Thread heart;
    private boolean reconnecting = false;
    private int sn = 0;

    public WebSocketHandler() {
    }

    // 当 WebSocket 连接打开时调用
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to the server.");
    }

    // 当从服务器接收到消息时调用
    @OnMessage
    public void onMessage(String message) throws JsonProcessingException, UnsupportedEncodingException, InterruptedException {
        message = new String(message.getBytes("ISO-8859-1"), "UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = mapper.readTree(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int s = root.get("s").asInt();
        switch (s) {
            case 0 -> { // TODO: 用 Event 队列优化
                String content = root.get("d").get("content").asText();
                if (content.startsWith("/")) {
                    String authorId = root.get("d").get("extra").get("author").get("id").asText();
                    if (!authorId.equals(BotConfig.BOTID)) { // 不识别机器人 id
                        sn = root.get("sn").asInt();
                        String channelId = root.get("d").get("target_id").asText();
                        System.out.println("send from: " + authorId + " in channel: " + channelId + ": " + content);
                        Main.sendMessageToChannel(1, channelId, "/你好", authorId);
                    }
                }
            }
            case 1 -> { // 服务器的 Hello 包
                this.sessionId = root.get("d").get("sessionId").asText();
                System.out.println(sessionId);
                if (heart != null || reconnecting) {
                    heart.interrupt();
                    sn = 0;
                }
                heart = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; ; ) {
                            sendMessage("{\"s\":2,\"sn\":" + sn + "}");
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
                heart.start();
            }
            case 3 -> { // PONG
//                System.out.println("PONG");
            }
            case 5 -> { // 收到重连信令
                reconnect();
            }
            case 6 -> { // 收到 RESUME ACK
                this.sessionId = root.get("d").get("sessionId").asText();
            }
            default -> {
                System.out.println(">>>s:" + s + " no support by now<<<");
                System.out.println(message);
            }
        }
    }

    // 当 WebSocket 连接关闭时调用
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Connection closed: " + closeReason.getReasonPhrase());
    }

    // 当发生错误时调用
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error occurred: " + throwable.getMessage());
    }

    // 发送消息到服务器
    public void sendMessage(String message) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        }
    }

    public void connect() {
        container = ContainerProvider.getWebSocketContainer();
        try {
            String gateway = Main.getGateway();
            container.connectToServer(this, new URI(gateway)); // 连接到 WebSocket 服务器
        } catch (DeploymentException | URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    public void reconnect() {
        reconnecting = true;
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        connect();
        reconnecting = false;
    }

    public void resume() {
        System.out.println("!!!resume!!!");
        sendMessage("""
                {
                    "s": 4,
                    "sn": 0
                }
                """);
    }
}
