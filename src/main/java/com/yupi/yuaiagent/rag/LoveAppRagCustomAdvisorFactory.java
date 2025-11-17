package com.yupi.yuaiagent.rag;

import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;

import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * 创建自定义的 RAG 检索增强顾问的工厂
 * 该工厂类负责创建和配置检索增强生成(RAG)顾问实例
 */
public class LoveAppRagCustomAdvisorFactory {

    /**
     * 创建自定义的 RAG 检索增强顾问
     * 该方法构建一个具有文档检索和查询增强功能的顾问实例

     *
     * @param vectorStore 向量存储，用于存储和检索文档向量
     * @param status      状态，用于过滤特定状态的文档
     * @return 自定义的 RAG 检索增强顾问，配置了文档检索器和查询增强器
     */
    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String status) {
        // 过滤特定状态的文档
        // 使用FilterExpressionBuilder构建过滤表达式，只选择状态匹配的文档
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();
        // 创建文档检索器
        // 配置向量存储文档检索器，设置过滤条件、相似度阈值和返回文档数量
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)           // 设置向量存储
                .filterExpression(expression) // 过滤条件
                .similarityThreshold(0.5) // 相似度阈值
                .topK(3) // 返回文档数量
                .build();
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())
                .build();
    }
}
