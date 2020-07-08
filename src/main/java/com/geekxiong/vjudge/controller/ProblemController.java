package com.geekxiong.vjudge.controller;

import com.geekxiong.vjudge.bean.ResponseBean;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/problem")
public class ProblemController {

    @GetMapping("/{ojName}-{problemId}")
    public ResponseBean info(
            HttpServletRequest request,
            @PathVariable("ojName") String ojName,
            @PathVariable("problemId") String problemId){
        ResponseBean responseBean = ResponseBean.newResponse();
        HttpSession session = request.getSession();
        session.setAttribute("xxx", ojName+"-"+problemId);


        return responseBean;
    }

}
