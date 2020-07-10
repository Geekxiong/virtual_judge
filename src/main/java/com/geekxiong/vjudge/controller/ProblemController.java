package com.geekxiong.vjudge.controller;

import com.geekxiong.vjudge.bean.ResponseBean;
import com.geekxiong.vjudge.entity.Problem;
import com.geekxiong.vjudge.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private ProblemService problemService;

    @Autowired
    public void setProblemService(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping("/{ojName}-{problemId}")
    public ResponseBean info(
            @PathVariable("ojName") String ojName,
            @PathVariable("problemId") String problemId){
        ResponseBean responseBean = ResponseBean.newResponse();
        Problem problem = problemService.getProblemByOjAndProbId(ojName, problemId);
        responseBean.setData(problem);
        responseBean.isSuccess();
        return responseBean;
    }

}
