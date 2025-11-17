package com.yupi.yuaiagent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * 查询重写器
 * 该类负责对用户输入的查询进行重写和优化
 */
@Component
public class QueryRewriter {

    private final QueryTransformer queryTransformer;

    /**
     * 构造函数，初始化查询重写器
     * @param dashscopeChatModel DashScope聊天模型，用于构建查询转换器
     */
    public QueryRewriter(ChatModel dashscopeChatModel) {
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        // 创建查询重写转换器
        // 使用ChatClient构建器来配置和初始化查询转换器
        queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();
    }

    /**
     * 执行查询重写
     * 将用户输入的原始查询转换为优化后的查询
     * @param prompt 用户输入的原始查询文本
     * @return 重写后的查询文本
     */
    public String doQueryRewrite(String prompt) {
        // 创建查询对象
        Query query = new Query(prompt);
        // 执行查询重写
        // 使用查询转换器对原始查询进行处理
        Query transformedQuery = queryTransformer.transform(query);
        // 输出重写后的查询
        // 返回转换后的查询文本
        return transformedQuery.text();
    }
}
