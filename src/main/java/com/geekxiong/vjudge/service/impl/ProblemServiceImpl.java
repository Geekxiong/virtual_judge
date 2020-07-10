package com.geekxiong.vjudge.service.impl;

import com.geekxiong.vjudge.bean.ProblemBean;
import com.geekxiong.vjudge.entity.Problem;
import com.geekxiong.vjudge.remote.RemoteUtil;
import com.geekxiong.vjudge.remote.RemoteUtilRegister;
import com.geekxiong.vjudge.repository.ProblemRepository;
import com.geekxiong.vjudge.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ProblemServiceImpl implements ProblemService {

    private ProblemRepository problemRepository;
    private RemoteUtilRegister remoteUtilRegister;

    @Autowired
    public void setProblemRepository(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @Autowired
    public void setRemoteUtilRegister(RemoteUtilRegister remoteUtilRegister) {
        this.remoteUtilRegister = remoteUtilRegister;
    }

    @Override
    public Problem getProblemByOjAndProbId(String ojName, String problemId) {
        Problem problem = problemRepository.findByOriginOjAndOriginProbId(ojName, problemId);
        if(problem==null){
            RemoteUtil remoteUtil = remoteUtilRegister.getRemoteUtil(ojName+"RemoteUtil");
            ProblemBean problemBean = remoteUtil.getProblem(problemId);
            problem = new Problem(problemBean);
            problem.setUpdateTime(new Date());
            problemRepository.save(problem);
        }
        return problem;
    }
}
