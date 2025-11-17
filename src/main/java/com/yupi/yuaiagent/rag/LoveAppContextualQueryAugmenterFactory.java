package com.yupi.yuaiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * 创建上下文查询增强器的工厂
 * 该工厂类负责创建和配置ContextualQueryAugmenter实例，
 * 用于处理恋爱相关问题的上下文增强
 */
public class LoveAppContextualQueryAugmenterFactory {

    /**
     * 创建并返回一个配置好的上下文查询增强器实例
     *
     * @return 返回一个配置好的ContextualQueryAugmenter实例
     *         该实例不允许空上下文，并设置了当上下文为空时的提示模板
     */
    public static ContextualQueryAugmenter createInstance() {
        // 创建一个空上下文时的提示模板，用于引导用户只咨询恋爱相关问题
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                你应该输出下面的内容：
                抱歉，我只能回答恋爱相关的问题，别的没办法帮到您哦，
                有问题可以联系编程导航客服 https://codefather.cn
                """);
        // 构建并返回一个不允许空上下文的查询增强器实例
        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)  // 设置不允许空上下文
                .emptyContextPromptTemplate(emptyContextPromptTemplate)  // 设置空上下文时的提示模板
                .build();  // 构建并返回实例
    }
}
