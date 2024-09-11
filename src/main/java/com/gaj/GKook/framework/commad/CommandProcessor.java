package com.gaj.GKook.framework.commad;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        commandString = commandString.substring(1);

        // 分割链式命令
        List<String> commands = splitCommand(commandString);
        Command rootCommand = null;
        Command curCmd = null;


        for (String command : commands) {
            String commandName = getCommandName(command);
            List<String> userArguments = getUserArguments(command);
            Command nextCmd = CommandFactory.createCommand(commandName, userArguments);
            if (curCmd != null) {
                curCmd.chain(nextCmd);
            }
            curCmd = nextCmd;
            if (rootCommand == null) {
                rootCommand = curCmd;
            }
        }

        return Optional.ofNullable(rootCommand);


//        // 分离命令名称和参数
//        String mainCommandPart = commandString.substring(1); // 去掉前面的 '/'
//        String commandName;
//        List<String> userArguments = new ArrayList<>();
//
//        // 检查是否包含参数部分
//        if (mainCommandPart.contains("(") && mainCommandPart.contains(")")) {
//            // 提取命令名称和参数部分
//            commandName = mainCommandPart.substring(0, mainCommandPart.indexOf('('));
//            String argsPart = mainCommandPart.substring(mainCommandPart.indexOf('(') + 1, mainCommandPart.indexOf(')'));
//            userArguments = Arrays.asList(argsPart.split(","));
//        } else {
//            // 没有参数部分，直接将整个字符串作为命令名称
//            commandName = mainCommandPart;
//        }
//
//        // 分割链式命令部分
//        List<String> parts = new ArrayList<>(Arrays.asList(commandName.split("\\.")));
//        Map<String, Object> contextParams = new HashMap<>();
//
//        // 初始化根命令
//        Command currentCommand = CommandFactory.createCommand(parts.get(0), contextParams, userArguments);
//        Command rootCommand = currentCommand;
//
//        // 处理链式命令
//        for (int i = 1; i < parts.size(); i++) {
//            Command nextCommand = CommandFactory.createCommand(parts.get(i), contextParams, Collections.emptyList());
//            if (currentCommand != null) {
//                currentCommand.chain(nextCommand);
//            }
//            currentCommand = nextCommand;
//        }
//
//        return Optional.ofNullable(rootCommand);
    }

    private boolean hasArguments(String command) {
        return Pattern.compile("\\([^()]*[^\\s()]+[^()]*\\)").matcher(command).find();
    }

    /**
     * 获取命令参数
     *
     * @param command
     * @return
     */
    private List<String> getUserArguments(String command) {
        // 没有参数返回空 list
        if (!hasArguments(command)) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\(([^)]*)\\)"); // 匹配括号内的内容
        Matcher matcher = pattern.matcher(command);

        while (matcher.find()) {
            String group = matcher.group(1); // 获取括号内的内容
            String[] params = group.split(","); // 用逗号分割参数
            for (String param : params) {
                result.add(param.trim()); // 去掉可能存在的空格并加入结果列表
            }
        }

        return result;
    }

    /**
     * 获取命令名字
     *
     * @param command
     * @return
     */
    private String getCommandName(String command) {
        if (hasArguments(command)) {
            return command.substring(0, command.indexOf("("));
        }
        return command;
    }

    /**
     * 分割链式命令
     *
     * @param command
     * @return
     */
    private List<String> splitCommand(String command) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("[^\\.()]+(?:\\([^)]*\\))?");
        Matcher matcher = pattern.matcher(command);

        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    // TODO: 拓展 execute
    // 执行命令
    public void execute(Command command) {
        if (command != null) {
            command.execute();
        }
    }
}
