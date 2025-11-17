package com.yupi.yuaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 配置类：LoveApp向量存储配置
 * 用于配置和创建向量存储相关的Bean
 */
@Configuration
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;  // 注入文档加载器，用于加载Markdown文档
    @Resource
    private  MyKeywordEnricher myKeywordEnricher;  // 注入关键词增强器，用于为文档补充元信息
    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;  // 注入切词器，用于切分文档

    /**
     * 创建向量存储Bean
     * @param dashscopeEmbeddingModel 阿里云Dashscope的嵌入模型，用于文本向量化
     * @return 配置好的向量存储实例
     */
    @Bean
    VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        // 加载文档
        List<Document> documentList = loveAppDocumentLoader.loadMarkdowns();
        // 自主切分文档
        // List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documentList);
        // 自动补充关键词元信息
        List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(documentList);
        simpleVectorStore.add(enrichedDocuments);
        return simpleVectorStore;
    }
}