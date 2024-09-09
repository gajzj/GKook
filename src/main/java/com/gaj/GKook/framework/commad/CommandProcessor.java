package com.gaj.GKook.framework.commad;

import java.util.*;

public class CommandProcessor {
    // 解释命令字符串并执行命令
    public void process(String commandString) {
        Optional<Command> commandOpt = interpret(commandString);
        commandOpt.ifPresent(this::execute);
    }

    // TODO: 有 bug 参数命令不能正确解析，链式命令不能正确创建
    // 解析命令字符串
    public Optional<Command> interpret(String commandString) {
        if (commandString == null || commandString.isEmpty()) {
            return Optional.empty();
        }

        // 移除命令前后的空格，并确保命令以 '/' 开头
        commandString = commandString.trim();
        if (!commandString.startsWith("/")) {
            return Optional.empty();
        }

        // 分离命令名称和参数
        String mainCommandPart = commandString.substring(1); // 去掉前面的 '/'
        String commandName;
        List<String> userArguments = new ArrayList<>();

        // 检查是否包含参数部分
        if (mainCommandPart.contains("(") && mainCommandPart.contains(")")) {
            // 提取命令名称和参数部分
            commandName = mainCommandPart.substring(0, mainCommandPart.indexOf('('));
            String argsPart = mainCommandPart.substring(mainCommandPart.indexOf('(') + 1, mainCommandPart.indexOf(')'));
            userArguments = Arrays.asList(argsPart.split(","));
        } else {
            // 没有参数部分，直接将整个字符串作为命令名称
            commandName = mainCommandPart;
        }

        // 分割链式命令部分
        List<String> parts = new ArrayList<>(Arrays.asList(commandName.split("\\.")));
        Map<String, Object> contextParams = new HashMap<>();

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

    // TODO: 拓展 execute
    // 执行命令
    public void execute(Command command) {
        if (command != null) {
            command.execute();
        }
    }
}
