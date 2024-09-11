package com.gaj.GKook.framework.commad;

import com.gaj.GKook.framework.config.CommandConfig;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CommandFactory {
    // 用于存储命令的注册表
    private static final Map<String, Supplier<Command>> commandRegistry = new HashMap<>();

    // 静态块注册基本命令
    static {
        // 注解加载
        try {
            init();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据设置的包名找到其下的所有被注解修饰的 Command 类
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static void init() throws IOException, ClassNotFoundException {
        String basePackage = CommandConfig.COMMAND_PACKAGE;
        String packagePath = basePackage.replace('.', '/');
        URL packageURL = Thread.currentThread().getContextClassLoader().getResource(packagePath);
        if (packageURL == null) {
            throw new IOException("Package not found: " + basePackage);
        }

        File directory = new File(URLDecoder.decode(packageURL.getFile(), StandardCharsets.UTF_8));

        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".class")) {
                        String className = basePackage + '.' + file.getName().replace(".class", "");
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(GCommand.class)) {
                            registerCommand(clazz.getAnnotation(GCommand.class).value(), () -> {
                                try {
                                    return (Command) clazz.getConstructor().newInstance();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    // 注册命令方法
    public static void registerCommand(String commandName, Supplier<Command> commandSupplier) {
        commandRegistry.put(commandName.toLowerCase(), commandSupplier);
    }

    // 创建命令的方法
    public static Command createCommand(String commandName, List<String> userArguments) {
        Supplier<Command> supplier = commandRegistry.get(commandName.toLowerCase());
//        System.out.println(commandName);
        if (supplier == null) {
            System.err.println("Unknown command: " + commandName);
            return null;
        }
        Command command = supplier.get();
        command.setUserArguments(userArguments);

        return command;
    }
}
