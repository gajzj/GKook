package com.gaj.GKook.framework.commad;

import java.util.*;

public class CommandProcessor {
    // 解释命令字符串并执行命令
    public void process(String commandString) {
        Optional<Command> commandOpt = interpret(commandString);
        commandOpt.ifPresent(this::execute);
    }

    // 解析命令字符串
    private Optional<Command> interpret(String commandString) {
        if (commandString == null || commandString.isEmpty()) {
            return Optional.empty();
        }

        // 移除命令前后的空格，并确保命令以 '/' 开头
        commandString = commandString.trim();
        if (!commandString.startsWith("/")) {
            return Optional.empty();
        }

        // 分割命令和参数
        String commandPart = commandString.split("\\(")[0]; // e.g., /command1
        List<String> parts = new ArrayList<>(Arrays.asList(commandPart.substring(1).split("\\.")));
        Map<String, Object> contextParams = new HashMap<>();

        List<String> userArguments = new ArrayList<>();
        if (commandString.contains("(")) {
            // 提取参数，假设格式为 /command1(arg1,arg2)
            String argsPart = commandString.substring(commandString.indexOf('(') + 1, commandString.indexOf(')'));
            userArguments = Arrays.asList(argsPart.split(","));
        }

        // 初始化根命令
        Command currentCommand = CommandFactory.createCommand(parts.get(0), contextParams, userArguments);
        Command rootCommand = currentCommand;

        // 处理链式命令
        for (int i = 1; i < parts.size(); i++) {
            Command nextCommand = CommandFactory.createCommand(parts.get(i), contextParams, Collections.emptyList());
            if (currentCommand != null) {
                currentCommand.chain(nextCommand);
            }
            currentCommand = nextCommand;
        }

        return Optional.ofNullable(rootCommand);
    }

    // 执行命令
    private void execute(Command command) {
        if (command != null) {
            command.execute();
        }
    }
}
