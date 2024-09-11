package com.gaj.GKook.framework.Handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaj.GKook.BotManager;
import com.gaj.GKook.framework.bean.EventType;
import com.gaj.GKook.framework.bean.User;
import com.gaj.GKook.framework.commad.Command;
import com.gaj.GKook.framework.config.BotConfig;
import com.gaj.GKook.imp.command.HelloCommand;

import javax.websocket.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ClientEndpoint
public class WebSocketHandler {

    private Session session;
    private WebSocketContainer container;
    private String sessionId;
    private final Thread heart;
    private boolean reconnecting = false;
    private int sn = 0;

    public WebSocketHandler() {
        heart = new Thread(new Runnable() {
            @Override
            public void run() {
                for (; ; ) {
                    sendMessage("{\"s\":2,\"sn\":" + sn + "}");
                    try {
                        Thread.sleep(25000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    // 当 WebSocket 连接打开时调用
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to the server.");
    }

    // 当从服务器接收到消息时调用
    @OnMessage
    public void onMessage(String message) throws UnsupportedEncodingException {
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
                content = content.replace("\\\\", "\\")
                        .replace("\\(", "(")
                        .replace("\\)", ")")
                        .replace("\\*", "*")
                        .replace("\\<", "<")
                        .replace("\\>", ">")
                        .replace("\\[", "[")
                        .replace("\\]", "]");
                System.out.println(content);
                if (content.startsWith("/")) {
                    String authorId = root.get("d").get("extra").get("author").get("id").asText();

                    // 不识别机器人 id
                    if (!authorId.equals(BotConfig.BOT_ID)) {
                        String channelId = root.get("d").get("target_id").asText();
                        Optional<Command> oc = BotManager.interpret(content);
                        Command rootCmd = null;
                        if (oc.isPresent()) {
                            rootCmd = oc.get();
                            if (rootCmd instanceof HelloCommand) {
                                Map<String, Object> contextParams = new HashMap<>();
                                contextParams.put("type", 1);
                                contextParams.put("targetId", channelId);
//                                contextParams.put("content", "hello");
                                contextParams.put("tempTargetId", "");
                                Command command = rootCmd;
                                do {
                                    command.setContextParameters(contextParams);
                                    command = command.next();
                                } while (command != null);
                                BotManager.execute(rootCmd);
                            }
                        }

                    }


//                        if (content.startsWith("/info")) {
//                            System.out.println(message);
//                        } else if (content.equals("/test")) {
//
//                        } else if (content.equals("/cleanup")) {
//                            BotManager.cleanupChannelMessage(channelId, authorId);
//                        }

                } else if (content.equals("[系统消息]")) {
                    String extraType = root.get("d").get("extra").get("type").asText();
                    EventType eventType = EventType.value(extraType);
                    switch (eventType) {
                        case JOINED_CHANNEL -> {
                            String userId = root.get("d").get("extra").get("body").get("user_id").asText();
                            String guildId = root.get("d").get("target_id").asText();
                            User user = BotManager.getUser(userId, guildId);
                            // TODO: 推送频道固定为 7870488044424418，需要更改
                            BotManager.sendMessageToChannel(1, "7870488044424418", user.getUsername() + "悄咪咪加入了语音", "");
                        }
                        case EXITED_CHANNEL -> {
//                            BotManager.sendMessageToChannel(1, "7870488044424418", "有人退出了语音", "");
                        }
                    }
                }
                sn = root.get("sn").asInt();
            }
            case 1 -> { // 服务器的 Hello 包
                this.sessionId = root.get("d").get("sessionId").asText();
                System.out.println(sessionId);
                if (reconnecting) { // 重连时 sn 置 0
                    sn = 0;
                } else { // 初次连接创建心跳线程
                    heart.start();
                }
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
        reconnect();
    }

    // 当发生错误时调用
    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
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
            String gateway = BotManager.getGateway();
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
