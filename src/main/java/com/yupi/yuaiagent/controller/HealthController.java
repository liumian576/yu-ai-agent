package com.yupi.yuaiagent.controller;

import com.yupi.yuaiagent.demo.rag.MultiQueryExpanderDemo;
import jakarta.annotation.Resource;
import org.springframework.ai.rag.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Resource
    private MultiQueryExpanderDemo queryExpander;

    @GetMapping("/expand")
    public List<Query> expandQuery(@RequestParam String query) {
        return queryExpander.expand(query);
    }
}
