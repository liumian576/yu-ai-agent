package com.yupi.yuaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * LoveAppDocumentLoader 类用于加载 Markdown 文档。
 * 它是一个 Spring 组件，使用 @Slf4j 注解提供日志功能。
 */
@Component
@Slf4j
class LoveAppDocumentLoader {

    /**
     * ResourcePatternResolver 是 Spring 框架中用于解析资源模式的接口，
     * 用于根据指定的路径模式查找资源。
     */
    private final ResourcePatternResolver resourcePatternResolver;

    /**
     * LoveAppDocumentLoader 的构造函数，通过依赖注入方式注入 ResourcePatternResolver。
     * @param resourcePatternResolver 用于解析资源路径的解析器
     */
    LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 加载 classpath:document/ 目录下所有的 Markdown 文档。
     * @return 返回包含所有加载文档的 List<Document> 列表，如果加载失败则返回空列表
     */
    public List<Document> loadMarkdowns() {
        // 创建一个空列表用于存储所有加载的文档
        List<Document> allDocuments = new ArrayList<>();
        try {
            // 获取 classpath:document/ 目录下所有的 .md 文件资源
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            // 遍历每个资源文件
            for (Resource resource : resources) {
                // 获取文件名
                String filename = resource.getFilename();
                // 提取文档倒数第 3 和第 2 个字作为标签
                String status = filename.substring(filename.length() - 6, filename.length() - 4);
                // 构建 Markdown 文档读取器配置
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)  // 启用水平规则创建文档
                        .withIncludeCodeBlock(false)            // 不包含代码块
                        .withIncludeBlockquote(false)           // 不包含引用块
                        .withAdditionalMetadata("filename", filename)  // 添加文件名作为元数据
                        .withAdditionalMetadata("status", status)      // 添加状态作为元数据
                        .build();
                // 创建 Markdown 文档读取器并读取文档内容
                MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
                // 将读取的文档添加到列表中
                allDocuments.addAll(markdownDocumentReader.get());
            }
        } catch (IOException e) {
            // 记录错误日志
            log.error("Markdown 文档加载失败", e);
        }
        // 返回所有加载的文档列表
        return allDocuments;
    }
}
