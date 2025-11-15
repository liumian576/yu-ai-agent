package com.yupi.yuaiagent.advisor;

import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * ReReadingAdvisor 类实现了 CallAroundAdvisor 和 StreamAroundAdvisor 接口，
 * 用于在调用前后处理请求，特别是重新读取输入查询的功能。
 */
public class ReReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {


    /**
     * 在请求处理之前进行预处理，将原始查询添加到用户参数中，
     * 并修改用户文本以包含重新读取查询的提示。
     *
     * @param advisedRequest 包含原始请求信息的对象
     * @return 返回处理后的 AdvisedRequest 对象
     */
    private AdvisedRequest before(AdvisedRequest advisedRequest) {

        // 创建用户参数的副本，并添加重新输入查询的参数
        Map<String, Object> advisedUserParams = new HashMap<>(advisedRequest.userParams());
        advisedUserParams.put("re2_input_query", advisedRequest.userText());

        // 构建新的请求对象，修改用户文本以包含重新读取查询的提示
        return AdvisedRequest.from(advisedRequest)
                .userText("""
                        {re2_input_query}
                        Read the question again: {re2_input_query}
                        """)
                .userParams(advisedUserParams)
                .build();
    }

    /**
     * 实现 CallAroundAdvisor 接口的方法，在调用链中处理请求
     *
     * @param advisedRequest 包含请求信息的对象
     * @param chain 调用链对象，用于传递请求到下一个处理器
     * @return 返回处理后的响应对象
     */
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(this.before(advisedRequest));
    }

    /**
     * 实现 StreamAroundAdvisor 接口的方法，在流式调用链中处理请求
     *
     * @param advisedRequest 包含请求信息的对象
     * @param chain 流式调用链对象，用于传递请求到下一个处理器
     * @return 返回处理后的响应流
     */
    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.before(advisedRequest));
    }

    /**
     * 获取处理器的顺序值，值越小优先级越高
     *
     * @return 返回处理器的顺序值
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 获取处理器的名称
     *
     * @return 返回处理器的类名
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
