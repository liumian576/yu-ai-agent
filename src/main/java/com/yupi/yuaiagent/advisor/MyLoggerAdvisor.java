package com.yupi.yuaiagent.advisor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

/**
 * MyLoggerAdvisor 类实现了 CallAroundAdvisor 和 StreamAroundAdvisor 接口，
 * 用于在AI调用前后添加日志记录功能
 */
@Slf4j
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    /**
     * 获取当前类的名称
     * @return 类的简单名称
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 获取 advisor 的顺序
     * @return 返回0，表示优先级
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 在调用前处理请求，记录请求日志
     * @param request 请求对象
     * @return 处理后的请求对象
     */
    private AdvisedRequest before(AdvisedRequest request) {
        log.info("AI Request: {}", request.userText());
        return request;
    }

/*******************    💫 Codegeex Suggestion    *******************/
    /**
     * 在调用后处理响应，记录响应日志
     * @param advisedResponse 响应对象
     */
    private void observeAfter(AdvisedResponse advisedResponse) {
        log.info("AI Response: {}", advisedResponse.response().getResult().getOutput().getText());
    }
/****************  661d6303a6444a05a02b2c2b961cbcc3  ****************/

    /**
     * 处理普通调用的环绕通知

     * 该方法实现了环绕通知的基本流程，包括前置处理、调用执行和后置处理
     * @param advisedRequest 请求对象，包含调用的各种参数和配置信息
     * @param chain 调用链，用于执行下一个环绕通知或目标方法
     * @return 响应对象，包含调用的结果和相关信息
     */
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        // 执行前置处理，可以在这里进行参数校验、日志记录等操作
        advisedRequest = this.before(advisedRequest);
        // 执行下一个环绕通知或目标方法，获取响应结果
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        // 执行后置处理，可以在这里进行结果处理、异常处理等操作
        this.observeAfter(advisedResponse);
        // 返回处理后的响应结果
        return advisedResponse;
    }

    /**
     * 处理流式调用的环绕通知

     * 该方法实现了对流式调用的前置处理、链式调用和后置处理逻辑
     * @param advisedRequest 请求对象，包含调用的所有参数和信息
     * @param chain 流式调用链，用于执行后续的拦截器或处理器
     * @return 响应对象的Flux流，聚合了所有处理结果
     */
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        // 执行前置处理，对请求对象进行预处理或验证
        advisedRequest = this.before(advisedRequest);
        // 调用下一个拦截器或处理器，获取响应流
        Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);
        // 使用消息聚合器处理响应流，并执行后置观察逻辑
        return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponses, this::observeAfter);
    }
}
