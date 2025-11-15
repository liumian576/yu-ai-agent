package com.yupi.yuaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Spring AI 框架调用 AI 大模型（Ollama）
 * 这是一个使用 Spring AI 框架调用 Ollama AI 模型的实现类
 * 通过 CommandLineRunner 接口，可以在项目启动时执行特定逻辑
 */
// 取消注释后，项目启动时会执行
//@Component  // Spring 注解，将此类标记为组件，使其成为 Spring 容器的一部分
public class OllamaAiInvoke implements CommandLineRunner {  // 实现 CommandLineRunner 接口，用于在 Spring 应用启动后执行特定代码

    @Resource  // Spring 注解，用于自动注入依赖
    private ChatModel ollamaChatModel;  // 注入 Ollama 聊天模型，用于与 AI 模型进行交互

    @Override  // 重写 CommandLineRunner 接口的 run 方法
    public void run(String... args) throws Exception {  // 项目启动时执行的方法
        // 调用 Ollama 聊天模型，发送问候语并获取响应
        AssistantMessage assistantMessage = ollamaChatModel.call(new Prompt("你好，我是鱼皮"))
                .getResult()  // 获取调用结果
                .getOutput();  // 获取输出内容
        // 打印 AI 模型的响应内容
        System.out.println(assistantMessage.getText());
    }
}
