package com.gaj.GKook.framework.commad;

import com.gaj.GKook.imp.command.HelloCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CommandFactory {
    // 用于存储命令的注册表
    private static final Map<String, Supplier<Command>> commandRegistry = new HashMap<>();

    // 静态块注册基本命令
    static {
        // TODO: 修改为注解加载？
         registerCommand("hello", HelloCommand::new);
    }

    // 注册命令方法
    public static void registerCommand(String commandName, Supplier<Command> commandSupplier) {
        commandRegistry.put(commandName.toLowerCase(), commandSupplier);
    }

    // 创建命令的方法
    public static Command createCommand(String commandName, Map<String, Object> contextParams, List<String> userArguments) {
        Supplier<Command> supplier = commandRegistry.get(commandName.toLowerCase());
        System.out.println(commandName);
        if (supplier == null) {
            System.err.println("Unknown command: " + commandName);
            return null;
        }

        Command command = supplier.get();
        command.setContextParameters(contextParams);
        command.setUserArguments(userArguments);

        return command;
    }
}
