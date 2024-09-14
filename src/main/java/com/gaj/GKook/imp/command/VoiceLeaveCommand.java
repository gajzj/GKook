package com.gaj.GKook.imp.command;

import com.gaj.GKook.BotManager;
import com.gaj.GKook.framework.commad.AbstractCommand;
import com.gaj.GKook.framework.commad.GCommand;

import java.util.List;

@GCommand("voiceLeave")
public class VoiceLeaveCommand extends AbstractCommand {
    @Override
    protected void executeCommand() {
        List<String> ua = getUserArguments();
        System.out.println(ua.get(0));
        BotManager.voiceLeave(ua.get(0));
    }
}
