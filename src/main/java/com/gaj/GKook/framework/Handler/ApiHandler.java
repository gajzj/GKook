package com.gaj.GKook.framework.Handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaj.GKook.config.ApiConfig;
import com.gaj.GKook.config.BotConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiHandler {
    // TODO: 抽出构造 http 请求逻辑，减少重复代码
    // TODO: 单例实现 ApiHandler
    // TODO: 每个 api 的速率控制是独立的
    // 全局HttpClient:
    private static HttpClient httpClient;
    private static ObjectMapper mapper = new ObjectMapper();
    private static int rateLimit;
    private static Instant overSpeedUntil;

    static {
        httpClient = HttpClient.newBuilder().build();
    }

    /**
     * 调用 /api/v3/message/list 获取消息列表
     *
     * @param channelId 要获取的频道 id
     * @return 接口调用响应体
     */
    public String getMessageListByChannelId(String channelId) {
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(ApiConfig.APIURL
                            + "/api/v3/message/list"
                            + "?target_id=" + channelId))
                    .header("Authorization", TYPE + " " + TOKEN)
                    .header("Content-Type", "application/json; utf-8")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());
            if (root.get("code").asInt() == 0) {
                System.out.println(response.headers());
                return response.body();
            } else
                throw new RuntimeException("!!!get message list failed!!!");
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 调用 /api/v3/message/delete 删除消息
     */
    public void cleanupChannelMessage(String channelId) { // TODO: 先写查询消息列表
        String messageList = this.getMessageListByChannelId(channelId);
        List<String> msgIdList = new ArrayList<>();
        try { // 获取需要清理的消息 id
            JsonNode rootNode = mapper.readTree(messageList);
            JsonNode itemsNode = rootNode.get("data").get("items");
            if (itemsNode.isArray()) {
                for (JsonNode item : itemsNode) {
                    String authorId = item.get("author").get("id").asText();
                    if (authorId.equals("1791210004") || authorId.equals(BotConfig.BOTID)) {
                        msgIdList.add(item.get("id").asText());
                    }
                }
            }
            // 请求调用 api
            for (String s : msgIdList) {
                HttpRequest request = HttpRequest.newBuilder(new URI(ApiConfig.APIURL + "/api/v3/message/delete"))
                        .header("Authorization", TYPE + " " + TOKEN)
                        .header("Content-Type", "application/json; utf-8")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"msg_id\":\"" + s + "\"}"))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonNode root = mapper.readTree(response.body());
                if (root.get("code").asInt() == 0) {
                    System.out.println(response.body());
                } else {
                    if (root.get("code").asInt() == 429) { // 超速
                        System.out.println(response.headers() + response.body());
                        String rest = response.headers().firstValue("x-rate-limit-reset").orElse(null);
                        // TODO: 测试
                        overSpeedUntil = Instant.now().plus(Integer.parseInt(rest), ChronoUnit.SECONDS);
                    }
                    System.out.println(response.headers() + response.body());
                    throw new RuntimeException("!!!clean message failed!!!");
                }
            }
        } catch (URISyntaxException | InterruptedException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 调用 /api/v3/user/view 获取用户信息
     *
     * @param userId  用户 id
     * @param guildId 用户所在服务器 id
     * @return 接口调用响应体，请在外部做包装工作
     */
    public String getUser(String userId, String guildId) {
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(ApiConfig.APIURL + "api/v3/user/view?user_id=" + userId + "&" + "guild_id=" + guildId))
                    .header("Authorization", TYPE + " " + TOKEN)
                    .header("Content-Type", "application/json; utf-8")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            if (root.get("code").asInt() == 0) {
//                System.out.println(response.body());
                return response.body();
            } else
                throw new RuntimeException("!!!get user failed!!!");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取 Kook 机器人的网关地址
     *
     * @return 机器人的网关地址
     */
    public String getGateway() {
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(ApiConfig.APIURL + "api/v3/gateway/index?compress=0"))
                    .header("Authorization", TYPE + " " + TOKEN)
                    .header("Content-Type", "application/json; utf-8")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            if (root.get("code").asInt() == 0) {
                System.out.println(">>>get gateway success<<<");
                String rest = response.headers().firstValue("x-rate-limit-reset").orElse(null);
                return root.get("data").get("url").asText();
            } else
                throw new RuntimeException("!!!get gateway failed!!!");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 调用 message/create api
     *
     * @param type         消息类型
     *                     1: 表示纯文本
     *                     其他: 暂未支持
     * @param targetId     目标频道
     * @param content      内容
     * @param tempTargetId （暂不支持）用于创建临时消息，为目标用户的id，目标用户下线后将消失
     */
    public void sendMessageToChannel(int type, String targetId, String content, String tempTargetId) {
        // TODO: 拓展消息种类 目前只接收 type = 1
        if (type != 1) {
            throw new IllegalArgumentException("type " + type + "no support by now");
        }
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", type);
            requestBody.put("target_id", targetId);
            requestBody.put("content", content);
//            requestBody.put("temp_target_id", tempTargetId); // 所有消息都将是临时消息

            String jsonInputString = mapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder(new URI(ApiConfig.APIURL + "/api/v3/message/create"))
                    .header("Authorization", TYPE + " " + TOKEN)
                    .header("Content-Type", "application/json; utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            if (root.get("code").asInt() == 0) {
                System.out.println(response.headers());
                response.headers().firstValue("x-rate-limit-reset").orElse(null);
            } else if (root.get("code").asInt() == 429) {
                System.out.println(response.headers());
                throw new RuntimeException("!!!over speed!!!");
            }
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
//            overSpeedUntil = Instant.now() +
            System.err.println("!!!api 调用超速!!!");
        }
    }

    // http 获取网关地址，不采用压缩
    private final static String TOKEN = "1/MzIzNDE=/t+pDOqFhhhR0G1c3LJOUkQ==";
    // token 类型
    private final static String TYPE = "Bot";
}
