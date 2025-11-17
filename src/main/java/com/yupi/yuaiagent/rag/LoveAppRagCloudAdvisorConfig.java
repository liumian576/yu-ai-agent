package com.yupi.yuaiagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;

import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义基于阿里云知识库服务的 RAG 增强顾问
 * 该配置类创建了一个基于阿里云DashScope API的检索增强(RAG)顾问，
 * 用于在恋爱大师知识库中检索相关信息并增强回答
 */
@Configuration // 标记此类为配置类，用于定义Spring Bean的配置
@Slf4j // Lombok注解，自动生成日志器
public class LoveAppRagCloudAdvisorConfig {

    @Value("${spring.ai.dashscope.api-key}") // 从配置文件中注入DashScope API密钥
    private String dashScopeApiKey;



    /**
     * 创建并配置一个RAG增强顾问Bean
     * @return 配置好的RetrievalAugmentationAdvisor实例，用于检索增强
     */
    @Bean // 标记此方法为Bean生成方法，Spring会管理其返回的对象
    public Advisor loveAppRagCloudAdvisor() {
        // 创建DashScope API客户端实例
        DashScopeApi dashScopeApi = new DashScopeApi(dashScopeApiKey);
        // 定义知识库名称常量
        final String KNOWLEDGE_INDEX = "恋爱大师";
        // 创建阿里云文档检索器，配置为使用"恋爱大师"知识库
        DocumentRetriever dashScopeDocumentRetriever = new DashScopeDocumentRetriever(dashScopeApi,
                DashScopeDocumentRetrieverOptions.builder()
                        .withIndexName(KNOWLEDGE_INDEX) // 设置知识库名称
                        .build());
        // 构建并返回检索增强顾问实例
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(dashScopeDocumentRetriever) // 设置文档检索器
                .build(); // 构建顾问实例
    }
}
