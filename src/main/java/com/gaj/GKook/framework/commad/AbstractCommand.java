package com.gaj.GKook.framework.commad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现该抽象类并传递 contextParameters 时按照对应调用 api 的参数名的小驼峰传递 String，
 * 在 executeCommand() 时获取对应的 contextParameter 要注意参数名字
 */
public abstract class AbstractCommand implements Command {
    private Map<String, Object> contextParameters = new HashMap<>();
    private List<String> userArguments = new ArrayList<>();
    private Command nextCommand;

    @Override
    public void setContextParameters(Map<String, Object> contextParams) {
        this.contextParameters.putAll(contextParams);
    }

    @Override
    public void setUserArguments(List<String> userArguments) {
        this.userArguments = userArguments;
    }

    protected Map<String, Object> getContextParameters() {
        return contextParameters;
    }

    protected List<String> getUserArguments() {
        return userArguments;
    }

    @Override
    public Command chain(Command nextCommand) {
        this.nextCommand = nextCommand;
        return this;
    }

    @Override
    public void execute() {
        executeCommand();
        if (nextCommand != null) {
            nextCommand.execute();
        }
    }

    @Override
    public Command next() {
        return this.nextCommand;
    }

    protected abstract void executeCommand();
}

