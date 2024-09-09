package com.gaj.GKook.imp.command;

import com.gaj.GKook.BotManager;
import com.gaj.GKook.framework.commad.AbstractCommand;
import com.gaj.GKook.framework.commad.GCommand;

import java.util.List;
import java.util.Map;

@GCommand("hello")
public class HelloCommand extends AbstractCommand {
    @Override
    protected void executeCommand() {
        Map<String, Object> contextParameters = getContextParameters();
        List<String> userArguments = getUserArguments();
        String content = "hello";
        if (!userArguments.isEmpty()) {
            content += userArguments.get(0);
        }
        BotManager.sendMessageToChannel((Integer) contextParameters.get("type"),
                (String) contextParameters.get("targetId"),
                content,
                contextParameters.get("tempTargetId").toString());
    }
}
