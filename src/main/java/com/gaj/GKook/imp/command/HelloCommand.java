package com.gaj.GKook.imp.command;

import com.gaj.GKook.framework.commad.AbstractCommand;
import com.gaj.GKook.framework.commad.GCommand;

@GCommand("hello")
public class HelloCommand extends AbstractCommand {

    @Override
    protected void executeCommand() {
        System.out.println("hello");
    }
}
