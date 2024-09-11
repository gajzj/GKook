package com.gaj.GKook.imp.command;

import com.gaj.GKook.BotManager;
import com.gaj.GKook.framework.commad.AbstractCommand;
import com.gaj.GKook.framework.commad.GCommand;
import com.gaj.GKook.framework.config.BotConfig;

import java.util.List;
import java.util.Map;

@GCommand("cleanup")
public class CleanupCommand extends AbstractCommand {

    @Override
    protected void executeCommand() {
        Map<String, Object> contextParameters = getContextParameters();
        List<String> userArguments = getUserArguments();
        if (!userArguments.isEmpty()) {
            BotManager.cleanupChannelMessage(
                    (String) contextParameters.get("targetId"),
                    (String) contextParameters.get("authorId"));

        } else { // 未指定 id 时清理机器人自己的
            BotManager.cleanupChannelMessage(
                    (String) contextParameters.get("targetId"),
                    BotConfig.BOT_ID);
        }
    }
}
