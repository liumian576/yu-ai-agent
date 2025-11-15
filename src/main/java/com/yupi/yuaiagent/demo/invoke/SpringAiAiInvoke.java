package com.yupi.yuaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Spring AI 框架调用 AI 大模型（阿里）
 */
// 取消注释后，项目启动时会执行
//@Component
public class SpringAiAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel; // 注入阿里云 Dashscope 的 ChatModel 实例

    @Override
    public void run(String... args) throws Exception { // 实现 CommandLineRunner 接口的 run 方法，在项目启动时执行
        // 创建提示并调用阿里云大模型，获取 AI 回复
        AssistantMessage assistantMessage = dashscopeChatModel.call(new Prompt("你好，我是鱼皮"))
                .getResult() // 获取调用结果
                .getOutput(); // 获取输出内容
        System.out.println(assistantMessage.getText()); // 打印 AI 回复的文本内容
    }
}
