package com.gaj.GKook.framework.commad;

import java.util.List;
import java.util.Map;

// 顶层接口表示所有命令
public interface Command {
    void execute();
    void setContextParameters(Map<String, Object> contextParams);
    void setUserArguments(List<String> userArguments); // 修改为 List<String>
    Command chain(Command nextCommand);
    Command next();
}

