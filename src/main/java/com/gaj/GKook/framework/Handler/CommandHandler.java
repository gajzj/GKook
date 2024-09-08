package com.gaj.GKook.framework.Handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaj.GKook.BotManager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    /*
        命令格式：
        1、以 / 开头 /command
        2、如果该命令需要传递参数，参数和用小括号括住 /command(arg1,arg2,arg3)
        3、多级命令通过 . 执行二级命令 /command1(arg1).command2
     */

    Map<String, Method> commands;
    private static CommandHandler instance;
    ObjectMapper mapper;

    private CommandHandler() {
        mapper = new ObjectMapper();
        commands = new HashMap<>();
        Class clz = CommandHandler.class;
        Method[] methods = clz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().endsWith("command")) {
                commands.put(method.getName().replace("command", ""), method);
            }
        }
    }

    public void invoke(String message) {
        try {
            Method method = commands.get(mapper.readTree(message).get("d").get("content").asText());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static CommandHandler getInstance() {
        if (instance == null) {
            instance = new CommandHandler();
        }
        return instance;
    }

    /**
     * 打招呼命令
     *
     * @param message
     */
    public void helloCommand(String message) {

        JsonNode root = null;
        try {
            root = mapper.readTree(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String content = root.get("d").get("content").asText();
        String authorId = root.get("d").get("extra").get("author").get("id").asText();
        String channelId = root.get("d").get("target_id").asText();
        String username = root.get("d").get("extra").get("author").get("username").asText();
        System.out.println("send from: " + authorId + " in channel: " + channelId + ": " + content);
        BotManager.sendMessageToChannel(1, channelId, "你好" + username, authorId);
    }
}
