package com.geekxiong.vjudge.controller;

import com.alibaba.fastjson.JSONObject;
import com.geekxiong.vjudge.bean.ResponseBean;
import com.geekxiong.vjudge.entity.Submission;
import com.geekxiong.vjudge.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submission")
public class SubmissionController {
    private SubmissionService submissionService;

    @Autowired
    public void setSubmissionService(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/")
    public ResponseBean submitCode(@RequestBody JSONObject request){
        ResponseBean responseBean = ResponseBean.newResponse();
        Long problemId = request.getLong("probId");
        String code = request.getString("code");
        String language = request.getString("language");
        // 测试用账号
        Long userId = 1L;
        Long submitId = submissionService.submitCode(userId, problemId, code, language);
        responseBean.setData(submitId);
        return responseBean;
    }

    @GetMapping("/{id}")
    public ResponseBean info(@PathVariable("id") Long submissionId){
        ResponseBean responseBean = ResponseBean.newResponse();
        Submission submission = submissionService.getById(submissionId);
        responseBean.setData(submission);
        responseBean.isSuccess();
        return responseBean;
    }


}
