package com.gaj.GKook.framework.commad;

public interface CommandWithArgs extends Command {
    void execute(String... args);
}
