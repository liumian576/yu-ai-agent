package com.yupi.yuaiagent.app;


import com.yupi.yuaiagent.advisor.MyLoggerAdvisor;
import com.yupi.yuaiagent.chatmemory.FileBasedChatMemory;
import com.yupi.yuaiagent.rag.LoveAppRagCustomAdvisorFactory;
import com.yupi.yuaiagent.rag.PgVectorVectorStoreConfig;
import com.yupi.yuaiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * LoveApp类是一个处理恋爱咨询的Spring组件，使用阿里云的通义千问模型实现聊天功能。
 * 该组件通过ChatClient与用户交互，提供恋爱心理咨询服务。
 */
@Component  // 将此类标记为Spring组件，由Spring容器管理
@Slf4j      // 使用Lombok的@Slf4j注解，自动生成日志记录器
public class LoveApp {

    // ChatClient实例，用于与用户进行对话
    private final ChatClient chatClient;

    // 系统提示词，定义了AI助手的角色和行为准则
    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    // 内部记录类LoveReport，用于封装恋爱报告数据结构
    record LoveReport(String title, List<String> suggestions) {
    }

    /**
     * 构造函数，初始化ChatClient
     * @param dashscopeChatModel 阿里云通义千问的ChatModel实例
     */
    public LoveApp(ChatModel dashscopeChatModel) {

        // 初始化基于文件的对话记忆存储路径
       String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
       // 创建基于文件的对话记忆实例
       ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        // 构建ChatClient，设置系统提示和记忆顾问
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)  // 设置默认系统提示
                .defaultAdvisors(              // 设置默认顾问列表
                        new MessageChatMemoryAdvisor(chatMemory)  // 添加消息记忆顾问
                        // 自定义日志 Advisor，可按需开启
                        // new MyLoggerAdvisor()
//                        // 自定义推理增强 Advisor，可按需开启
//                       ,new ReReadingAdvisor()
                )
                .build();  // 构建ChatClient实例
    }

    /**
     * 处理用户聊天消息的方法
     * @param message 用户输入的消息
     * @param chatId 聊天会话ID，用于区分不同的对话
     * @return AI助手的回复内容
     */
    public String doChat(String message, String chatId) {
        // 发送用户消息并获取AI回复
        ChatResponse response = chatClient
                .prompt()  // 创建新的对话提示
                .user(message)  // 设置用户消息
                // 设置聊天记忆的会话ID和检索消息数量
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()  // 执行对话调用
                .chatResponse();  // 获取对话响应
        // 获取AI回复的文本内容
        String content = response.getResult().getOutput().getText();
        // 记录日志
        log.info("content: {}", content);
        return content;  // 返回AI回复内容
    }

    /**
     * 处理用户聊天消息并生成恋爱报告的方法
     * @param message 用户输入的消息
     * @param chatId 聊天会话ID，用于区分不同的对话
     * @return 包含标题和建议列表的恋爱报告对象
     */
    public LoveReport doChatWithReport(String message, String chatId) {
        // 使用ChatClient发送消息并获取恋爱报告
        LoveReport loveReport = chatClient
                .prompt()  // 创建新的对话提示
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")  // 设置系统提示
                .user(message)  // 设置用户消息
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)  // 设置顾问参数
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()  // 执行对话调用
                .entity(LoveReport.class);  // 将响应转换为LoveReport对象
        // 记录日志
        log.info("loveReport: {}", loveReport);
        return loveReport;  // 返回恋爱报告
    }

    // 被注释掉的VectorStore资源注入
    @Resource
    private VectorStore loveAppVectorStore;

    @Resource  // 使用@Resource注解注入RAG云顾问
    private Advisor loveAppRagCloudAdvisor;

    @Resource  // 使用@Resource注解注入向量存储配置顾问
    private VectorStore pgVectorVectorStore;
    @Resource
    private QueryRewriter queryRewriter;

/**
 * 执行基于RAG(检索增强生成)的聊天对话
 * @param message 用户输入的消息内容
 * @param chatId 聊天会话ID，用于区分不同的对话上下文
 * @return AI助手的回复内容
 */
    /**
     * 和 RAG 知识库进行对话
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {
        // 查询重写
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                // 使用改写后的查询
                .user(rewrittenMessage)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // // 应用 RAG 知识库问答
               //  .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
               //  // 应用 RAG 检索增强服务（基于云知识库服务）
               // .advisors(loveAppRagCloudAdvisor)
               //  // 应用 RAG 检索增强服务（基于 PgVector 向量存储）
               // .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
               //  // 应用自定义的 RAG 检索增强服务（文档查询器 + 上下文增强器）
               // .advisors(
               //         LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
               //                 loveAppVectorStore, "单身"
               //         )
               // )
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    @Resource
    private ToolCallback[] allTools;

    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))

                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


}