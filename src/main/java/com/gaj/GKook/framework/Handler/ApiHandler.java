package com.gaj.GKook.framework.Handler;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    // TODO: 每个 api 的速率控制是独立的，但有全局限制
    // 全局HttpClient:
    private static HttpClient httpClient;
    private static ObjectMapper mapper = new ObjectMapper();
    private static int timesRemaining;
    private static Instant lastUpdate;

    static {
        httpClient = HttpClient.newBuilder().build();
        timesRemaining = ApiConfig.MAX_TIMES;
        lastUpdate = Instant.now();
    }

    /**
     * 是否还有剩余 api 调用次数
     */
    private void hasTimeRemaining() {
        if (timesRemaining == 0) {
            if (lastUpdate.plusSeconds(60).isAfter(Instant.now())) {
                lastUpdate = Instant.now().plus(60, ChronoUnit.SECONDS);
                throw new RuntimeException("!!!api request over speed, request at least one minute later!!!");
            } else {
                lastUpdate = Instant.now();
                timesRemaining = ApiConfig.MAX_TIMES;
            }
        }
        timesRemaining--;
    }

    /**
     * 处理 get 请求
     *
     * @param url
     * @return
     */
    private HttpResponse<String> get(String url) {
        hasTimeRemaining();
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(url))
                    .header("Authorization", BotConfig.BOTTOKEN)
                    .header("Content-Type", "application/json; utf-8")
                    .build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("!!!url: " + url + " is invalid, plz check out!!!");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理 post 请求
     *
     * @param url
     * @param body
     * @return
     */
    private HttpResponse<String> post(String url, Map<?, ?> body) {
        hasTimeRemaining();
        try {
            String jsonString = mapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder(new URI(url))
                    .header("Authorization", BotConfig.BOTTOKEN)
                    .header("Content-Type", "application/json; utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonString)).build();
            System.out.println(jsonString);
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("!!!the body format is invalid!!!");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse<String> post(String url, String body) {
        hasTimeRemaining();
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(url))
                    .header("Authorization", BotConfig.BOTTOKEN)
                    .header("Content-Type", "application/json; utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("!!!the body format is invalid!!!");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 调用 /api/v3/message/list 获取消息列表
     *
     * @param channelId 要获取的频道 id
     * @return 接口调用响应体
     */
    public String getMessageListByChannelId(String channelId) {
        try {
            HttpResponse<String> response = get(ApiConfig.API_URL + "/api/v3/message/list" + "?target_id=" + channelId);
            JsonNode root = mapper.readTree(response.body());
            if (root.get("code").asInt() == 0) {
                return response.body();
            } else
                throw new RuntimeException("!!!get message list failed!!!");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 调用 /api/v3/message/delete 删除消息
     */
    public void cleanupChannelMessage(String channelId) {
        String list = this.getMessageListByChannelId(channelId);
        List<String> msgIdList = new ArrayList<>();
        try { // 获取需要清理的消息 id
            JsonNode rootNode = mapper.readTree(list);
            JsonNode itemsNode = rootNode.get("data").get("items");
            if (itemsNode.isArray()) {
                for (JsonNode item : itemsNode) {
                    String authorId = item.get("author").get("id").asText();
                    if (authorId.equals("1791210004") || authorId.equals(BotConfig.BOTID)) {
                    }
                    msgIdList.add(item.get("id").asText());
                }
            }
            for (String s : msgIdList) {
                HttpResponse<String> response = post(ApiConfig.API_URL + "/api/v3/message/delete", "{\"msg_id\":\"" + s + "\"}");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 调用 /api/v3/user/view 获取用户信息
     *
     * @param userId  用户 id
     * @param guildId 用户所在服务器 id
     * @return 返回获取的 user 实例
     */
    public String getUser(String userId, String guildId) {
        try {
            HttpResponse<String> response = get(ApiConfig.API_URL + "api/v3/user/view?user_id=" + userId + "&" + "guild_id=" + guildId);
            JsonNode root = mapper.readTree(response.body());
            if (root.get("code").asInt() == 0) {
                return response.body();
            } else
                throw new RuntimeException("!!!get user failed!!!");
        } catch (IOException e) {
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
            HttpResponse<String> response = get(ApiConfig.API_URL + "api/v3/gateway/index?compress=0");
            JsonNode root = mapper.readTree(response.body());
            if (root.get("code").asInt() == 0) {
                System.out.println(">>>get gateway success<<<");
                return root.get("data").get("url").asText();
            } else
                throw new RuntimeException("!!!get gateway failed!!!");
        } catch (IOException e) {
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

            HttpResponse<String> response = post(ApiConfig.API_URL + "/api/v3/message/create", requestBody);

            JsonNode root = mapper.readTree(response.body());
            if (root.get("code").asInt() == 0) {
                // success
            } else {
                System.out.println(response.headers());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 机器 token
    private final static String TOKEN = "1/MzIzNDE=/t+pDOqFhhhR0G1c3LJOUkQ==";
    // token 类型
    private final static String TYPE = "Bot";
}
