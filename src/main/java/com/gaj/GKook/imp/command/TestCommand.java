package com.gaj.GKook.imp.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaj.GKook.BotManager;
import com.gaj.GKook.framework.commad.AbstractCommand;
import com.gaj.GKook.framework.commad.GCommand;

import java.util.List;
import java.util.Map;

@GCommand("test")
public class TestCommand extends AbstractCommand {
    @Override
    protected void executeCommand() {
        Map<String, Object> cp = getContextParameters();

        String content = """
                [
                  {
                    "type": "card",
                    "size": "lg",
                    "theme": "warning",
                    "modules": [
                      {
                        "type": "divider"
                      },
                      {
                        "type": "section",
                        "text": {
                          "type": "kmarkdown",
                          "content": "使用`/help`来获取机器人的命令使用帮助吧~:smile:"
                        },
                        "mode": "right",
                        "accessory": {
                          "type": "button",
                          "theme": "primary",
                          "value": "/help",
                          "click": "return-val",
                          "text": {
                            "type": "plain-text",
                            "content": "帮助命令"
                          }
                        }
                      },
                      {
                        "type": "section",
                        "text": {
                          "type": "plain-text",
                          "content": "和我打个招呼吧~"
                        },
                        "mode": "right",
                        "accessory": {
                          "type": "button",
                          "theme": "primary",
                          "value": "/hello",
                          "click": "return-val",
                          "text": {
                            "type": "plain-text",
                            "content": "你好呀~"
                          }
                        }
                      }
                    ]
                  }
                ]
                """;

        BotManager.sendMessageToChannel( 10,
                (String) cp.get("targetId"),
                content,
                cp.get("tempTargetId").toString());

    }
}
