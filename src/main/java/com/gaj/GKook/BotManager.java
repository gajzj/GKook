package com.gaj.GKook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaj.GKook.framework.bean.User;
import com.gaj.GKook.framework.Handler.ApiHandler;
import com.gaj.GKook.framework.Handler.CommandHandler;
import com.gaj.GKook.framework.Handler.WebSocketHandler;
import com.gaj.GKook.framework.commad.Command;
import com.gaj.GKook.framework.commad.CommandProcessor;

import java.util.Optional;

public class BotManager {

    private static WebSocketHandler wsHandler;
    private static ApiHandler apiHandler;
    private static CommandHandler commandHandler;
    private static ObjectMapper mapper;
    private static CommandProcessor cp;

    public static void main(String[] args) throws Exception {
        apiHandler = new ApiHandler();
        wsHandler = new WebSocketHandler();
        commandHandler = CommandHandler.getInstance();
        mapper = new ObjectMapper();
        wsHandler.connect();
        cp = new CommandProcessor();

        for (; ; ) {

        }
    }

    public static Optional<Command> interpret(String commandString) {
        return cp.interpret(commandString);
    }

    public static void execute(Command command) {
        cp.execute(command);
    }

    public static String getGateway() {
        return apiHandler.getGateway();
    }

    public static void sendMessageToChannel(int type, String targetId, String content, String tempTargetId) {
        apiHandler.sendMessageToChannel(type, targetId, content, tempTargetId);
    }

    public static User getUser(String userId, String guildId) {
        try {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String data = apiHandler.getUser(userId, guildId);
            // TODO: 这么截断字符串太逆天了
            int startIndex = data.indexOf("\"data\":") + "\"data\":".length();
            int endIndex = data.lastIndexOf("}");
            data = data.substring(startIndex, endIndex);
            return mapper.readValue(data, User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void cleanupChannelMessage(String targetId, String authorId) {
        apiHandler.cleanupChannelMessage(targetId, authorId);
    }

    public static void getMessageListByChannelId(String channelId) {
        apiHandler.getMessageListByChannelId(channelId);
    }
}
