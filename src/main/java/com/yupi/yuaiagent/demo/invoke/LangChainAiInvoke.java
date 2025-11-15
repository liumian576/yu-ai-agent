package com.yupi.yuaiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;

/**
 * LangChainAiInvoke 类
 * 该类演示了如何调用通义千言(Qwen)聊天模型进行对话
 */
public class LangChainAiInvoke {

    /**
     * 程序入口方法
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 创建并配置通义千言聊天模型实例
        // 设置API密钥和模型名称
        ChatLanguageModel qwenChatModel = QwenChatModel.builder()
                .apiKey(TestApiKey.API_KEY)    // 使用测试API密钥
                .modelName("qwen-max")         // 指定使用qwen-max模型
                .build();
        // 使用聊天模型进行对话，传入预设的问题
        String answer = qwenChatModel.chat("我是程序员鱼皮，这是编程导航 codefather.cn 的 AI 超级智能体原创项目");
        // 输出模型的回答
        System.out.println(answer);
    }
}
