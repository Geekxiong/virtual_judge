package com.geekxiong.vjudge.controller;

import com.alibaba.fastjson.JSONObject;
import com.geekxiong.vjudge.bean.ResponseBean;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submission")
public class SubmissionController {

    @PostMapping("/")
    public ResponseBean submitCode(@RequestBody JSONObject request){
        ResponseBean responseBean = ResponseBean.newResponse();
        Long problemId = request.getLong("probId");
        String code = request.getString("code");
        String language = request.getString("language");
        Long userId = 1L;



        return responseBean;
    }
}
