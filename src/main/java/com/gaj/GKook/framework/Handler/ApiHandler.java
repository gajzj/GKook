package com.gaj.GKook.framework.Handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ApiHandler {
    // 全局HttpClient:
    private static HttpClient httpClient;

    static {
        httpClient = HttpClient.newBuilder().build();
    }

    /**
     * 获取 Kook 机器人的网关地址
     *
     * @return 机器人的网关地址
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public String getGateway() {
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(URL + "api/v3/gateway/index?compress=0"))
                    .header("Authorization", TYPE + " " + TOKEN)
                    .header("Content-Type", "application/json; utf-8")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());
            if (root.get("code").asInt() == 0) {
                System.out.println(">>>get gateway success<<<");
                return root.get("data").get("url").asText();
            } else
                throw new RuntimeException("!!!get gateway failed!!!");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToChannel(int type, String targetId, String content, String tempTargetId) {
        // TODO: 拓展消息种类 目前只接收 type = 1
        if (type != 1) {
            throw new IllegalArgumentException("type "  + type + "no support by now");
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("type", type);
            requestBody.put("target_id", targetId);
            requestBody.put("content", content);
//            requestBody.put("temp_target_id", tempTargetId); // 所有消息都将是临时消息

            String jsonInputString = mapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder(new URI(URL + "/api/v3/message/create"))
                    .header("Authorization", TYPE + " " + TOKEN)
                    .header("Content-Type", "application/json; utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                    .build();

            System.out.println(requestBody);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            if (root.get("code").asInt() == 0) {
                System.out.println(response.body());
                System.out.println(">>>send message success<<<");
            } else
                throw new RuntimeException("!!!send message failed!!!");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    // http 获取网关地址，不采用压缩
    private final static String URL = "https://www.kookapp.cn/";
    private final static String TOKEN = "1/MzIzNDE=/t+pDOqFhhhR0G1c3LJOUkQ==";
    // token 类型
    private final static String TYPE = "Bot";
}
