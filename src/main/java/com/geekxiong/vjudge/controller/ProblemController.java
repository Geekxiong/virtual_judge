package com.geekxiong.vjudge.controller;

import com.geekxiong.vjudge.bean.ResponseBean;
import com.geekxiong.vjudge.entity.Problem;
import com.geekxiong.vjudge.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author xiong
 */
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

    @GetMapping("/{id}")
    public ResponseBean info(@PathVariable("id") Long problemId){
        ResponseBean responseBean = ResponseBean.newResponse();
        Problem problem = problemService.getProblemById(problemId);
        responseBean.setData(problem);
        responseBean.isSuccess();
        return responseBean;
    }

    @PutMapping("/{id}")
    public ResponseBean update(@PathVariable("id")Long problemId){
        ResponseBean responseBean = ResponseBean.newResponse();
        Problem problem = problemService.updateProblemById(problemId);
        responseBean.setData(problem);
        responseBean.isSuccess();
        return responseBean;
    }

}
